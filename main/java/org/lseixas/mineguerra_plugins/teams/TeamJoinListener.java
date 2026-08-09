package org.lseixas.mineguerra_plugins.teams;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class TeamJoinListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        TeamRegistry.teams().applyPlayerTeam(player);
        TeamRegistry.flags().applyEliminatedState(player);

        if (TeamRegistry.leaderboard().isEnabled()) {
            TeamRegistry.leaderboard().refreshPlayer(player);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        TeamRegistry.leaderboard().hidePlayer(event.getPlayer());
    }
}
