package org.lseixas.mineguerra_plugins.traders;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpawnVillagerCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cApenas jogadores podem usar este comando!");
            return true;
        }

        if (!player.hasPermission("mineguerra.spawnvillager")) {
            player.sendMessage("§cVocê não tem permissão para usar este comando!");
            return true;
        }

        if (args.length < 1) {
            player.sendMessage("§cUso: §e/spawnvillager <tipo> §7[opcional: x y z]");
            player.sendMessage("§7Tipos: §f" + TraderType.allIds());
            player.sendMessage("§7Dica: §e/grantTraderTool <tipo> §7para ferramenta visual de spawn/config.");
            return true;
        }

        var typeOpt = TraderType.fromId(args[0]);
        if (typeOpt.isEmpty()) {
            player.sendMessage("§cTipo inválido! Disponíveis: §f" + TraderType.allIds());
            return true;
        }

        Location spawnLocation;
        if (args.length >= 4) {
            try {
                double x = Double.parseDouble(args[1]);
                double y = Double.parseDouble(args[2]);
                double z = Double.parseDouble(args[3]);
                spawnLocation = new Location(player.getWorld(), x, y, z);
            } catch (NumberFormatException e) {
                player.sendMessage("§cCoordenadas inválidas! Use números.");
                return true;
            }
        } else {
            spawnLocation = player.getLocation();
        }

        typeOpt.get().spawn(spawnLocation);
        player.sendMessage("§a§l[MineGuerra] §7" + typeOpt.get().getDisplayName() + " §7spawnado.");
        return true;
    }
}
