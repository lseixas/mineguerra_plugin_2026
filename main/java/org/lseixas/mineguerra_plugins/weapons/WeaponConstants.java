package org.lseixas.mineguerra_plugins.weapons;

import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Chaves e namespaces compartilhados pelo sistema de armas.
 */
public final class WeaponConstants {

    public static final String PDC_WEAPON_ID = "weapon_id";

    private WeaponConstants() {
    }

    public static NamespacedKey weaponIdKey(JavaPlugin plugin) {
        return new NamespacedKey(plugin, PDC_WEAPON_ID);
    }
}
