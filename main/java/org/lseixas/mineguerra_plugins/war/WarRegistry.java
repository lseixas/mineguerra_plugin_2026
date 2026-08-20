package org.lseixas.mineguerra_plugins.war;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

/**
 * Ponto de acesso ao motor do evento (inicializado no onEnable).
 */
public final class WarRegistry {

    private static final String SCHEDULE_FILE = "war-schedule.yml";

    private static JavaPlugin plugin;
    private static WarStateStore stateStore;
    private static WarService warService;
    private static WarScheduler scheduler;
    private static BorderService borderService;

    private WarRegistry() {
    }

    public static void init(JavaPlugin plugin) {
        WarRegistry.plugin = plugin;

        plugin.saveResource(SCHEDULE_FILE, false);

        stateStore = new WarStateStore(plugin);
        stateStore.load();

        borderService = new BorderService();
        warService = new WarService(plugin, stateStore, loadSchedule(), borderService);
        logWarnings();
        warService.catchUp();

        scheduler = new WarScheduler(plugin, warService, stateStore);
        if (stateStore.isRunning()) {
            scheduler.start();
        }

        plugin.getServer().getPluginManager().registerEvents(new PvpToggleListener(stateStore), plugin);
        plugin.getServer().getPluginManager().registerEvents(new HardcoreDeathListener(stateStore), plugin);
    }

    public static void shutdown() {
        if (scheduler != null) {
            scheduler.stop();
        }
        if (stateStore != null) {
            stateStore.save();
        }
    }

    /** Recarrega {@code war-schedule.yml} do disco. */
    public static void reloadSchedule() {
        warService.setSchedule(loadSchedule());
        logWarnings();
    }

    private static WarSchedule loadSchedule() {
        File file = new File(plugin.getDataFolder(), SCHEDULE_FILE);
        return WarSchedule.fromConfig(YamlConfiguration.loadConfiguration(file));
    }

    private static void logWarnings() {
        for (String warning : warService.getSchedule().getWarnings()) {
            plugin.getLogger().warning("war-schedule.yml: " + warning);
        }
    }

    public static WarStateStore state() {
        return stateStore;
    }

    public static WarService service() {
        return warService;
    }

    public static WarScheduler scheduler() {
        return scheduler;
    }

    public static BorderService border() {
        return borderService;
    }
}
