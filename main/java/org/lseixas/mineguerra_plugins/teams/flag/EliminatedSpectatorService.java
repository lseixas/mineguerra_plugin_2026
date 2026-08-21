package org.lseixas.mineguerra_plugins.teams.flag;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.lseixas.mineguerra_plugins.teams.TeamService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Keeps eliminated players locked to living teammates' cameras (no free-roam,
 * no spectating other teams).
 */
public class EliminatedSpectatorService {

    private static final long ATTACH_DELAY_TICKS = 5L;
    private static final long ENFORCE_PERIOD_TICKS = 10L;

    private final JavaPlugin plugin;
    private final FlagService flagService;
    private final TeamService teamService;
    private BukkitTask enforceTask;

    public EliminatedSpectatorService(JavaPlugin plugin, FlagService flagService, TeamService teamService) {
        this.plugin = plugin;
        this.flagService = flagService;
        this.teamService = teamService;
    }

    public void start() {
        if (enforceTask != null) {
            return;
        }
        enforceTask = Bukkit.getScheduler().runTaskTimer(
                plugin,
                this::enforceAll,
                ENFORCE_PERIOD_TICKS,
                ENFORCE_PERIOD_TICKS
        );
    }

    public void stop() {
        if (enforceTask != null) {
            enforceTask.cancel();
            enforceTask = null;
        }
    }

    public void attachSoon(Player spectator) {
        Bukkit.getScheduler().runTaskLater(plugin, () -> attach(spectator), ATTACH_DELAY_TICKS);
    }

    public void attach(Player spectator) {
        if (!canControl(spectator)) {
            return;
        }
        spectator.setGameMode(GameMode.SPECTATOR);

        List<Player> allies = livingAllies(spectator);
        if (allies.isEmpty()) {
            parkWithoutTarget(spectator);
            return;
        }

        Player current = asPlayer(spectator.getSpectatorTarget());
        Player target = (current != null && allies.contains(current)) ? current : allies.get(0);
        bind(spectator, target);
    }

    /**
     * Shift (exit camera) cycles to the next living ally instead of free-cam.
     */
    public void cycle(Player spectator) {
        if (!canControl(spectator)) {
            return;
        }
        spectator.setGameMode(GameMode.SPECTATOR);

        List<Player> allies = livingAllies(spectator);
        if (allies.isEmpty()) {
            parkWithoutTarget(spectator);
            return;
        }

        Player current = asPlayer(spectator.getSpectatorTarget());
        int index = current == null ? -1 : allies.indexOf(current);
        Player next = allies.get((index + 1) % allies.size());
        bind(spectator, next);
        spectator.sendMessage("§7Espectando: §f" + next.getName());
    }

    public void onRevived(Player player) {
        if (player == null || !player.isOnline()) {
            return;
        }
        player.setSpectatorTarget(null);
    }

    public boolean hasValidAllyTarget(Player spectator) {
        Player target = asPlayer(spectator.getSpectatorTarget());
        return target != null && isValidAllyTarget(spectator, target);
    }

    private void enforceAll() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!flagService.isEliminated(player)) {
                continue;
            }
            if (player.getGameMode() != GameMode.SPECTATOR) {
                player.setGameMode(GameMode.SPECTATOR);
            }
            if (!hasValidAllyTarget(player)) {
                attach(player);
            }
        }
    }

    private boolean canControl(Player spectator) {
        return spectator != null
                && spectator.isOnline()
                && flagService.isEliminated(spectator);
    }

    private List<Player> livingAllies(Player spectator) {
        String teamId = teamService.getTeamId(spectator);
        if (teamId == null) {
            return List.of();
        }
        List<Player> allies = new ArrayList<>();
        for (Player member : teamService.getOnlineMembers(teamId)) {
            if (isValidAllyTarget(spectator, member)) {
                allies.add(member);
            }
        }
        return allies;
    }

    private boolean isValidAllyTarget(Player spectator, Player candidate) {
        if (candidate == null || !candidate.isOnline() || candidate.equals(spectator)) {
            return false;
        }
        if (flagService.isEliminated(candidate) || candidate.isDead()) {
            return false;
        }
        if (candidate.getGameMode() == GameMode.SPECTATOR) {
            return false;
        }
        String spectatorTeam = teamService.getTeamId(spectator);
        String candidateTeam = teamService.getTeamId(candidate);
        return spectatorTeam != null && spectatorTeam.equals(candidateTeam);
    }

    private void bind(Player spectator, Player target) {
        if (!spectator.getWorld().equals(target.getWorld())) {
            spectator.teleport(target.getLocation());
        }
        spectator.setSpectatorTarget(target);
    }

    private void parkWithoutTarget(Player spectator) {
        spectator.setSpectatorTarget(null);
        Location anchor = resolveAnchor(spectator);
        if (anchor != null && !sameBlock(spectator.getLocation(), anchor)) {
            spectator.teleport(anchor);
        }
    }

    private Location resolveAnchor(Player spectator) {
        String teamId = teamService.getTeamId(spectator);
        if (teamId != null) {
            Optional<TeamFlag> flag = flagService.getFlag(teamId);
            if (flag.isPresent()) {
                Location flagLoc = flag.get().toLocation();
                if (flagLoc != null) {
                    return flagLoc;
                }
            }
        }
        return spectator.getLocation();
    }

    private static Player asPlayer(Entity entity) {
        return entity instanceof Player player ? player : null;
    }

    private static boolean sameBlock(Location a, Location b) {
        return a.getWorld() != null
                && a.getWorld().equals(b.getWorld())
                && a.getBlockX() == b.getBlockX()
                && a.getBlockY() == b.getBlockY()
                && a.getBlockZ() == b.getBlockZ();
    }
}
