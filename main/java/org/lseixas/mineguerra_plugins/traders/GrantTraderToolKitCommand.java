package org.lseixas.mineguerra_plugins.traders;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GrantTraderToolKitCommand implements CommandExecutor {

    private final TraderToolService traderToolService;

    public GrantTraderToolKitCommand(TraderToolService traderToolService) {
        this.traderToolService = traderToolService;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cApenas jogadores podem usar este comando.");
            return true;
        }

        if (!player.hasPermission("mineguerra.tradertool")) {
            player.sendMessage("§cVoce nao tem permissao (mineguerra.tradertool).");
            return true;
        }

        for (TraderType type : TraderType.values()) {
            if (type.isWeaponExplorer()) {
                player.getInventory().addItem(traderToolService.createTool(type));
            }
        }

        player.sendMessage("§a§l[MineGuerra] §7Kit dos 4 exploradores de armas recebido.");
        return true;
    }
}
