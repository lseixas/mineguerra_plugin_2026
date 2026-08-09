package org.lseixas.mineguerra_plugins.teams;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class TeamKillListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = victim.getKiller();

        if (killer == null || killer.equals(victim)) {
            return;
        }

        TeamService teamService = TeamRegistry.teams();
        String killerTeamId = teamService.getTeamId(killer);
        String victimTeamId = teamService.getTeamId(victim);

        if (killerTeamId == null || victimTeamId == null) {
            return;
        }

        if (killerTeamId.equals(victimTeamId)) {
            return;
        }

        TeamRegistry.kills().increment(killerTeamId);

        if (TeamRegistry.leaderboard().isEnabled()) {
            TeamRegistry.leaderboard().refreshAll();
        }
    }
}
