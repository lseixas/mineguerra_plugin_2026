package org.lseixas.mineguerra_plugins.teams;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.lseixas.mineguerra_plugins.teams.flag.FlagService;
import org.lseixas.mineguerra_plugins.teams.flag.TeamFlag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class TeamCommand implements CommandExecutor, TabCompleter {

    private static final List<String> SUBCOMMANDS = List.of(
            "create", "delete", "join", "leave", "clear", "list", "info", "flag"
    );
    private static final List<String> FLAG_SUBCOMMANDS =
            List.of("set", "remove", "repair", "clear", "status", "list");
    private static final List<String> FLAG_SUBCOMMANDS_WITH_TEAM =
            List.of("set", "remove", "repair", "clear", "status");

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("mineguerra.team")) {
            sender.sendMessage("§cVoce nao tem permissao (mineguerra.team).");
            return true;
        }

        if (args.length < 1) {
            sendUsage(sender);
            return true;
        }

        String sub = args[0].toLowerCase(Locale.ROOT);
        TeamService teamService = TeamRegistry.teams();
        KillStatsService killStats = TeamRegistry.kills();

        switch (sub) {
            case "create" -> handleCreate(sender, args, teamService);
            case "delete" -> handleDelete(sender, args, teamService);
            case "join" -> handleJoin(sender, args, teamService);
            case "leave" -> handleLeave(sender, args, teamService);
            case "clear" -> handleClear(sender, args, teamService);
            case "list" -> handleList(sender, teamService, killStats);
            case "info" -> handleInfo(sender, args, teamService);
            case "flag" -> handleFlag(sender, args, teamService);
            default -> sendUsage(sender);
        }

        return true;
    }

    private void handleCreate(CommandSender sender, String[] args, TeamService teamService) {
        if (args.length < 2) {
            sender.sendMessage("§cUso: /team create <id> [nome exibicao] [cor]");
            sender.sendMessage("§7Cores: RED, BLUE, GREEN, YELLOW, AQUA, ...");
            return;
        }

        String teamId = teamService.normalizeTeamId(args[1]);
        String displayName = teamId;
        ChatColor color = ChatColor.WHITE;

        if (args.length == 3) {
            if (isChatColorName(args[2])) {
                color = ChatColor.valueOf(args[2].toUpperCase(Locale.ROOT));
            } else {
                displayName = args[2];
            }
        } else if (args.length >= 4) {
            if (isChatColorName(args[args.length - 1])) {
                color = ChatColor.valueOf(args[args.length - 1].toUpperCase(Locale.ROOT));
                displayName = joinArgs(args, 2, args.length - 2);
            } else {
                displayName = joinArgs(args, 2, args.length - 1);
            }
        }

        TeamService.CreateResult result = teamService.createTeam(teamId, displayName, color);
        switch (result) {
            case SUCCESS -> {
                sender.sendMessage("§a§l[MineGuerra] §7Time criado: §f" + displayName + " §7(§f" + teamId + "§7)");
                if (TeamRegistry.leaderboard().isEnabled()) {
                    TeamRegistry.leaderboard().refreshAll();
                }
            }
            case INVALID_ID -> sender.sendMessage("§cID invalido. Use apenas a-z, 0-9, _ e -");
            case ALREADY_EXISTS -> sender.sendMessage("§cJa existe um time com esse id.");
            case TOO_MANY -> sender.sendMessage("§cLimite de 12 times atingido.");
        }
    }

    private void handleDelete(CommandSender sender, String[] args, TeamService teamService) {
        if (args.length < 2) {
            sender.sendMessage("§cUso: /team delete <id>");
            return;
        }

        String teamId = teamService.normalizeTeamId(args[1]);
        if (teamService.deleteTeam(teamId)) {
            sender.sendMessage("§a§l[MineGuerra] §7Time removido: §f" + teamId);
            if (TeamRegistry.leaderboard().isEnabled()) {
                TeamRegistry.leaderboard().refreshAll();
            }
        } else {
            sender.sendMessage("§cTime nao encontrado.");
        }
    }

    private void handleJoin(CommandSender sender, String[] args, TeamService teamService) {
        if (args.length < 3) {
            sender.sendMessage("§cUso: /team join <nick> <time>");
            return;
        }

        String teamId = teamService.normalizeTeamId(args[2]);
        var resultOpt = teamService.assignPlayerByName(args[1], teamId);
        if (resultOpt.isEmpty()) {
            sender.sendMessage("§cTime nao encontrado.");
            return;
        }

        TeamService.AssignResult result = resultOpt.get();
        if (result.wasOnline()) {
            sender.sendMessage("§a§l[MineGuerra] §f" + result.playerName() + " §7entrou no time §f" + teamId);
            Player online = Bukkit.getPlayerExact(result.playerName());
            if (online != null) {
                online.sendMessage("§a§l[MineGuerra] §7Voce foi adicionado ao time §f" + teamId);
            }
        } else if (result.pendingLogin()) {
            sender.sendMessage("§a§l[MineGuerra] §f" + result.playerName()
                    + " §7sera adicionado ao time §f" + teamId + " §7quando entrar.");
        } else {
            sender.sendMessage("§a§l[MineGuerra] §f" + result.playerName()
                    + " §7(offline) adicionado ao time §f" + teamId);
        }
    }

    private void handleLeave(CommandSender sender, String[] args, TeamService teamService) {
        if (args.length < 2) {
            sender.sendMessage("§cUso: /team leave <nick>");
            return;
        }

        TeamService.RemoveResult result = teamService.removePlayerByName(args[1]);
        switch (result) {
            case REMOVED_ONLINE -> {
                sender.sendMessage("§a§l[MineGuerra] §7Tag removida de §f" + args[1]);
                Player online = Bukkit.getPlayer(args[1]);
                if (online == null) {
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        if (p.getName().equalsIgnoreCase(args[1])) {
                            online = p;
                            break;
                        }
                    }
                }
                if (online != null) {
                    online.sendMessage("§7§l[MineGuerra] §7Voce saiu do seu time.");
                }
            }
            case REMOVED_OFFLINE -> sender.sendMessage(
                    "§a§l[MineGuerra] §7Removido do time (offline/pendente): §f" + args[1]);
            case NOT_IN_TEAM -> sender.sendMessage("§cEsse jogador nao esta em nenhum time.");
        }
    }

    private void handleClear(CommandSender sender, String[] args, TeamService teamService) {
        if (args.length < 2 || !"confirm".equalsIgnoreCase(args[1])) {
            sender.sendMessage("§cIsso apaga TODOS os times, membros, kills e bandeiras.");
            sender.sendMessage("§cUso: /team clear confirm");
            return;
        }

        int removed = teamService.clearAllTeams();
        if (TeamRegistry.leaderboard().isEnabled()) {
            TeamRegistry.leaderboard().refreshAll();
        }
        sender.sendMessage("§a§l[MineGuerra] §7Limpeza concluida: §f" + removed + " §7time(s) removido(s).");
    }

    private void handleList(CommandSender sender, TeamService teamService, KillStatsService killStats) {
        if (teamService.getAllTeams().isEmpty()) {
            sender.sendMessage("§7Nenhum time cadastrado.");
            return;
        }

        sender.sendMessage("§6§l[MineGuerra] §7Times:");
        for (TeamDefinition team : killStats.getTeamsSortedByKills()) {
            int kills = killStats.getKills(team.getId());
            int members = teamService.countMembers(team.getId());
            sender.sendMessage(team.getColor() + team.getDisplayName()
                    + " §7(§f" + team.getId() + "§7) — §f" + kills + " kills §7— §f" + members + " membros");
        }
    }

    private void handleInfo(CommandSender sender, String[] args, TeamService teamService) {
        if (args.length < 2) {
            sender.sendMessage("§cUso: /team info <id>");
            return;
        }

        String teamId = teamService.normalizeTeamId(args[1]);
        var teamOpt = teamService.getTeam(teamId);
        if (teamOpt.isEmpty()) {
            sender.sendMessage("§cTime nao encontrado.");
            return;
        }

        TeamDefinition team = teamOpt.get();
        sender.sendMessage("§6§l[MineGuerra] §7" + team.getColoredPrefix() + team.getDisplayName());
        sender.sendMessage("§7ID: §f" + team.getId() + " §7| Kills: §f" + TeamRegistry.kills().getKills(team.getId()));

        List<String> memberNames = new ArrayList<>();
        for (Map.Entry<UUID, String> entry : TeamRegistry.data().getPlayerTeams().entrySet()) {
            if (!teamId.equals(entry.getValue())) {
                continue;
            }
            String name = Bukkit.getOfflinePlayer(entry.getKey()).getName();
            memberNames.add(name != null ? name : entry.getKey().toString());
        }
        List<String> pendingNames = new ArrayList<>();
        for (Map.Entry<String, String> entry : TeamRegistry.data().getPendingByName().entrySet()) {
            if (teamId.equals(entry.getValue())) {
                pendingNames.add(entry.getKey() + " §8(pendente)");
            }
        }

        if (memberNames.isEmpty() && pendingNames.isEmpty()) {
            sender.sendMessage("§7Membros: §8(nenhum)");
        } else {
            List<String> all = new ArrayList<>(memberNames);
            all.addAll(pendingNames);
            sender.sendMessage("§7Membros: §f" + String.join("§7, §f", all));
        }
    }

    private void handleFlag(CommandSender sender, String[] args, TeamService teamService) {
        if (args.length < 2) {
            sender.sendMessage("§cUso: /team flag <set|remove|repair|clear|status|list> ...");
            return;
        }

        String flagSub = args[1].toLowerCase(Locale.ROOT);
        FlagService flags = TeamRegistry.flags();

        switch (flagSub) {
            case "set" -> {
                if (!(sender instanceof Player player)) {
                    sender.sendMessage("§cApenas jogadores podem definir bandeira.");
                    return;
                }
                if (args.length < 3) {
                    sender.sendMessage("§cUso: /team flag set <time>");
                    return;
                }
                String teamId = teamService.normalizeTeamId(args[2]);
                FlagService.SetResult result = flags.setFlag(teamId, player);
                switch (result) {
                    case SUCCESS -> sender.sendMessage("§a§l[MineGuerra] §7Bandeira definida para §f" + teamId);
                    case TEAM_NOT_FOUND -> sender.sendMessage("§cTime nao encontrado.");
                    case NO_TARGET_BLOCK -> sender.sendMessage("§cOlhe para um bloco valido (ate 5 blocos).");
                    case UNSUPPORTED_COLOR -> sender.sendMessage("§cCor do time nao suportada para banner.");
                }
            }
            case "remove" -> {
                if (args.length < 3) {
                    sender.sendMessage("§cUso: /team flag remove <time>");
                    return;
                }
                String teamId = teamService.normalizeTeamId(args[2]);
                if (flags.removeFlag(teamId)) {
                    sender.sendMessage("§a§l[MineGuerra] §7Registro de bandeira removido: §f" + teamId);
                } else {
                    sender.sendMessage("§cTime sem bandeira registrada.");
                }
            }
            case "repair" -> {
                if (args.length < 3) {
                    sender.sendMessage("§cUso: /team flag repair <time>");
                    return;
                }
                String teamId = teamService.normalizeTeamId(args[2]);
                FlagService.RepairResult result = flags.repairFlag(teamId);
                switch (result) {
                    case SUCCESS -> sender.sendMessage("§a§l[MineGuerra] §7Bandeira reparada para §f" + teamId);
                    case NO_FLAG -> sender.sendMessage("§cTime sem bandeira registrada.");
                    case TEAM_NOT_FOUND -> sender.sendMessage("§cTime nao encontrado.");
                    case WORLD_UNLOADED -> sender.sendMessage("§cMundo da bandeira nao esta carregado.");
                    case UNSUPPORTED_COLOR -> sender.sendMessage("§cCor do time nao suportada para banner.");
                }
            }
            case "clear" -> {
                if (args.length < 3) {
                    sender.sendMessage("§cUso: /team flag clear <time>");
                    return;
                }
                String teamId = teamService.normalizeTeamId(args[2]);
                int cleared = flags.clearArea(teamId);
                if (cleared < 0) {
                    sender.sendMessage("§cTime sem bandeira registrada (ou mundo descarregado).");
                } else {
                    sender.sendMessage("§a§l[MineGuerra] §7Raio da bandeira §f" + teamId
                            + " §7limpo: §f" + cleared + " §7blocos.");
                }
            }
            case "status" -> {
                if (args.length >= 3) {
                    printFlagStatus(sender, teamService.normalizeTeamId(args[2]), flags, sender.hasPermission("mineguerra.admin"));
                    return;
                }
                if (sender instanceof Player player) {
                    String ownTeam = teamService.getTeamId(player);
                    if (ownTeam == null) {
                        sender.sendMessage("§cVoce nao esta em um time.");
                        return;
                    }
                    printFlagStatus(sender, ownTeam, flags, false);
                } else {
                    sender.sendMessage("§cUso: /team flag status <time>");
                }
            }
            case "list" -> {
                boolean showCoords = sender.hasPermission("mineguerra.admin");
                if (teamService.getAllTeams().isEmpty()) {
                    sender.sendMessage("§7Nenhum time cadastrado.");
                    return;
                }
                sender.sendMessage("§6§l[MineGuerra] §7Bandeiras:");
                for (TeamDefinition team : teamService.getAllTeams()) {
                    var flagOpt = flags.getFlag(team.getId());
                    if (flagOpt.isEmpty()) {
                        sender.sendMessage(team.getColor() + team.getDisplayName() + " §7— §8sem bandeira");
                    } else {
                        TeamFlag flag = flagOpt.get();
                        String status = flag.isAlive() ? "§aviva" : "§cmorta";
                        if (showCoords) {
                            sender.sendMessage(team.getColor() + team.getDisplayName() + " §7— " + status
                                    + " §7@ §f" + flag.getWorldName() + " "
                                    + (int) flag.getX() + "," + (int) flag.getY() + "," + (int) flag.getZ());
                        } else {
                            sender.sendMessage(team.getColor() + team.getDisplayName() + " §7— " + status);
                        }
                    }
                }
            }
            default -> sender.sendMessage("§cUso: /team flag <set|remove|repair|clear|status|list>");
        }
    }

    private void printFlagStatus(CommandSender sender, String teamId, FlagService flags, boolean showCoords) {
        var teamOpt = TeamRegistry.teams().getTeam(teamId);
        if (teamOpt.isEmpty()) {
            sender.sendMessage("§cTime nao encontrado.");
            return;
        }
        var flagOpt = flags.getFlag(teamId);
        if (flagOpt.isEmpty()) {
            sender.sendMessage("§7Time §f" + teamId + " §7sem bandeira registrada.");
            return;
        }
        TeamFlag flag = flagOpt.get();
        String status = flag.isAlive() ? "§aviva" : "§cmorta";
        if (showCoords) {
            sender.sendMessage("§7Bandeira §f" + teamId + " §7— " + status
                    + " §7@ §f" + flag.getWorldName() + " "
                    + (int) flag.getX() + "," + (int) flag.getY() + "," + (int) flag.getZ());
        } else {
            sender.sendMessage("§7Sua bandeira esta " + status + "§7.");
        }
    }

    private void sendUsage(CommandSender sender) {
        sender.sendMessage("§6§l[MineGuerra] §7Comandos de time:");
        sender.sendMessage("§e/team create <id> [nome] [cor]");
        sender.sendMessage("§e/team delete <id>");
        sender.sendMessage("§e/team join <nick> <time> §7(funciona offline)");
        sender.sendMessage("§e/team leave <nick> §7(funciona offline)");
        sender.sendMessage("§e/team clear confirm §7(apaga todos os times)");
        sender.sendMessage("§e/team list");
        sender.sendMessage("§e/team info <id>");
        sender.sendMessage("§e/team flag <set|remove|repair|clear|status|list>");
    }

    private static boolean isChatColorName(String value) {
        try {
            ChatColor c = ChatColor.valueOf(value.toUpperCase(Locale.ROOT));
            return c.isColor();
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }

    private static String joinArgs(String[] args, int from, int toInclusive) {
        StringBuilder builder = new StringBuilder();
        for (int i = from; i <= toInclusive && i < args.length; i++) {
            if (builder.length() > 0) {
                builder.append(' ');
            }
            builder.append(args[i]);
        }
        return builder.toString();
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!sender.hasPermission("mineguerra.team")) {
            return List.of();
        }

        if (args.length == 1) {
            return filterPrefix(SUBCOMMANDS, args[0]);
        }

        String sub = args[0].toLowerCase(Locale.ROOT);
        TeamService teamService = TeamRegistry.teams();

        if ("flag".equals(sub) && args.length == 2) {
            return filterPrefix(FLAG_SUBCOMMANDS, args[1]);
        }

        if ("flag".equals(sub) && args.length == 3) {
            String flagSub = args[1].toLowerCase(Locale.ROOT);
            if (FLAG_SUBCOMMANDS_WITH_TEAM.contains(flagSub)) {
                return filterPrefix(teamIds(), args[2]);
            }
        }

        if (args.length == 2) {
            return switch (sub) {
                case "delete", "info" -> filterPrefix(teamIds(), args[1]);
                case "join", "leave" -> filterPrefix(suggestPlayerNames(), args[1]);
                case "clear" -> filterPrefix(List.of("confirm"), args[1]);
                default -> List.of();
            };
        }

        if (args.length == 3 && "join".equals(sub)) {
            return filterPrefix(teamIds(), args[2]);
        }

        if (args.length >= 3 && "create".equals(sub) && args.length == 3) {
            return filterPrefix(colorNames(), args[2]);
        }

        return List.of();
    }

    private List<String> teamIds() {
        return TeamRegistry.teams().getAllTeams().stream()
                .map(TeamDefinition::getId)
                .collect(Collectors.toList());
    }

    private List<String> suggestPlayerNames() {
        List<String> names = new ArrayList<>(onlineNames());
        for (String pending : TeamRegistry.data().getPendingByName().keySet()) {
            if (!names.contains(pending)) {
                names.add(pending);
            }
        }
        for (UUID uuid : TeamRegistry.data().getPlayerTeams().keySet()) {
            String name = Bukkit.getOfflinePlayer(uuid).getName();
            if (name != null && !names.contains(name)) {
                names.add(name);
            }
        }
        return names;
    }

    private List<String> onlineNames() {
        return Bukkit.getOnlinePlayers().stream()
                .map(Player::getName)
                .collect(Collectors.toList());
    }

    private List<String> colorNames() {
        return Arrays.stream(ChatColor.values())
                .filter(ChatColor::isColor)
                .map(Enum::name)
                .collect(Collectors.toList());
    }

    private List<String> filterPrefix(List<String> options, String prefix) {
        String lower = prefix.toLowerCase(Locale.ROOT);
        return options.stream()
                .filter(s -> s.toLowerCase(Locale.ROOT).startsWith(lower))
                .collect(Collectors.toList());
    }
}
