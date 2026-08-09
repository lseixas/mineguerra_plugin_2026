package org.lseixas.mineguerra_plugins.stormrider;

import org.bukkit.inventory.ItemStack;
import org.lseixas.mineguerra_plugins.weapons.WeaponId;
import org.lseixas.mineguerra_plugins.weapons.WeaponRegistry;

public class StormRiderFactory {

    private StormRiderFactory() {
    }

    public static ItemStack createStormRider() {
        return WeaponRegistry.items().create(WeaponId.STORM_RIDER);
    }
}
