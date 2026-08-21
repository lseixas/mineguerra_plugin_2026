package org.lseixas.mineguerra_plugins.metrics;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * Access point for event metrics (initialized in onEnable).
 */
public final class MetricsRegistry {

    private static JavaPlugin plugin;
    private static MetricsSessionStore store;
    private static MetricsService service;

    private MetricsRegistry() {
    }

    public static void init(JavaPlugin plugin) {
        MetricsRegistry.plugin = plugin;
        store = new MetricsSessionStore(plugin);
        service = new MetricsService(plugin, store);
        plugin.getServer().getPluginManager().registerEvents(new MetricsListener(service), plugin);

        // Resume after WarRegistry.init so isRunning() is accurate
        service.resumeIfRunning();
    }

    public static void shutdown() {
        if (service != null) {
            service.shutdown();
        }
    }

    public static MetricsService service() {
        return service;
    }

    public static boolean isRecording() {
        return service != null && service.isRecording();
    }

    public static void startSession() {
        if (service != null) {
            service.startSession();
        }
    }

    public static void stopSession() {
        if (service != null) {
            service.stopSession();
        }
    }

    public static void resetSession() {
        if (service != null) {
            service.resetSession();
        }
    }

    public static void resumeIfRunning() {
        if (service != null) {
            service.resumeIfRunning();
        }
    }
}
