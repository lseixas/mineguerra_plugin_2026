package org.lseixas.mineguerra_plugins.nospawn;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class GrantNoSpawnToolCommand implements CommandExecutor, TabCompleter {

    private final NoSpawnToolService toolService;
    private final NoSpawnZoneService zoneService;

    public GrantNoSpawnToolCommand(NoSpawnToolService toolService, NoSpawnZoneService zoneService) {
        this.toolService = toolService;
        this.zoneService = zoneService;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("mineguerra.nospawn")) {
            sender.sendMessage("§cVoce nao tem permissao (mineguerra.nospawn).");
            return true;
        }

        if (args.length >= 1) {
            String sub = args[0].toLowerCase(Locale.ROOT);
            switch (sub) {
                case "list" -> {
                    var zones = zoneService.all();
                    if (zones.isEmpty()) {
                        sender.sendMessage("§e§l[MineGuerra] §7Nenhuma zona no-spawn.");
                        return true;
                    }
                    sender.sendMessage("§b§l[MineGuerra] §7Zonas no-spawn (" + zones.size() + "):");
                    for (NoSpawnZone zone : zones) {
                        sender.sendMessage("§7- §f" + zone.id()
                                + " §8@ §7" + zone.worldName()
                                + " §8(" + zone.sizeLabel() + ")"
                                + " §8[" + zone.minX() + "," + zone.minY() + "," + zone.minZ()
                                + " → " + zone.maxX() + "," + zone.maxY() + "," + zone.maxZ() + "]");
                    }
                    return true;
                }
                case "clear" -> {
                    int removed = zoneService.clearAll();
                    sender.sendMessage("§a§l[MineGuerra] §7Removidas §f" + removed + " §7zonas no-spawn.");
                    return true;
                }
                case "tool", "give" -> {
                    // fall through to give tool
                }
                default -> {
                    sender.sendMessage("§eUso: /grantNoSpawnTool [tool|list|clear]");
                    return true;
                }
            }
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cApenas jogadores podem receber a ferramenta. Use list/clear no console.");
            return true;
        }

        player.getInventory().addItem(toolService.createTool());
        player.sendMessage("§a§l[MineGuerra] §7Delimitador No-Spawn recebido.");
        player.sendMessage("§7Clique direito = cantos | Shift+direito = confirma | Shift+esquerdo = remove");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            String prefix = args[0].toLowerCase(Locale.ROOT);
            List<String> options = List.of("tool", "list", "clear");
            List<String> out = new ArrayList<>();
            for (String opt : options) {
                if (opt.startsWith(prefix)) {
                    out.add(opt);
                }
            }
            return out;
        }
        return List.of();
    }
}
