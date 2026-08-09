package org.lseixas.mineguerra_plugins.fluxCommands;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.lseixas.mineguerra_plugins.Mineguerra_plugins;
import org.lseixas.mineguerra_plugins.traders.VillagerSpawner;

public class StartGuerraCommand implements CommandExecutor {

    private final Mineguerra_plugins plugin;

    public StartGuerraCommand(Mineguerra_plugins plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        sender.sendMessage("Timer iniciado! A Guerra esta valendo!");

        long ticks = 100; // 5 segundos para testar o spawn

        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {

            World w = Bukkit.getWorld("world");
            Location loc = w.getPlayers().get(0).getLocation();

            VillagerSpawner.spawnFazendeiro(loc);

            sender.sendMessage("Villager spawnado!");

        }, ticks);

        return true;

    }

}
