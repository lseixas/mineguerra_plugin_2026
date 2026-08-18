package org.lseixas.mineguerra_plugins.traders;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Itens meme do Trapaceiro (1 uso / nomes custom).
 */
final class TrapaceiroItems {

    private TrapaceiroItems() {
    }

    static ItemStack goldenBow() {
        return makeOneUseItem(
                Material.BOW,
                minecraftEnchantment("power"),
                9999,
                ChatColor.GOLD + "" + ChatColor.BOLD + "Golden Bow");
    }

    static ItemStack knockbackStick() {
        return makeOneUseItem(
                Material.STICK,
                minecraftEnchantment("knockback"),
                32,
                ChatColor.RED + "" + ChatColor.BOLD + "Graveto da Repulsão");
    }

    static ItemStack capirotoApple() {
        ItemStack apple = new ItemStack(Material.ENCHANTED_GOLDEN_APPLE, 1);
        ItemMeta meta = apple.getItemMeta();
        if (meta == null) {
            return apple;
        }
        meta.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "Maçã do Capiroto");
        apple.setItemMeta(meta);
        return apple;
    }

    static ItemStack makeOneUseItem(Material material, Enchantment enchantment, int level, String name) {
        ItemStack item = new ItemStack(material, 1);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return item;
        }
        meta.setDisplayName(name);
        if (meta instanceof Damageable damageable) {
            damageable.setMaxDamage(1);
        }
        meta.addEnchant(enchantment, level, true);
        item.setItemMeta(meta);
        return item;
    }

    static Enchantment minecraftEnchantment(String key) {
        Enchantment enchantment = Registry.ENCHANTMENT.get(NamespacedKey.minecraft(key));
        if (enchantment == null) {
            throw new IllegalStateException("Enchantment ausente: " + key);
        }
        return enchantment;
    }
}
