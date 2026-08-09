package org.lseixas.mineguerra_plugins.teams.flag;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.lseixas.mineguerra_plugins.teams.TeamRegistry;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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

        if (breakerTeam == null) {
            event.setCancelled(true);
            breaker.sendMessage("§c§l[MineGuerra] §7Voce precisa estar em um time para capturar bandeiras.");
            return;
        }

        if (flagTeamId.equals(breakerTeam)) {
            event.setCancelled(true);
            breaker.sendMessage("§c§l[MineGuerra] §7Voce nao pode destruir a bandeira do seu time.");
            return;
        }

        event.setDropItems(false);
        event.setExpToDrop(0);
        flags.onFlagDestroyed(flagTeamId, breaker);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityExplode(EntityExplodeEvent event) {
        handleExplosionBlocks(event.blockList());
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockExplode(BlockExplodeEvent event) {
        handleExplosionBlocks(event.blockList());
    }

    private void handleExplosionBlocks(List<Block> blocks) {
        FlagService flags = TeamRegistry.flags();
        List<Block> flagBlocks = new ArrayList<>();
        List<String> flagTeamIds = new ArrayList<>();

        Iterator<Block> iterator = blocks.iterator();
        while (iterator.hasNext()) {
            Block block = iterator.next();
            Optional<String> teamIdOpt = flags.getTeamIdAtBlock(block);
            if (teamIdOpt.isEmpty()) {
                continue;
            }
            iterator.remove();
            flagBlocks.add(block);
            flagTeamIds.add(teamIdOpt.get());
        }

        for (int i = 0; i < flagBlocks.size(); i++) {
            flags.onFlagDestroyed(flagTeamIds.get(i), null);
            flagBlocks.get(i).setType(Material.AIR);
        }
    }
}
