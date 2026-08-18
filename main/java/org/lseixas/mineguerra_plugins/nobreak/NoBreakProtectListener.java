package org.lseixas.mineguerra_plugins.nobreak;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

import java.util.List;

/**
 * Impede quebra, explosão, fogo, pistão e mobs alterando blocos nas zonas no-break.
 * Staff com {@code mineguerra.nobreak} ainda pode quebrar à mão.
 */
public class NoBreakProtectListener implements Listener {

    private final NoBreakZoneService zoneService;

    public NoBreakProtectListener(NoBreakZoneService zoneService) {
        this.zoneService = zoneService;
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        if (!zoneService.isProtected(event.getBlock())) {
            return;
        }
        Player player = event.getPlayer();
        if (player.hasPermission("mineguerra.nobreak")) {
            return;
        }
        event.setCancelled(true);
        player.sendMessage("§c§l[MineGuerra] §7Este bloco esta protegido.");
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onEntityExplode(EntityExplodeEvent event) {
        event.blockList().removeIf(zoneService::isProtected);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBlockExplode(BlockExplodeEvent event) {
        event.blockList().removeIf(zoneService::isProtected);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBurn(BlockBurnEvent event) {
        if (zoneService.isProtected(event.getBlock())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        if (zoneService.isProtected(event.getBlock())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPistonExtend(BlockPistonExtendEvent event) {
        if (affectsProtected(event.getBlocks(), event.getDirection(), event.getBlock())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPistonRetract(BlockPistonRetractEvent event) {
        if (affectsProtected(event.getBlocks(), event.getDirection(), event.getBlock())) {
            event.setCancelled(true);
        }
    }

    private boolean affectsProtected(List<Block> moved, BlockFace direction, Block piston) {
        if (zoneService.isProtected(piston)) {
            return true;
        }
        for (Block block : moved) {
            if (zoneService.isProtected(block) || zoneService.isProtected(block.getRelative(direction))) {
                return true;
            }
        }
        return false;
    }
}
