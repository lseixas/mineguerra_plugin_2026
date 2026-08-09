package org.lseixas.mineguerra_plugins.teams.flag;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.lseixas.mineguerra_plugins.teams.TeamRegistry;

import java.util.Optional;

public class FlagBreakListener implements Listener {

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        FlagService flags = TeamRegistry.flags();
        Optional<String> teamIdOpt = flags.getTeamIdAtBlock(event.getBlock());
        if (teamIdOpt.isEmpty()) {
            return;
        }

        String flagTeamId = teamIdOpt.get();
        Player breaker = event.getPlayer();
        String breakerTeam = TeamRegistry.teams().getTeamId(breaker);

        if (flagTeamId.equals(breakerTeam)) {
            event.setCancelled(true);
            breaker.sendMessage("§c§l[MineGuerra] §7Voce nao pode destruir a bandeira do seu time.");
            return;
        }

        event.setDropItems(false);
        event.setExpToDrop(0);
        flags.onFlagDestroyed(flagTeamId, breaker);
    }
}
