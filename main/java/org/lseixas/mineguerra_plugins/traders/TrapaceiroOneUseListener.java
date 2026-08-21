package org.lseixas.mineguerra_plugins.traders;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Garante 1 uso real nos itens meme do Trapaceiro.
 *
 * <p>{@code maxDamage=1} no Paper às vezes não basta (criativo, quirks de arco);
 * depois do disparo/acerto o item some de vez.
 */
public class TrapaceiroOneUseListener implements Listener {

    private final JavaPlugin plugin;

    public TrapaceiroOneUseListener(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onShoot(EntityShootBowEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        ItemStack bow = event.getBow();
        if (!TrapaceiroItems.isOneUseItem(bow) || bow.getType() != Material.BOW) {
            return;
        }
        // Próximo tick: remove da mão que atirou (criativo não gasta durabilidade sozinho).
        plugin.getServer().getScheduler().runTask(plugin, () -> consumeOneUse(player, Material.BOW));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onMeleeHit(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player player)) {
            return;
        }
        ItemStack weapon = player.getInventory().getItemInMainHand();
        if (!TrapaceiroItems.isOneUseItem(weapon) || weapon.getType() != Material.STICK) {
            return;
        }
        plugin.getServer().getScheduler().runTask(plugin, () -> consumeOneUse(player, Material.STICK));
    }

    private void consumeOneUse(Player player, Material expectedType) {
        if (!player.isOnline()) {
            return;
        }
        PlayerInventory inventory = player.getInventory();
        if (breakIfMatch(player, inventory, EquipmentSlot.HAND, expectedType)
                || breakIfMatch(player, inventory, EquipmentSlot.OFF_HAND, expectedType)) {
            player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1f, 1f);
            player.updateInventory();
        }
    }

    private boolean breakIfMatch(Player player, PlayerInventory inventory, EquipmentSlot slot, Material expectedType) {
        ItemStack item = slot == EquipmentSlot.HAND
                ? inventory.getItemInMainHand()
                : inventory.getItemInOffHand();
        if (!TrapaceiroItems.isOneUseItem(item) || item.getType() != expectedType) {
            return false;
        }

        // Em survival: força a quebra; em criativo: remove o stack (criativo não gasta dura).
        if (player.getGameMode() == GameMode.CREATIVE) {
            if (slot == EquipmentSlot.HAND) {
                inventory.setItemInMainHand(null);
            } else {
                inventory.setItemInOffHand(null);
            }
            return true;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta instanceof Damageable damageable) {
            int max = damageable.hasMaxDamage() ? damageable.getMaxDamage() : 1;
            damageable.setDamage(max);
            item.setItemMeta(meta);
        }
        if (slot == EquipmentSlot.HAND) {
            inventory.setItemInMainHand(null);
        } else {
            inventory.setItemInOffHand(null);
        }
        return true;
    }
}
