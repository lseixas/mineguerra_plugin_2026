package org.lseixas.mineguerra_plugins;

import org.bukkit.plugin.java.JavaPlugin;
import org.lseixas.mineguerra_plugins.doomhammer.DoomHammerCommand;
import org.lseixas.mineguerra_plugins.doomhammer.DoomHammerListener;
import org.lseixas.mineguerra_plugins.dragonslayer.DragonSlayerCommand;
import org.lseixas.mineguerra_plugins.dragonslayer.DragonSlayerListener;
import org.lseixas.mineguerra_plugins.fluxCommands.StartGuerraCommand;
import org.lseixas.mineguerra_plugins.soulflayerbow.SoulflayerBowCommand;
import org.lseixas.mineguerra_plugins.soulflayerbow.SoulflayerBowListener;
import org.lseixas.mineguerra_plugins.soulflayerbow.skills.HellfireSkullProtectListener;
import org.lseixas.mineguerra_plugins.stormrider.StormRiderCommand;
import org.lseixas.mineguerra_plugins.stormrider.StormRiderListener;
import org.lseixas.mineguerra_plugins.traders.GrantTraderToolCommand;
import org.lseixas.mineguerra_plugins.traders.GrantTraderToolKitCommand;
import org.lseixas.mineguerra_plugins.traders.SpawnVillagerCommand;
import org.lseixas.mineguerra_plugins.traders.SuicidePactListener;
import org.lseixas.mineguerra_plugins.traders.TraderToolListener;
import org.lseixas.mineguerra_plugins.traders.TrapaceiroOneUseListener;
import org.lseixas.mineguerra_plugins.teams.MgCommand;
import org.lseixas.mineguerra_plugins.teams.TeamCommand;
import org.lseixas.mineguerra_plugins.teams.TeamJoinListener;
import org.lseixas.mineguerra_plugins.teams.TeamKillListener;
import org.lseixas.mineguerra_plugins.teams.TeamRegistry;
import org.lseixas.mineguerra_plugins.teams.flag.EliminatedSpectatorListener;
import org.lseixas.mineguerra_plugins.teams.flag.FlagAreaListener;
import org.lseixas.mineguerra_plugins.teams.flag.FlagBreakListener;
import org.lseixas.mineguerra_plugins.teams.flag.FlagPhysicsListener;
import org.lseixas.mineguerra_plugins.teams.flag.FlagRespawnListener;
import org.lseixas.mineguerra_plugins.traders.WeaponTraderRefreshListener;
import org.lseixas.mineguerra_plugins.traders.WanderingTraderVisualListener;
import org.lseixas.mineguerra_plugins.clientaudit.ClientAuditRegistry;
import org.lseixas.mineguerra_plugins.nobreak.GrantNoBreakToolCommand;
import org.lseixas.mineguerra_plugins.nobreak.NoBreakRegistry;
import org.lseixas.mineguerra_plugins.nospawn.GrantNoSpawnToolCommand;
import org.lseixas.mineguerra_plugins.nospawn.NoSpawnRegistry;
import org.lseixas.mineguerra_plugins.metrics.MetricsRegistry;
import org.lseixas.mineguerra_plugins.war.WarRegistry;
import org.lseixas.mineguerra_plugins.weapons.LegendaryWeaponListener;
import org.lseixas.mineguerra_plugins.weapons.WeaponRegistry;
import org.lseixas.mineguerra_plugins.world.FireSpreadGuard;
import org.lseixas.mineguerra_plugins.world.LocatorBarGuard;

public final class Mineguerra_plugins extends JavaPlugin {

    @Override
    public void onEnable() {

        WeaponRegistry.init(this);
        TeamRegistry.init(this);
        NoSpawnRegistry.init(this);
        NoBreakRegistry.init(this);
        ClientAuditRegistry.init(this);
        WarRegistry.init(this);
        MetricsRegistry.init(this);

        getServer().getPluginManager().registerEvents(new SoulflayerBowListener(this), this);
        getServer().getPluginManager().registerEvents(new HellfireSkullProtectListener(), this);
        getServer().getPluginManager().registerEvents(new DragonSlayerListener(this), this);
        getServer().getPluginManager().registerEvents(new DoomHammerListener(), this);
        getServer().getPluginManager().registerEvents(new StormRiderListener(this), this);

        FireSpreadGuard fireSpreadGuard = new FireSpreadGuard(this);
        getServer().getPluginManager().registerEvents(fireSpreadGuard, this);
        fireSpreadGuard.applyToAllWorlds();

        LocatorBarGuard locatorBarGuard = new LocatorBarGuard(this);
        getServer().getPluginManager().registerEvents(locatorBarGuard, this);
        locatorBarGuard.applyToAllWorlds();

        TraderToolListener traderToolListener = new TraderToolListener(this);
        getServer().getPluginManager().registerEvents(traderToolListener, this);
        getServer().getPluginManager().registerEvents(new TeamKillListener(), this);
        getServer().getPluginManager().registerEvents(new TeamJoinListener(), this);
        getServer().getPluginManager().registerEvents(new FlagBreakListener(), this);
        getServer().getPluginManager().registerEvents(new FlagRespawnListener(this), this);
        getServer().getPluginManager().registerEvents(new EliminatedSpectatorListener(this), this);
        getServer().getPluginManager().registerEvents(new FlagAreaListener(), this);
        getServer().getPluginManager().registerEvents(new FlagPhysicsListener(), this);
        getServer().getPluginManager().registerEvents(
                new LegendaryWeaponListener(WeaponRegistry.items()), this);
        getServer().getPluginManager().registerEvents(new WeaponTraderRefreshListener(), this);
        getServer().getPluginManager().registerEvents(new WanderingTraderVisualListener(), this);
        getServer().getPluginManager().registerEvents(new SuicidePactListener(this), this);
        getServer().getPluginManager().registerEvents(new TrapaceiroOneUseListener(this), this);

        getLogger().info("Mineguerra plugins has been enabled!");

        getCommand("spawnvillager").setExecutor(new SpawnVillagerCommand());
        getCommand("grantTraderTool").setExecutor(
                new GrantTraderToolCommand(traderToolListener.getTraderToolService()));
        getCommand("grantTraderToolKit").setExecutor(
                new GrantTraderToolKitCommand(traderToolListener.getTraderToolService()));
        StartGuerraCommand startGuerraCommand = new StartGuerraCommand(this);
        getCommand("startGuerra").setExecutor(startGuerraCommand);
        getCommand("startGuerra").setTabCompleter(startGuerraCommand);
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

        GrantNoSpawnToolCommand noSpawnCommand =
                new GrantNoSpawnToolCommand(NoSpawnRegistry.tools(), NoSpawnRegistry.zones());
        getCommand("grantNoSpawnTool").setExecutor(noSpawnCommand);
        getCommand("grantNoSpawnTool").setTabCompleter(noSpawnCommand);

        GrantNoBreakToolCommand noBreakCommand =
                new GrantNoBreakToolCommand(NoBreakRegistry.tools(), NoBreakRegistry.zones());
        getCommand("grantNoBreakTool").setExecutor(noBreakCommand);
        getCommand("grantNoBreakTool").setTabCompleter(noBreakCommand);

    }

    @Override
    public void onDisable() {
        MetricsRegistry.shutdown();
        WarRegistry.shutdown();
        ClientAuditRegistry.shutdown();
        NoBreakRegistry.shutdown();
        NoSpawnRegistry.shutdown();
        TeamRegistry.shutdown();
        getLogger().info("Plugin desativado!");

    }
}
