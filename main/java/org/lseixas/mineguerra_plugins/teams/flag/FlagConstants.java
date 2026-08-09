package org.lseixas.mineguerra_plugins.teams.flag;

import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

public final class FlagConstants {

    public static final String PDC_TEAM_FLAG = "team_flag";

    private FlagConstants() {
    }

    public static NamespacedKey teamFlagKey(JavaPlugin plugin) {
        return new NamespacedKey(plugin, PDC_TEAM_FLAG);
    }
}
