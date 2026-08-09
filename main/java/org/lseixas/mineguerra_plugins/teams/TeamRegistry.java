package org.lseixas.mineguerra_plugins.teams;

import org.bukkit.plugin.java.JavaPlugin;
import org.lseixas.mineguerra_plugins.teams.flag.FlagService;
import org.lseixas.mineguerra_plugins.weapons.WeaponItemService;
import org.lseixas.mineguerra_plugins.weapons.WeaponOwnershipService;
import org.lseixas.mineguerra_plugins.weapons.WeaponRegistry;

/**
 * Ponto de acesso aos serviços de times (inicializado no onEnable).
 */
public final class TeamRegistry {

    private static JavaPlugin plugin;
    private static TeamsDataStore dataStore;
    private static TeamService teamService;
    private static KillStatsService killStatsService;
    private static LeaderboardService leaderboardService;
    private static FlagService flagService;
    private static WeaponOwnershipService weaponOwnershipService;

    private TeamRegistry() {
    }

    public static void init(JavaPlugin plugin) {
        TeamRegistry.plugin = plugin;

        dataStore = new TeamsDataStore(plugin);
        dataStore.load();

        teamService = new TeamService(plugin, dataStore);
        killStatsService = new KillStatsService(dataStore);
        weaponOwnershipService = new WeaponOwnershipService(dataStore, WeaponRegistry.items());
        flagService = new FlagService(plugin, dataStore, teamService);
        flagService.clearEliminated();

        leaderboardService = new LeaderboardService(
                dataStore,
                teamService,
                killStatsService,
                weaponOwnershipService
        );

        teamService.syncAllOnlinePlayers();
        weaponOwnershipService.rescanAll();
        if (dataStore.isLeaderboardEnabled()) {
            leaderboardService.refreshAll();
        }

        for (var player : org.bukkit.Bukkit.getOnlinePlayers()) {
            flagService.applyEliminatedState(player);
        }
    }

    public static void shutdown() {
        if (leaderboardService != null) {
            leaderboardService.hideAll();
        }
        if (dataStore != null) {
            dataStore.save();
        }
    }

    public static JavaPlugin plugin() {
        return plugin;
    }

    public static TeamsDataStore data() {
        return dataStore;
    }

    public static TeamService teams() {
        return teamService;
    }

    public static KillStatsService kills() {
        return killStatsService;
    }

    public static LeaderboardService leaderboard() {
        return leaderboardService;
    }

    public static FlagService flags() {
        return flagService;
    }

    public static WeaponOwnershipService weapons() {
        return weaponOwnershipService;
    }
}
