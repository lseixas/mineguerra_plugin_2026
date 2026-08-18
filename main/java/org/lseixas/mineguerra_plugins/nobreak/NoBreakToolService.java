package org.lseixas.mineguerra_plugins.nobreak;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

/**
 * Pincel de staff para delimitar áreas onde blocos não quebram.
 */
public class NoBreakToolService {

    public static final String PDC_FLAG = "nobreak_tool";

    private final org.bukkit.NamespacedKey toolKey;

    public NoBreakToolService(JavaPlugin plugin) {
        this.toolKey = new org.bukkit.NamespacedKey(plugin, PDC_FLAG);
    }

    public ItemStack createTool() {
        ItemStack item = new ItemStack(Material.BRUSH);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return item;
        }

        meta.setDisplayName("§6§lDelimitador No-Break");
        meta.setLore(List.of(
                "§7Cria uma area em que blocos nao podem ser quebrados.",
                "",
                "§aClique direito §7— marca canto 1 / canto 2",
                "§aShift + clique direito §7— confirma a zona",
                "§cShift + clique esquerdo §7— remove zona no bloco",
                "§eClique esquerdo no ar §7— limpa selecao",
                "",
                "§8Particulas mostram a area selecionada"
        ));

        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.set(toolKey, PersistentDataType.BYTE, (byte) 1);

        item.setItemMeta(meta);
        return item;
    }

    public boolean isNoBreakTool(ItemStack item) {
        if (item == null || item.getType() != Material.BRUSH || !item.hasItemMeta()) {
            return false;
        }
        ItemMeta meta = item.getItemMeta();
        return meta.getPersistentDataContainer().has(toolKey, PersistentDataType.BYTE);
    }
}
