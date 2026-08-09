package org.lseixas.mineguerra_plugins.dragonslayer;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.lseixas.mineguerra_plugins.dragonslayer.skills.DragonsBreath;
import org.lseixas.mineguerra_plugins.dragonslayer.skills.RageOfTheDragon;
import org.lseixas.mineguerra_plugins.weapons.WeaponId;
import org.lseixas.mineguerra_plugins.weapons.WeaponRegistry;

public class DragonSlayerListener implements Listener {

    private final DragonsBreath dragonsBreath;
    private final RageOfTheDragon rageOfTheDragon;

    public DragonSlayerListener(JavaPlugin plugin) {
        this.dragonsBreath = new DragonsBreath(plugin);
        this.rageOfTheDragon = new RageOfTheDragon();
    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        Player player = event.getPlayer();
        if (!WeaponRegistry.items().isInMainHand(player, WeaponId.DRAGON_SLAYER)) {
            return;
        }

        if (dragonsBreath.isBlockedByCooldown(player)) {
            return;
        }

        dragonsBreath.activateDragonsBreath(player);
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        if (!hasDragonSlayerInInventory(player)) {
            return;
        }

        double maxHealth = player.getAttribute(Attribute.MAX_HEALTH).getValue();
        double currentHealth = player.getHealth();
        double finalHealth = currentHealth - event.getFinalDamage();

        if (finalHealth > 0 && finalHealth <= (maxHealth * 0.10)) {
            rageOfTheDragon.activateRageOfTheDragon(player);
        }
    }

    private boolean hasDragonSlayerInInventory(Player player) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (WeaponRegistry.items().matches(item, WeaponId.DRAGON_SLAYER)) {
                return true;
            }
        }
        return false;
    }
}
