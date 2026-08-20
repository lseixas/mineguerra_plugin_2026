package org.lseixas.mineguerra_plugins.weapons;

import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.Optional;

/**
 * Criação e validação de itens de arma (PDC + CustomModelData + material).
 */
public class WeaponItemService {

    private final NamespacedKey weaponIdKey;

    public WeaponItemService(JavaPlugin plugin) {
        this.weaponIdKey = WeaponConstants.weaponIdKey(plugin);
    }

    public ItemStack create(WeaponId weaponId) {
        ItemStack item = new ItemStack(weaponId.getMaterial());
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return item;
        }

        meta.setDisplayName(weaponId.getDisplayName());
        meta.setLore(weaponId.getLoreLines());
        meta.setCustomModelData(weaponId.getCustomModelData());
        applyDefaultEnchantments(meta, weaponId);

        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.set(weaponIdKey, PersistentDataType.STRING, weaponId.name());

        item.setItemMeta(meta);
        return item;
    }

    private void applyDefaultEnchantments(ItemMeta meta, WeaponId weaponId) {
        for (Map.Entry<String, Integer> entry : weaponId.getDefaultEnchantments().entrySet()) {
            Enchantment enchantment = Registry.ENCHANTMENT.get(NamespacedKey.minecraft(entry.getKey()));
            if (enchantment == null) {
                continue;
            }
            meta.addEnchant(enchantment, entry.getValue(), true);
        }
    }

    public boolean matches(ItemStack item, WeaponId weaponId) {
        if (item == null || item.getType().isAir()) {
            return false;
        }
        if (item.getType() != weaponId.getMaterial()) {
            return false;
        }
        if (!item.hasItemMeta()) {
            return false;
        }

        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        if (pdc.has(weaponIdKey, PersistentDataType.STRING)) {
            String stored = pdc.get(weaponIdKey, PersistentDataType.STRING);
            return weaponId.name().equals(stored);
        }

        // Fallback: itens legados só com CustomModelData
        if (!meta.hasCustomModelData()) {
            return false;
        }
        return meta.getCustomModelData() == weaponId.getCustomModelData();
    }

    public boolean isInMainHand(Player player, WeaponId weaponId) {
        return matches(player.getInventory().getItemInMainHand(), weaponId);
    }

    public Optional<WeaponId> identify(ItemStack item) {
        if (item == null || item.getType().isAir()) {
            return Optional.empty();
        }
        for (WeaponId weaponId : WeaponId.values()) {
            if (matches(item, weaponId)) {
                return Optional.of(weaponId);
            }
        }
        return Optional.empty();
    }

    public void stripFromPlayer(Player player) {
        ItemStack[] contents = player.getInventory().getContents();
        for (int i = 0; i < contents.length; i++) {
            if (identify(contents[i]).isPresent()) {
                player.getInventory().setItem(i, null);
            }
        }
        ItemStack off = player.getInventory().getItemInOffHand();
        if (identify(off).isPresent()) {
            player.getInventory().setItemInOffHand(null);
        }
    }
}
