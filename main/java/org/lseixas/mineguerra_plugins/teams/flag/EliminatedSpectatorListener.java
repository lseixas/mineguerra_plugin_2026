package org.lseixas.mineguerra_plugins.teams.flag;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.lseixas.mineguerra_plugins.teams.TeamRegistry;

/**
 * Blocks free-cam for eliminated players and cycles ally cameras on sneak.
 */
public class EliminatedSpectatorListener implements Listener {

    private final JavaPlugin plugin;

    public EliminatedSpectatorListener(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onSneak(PlayerToggleSneakEvent event) {
        if (!event.isSneaking()) {
            return;
        }
        Player player = event.getPlayer();
        if (!TeamRegistry.flags().isEliminated(player)) {
            return;
        }
        if (player.getGameMode() != GameMode.SPECTATOR) {
            return;
        }
        // Vanilla unhooks on sneak; next tick reattach to the next ally.
        Bukkit.getScheduler().runTask(plugin, () -> TeamRegistry.eliminatedSpectators().cycle(player));
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (!TeamRegistry.flags().isEliminated(player)) {
            return;
        }
        if (TeamRegistry.eliminatedSpectators().hasValidAllyTarget(player)) {
            return;
        }
        Location from = event.getFrom();
        Location to = event.getTo();
        if (to == null) {
            return;
        }
        // Look around only — no free flight when there is no ally camera.
        if (from.getX() != to.getX() || from.getY() != to.getY() || from.getZ() != to.getZ()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player leaving = event.getPlayer();
        EliminatedSpectatorService spectators = TeamRegistry.eliminatedSpectators();
        for (Player online : Bukkit.getOnlinePlayers()) {
            if (!TeamRegistry.flags().isEliminated(online)) {
                continue;
            }
            if (leaving.equals(online.getSpectatorTarget())) {
                Bukkit.getScheduler().runTask(plugin, () -> spectators.attach(online));
            }
        }
    }
}
