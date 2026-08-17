package org.lseixas.mineguerra_plugins.nospawn;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * Bootstrap do sistema de zonas sem spawn.
 */
public final class NoSpawnRegistry {

    private static JavaPlugin plugin;
    private static NoSpawnZoneStore store;
    private static NoSpawnZoneService zoneService;
    private static NoSpawnToolService toolService;
    private static NoSpawnToolListener toolListener;

    private NoSpawnRegistry() {
    }

    public static void init(JavaPlugin plugin) {
        NoSpawnRegistry.plugin = plugin;
        store = new NoSpawnZoneStore(plugin);
        store.load();
        zoneService = new NoSpawnZoneService(store);
        toolService = new NoSpawnToolService(plugin);
        toolListener = new NoSpawnToolListener(plugin, toolService, zoneService);

        plugin.getServer().getPluginManager().registerEvents(toolListener, plugin);
        plugin.getServer().getPluginManager().registerEvents(new NoSpawnSpawnListener(zoneService), plugin);
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

    public static NoSpawnZoneService zones() {
        return zoneService;
    }

    public static NoSpawnToolService tools() {
        return toolService;
    }
}
