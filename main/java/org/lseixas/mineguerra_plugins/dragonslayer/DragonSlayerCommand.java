package org.lseixas.mineguerra_plugins.dragonslayer;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.lseixas.mineguerra_plugins.weapons.WeaponId;
import org.lseixas.mineguerra_plugins.weapons.WeaponMessages;

public class DragonSlayerCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String lbl, String[] args) {
        if (!sender.hasPermission("mineguerra.admin")) {
            sender.sendMessage("§cVoce nao tem permissao (mineguerra.admin).");
            return true;
        }
        if (!(sender instanceof Player player)) {
            return true;
        }

        player.getInventory().addItem(DragonSlayerFactory.createDragonSlayer());
        WeaponMessages.sendGrant(player, WeaponId.DRAGON_SLAYER);
        return true;
    }
}
