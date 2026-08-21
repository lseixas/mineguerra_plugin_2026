package org.lseixas.mineguerra_plugins.teams.flag;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.lseixas.mineguerra_plugins.teams.TeamRegistry;

import java.util.Optional;

public class FlagRespawnListener implements Listener {

    private final JavaPlugin plugin;

    public FlagRespawnListener(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        FlagService flags = TeamRegistry.flags();

        if (flags.isEliminated(player)) {
            return;
        }

        String teamId = TeamRegistry.teams().getTeamId(player);
        if (teamId == null) {
            return;
        }

        Optional<TeamFlag> flagOpt = flags.getFlag(teamId);
        if (flagOpt.isPresent() && !flagOpt.get().isAlive()) {
            flags.eliminatePlayer(player);
            TeamRegistry.weapons().rescanAll();
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        FlagService flags = TeamRegistry.flags();

        if (flags.isEliminated(player)) {
            Bukkit.getScheduler().runTask(plugin, () -> {
                player.setGameMode(GameMode.SPECTATOR);
                TeamRegistry.eliminatedSpectators().attach(player);
            });
            return;
        }

        String teamId = TeamRegistry.teams().getTeamId(player);
        if (teamId == null) {
            return;
        }

        Optional<TeamFlag> flagOpt = flags.getFlag(teamId);
        if (flagOpt.isEmpty() || !flagOpt.get().isAlive()) {
            return;
        }

        Location flagLoc = flagOpt.get().toLocation();
        if (flagLoc != null) {
            event.setRespawnLocation(flagLoc);
        }
    }
}
