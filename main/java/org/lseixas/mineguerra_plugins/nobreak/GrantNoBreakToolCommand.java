package org.lseixas.mineguerra_plugins.nobreak;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class GrantNoBreakToolCommand implements CommandExecutor, TabCompleter {

    private final NoBreakToolService toolService;
    private final NoBreakZoneService zoneService;

    public GrantNoBreakToolCommand(NoBreakToolService toolService, NoBreakZoneService zoneService) {
        this.toolService = toolService;
        this.zoneService = zoneService;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("mineguerra.nobreak")) {
            sender.sendMessage("§cVoce nao tem permissao (mineguerra.nobreak).");
            return true;
        }

        if (args.length >= 1) {
            String sub = args[0].toLowerCase(Locale.ROOT);
            switch (sub) {
                case "list" -> {
                    var zones = zoneService.all();
                    if (zones.isEmpty()) {
                        sender.sendMessage("§e§l[MineGuerra] §7Nenhuma zona no-break.");
                        return true;
                    }
                    sender.sendMessage("§6§l[MineGuerra] §7Zonas no-break (" + zones.size() + "):");
                    for (NoBreakZone zone : zones) {
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
                    sender.sendMessage("§a§l[MineGuerra] §7Removidas §f" + removed + " §7zonas no-break.");
                    return true;
                }
                case "tool", "give" -> {
                    // fall through to give tool
                }
                default -> {
                    sender.sendMessage("§eUso: /grantNoBreakTool [tool|list|clear]");
                    return true;
                }
            }
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cApenas jogadores podem receber a ferramenta. Use list/clear no console.");
            return true;
        }

        player.getInventory().addItem(toolService.createTool());
        player.sendMessage("§a§l[MineGuerra] §7Delimitador No-Break recebido.");
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
