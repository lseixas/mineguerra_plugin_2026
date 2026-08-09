package org.lseixas.mineguerra_plugins;

import org.bukkit.plugin.java.JavaPlugin;
import org.lseixas.mineguerra_plugins.doomhammer.DoomHammerCommand;
import org.lseixas.mineguerra_plugins.doomhammer.DoomHammerListener;
import org.lseixas.mineguerra_plugins.dragonslayer.DragonSlayerCommand;
import org.lseixas.mineguerra_plugins.dragonslayer.DragonSlayerListener;
import org.lseixas.mineguerra_plugins.fluxCommands.StartGuerraCommand;
import org.lseixas.mineguerra_plugins.soulflayerbow.SoulflayerBowCommand;
import org.lseixas.mineguerra_plugins.soulflayerbow.SoulflayerBowListener;
import org.lseixas.mineguerra_plugins.stormrider.StormRiderCommand;
import org.lseixas.mineguerra_plugins.stormrider.StormRiderListener;
import org.lseixas.mineguerra_plugins.traders.GrantTraderToolCommand;
import org.lseixas.mineguerra_plugins.traders.GrantTraderToolKitCommand;
import org.lseixas.mineguerra_plugins.traders.SpawnVillagerCommand;
import org.lseixas.mineguerra_plugins.traders.TraderToolListener;
import org.lseixas.mineguerra_plugins.teams.MgCommand;
import org.lseixas.mineguerra_plugins.teams.TeamCommand;
import org.lseixas.mineguerra_plugins.teams.TeamJoinListener;
import org.lseixas.mineguerra_plugins.teams.TeamKillListener;
import org.lseixas.mineguerra_plugins.teams.TeamRegistry;
import org.lseixas.mineguerra_plugins.teams.flag.FlagBreakListener;
import org.lseixas.mineguerra_plugins.teams.flag.FlagRespawnListener;
import org.lseixas.mineguerra_plugins.traders.WeaponTraderRefreshListener;
import org.lseixas.mineguerra_plugins.weapons.LegendaryWeaponListener;
import org.lseixas.mineguerra_plugins.weapons.WeaponRegistry;

public final class Mineguerra_plugins extends JavaPlugin {

    @Override
    public void onEnable() {

        WeaponRegistry.init(this);
        TeamRegistry.init(this);

        getServer().getPluginManager().registerEvents(new SoulflayerBowListener(this), this);
        getServer().getPluginManager().registerEvents(new DragonSlayerListener(this), this);
        getServer().getPluginManager().registerEvents(new DoomHammerListener(), this);
        getServer().getPluginManager().registerEvents(new StormRiderListener(this), this);

        TraderToolListener traderToolListener = new TraderToolListener(this);
        getServer().getPluginManager().registerEvents(traderToolListener, this);
        getServer().getPluginManager().registerEvents(new TeamKillListener(), this);
        getServer().getPluginManager().registerEvents(new TeamJoinListener(), this);
        getServer().getPluginManager().registerEvents(new FlagBreakListener(), this);
        getServer().getPluginManager().registerEvents(new FlagRespawnListener(this), this);
        getServer().getPluginManager().registerEvents(
                new LegendaryWeaponListener(WeaponRegistry.items()), this);
        getServer().getPluginManager().registerEvents(new WeaponTraderRefreshListener(), this);

        getLogger().info("Mineguerra plugins has been enabled!");

        getCommand("spawnvillager").setExecutor(new SpawnVillagerCommand());
        getCommand("grantTraderTool").setExecutor(
                new GrantTraderToolCommand(traderToolListener.getTraderToolService()));
        getCommand("grantTraderToolKit").setExecutor(
                new GrantTraderToolKitCommand(traderToolListener.getTraderToolService()));
        getCommand("startGuerra").setExecutor(new StartGuerraCommand(this));
        getCommand("grantSoulflayerBow").setExecutor(new SoulflayerBowCommand());
        getCommand("grantDragonSlayer").setExecutor(new DragonSlayerCommand());
        getCommand("grantDoomHammer").setExecutor(new DoomHammerCommand());
        getCommand("grantStormRider").setExecutor(new StormRiderCommand());

        TeamCommand teamCommand = new TeamCommand();
        getCommand("team").setExecutor(teamCommand);
        getCommand("team").setTabCompleter(teamCommand);

        MgCommand mgCommand = new MgCommand();
        getCommand("mg").setExecutor(mgCommand);
        getCommand("mg").setTabCompleter(mgCommand);

    }

    @Override
    public void onDisable() {
        TeamRegistry.shutdown();
        getLogger().info("Plugin desativado!");

    }
}
