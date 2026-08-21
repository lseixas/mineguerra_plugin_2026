package org.lseixas.mineguerra_plugins.teams;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.lseixas.mineguerra_plugins.war.WarPhase;
import org.lseixas.mineguerra_plugins.war.WarRegistry;
import org.lseixas.mineguerra_plugins.war.WarSchedule;
import org.lseixas.mineguerra_plugins.war.WarService;
import org.lseixas.mineguerra_plugins.war.WarStateStore;
import org.lseixas.mineguerra_plugins.weapons.WeaponId;
import org.lseixas.mineguerra_plugins.weapons.WeaponOwnershipService;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class LeaderboardService {

    private static final String OBJECTIVE_NAME = "mg_kills";
    private static final String OBJECTIVE_DISPLAY = "§6§lMineGuerra";
    private static final int MAX_SIDEBAR_LINES = 15;
    private static final long CLOCK_PERIOD_TICKS = 20L;
    private static final DateTimeFormatter CLOCK_FORMAT = DateTimeFormatter.ofPattern("HH:mm");
    private static final ZoneId FALLBACK_ZONE = ZoneId.of("America/Sao_Paulo");

    private final JavaPlugin plugin;
    private final TeamsDataStore dataStore;
    private final TeamService teamService;
    private final KillStatsService killStatsService;
    private final WeaponOwnershipService weaponOwnership;

    private BukkitTask clockTask;

    public LeaderboardService(
            JavaPlugin plugin,
            TeamsDataStore dataStore,
            TeamService teamService,
            KillStatsService killStatsService,
            WeaponOwnershipService weaponOwnership
    ) {
        this.plugin = plugin;
        this.dataStore = dataStore;
        this.teamService = teamService;
        this.killStatsService = killStatsService;
        this.weaponOwnership = weaponOwnership;
    }

    public boolean isEnabled() {
        return dataStore.isLeaderboardEnabled();
    }

    public void setEnabled(boolean enabled) {
        dataStore.setLeaderboardEnabled(enabled);
        dataStore.save();

        if (enabled) {
            refreshAll();
            startClock();
        } else {
            stopClock();
            hideAll();
        }
    }

    public void refreshAll() {
        if (!dataStore.isLeaderboardEnabled()) {
            return;
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            refreshPlayer(player);
        }
    }

    public void refreshPlayer(Player player) {
        if (!dataStore.isLeaderboardEnabled()) {
            return;
        }

        Scoreboard board = teamService.getOrCreatePlayerScoreboard(player);
        player.setScoreboard(board);

        Objective existing = board.getObjective(OBJECTIVE_NAME);
        if (existing != null) {
            existing.unregister();
        }

        Objective objective = board.registerNewObjective(
                OBJECTIVE_NAME,
                org.bukkit.scoreboard.Criteria.DUMMY,
                OBJECTIVE_DISPLAY
        );
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        List<TeamDefinition> teams = killStatsService.getTeamsSortedByKills();
        int score = MAX_SIDEBAR_LINES;
        Set<String> usedEntries = new HashSet<>();

        String phaseLine = uniqueEntry(usedEntries, formatPhaseLine(), score);
        objective.getScore(phaseLine).setScore(score);
        score--;

        if (score > 0) {
            String clockLine = uniqueEntry(usedEntries, formatClockLine(), score);
            objective.getScore(clockLine).setScore(score);
            score--;
        }

        if (score > 0) {
            String nextLine = uniqueEntry(usedEntries, formatNextPhaseLine(), score);
            objective.getScore(nextLine).setScore(score);
            score--;
        }

        String killsHeader = uniqueEntry(usedEntries, "§7— Kills —", score);
        objective.getScore(killsHeader).setScore(score);
        score--;

        for (TeamDefinition teamDef : teams) {
            if (score <= 0) {
                break;
            }
            String line = formatTeamKillLine(teamDef);
            String entry = uniqueEntry(usedEntries, line, score);
            objective.getScore(entry).setScore(score);
            score--;
        }

        if (score > 0) {
            String weaponsHeader = uniqueEntry(usedEntries, "§7— Armas —", score);
            objective.getScore(weaponsHeader).setScore(score);
            score--;
        }

        for (TeamDefinition teamDef : teams) {
            if (score <= 0) {
                break;
            }
            String weaponLine = formatTeamWeaponLine(teamDef);
            String entry = uniqueEntry(usedEntries, weaponLine, score);
            objective.getScore(entry).setScore(score);
            score--;
        }

        for (WeaponId weaponId : WeaponId.values()) {
            if (score <= 0) {
                break;
            }
            String status = weaponOwnership.isAvailable(weaponId)
                    ? "§alivre"
                    : "§clockada";
            String line = "§7" + weaponId.getShortName() + " §7— " + status;
            String entry = uniqueEntry(usedEntries, line, score);
            objective.getScore(entry).setScore(score);
            score--;
        }
    }

    public void hideAll() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            hidePlayer(player);
        }
    }

    public void hidePlayer(Player player) {
        Scoreboard board = player.getScoreboard();
        Objective objective = board.getObjective(OBJECTIVE_NAME);
        if (objective != null) {
            objective.unregister();
        }
    }

    /** Liga o ticker de 1s se o placar estiver ativo (ex.: após reload com flag persistida). */
    public void ensureClockRunning() {
        if (dataStore.isLeaderboardEnabled()) {
            startClock();
        }
    }

    public void shutdown() {
        stopClock();
        hideAll();
    }

    private void startClock() {
        if (clockTask != null) {
            return;
        }
        clockTask = Bukkit.getScheduler().runTaskTimer(plugin, this::refreshAll, CLOCK_PERIOD_TICKS, CLOCK_PERIOD_TICKS);
    }

    private void stopClock() {
        if (clockTask == null) {
            return;
        }
        clockTask.cancel();
        clockTask = null;
    }

    private String formatPhaseLine() {
        WarStateStore state = WarRegistry.state();
        if (state == null) {
            return "§7Fase: §8Aguardando";
        }
        return state.getCurrentPhase()
                .map(phase -> "§eFase: §f" + phase.getDisplayName())
                .orElse("§7Fase: §8Aguardando");
    }

    private String formatClockLine() {
        return "§7Agora: §f" + CLOCK_FORMAT.format(now());
    }

    private String formatNextPhaseLine() {
        WarService warService = WarRegistry.service();
        if (warService == null) {
            return "§7Prox: §8—";
        }

        ZonedDateTime now = warService.now();
        WarSchedule schedule = warService.getSchedule();
        Optional<WarPhase> nextOpt = schedule.getNextPhase(now);
        if (nextOpt.isEmpty()) {
            return "§7Prox: §8fim";
        }

        WarPhase next = nextOpt.get();
        ZonedDateTime at = schedule.getTime(next).orElse(null);
        if (at == null) {
            return "§7Prox: §f" + next.getDisplayName();
        }

        String remaining = formatCountdown(Duration.between(now, at));
        return "§7Prox: §f" + next.getDisplayName() + " §8" + remaining;
    }

    static String formatCountdown(Duration duration) {
        long totalSeconds = Math.max(0, duration.getSeconds());
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;
        if (hours > 0) {
            return hours + "h" + String.format("%02d", minutes) + "m";
        }
        if (minutes > 0) {
            return minutes + "m" + String.format("%02d", seconds) + "s";
        }
        return seconds + "s";
    }

    private ZonedDateTime now() {
        WarService warService = WarRegistry.service();
        if (warService != null) {
            return warService.now();
        }
        return ZonedDateTime.now(FALLBACK_ZONE);
    }

    private String formatTeamKillLine(TeamDefinition team) {
        int kills = killStatsService.getKills(team.getId());
        String killLabel = kills == 1 ? "kill" : "kills";
        return team.getColor() + team.getDisplayName() + " §7— §f" + kills + " " + killLabel;
    }

    private String formatTeamWeaponLine(TeamDefinition team) {
        String weaponDisplay = weaponOwnership.getTeamWeaponDisplayName(team.getId());
        return team.getColor() + team.getDisplayName() + " §7— " + weaponDisplay;
    }

    private String uniqueEntry(Set<String> used, String display, int score) {
        String entry = display;
        int suffix = 0;
        while (used.contains(entry)) {
            suffix++;
            entry = display + "§r§" + Integer.toHexString(suffix % 16);
        }
        if (entry.length() > 40) {
            entry = entry.substring(0, 40);
        }
        used.add(entry);
        return entry;
    }
}
