package org.lseixas.mineguerra_plugins.nobreak;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

/**
 * Impede quebra, colocação, explosão, fogo e mobs alterando blocos nas zonas no-break.
 * Staff com {@code mineguerra.nobreak} ainda pode quebrar/colocar à mão.
 *
 * <p>Pistões não são bloqueados: a redstone dentro da zona precisa funcionar.
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
    public void onBlockPlace(BlockPlaceEvent event) {
        if (!zoneService.isProtected(event.getBlock())) {
            return;
        }
        Player player = event.getPlayer();
        if (player.hasPermission("mineguerra.nobreak")) {
            return;
        }
        event.setCancelled(true);
        player.sendMessage("§c§l[MineGuerra] §7Nao da para construir aqui.");
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

}
