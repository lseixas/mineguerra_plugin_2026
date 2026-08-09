package org.lseixas.mineguerra_plugins.weapons;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * Ponto de acesso estático ao serviço de itens, inicializado no {@code onEnable}.
 */
public final class WeaponRegistry {

    private static WeaponItemService itemService;

    private WeaponRegistry() {
    }

    public static void init(JavaPlugin plugin) {
        itemService = new WeaponItemService(plugin);
    }

    public static WeaponItemService items() {
        if (itemService == null) {
            throw new IllegalStateException("WeaponRegistry.init(plugin) must be called from onEnable");
        }
        return itemService;
    }
}
