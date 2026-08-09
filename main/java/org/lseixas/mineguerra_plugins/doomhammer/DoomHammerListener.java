package org.lseixas.mineguerra_plugins.doomhammer;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.lseixas.mineguerra_plugins.doomhammer.skills.PowerJump;
import org.lseixas.mineguerra_plugins.weapons.WeaponId;
import org.lseixas.mineguerra_plugins.weapons.WeaponRegistry;

public class DoomHammerListener implements Listener {

    private final PowerJump powerJump = new PowerJump();

    @EventHandler
    public void onRightClick(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        Player player = event.getPlayer();
        if (!WeaponRegistry.items().isInMainHand(player, WeaponId.DOOM_HAMMER)) {
            return;
        }

        powerJump.activatePowerJump(player);
    }

    @EventHandler
    public void onFallDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        if (event.getCause() != EntityDamageEvent.DamageCause.FALL) {
            return;
        }

        if (WeaponRegistry.items().isInMainHand(player, WeaponId.DOOM_HAMMER)) {
            event.setCancelled(true);
        }
    }
}
