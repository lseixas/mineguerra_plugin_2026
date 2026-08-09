package org.lseixas.mineguerra_plugins.soulflayerbow;

import org.bukkit.inventory.ItemStack;
import org.lseixas.mineguerra_plugins.weapons.WeaponId;
import org.lseixas.mineguerra_plugins.weapons.WeaponRegistry;

public class SoulflayerBowFactory {

    private SoulflayerBowFactory() {
    }

    public static ItemStack createSoulflayerBow() {
        return WeaponRegistry.items().create(WeaponId.SOULFLAYER_BOW);
    }

    /** @deprecated Use {@link #createSoulflayerBow()} */
    public static ItemStack createStarShooter() {
        return createSoulflayerBow();
    }
}
