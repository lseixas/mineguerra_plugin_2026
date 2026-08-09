package org.lseixas.mineguerra_plugins.traders;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GrantTraderToolCommand implements CommandExecutor {

    private final TraderToolService traderToolService;

    public GrantTraderToolCommand(TraderToolService traderToolService) {
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

        if (args.length < 1) {
            player.sendMessage("§eUso: /grantTraderTool <tipo>");
            player.sendMessage("§7Exploradores de arma: §foceano§7, §fprofundezas§7, §fnether§7, §fend");
            player.sendMessage("§7Outros: §f" + TraderType.allIds());
            return true;
        }

        var typeOpt = TraderType.fromId(args[0]);
        if (typeOpt.isEmpty()) {
            player.sendMessage("§cTipo invalido. Disponiveis: §f" + TraderType.allIds());
            return true;
        }

        TraderType type = typeOpt.get();
        player.getInventory().addItem(traderToolService.createTool(type));
        player.sendMessage("§a§l[MineGuerra] §7Ferramenta recebida: §f" + type.getDisplayName());
        player.sendMessage("§7Clique no chão para spawnar | clique no villager para configurar trades.");
        return true;
    }
}
