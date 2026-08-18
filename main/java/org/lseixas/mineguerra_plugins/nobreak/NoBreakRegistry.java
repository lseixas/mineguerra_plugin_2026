package org.lseixas.mineguerra_plugins.nobreak;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * Bootstrap do sistema de zonas sem quebra de bloco.
 */
public final class NoBreakRegistry {

    private static JavaPlugin plugin;
    private static NoBreakZoneStore store;
    private static NoBreakZoneService zoneService;
    private static NoBreakToolService toolService;
    private static NoBreakToolListener toolListener;

    private NoBreakRegistry() {
    }

    public static void init(JavaPlugin plugin) {
        NoBreakRegistry.plugin = plugin;
        store = new NoBreakZoneStore(plugin);
        store.load();
        zoneService = new NoBreakZoneService(store);
        toolService = new NoBreakToolService(plugin);
        toolListener = new NoBreakToolListener(plugin, toolService, zoneService);

        plugin.getServer().getPluginManager().registerEvents(toolListener, plugin);
        plugin.getServer().getPluginManager().registerEvents(new NoBreakProtectListener(zoneService), plugin);
    }

    public static void shutdown() {
        if (toolListener != null) {
            toolListener.shutdown();
        }
        if (store != null) {
            store.save();
        }
    }

    public static JavaPlugin plugin() {
        return plugin;
    }

    public static NoBreakZoneService zones() {
        return zoneService;
    }

    public static NoBreakToolService tools() {
        return toolService;
    }
}
