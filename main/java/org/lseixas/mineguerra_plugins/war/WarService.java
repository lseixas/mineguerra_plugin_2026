package org.lseixas.mineguerra_plugins.war;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.lseixas.mineguerra_plugins.teams.TeamDefinition;
import org.lseixas.mineguerra_plugins.teams.TeamRegistry;
import org.lseixas.mineguerra_plugins.teams.flag.TeamFlag;
import org.lseixas.mineguerra_plugins.traders.TraderType;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Aplica as fases do evento. Cada fase é idempotente: aplicar duas vezes não
 * duplica efeito, e o que já venceu antes de um restart é reaplicado em silêncio.
 */
public class WarService {

    private final JavaPlugin plugin;
    private final WarStateStore state;
    private final BorderService borderService;
    private WarSchedule schedule;

    public WarService(JavaPlugin plugin, WarStateStore state, WarSchedule schedule, BorderService borderService) {
        this.plugin = plugin;
        this.state = state;
        this.schedule = schedule;
        this.borderService = borderService;
    }

    public WarSchedule getSchedule() {
        return schedule;
    }

    public void setSchedule(WarSchedule schedule) {
        this.schedule = schedule;
    }

    public ZonedDateTime now() {
        return ZonedDateTime.now(schedule.getZone());
    }

    /**
     * Reaplica em silêncio o estado das fases já vencidas (usado no onEnable).
     * Não anuncia nada: os jogadores já viram o broadcast quando a fase virou.
     */
    public void catchUp() {
        boolean changed = false;
        for (WarPhase phase : schedule.getDuePhases(now())) {
            if (state.markApplied(phase)) {
                applyEffect(phase, false);
                changed = true;
            }
        }
        if (changed) {
            state.save();
        }
        applyPersistentState();
    }

    /** Reaplica efeitos que vivem em estado (PvP, border) depois de um restart. */
    private void applyPersistentState() {
        if (state.isApplied(WarPhase.FECHAR_CENTRO)) {
            schedule.getBorder().ifPresent(settings -> {
                World world = resolveWorld();
                if (world != null) {
                    borderService.startShrink(world, settings);
                }
            });
        }
    }

    /** Aplica as fases vencidas que ainda faltam, com anúncio. Chamado pelo ticker. */
    public List<WarPhase> advance() {
        List<WarPhase> applied = new ArrayList<>();
        for (WarPhase phase : schedule.getDuePhases(now())) {
            if (state.markApplied(phase)) {
                applyEffect(phase, true);
                applied.add(phase);
            }
        }
        if (!applied.isEmpty()) {
            state.save();
        }
        return applied;
    }

    /** Força uma fase fora de hora (teste/staff), reaplicando o efeito. */
    public void forcePhase(WarPhase phase) {
        state.markApplied(phase);
        applyEffect(phase, true);
        state.save();
    }

    private void applyEffect(WarPhase phase, boolean announce) {
        switch (phase) {
            case INICIO -> state.setPvpEnabled(false);
            case PVP_ON -> {
                state.setPvpEnabled(true);
                if (announce) {
                    warnTeamsWithoutFlag();
                }
            }
            // Spawna também no catch-up: se o servidor estava fora no horário
            // da fase, o NPC nunca teria aparecido. markApplied evita duplicar.
            case TRAPACEIRO -> spawnTrapaceiro(announce);
            case JULGAMENTO -> {
                if (announce) {
                    runJudgement();
                }
            }
            case HARDCORE -> state.setHardcore(true);
            case FECHAR_CENTRO -> startBorderShrink();
        }

        if (announce) {
            announcePhase(phase);
        }
        plugin.getLogger().info("Fase do evento aplicada: " + phase.getConfigKey()
                + (announce ? "" : " (catch-up silencioso)"));
    }

    private void announcePhase(WarPhase phase) {
        Bukkit.broadcastMessage("§8§m----------------------------------------");
        Bukkit.broadcastMessage(phase.getTitle());
        Bukkit.broadcastMessage(phase.getSubtitle());
        Bukkit.broadcastMessage("§8§m----------------------------------------");
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendTitle(phase.getTitle(), phase.getSubtitle(), 10, 60, 20);
        }
    }

    private void warnTeamsWithoutFlag() {
        List<String> missing = new ArrayList<>();
        for (TeamDefinition team : TeamRegistry.teams().getAllTeams()) {
            Optional<TeamFlag> flag = TeamRegistry.flags().getFlag(team.getId());
            if (flag.isEmpty() || !flag.get().isAlive()) {
                missing.add(team.getId());
            }
        }
        if (missing.isEmpty()) {
            return;
        }
        Bukkit.broadcastMessage("§e§l[MineGuerra] §7Times sem bandeira de pe: §f"
                + String.join("§7, §f", missing));
    }

    private void spawnTrapaceiro(boolean announce) {
        Optional<WarSchedule.TraderSpawn> spawnOpt = schedule.getTraderSpawn();
        World world = resolveWorld();
        if (spawnOpt.isEmpty() || world == null) {
            plugin.getLogger().warning("Fase trapaceiro sem coordenadas ou mundo valido.");
            return;
        }
        WarSchedule.TraderSpawn spawn = spawnOpt.get();
        Location location = new Location(world, spawn.x() + 0.5, spawn.y(), spawn.z() + 0.5);
        // No catch-up isso roda dentro do onEnable; o primeiro tick é mais seguro
        // para carregar o chunk e spawnar a entidade.
        plugin.getServer().getScheduler().runTask(plugin, () -> TraderType.TRAPACEIRO.spawn(location));
        if (announce) {
            Bukkit.broadcastMessage("§6§l[MineGuerra] §7Trapaceiro em §f"
                    + (int) spawn.x() + " " + (int) spawn.y() + " " + (int) spawn.z());
        }
    }

    private void runJudgement() {
        List<TeamFlag> alive = new ArrayList<>();
        for (TeamDefinition team : TeamRegistry.teams().getAllTeams()) {
            TeamRegistry.flags().getFlag(team.getId())
                    .filter(TeamFlag::isAlive)
                    .ifPresent(alive::add);
        }

        if (alive.isEmpty()) {
            Bukkit.broadcastMessage("§c§l[MineGuerra] §7Nenhuma bandeira de pe. Sem vencedor por bandeira.");
            return;
        }

        if (alive.size() == 1) {
            String teamId = alive.get(0).getTeamId();
            String display = TeamRegistry.teams().getTeam(teamId)
                    .map(team -> team.getColor() + team.getDisplayName())
                    .orElse("§f" + teamId);
            Bukkit.broadcastMessage("§6§l[MineGuerra] §7Unica bandeira de pe: " + display);
            Bukkit.broadcastMessage("§a§lVITORIA POR BANDEIRA: " + display);
            return;
        }

        Bukkit.broadcastMessage("§e§l[MineGuerra] §7Mais de uma bandeira de pe — coordenadas liberadas:");
        for (TeamFlag flag : alive) {
            String display = TeamRegistry.teams().getTeam(flag.getTeamId())
                    .map(team -> team.getColor() + team.getDisplayName())
                    .orElse("§f" + flag.getTeamId());
            Bukkit.broadcastMessage(display + " §7— §f" + flag.getWorldName() + " "
                    + (int) flag.getX() + " " + (int) flag.getY() + " " + (int) flag.getZ());
        }
    }

    private void startBorderShrink() {
        Optional<WarSchedule.BorderSettings> settings = schedule.getBorder();
        World world = resolveWorld();
        if (settings.isEmpty() || world == null) {
            plugin.getLogger().warning("Fase fechar-centro sem configuracao ou mundo valido.");
            return;
        }
        borderService.startShrink(world, settings.get());
    }

    private World resolveWorld() {
        World world = Bukkit.getWorld(schedule.getWorldName());
        if (world != null) {
            return world;
        }
        List<World> worlds = Bukkit.getWorlds();
        return worlds.isEmpty() ? null : worlds.get(0);
    }
}
