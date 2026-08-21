package org.lseixas.mineguerra_plugins.traders;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;

/**
 * Itens meme do Trapaceiro (1 uso / nomes custom).
 */
final class TrapaceiroItems {

    static final NamespacedKey PACT_KEY =
            Objects.requireNonNull(NamespacedKey.fromString("mineguerra:pact_chestplate"));
    static final NamespacedKey ONE_USE_KEY =
            Objects.requireNonNull(NamespacedKey.fromString("mineguerra:trapaceiro_one_use"));

    private TrapaceiroItems() {
    }

    static ItemStack goldenBow() {
        ItemStack bow = makeOneUseItem(
                Material.BOW,
                minecraftEnchantment("power"),
                9999,
                ChatColor.GOLD + "" + ChatColor.BOLD + "Golden Bow");
        ItemMeta meta = bow.getItemMeta();
        if (meta != null) {
            meta.setLore(java.util.List.of(
                    ChatColor.GRAY + "Um arco do Trapaceiro.",
                    ChatColor.RED + "Quebra apos 1 disparo."));
            bow.setItemMeta(meta);
        }
        return bow;
    }

    static ItemStack knockbackStick() {
        ItemStack stick = makeOneUseItem(
                Material.STICK,
                minecraftEnchantment("knockback"),
                32,
                ChatColor.RED + "" + ChatColor.BOLD + "Graveto da Repulsão");
        ItemMeta meta = stick.getItemMeta();
        if (meta != null) {
            meta.setLore(java.util.List.of(
                    ChatColor.GRAY + "Um graveto do Trapaceiro.",
                    ChatColor.RED + "Quebra apos 1 acerto."));
            stick.setItemMeta(meta);
        }
        return stick;
    }

    /**
     * Peitoral do Pacto: quem veste morre com qualquer hit e leva o agressor junto.
     * Os enchants são cosméticos/temáticos — o efeito real vem do
     * {@link SuicidePactListener}, porque o vanilla ignora Protection negativa.
     */
    static ItemStack pactChestplate() {
        ItemStack chestplate = new ItemStack(Material.CHAINMAIL_CHESTPLATE, 1);
        ItemMeta meta = chestplate.getItemMeta();
        if (meta == null) {
            return chestplate;
        }
        meta.setDisplayName(ChatColor.DARK_RED + "" + ChatColor.BOLD + "Peitoral do Pacto");
        meta.setLore(java.util.List.of(
                ChatColor.GRAY + "Um acordo firmado com o Trapaceiro.",
                "",
                ChatColor.RED + "Qualquer dano te mata na hora.",
                ChatColor.RED + "Quem te matou morre junto."
        ));
        meta.addEnchant(minecraftEnchantment("binding_curse"), 1, true);
        meta.addEnchant(minecraftEnchantment("thorns"), 9999, true);
        meta.addEnchant(minecraftEnchantment("protection"), -9999, true);
        meta.getPersistentDataContainer().set(PACT_KEY, PersistentDataType.BYTE, (byte) 1);
        chestplate.setItemMeta(meta);
        return chestplate;
    }

    static boolean isPactChestplate(ItemStack item) {
        if (item == null || item.getType() != Material.CHAINMAIL_CHESTPLATE || !item.hasItemMeta()) {
            return false;
        }
        ItemMeta meta = item.getItemMeta();
        return meta != null
                && meta.getPersistentDataContainer().has(PACT_KEY, PersistentDataType.BYTE);
    }

    static boolean isOneUseItem(ItemStack item) {
        if (item == null || item.getType().isAir() || !item.hasItemMeta()) {
            return false;
        }
        ItemMeta meta = item.getItemMeta();
        return meta != null
                && meta.getPersistentDataContainer().has(ONE_USE_KEY, PersistentDataType.BYTE);
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
        meta.setUnbreakable(false);
        if (meta instanceof Damageable damageable) {
            // 1 ponto de durabilidade = quebra no primeiro uso (Paper 1.21 components).
            damageable.setMaxDamage(1);
            damageable.setDamage(0);
        }
        meta.addEnchant(enchantment, level, true);
        meta.getPersistentDataContainer().set(ONE_USE_KEY, PersistentDataType.BYTE, (byte) 1);
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
