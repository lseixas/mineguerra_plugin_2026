package org.lseixas.mineguerra_plugins.war;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

/**
 * Kit de abertura do evento (fase {@link WarPhase#INICIO}).
 */
final class StarterKit {

    private StarterKit() {
    }

    static void give(Player player) {
        ItemStack[] kit = {
                new ItemStack(Material.IRON_SWORD),
                new ItemStack(Material.IRON_PICKAXE),
                new ItemStack(Material.IRON_AXE),
                new ItemStack(Material.IRON_SHOVEL),
                new ItemStack(Material.IRON_HOE),
                new ItemStack(Material.LEATHER_CHESTPLATE),
                new ItemStack(Material.LEATHER_BOOTS),
                new ItemStack(Material.SHIELD),
                new ItemStack(Material.BREAD, 64),
        };
        Map<Integer, ItemStack> leftover = player.getInventory().addItem(kit);
        for (ItemStack item : leftover.values()) {
            player.getWorld().dropItemNaturally(player.getLocation(), item);
        }
    }
}
