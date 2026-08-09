package org.lseixas.mineguerra_plugins.dragonslayer;

import org.bukkit.inventory.ItemStack;
import org.lseixas.mineguerra_plugins.weapons.WeaponId;
import org.lseixas.mineguerra_plugins.weapons.WeaponRegistry;

public class DragonSlayerFactory {

    private DragonSlayerFactory() {
    }

    public static ItemStack createDragonSlayer() {
        return WeaponRegistry.items().create(WeaponId.DRAGON_SLAYER);
    }
}
