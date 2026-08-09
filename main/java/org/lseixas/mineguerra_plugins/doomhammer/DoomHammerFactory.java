package org.lseixas.mineguerra_plugins.doomhammer;

import org.bukkit.inventory.ItemStack;
import org.lseixas.mineguerra_plugins.weapons.WeaponId;
import org.lseixas.mineguerra_plugins.weapons.WeaponRegistry;

public class DoomHammerFactory {

    private DoomHammerFactory() {
    }

    public static ItemStack createDoomHammer() {
        return WeaponRegistry.items().create(WeaponId.DOOM_HAMMER);
    }
}
