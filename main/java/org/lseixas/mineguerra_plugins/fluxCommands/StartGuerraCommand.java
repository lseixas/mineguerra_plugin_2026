package org.lseixas.mineguerra_plugins.fluxCommands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.lseixas.mineguerra_plugins.Mineguerra_plugins;

public class StartGuerraCommand implements CommandExecutor {

    public StartGuerraCommand(Mineguerra_plugins plugin) {
        // Kept for constructor compatibility with plugin registration.
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("mineguerra.admin")) {
            sender.sendMessage("§cVoce nao tem permissao (mineguerra.admin).");
            return true;
        }

        sender.sendMessage("§c§l[MineGuerra] §7/startGuerra ainda nao esta implementado.");
        sender.sendMessage("§7Use §e/grantTraderToolKit §7ou §e/spawnvillager §7para posicionar NPCs.");
        return true;
    }
}
