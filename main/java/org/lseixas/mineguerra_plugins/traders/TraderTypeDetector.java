package org.lseixas.mineguerra_plugins.traders;

import org.bukkit.ChatColor;
import org.bukkit.inventory.Merchant;

public final class TraderTypeDetector {

    private TraderTypeDetector() {
    }

    public static TraderType fromMerchant(Merchant merchant) {
        if (!(merchant instanceof org.bukkit.entity.LivingEntity living)) {
            return null;
        }
        String name = living.getCustomName();
        if (name == null) {
            return null;
        }
        String plain = ChatColor.stripColor(name);
        for (TraderType type : TraderType.values()) {
            if (!type.isWeaponExplorer()) {
                continue;
            }
            if (plain.equalsIgnoreCase(type.getDisplayName())
                    || plain.contains(type.getDisplayName())) {
                return type;
            }
        }
        return null;
    }
}
