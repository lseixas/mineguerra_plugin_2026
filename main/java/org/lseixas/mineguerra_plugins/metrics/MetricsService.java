package org.lseixas.mineguerra_plugins.metrics;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.lseixas.mineguerra_plugins.teams.TeamRegistry;
import org.lseixas.mineguerra_plugins.war.WarRegistry;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

/**
 * In-memory aggregates + JSONL/snapshot writer. Only records while a session is open
 * and the war schedule is running.
 */
public final class MetricsService {

    private static final DateTimeFormatter SESSION_ID =
            DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'").withZone(ZoneOffset.UTC);
    private static final long SNAPSHOT_PERIOD_TICKS = 20L * 30L;
    private static final long EVENT_FLUSH_PERIOD_TICKS = 20L * 5L;
    private static final long TICK_DELTA_PERIOD_TICKS = 20L * 60L;

    private final JavaPlugin plugin;
    private final MetricsSessionStore store;

    private volatile boolean recording;
    private String sessionId;
    private Instant startedAt;
    private Instant updatedAt;
    private Instant lastFlushAt;
    private long eventCount;

    private final Map<UUID, PlayerMetrics> players = new ConcurrentHashMap<>();
    private final Map<UUID, PlayerMetrics> lastTickBaseline = new ConcurrentHashMap<>();

    private BukkitTask snapshotTask;
    private BukkitTask eventFlushTask;
    private BukkitTask tickDeltaTask;

    public MetricsService(JavaPlugin plugin, MetricsSessionStore store) {
        this.plugin = plugin;
        this.store = store;
    }

    public MetricsSessionStore getStore() {
        return store;
    }

    public boolean isRecording() {
        return recording && WarRegistry.state() != null && WarRegistry.state().isRunning();
    }

    public Optional<String> getSessionId() {
        return Optional.ofNullable(sessionId);
    }

    public long getEventCount() {
        return eventCount;
    }

    public Optional<Instant> getStartedAt() {
        return Optional.ofNullable(startedAt);
    }

    public Optional<Instant> getLastFlushAt() {
        return Optional.ofNullable(lastFlushAt);
    }

    public Optional<java.nio.file.Path> getSessionDir() {
        return store.getSessionDir();
    }

    public synchronized void startSession() {
        if (recording) {
            return;
        }
        String id = SESSION_ID.format(Instant.now());
        try {
            store.openNewSession(id);
        } catch (IOException ex) {
            plugin.getLogger().log(Level.SEVERE, "Failed to open metrics session", ex);
            return;
        }
        players.clear();
        lastTickBaseline.clear();
        sessionId = id;
        startedAt = Instant.now();
        updatedAt = startedAt;
        eventCount = 0;
        recording = true;
        startTasks();
        emitSessionEvent("session_start");
        flushNow();
        plugin.getLogger().info("Metrics session started: " + id);
    }

    public synchronized void resumeIfRunning() {
        if (WarRegistry.state() == null || !WarRegistry.state().isRunning()) {
            return;
        }
        if (recording) {
            return;
        }
        Optional<String> active = store.readActivePointer();
        if (active.isEmpty()) {
            startSession();
            return;
        }
        try {
            if (!store.openExistingSession(active.get())) {
                startSession();
                return;
            }
        } catch (IOException ex) {
            plugin.getLogger().log(Level.WARNING, "Failed to resume metrics session; starting new", ex);
            startSession();
            return;
        }

        sessionId = active.get();
        Optional<MetricsSnapshot> loaded = store.loadSnapshot();
        if (loaded.isPresent()) {
            applySnapshot(loaded.get());
        } else {
            startedAt = Instant.now();
            eventCount = 0;
            players.clear();
        }
        updatedAt = Instant.now();
        recording = true;
        startTasks();
        emitSessionEvent("session_resume");
        flushNow();
        plugin.getLogger().info("Metrics session resumed: " + sessionId);
    }

    public synchronized void stopSession() {
        if (!recording && sessionId == null) {
            return;
        }
        if (recording) {
            emitSessionEvent("session_stop");
        }
        recording = false;
        stopTasks();
        flushNow();
        store.clearActivePointer();
        store.closeQuietly();
        plugin.getLogger().info("Metrics session stopped: " + sessionId);
        sessionId = null;
    }

    /** Called on war reset: finalize current session without starting a new one. */
    public synchronized void resetSession() {
        if (recording || sessionId != null) {
            if (recording) {
                emitSessionEvent("session_reset");
            }
            recording = false;
            stopTasks();
            flushNow();
            store.clearActivePointer();
            store.closeQuietly();
            sessionId = null;
            players.clear();
            lastTickBaseline.clear();
            eventCount = 0;
            startedAt = null;
            updatedAt = null;
            lastFlushAt = null;
        }
    }

    public synchronized void flushNow() {
        if (sessionId == null) {
            return;
        }
        refreshOnlineStats();
        MetricsSnapshot snapshot = buildSnapshot();
        store.writeSnapshot(snapshot);
        lastFlushAt = Instant.now();
    }

    public void shutdown() {
        if (recording) {
            emitSessionEvent("session_shutdown");
            recording = false;
        }
        stopTasks();
        flushNow();
        store.closeQuietly();
    }

    // --- recording API ---

    public void recordDeath(Player victim, Player killer, boolean validPvpKill) {
        if (!isRecording()) {
            return;
        }
        PlayerMetrics victimMetrics = ensurePlayer(victim);
        victimMetrics.addDeath(1);

        Map<String, Object> death = basePlayerEvent(victim, "death");
        if (killer != null) {
            death.put("killer", killer.getUniqueId().toString());
            death.put("killerName", killer.getName());
            death.put("killerTeam", TeamRegistry.teams().getTeamId(killer));
        }
        appendEvent(death);

        if (validPvpKill && killer != null) {
            PlayerMetrics killerMetrics = ensurePlayer(killer);
            killerMetrics.addKill(1);
            Map<String, Object> kill = basePlayerEvent(killer, "pvp_kill");
            kill.put("victim", victim.getUniqueId().toString());
            kill.put("victimName", victim.getName());
            kill.put("victimTeam", TeamRegistry.teams().getTeamId(victim));
            appendEvent(kill);
        }
    }

    public void recordMobKill(Player killer, String entityType) {
        if (!isRecording() || killer == null) {
            return;
        }
        ensurePlayer(killer).addMobKill(1);
        Map<String, Object> event = basePlayerEvent(killer, "mob_kill");
        event.put("entity", entityType);
        appendEvent(event);
    }

    public void recordBlockBreak(Player player, Material material) {
        if (!isRecording() || player == null || material == null) {
            return;
        }
        PlayerMetrics metrics = ensurePlayer(player);
        BlockCategory category = BlockCategory.of(material);
        metrics.addBlockBroken(category, 1);
        if (CropMaterials.isHarvestable(material)) {
            metrics.addCropHarvested(1);
            Map<String, Object> event = basePlayerEvent(player, "crop_harvest");
            event.put("material", material.name());
            appendEvent(event);
        }
    }

    public void recordBlockPlace(Player player, Material material) {
        if (!isRecording() || player == null || material == null) {
            return;
        }
        PlayerMetrics metrics = ensurePlayer(player);
        BlockCategory category = BlockCategory.of(material);
        metrics.addBlockPlaced(category, 1);
        if (CropMaterials.isPlantable(material) || CropMaterials.isHarvestable(material)) {
            // Seeds/crops placed count as plant; harvestable blocks placed (e.g. sugar cane) too
            if (CropMaterials.isPlantable(material)
                    || material == Material.SUGAR_CANE
                    || material == Material.NETHER_WART
                    || material == Material.CACTUS
                    || material == Material.BAMBOO
                    || material == Material.KELP
                    || material == Material.CHORUS_FLOWER) {
                metrics.addCropPlanted(1);
                Map<String, Object> event = basePlayerEvent(player, "crop_plant");
                event.put("material", material.name());
                appendEvent(event);
            }
        }
    }

    public void recordItemGain(Player player, Material material, int amount, String source) {
        if (!isRecording() || player == null || material == null || amount <= 0) {
            return;
        }
        if (!TrackedMaterials.isTracked(material)) {
            return;
        }
        PlayerMetrics metrics = ensurePlayer(player);
        metrics.addItemGained(material.name(), amount);
        int emeralds = TrackedMaterials.toEmeraldUnits(material, amount);
        if (emeralds > 0) {
            metrics.addEmeraldsGained(emeralds);
        }
        Map<String, Object> event = basePlayerEvent(player, "item_gain");
        event.put("item", material.name());
        event.put("amount", amount);
        event.put("source", source);
        appendEvent(event);
    }

    public void recordItemSpend(Player player, Material material, int amount, String source) {
        if (!isRecording() || player == null || material == null || amount <= 0) {
            return;
        }
        if (!TrackedMaterials.isTracked(material)) {
            return;
        }
        PlayerMetrics metrics = ensurePlayer(player);
        metrics.addItemSpent(material.name(), amount);
        int emeralds = TrackedMaterials.toEmeraldUnits(material, amount);
        if (emeralds > 0) {
            metrics.addEmeraldsSpent(emeralds);
        }
        Map<String, Object> event = basePlayerEvent(player, "item_spend");
        event.put("item", material.name());
        event.put("amount", amount);
        event.put("source", source);
        appendEvent(event);
    }

    public void recordFlagCapture(Player breaker, String flagTeamId) {
        if (!isRecording()) {
            return;
        }
        if (breaker != null) {
            ensurePlayer(breaker).addFlagCapture(1);
            Map<String, Object> event = basePlayerEvent(breaker, "flag_capture");
            event.put("flagTeam", flagTeamId);
            appendEvent(event);
        } else {
            Map<String, Object> event = new LinkedHashMap<>();
            event.put("ts", Instant.now().toString());
            event.put("type", "flag_capture");
            event.put("flagTeam", flagTeamId);
            event.put("source", "explosion");
            appendEvent(event);
        }
    }

    public void recordPhase(String phaseKey, boolean announce) {
        if (!isRecording()) {
            return;
        }
        Map<String, Object> event = new LinkedHashMap<>();
        event.put("ts", Instant.now().toString());
        event.put("type", "phase");
        event.put("phase", phaseKey);
        event.put("announce", announce);
        appendEvent(event);
    }

    public void emitTickDeltas() {
        if (!isRecording()) {
            return;
        }
        refreshOnlineStats();
        for (Map.Entry<UUID, PlayerMetrics> entry : players.entrySet()) {
            UUID uuid = entry.getKey();
            PlayerMetrics current = entry.getValue();
            PlayerMetrics baseline = lastTickBaseline.computeIfAbsent(uuid, id -> current.copy());
            Map<String, Long> delta = current.deltaFrom(baseline);
            if (delta.isEmpty()) {
                continue;
            }
            Map<String, Object> event = new LinkedHashMap<>();
            event.put("ts", Instant.now().toString());
            event.put("type", "tick_delta");
            event.put("player", uuid.toString());
            event.put("name", current.getName());
            event.put("team", current.getTeamId());
            event.put("delta", delta);
            appendEvent(event);
            lastTickBaseline.put(uuid, current.copy());
        }
    }

    // --- internals ---

    private PlayerMetrics ensurePlayer(Player player) {
        PlayerMetrics metrics = players.computeIfAbsent(player.getUniqueId(), id -> new PlayerMetrics());
        metrics.setName(player.getName());
        if (TeamRegistry.teams() != null) {
            metrics.setTeamId(TeamRegistry.teams().getTeamId(player));
        }
        return metrics;
    }

    private Map<String, Object> basePlayerEvent(Player player, String type) {
        Map<String, Object> event = new LinkedHashMap<>();
        event.put("ts", Instant.now().toString());
        event.put("type", type);
        event.put("player", player.getUniqueId().toString());
        event.put("name", player.getName());
        event.put("team", TeamRegistry.teams() != null ? TeamRegistry.teams().getTeamId(player) : null);
        return event;
    }

    private void appendEvent(Map<String, Object> event) {
        updatedAt = Instant.now();
        eventCount++;
        store.appendEvent(event);
    }

    private void emitSessionEvent(String type) {
        Map<String, Object> event = new LinkedHashMap<>();
        event.put("ts", Instant.now().toString());
        event.put("type", type);
        event.put("sessionId", sessionId);
        appendEvent(event);
    }

    private void applySnapshot(MetricsSnapshot snapshot) {
        startedAt = parseInstant(snapshot.getStartedAt()).orElse(Instant.now());
        eventCount = snapshot.getEventCount();
        players.clear();
        if (snapshot.getPlayers() != null) {
            for (Map.Entry<String, PlayerMetrics> entry : snapshot.getPlayers().entrySet()) {
                try {
                    UUID uuid = UUID.fromString(entry.getKey());
                    PlayerMetrics metrics = entry.getValue();
                    if (metrics != null) {
                        players.put(uuid, metrics);
                    }
                } catch (IllegalArgumentException ignored) {
                    // skip malformed uuid
                }
            }
        }
        lastTickBaseline.clear();
        for (Map.Entry<UUID, PlayerMetrics> entry : players.entrySet()) {
            lastTickBaseline.put(entry.getKey(), entry.getValue().copy());
        }
    }

    private Optional<Instant> parseInstant(String value) {
        if (value == null || value.isBlank()) {
            return Optional.empty();
        }
        try {
            return Optional.of(Instant.parse(value));
        } catch (Exception ex) {
            return Optional.empty();
        }
    }

    private MetricsSnapshot buildSnapshot() {
        MetricsSnapshot snapshot = new MetricsSnapshot();
        snapshot.setSessionId(sessionId);
        snapshot.setStartedAt(startedAt != null ? startedAt.toString() : null);
        snapshot.setUpdatedAt(Instant.now().toString());
        snapshot.setRecording(recording);
        snapshot.setEventCount(eventCount);

        Map<String, PlayerMetrics> playerMap = new LinkedHashMap<>();
        Map<String, TeamMetrics> teamMap = new LinkedHashMap<>();
        for (Map.Entry<UUID, PlayerMetrics> entry : players.entrySet()) {
            PlayerMetrics copy = entry.getValue().copy();
            playerMap.put(entry.getKey().toString(), copy);
            String teamId = copy.getTeamId();
            if (teamId != null && !teamId.isBlank()) {
                teamMap.computeIfAbsent(teamId, id -> new TeamMetrics()).add(copy);
            }
        }
        snapshot.setPlayers(playerMap);
        snapshot.setTeams(teamMap);
        return snapshot;
    }

    private void refreshOnlineStats() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            PlayerMetrics metrics = players.get(player.getUniqueId());
            if (metrics == null) {
                if (!recording) {
                    continue;
                }
                metrics = ensurePlayer(player);
            } else {
                metrics.setName(player.getName());
                String teamId = TeamRegistry.teams() != null
                        ? TeamRegistry.teams().getTeamId(player)
                        : null;
                metrics.setTeamId(teamId);
            }
            try {
                // PLAY_ONE_MINUTE is measured in ticks (1/20 s); convert to ms
                long ticks = player.getStatistic(Statistic.PLAY_ONE_MINUTE);
                metrics.setPlayTimeMs(ticks * 50L);
            } catch (Exception ignored) {
                // statistic unavailable
            }
            try {
                long walk = player.getStatistic(Statistic.WALK_ONE_CM);
                long sprint = player.getStatistic(Statistic.SPRINT_ONE_CM);
                long crouch = player.getStatistic(Statistic.CROUCH_ONE_CM);
                long swim = player.getStatistic(Statistic.SWIM_ONE_CM);
                long fly = player.getStatistic(Statistic.FLY_ONE_CM);
                long boat = player.getStatistic(Statistic.BOAT_ONE_CM);
                long horse = player.getStatistic(Statistic.HORSE_ONE_CM);
                long aviate = player.getStatistic(Statistic.AVIATE_ONE_CM);
                metrics.setDistanceCm(walk + sprint + crouch + swim + fly + boat + horse + aviate);
            } catch (Exception ignored) {
                // statistic unavailable
            }
        }
    }

    private void startTasks() {
        stopTasks();
        snapshotTask = plugin.getServer().getScheduler().runTaskTimer(plugin, this::flushNow,
                SNAPSHOT_PERIOD_TICKS, SNAPSHOT_PERIOD_TICKS);
        eventFlushTask = plugin.getServer().getScheduler().runTaskTimer(plugin, store::flushEvents,
                EVENT_FLUSH_PERIOD_TICKS, EVENT_FLUSH_PERIOD_TICKS);
        tickDeltaTask = plugin.getServer().getScheduler().runTaskTimer(plugin, this::emitTickDeltas,
                TICK_DELTA_PERIOD_TICKS, TICK_DELTA_PERIOD_TICKS);
    }

    private void stopTasks() {
        if (snapshotTask != null) {
            snapshotTask.cancel();
            snapshotTask = null;
        }
        if (eventFlushTask != null) {
            eventFlushTask.cancel();
            eventFlushTask = null;
        }
        if (tickDeltaTask != null) {
            tickDeltaTask.cancel();
            tickDeltaTask = null;
        }
    }
}
