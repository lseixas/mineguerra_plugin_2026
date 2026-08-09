package org.lseixas.mineguerra_plugins.traders;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.List;
import java.util.Optional;

/**
 * Item de staff para spawnar ou configurar comerciantes do evento.
 */
public class TraderToolService {

    public static final String PDC_TRADER_TYPE = "trader_type";

    private final org.bukkit.NamespacedKey traderTypeKey;

    public TraderToolService(JavaPlugin plugin) {
        this.traderTypeKey = new org.bukkit.NamespacedKey(plugin, PDC_TRADER_TYPE);
    }

    public ItemStack createTool(TraderType type) {
        ItemStack item = new ItemStack(Material.BLAZE_ROD);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return item;
        }

        meta.setDisplayName("§e§lFerramenta de Comerciante");
        meta.setLore(List.of(
                "§7Tipo: §f" + type.getDisplayName(),
                "§8(" + type.getId() + ")",
                "",
                "§aClique direito no chão §7— spawna o NPC",
                "§aClique direito no villager §7— aplica as trades",
                type.isWeaponExplorer()
                        ? "§5Inclui troca da arma customizada"
                        : "§7Trades utilitárias do evento"
        ));

        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.set(traderTypeKey, PersistentDataType.STRING, type.name());

        item.setItemMeta(meta);
        return item;
    }

    public boolean isTraderTool(ItemStack item) {
        return getType(item).isPresent();
    }

    public Optional<TraderType> getType(ItemStack item) {
        if (item == null || item.getType() != Material.BLAZE_ROD || !item.hasItemMeta()) {
            return Optional.empty();
        }

        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        if (!pdc.has(traderTypeKey, PersistentDataType.STRING)) {
            return Optional.empty();
        }

        String stored = pdc.get(traderTypeKey, PersistentDataType.STRING);
        if (stored == null) {
            return Optional.empty();
        }

        try {
            return Optional.of(TraderType.valueOf(stored));
        } catch (IllegalArgumentException ex) {
            return Optional.empty();
        }
    }
}
