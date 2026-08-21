package org.lseixas.mineguerra_plugins.teams;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.lseixas.mineguerra_plugins.teams.flag.FlagService;
import org.lseixas.mineguerra_plugins.metrics.MetricsRegistry;
import org.lseixas.mineguerra_plugins.metrics.MetricsService;
import org.lseixas.mineguerra_plugins.weapons.WeaponId;
import org.lseixas.mineguerra_plugins.weapons.WeaponOwnershipService;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class MgCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("mineguerra.admin")) {
            sender.sendMessage("§cVoce nao tem permissao (mineguerra.admin).");
            return true;
        }

        if (args.length < 1) {
            sendUsage(sender);
            return true;
        }

        String sub = args[0].toLowerCase(Locale.ROOT);

        if ("leaderboard".equals(sub)) {
            handleLeaderboard(sender, args);
            return true;
        }

        if ("kills".equals(sub)) {
            handleKills(sender, args);
            return true;
        }

        if ("weapons".equals(sub)) {
            handleWeapons(sender, args);
            return true;
        }

        if ("revive".equals(sub)) {
            handleRevive(sender, args);
            return true;
        }

        if ("metrics".equals(sub)) {
            handleMetrics(sender, args);
            return true;
        }

        sendUsage(sender);
        return true;
    }

    private void handleMetrics(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("§cUso: /mg metrics <status|dump|open>");
            return;
        }

        MetricsService metrics = MetricsRegistry.service();
        if (metrics == null) {
            sender.sendMessage("§cMetricas nao inicializadas.");
            return;
        }

        String action = args[1].toLowerCase(Locale.ROOT);
        switch (action) {
            case "status" -> {
                sender.sendMessage("§6§l[MineGuerra] §7Metricas:");
                sender.sendMessage("§7Gravando: " + (metrics.isRecording() ? "§asim" : "§cnao"));
                sender.sendMessage("§7Sessao: §f" + metrics.getSessionId().orElse("-"));
                sender.sendMessage("§7Eventos: §f" + metrics.getEventCount());
                sender.sendMessage("§7Inicio: §f" + metrics.getStartedAt().map(Object::toString).orElse("-"));
                sender.sendMessage("§7Ultimo flush: §f"
                        + metrics.getLastFlushAt().map(Object::toString).orElse("-"));
                metrics.getSessionDir().ifPresent(dir ->
                        sender.sendMessage("§7Pasta: §f" + dir.toAbsolutePath()));
            }
            case "dump" -> {
                if (metrics.getSessionId().isEmpty()) {
                    sender.sendMessage("§cNenhuma sessao de metricas aberta.");
                    return;
                }
                metrics.flushNow();
                sender.sendMessage("§a§l[MineGuerra] §7Snapshot gravado.");
                metrics.getSessionDir().ifPresent(dir ->
                        sender.sendMessage("§7" + dir.resolve("snapshot.json").toAbsolutePath()));
            }
            case "open" -> {
                Path dir = metrics.getSessionDir().orElse(null);
                if (dir == null) {
                    Path root = metrics.getStore().getRoot();
                    sender.sendMessage("§eNenhuma sessao ativa. Root: §f" + root.toAbsolutePath());
                    return;
                }
                sender.sendMessage("§a§l[MineGuerra] §7Pasta da sessao:");
                sender.sendMessage("§f" + dir.toAbsolutePath());
                sender.sendMessage("§7events.jsonl + snapshot.json");
            }
            default -> sender.sendMessage("§cUso: /mg metrics <status|dump|open>");
        }
    }

    private void handleLeaderboard(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("§cUso: /mg leaderboard <on|off>");
            return;
        }

        String mode = args[1].toLowerCase(Locale.ROOT);
        LeaderboardService leaderboard = TeamRegistry.leaderboard();

        switch (mode) {
            case "on", "true", "enable" -> {
                leaderboard.setEnabled(true);
                sender.sendMessage("§a§l[MineGuerra] §7Leaderboard §aativado§7.");
            }
            case "off", "false", "disable" -> {
                leaderboard.setEnabled(false);
                sender.sendMessage("§a§l[MineGuerra] §7Leaderboard §cdesativado§7.");
            }
            default -> sender.sendMessage("§cUse: on ou off");
        }
    }

    private void handleKills(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("§cUso: /mg kills <set|reset|resetall> ...");
            return;
        }

        KillStatsService kills = TeamRegistry.kills();
        TeamService teams = TeamRegistry.teams();
        String action = args[1].toLowerCase(Locale.ROOT);

        switch (action) {
            case "set" -> {
                if (args.length < 4) {
                    sender.sendMessage("§cUso: /mg kills set <time> <quantidade>");
                    return;
                }
                String teamId = teams.normalizeTeamId(args[2]);
                int amount;
                try {
                    amount = Integer.parseInt(args[3]);
                } catch (NumberFormatException ex) {
                    sender.sendMessage("§cQuantidade invalida.");
                    return;
                }
                if (kills.setKills(teamId, amount)) {
                    sender.sendMessage("§a§l[MineGuerra] §7Kills de §f" + teamId + " §7= §f" + amount);
                    if (TeamRegistry.leaderboard().isEnabled()) {
                        TeamRegistry.leaderboard().refreshAll();
                    }
                } else {
                    sender.sendMessage("§cTime nao encontrado.");
                }
            }
            case "reset" -> {
                if (args.length < 3) {
                    sender.sendMessage("§cUso: /mg kills reset <time>");
                    return;
                }
                String teamId = teams.normalizeTeamId(args[2]);
                if (kills.resetKills(teamId)) {
                    sender.sendMessage("§a§l[MineGuerra] §7Kills de §f" + teamId + " §7zeradas.");
                    if (TeamRegistry.leaderboard().isEnabled()) {
                        TeamRegistry.leaderboard().refreshAll();
                    }
                } else {
                    sender.sendMessage("§cTime nao encontrado.");
                }
            }
            case "resetall" -> {
                kills.resetAllKills();
                sender.sendMessage("§a§l[MineGuerra] §7Todas as kills foram zeradas.");
                if (TeamRegistry.leaderboard().isEnabled()) {
                    TeamRegistry.leaderboard().refreshAll();
                }
            }
            default -> sender.sendMessage("§cUso: /mg kills <set|reset|resetall>");
        }
    }

    private void handleWeapons(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("§cUso: /mg weapons <status|reset>");
            return;
        }

        WeaponOwnershipService weapons = TeamRegistry.weapons();
        String action = args[1].toLowerCase(Locale.ROOT);

        switch (action) {
            case "status" -> {
                sender.sendMessage("§6§l[MineGuerra] §7Armas lendarias:");
                Map<WeaponId, String> summary = weapons.getStatusSummary();
                for (WeaponId weaponId : WeaponId.values()) {
                    String status = summary.getOrDefault(weaponId, "§7?");
                    sender.sendMessage("§7" + weaponId.getShortName() + " §7— " + status);
                }
            }
            case "reset" -> {
                weapons.resetAll();
                weapons.rescanAll();
                sender.sendMessage("§a§l[MineGuerra] §7Todas as armas foram liberadas para trade.");
            }
            default -> sender.sendMessage("§cUso: /mg weapons <status|reset>");
        }
    }

    private void handleRevive(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("§cUso: /mg revive <jogador|all>");
            return;
        }

        FlagService flags = TeamRegistry.flags();
        String targetArg = args[1];

        if ("all".equalsIgnoreCase(targetArg)) {
            int count = flags.reviveAll();
            sender.sendMessage("§a§l[MineGuerra] §7" + count + " jogador(es) revivido(s).");
            return;
        }

        Player online = Bukkit.getPlayer(targetArg);
        UUID uuid;
        String name;
        if (online != null) {
            uuid = online.getUniqueId();
            name = online.getName();
        } else {
            OfflinePlayer offline = resolveOfflinePlayer(targetArg);
            if (offline == null) {
                sender.sendMessage("§cJogador nao encontrado.");
                return;
            }
            uuid = offline.getUniqueId();
            name = offline.getName() != null ? offline.getName() : targetArg;
        }

        if (flags.revivePlayer(uuid)) {
            sender.sendMessage("§a§l[MineGuerra] §f" + name + " §7foi revivido.");
        } else {
            sender.sendMessage("§cEsse jogador nao esta eliminado.");
        }
    }

    @SuppressWarnings("deprecation")
    private OfflinePlayer resolveOfflinePlayer(String name) {
        OfflinePlayer offline = Bukkit.getOfflinePlayer(name);
        if (offline.hasPlayedBefore() || offline.isOnline()) {
            return offline;
        }
        return null;
    }

    private void sendUsage(CommandSender sender) {
        sender.sendMessage("§6§l[MineGuerra] §7Admin:");
        sender.sendMessage("§e/mg leaderboard <on|off>");
        sender.sendMessage("§e/mg kills set <time> <quantidade>");
        sender.sendMessage("§e/mg kills reset <time>");
        sender.sendMessage("§e/mg kills resetall");
        sender.sendMessage("§e/mg weapons status");
        sender.sendMessage("§e/mg weapons reset");
        sender.sendMessage("§e/mg revive <jogador|all>");
        sender.sendMessage("§e/mg metrics status");
        sender.sendMessage("§e/mg metrics dump");
        sender.sendMessage("§e/mg metrics open");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!sender.hasPermission("mineguerra.admin")) {
            return List.of();
        }

        if (args.length == 1) {
            return filter(List.of("leaderboard", "kills", "weapons", "revive", "metrics"), args[0]);
        }

        if (args.length == 2) {
            if ("leaderboard".equalsIgnoreCase(args[0])) {
                return filter(List.of("on", "off"), args[1]);
            }
            if ("kills".equalsIgnoreCase(args[0])) {
                return filter(List.of("set", "reset", "resetall"), args[1]);
            }
            if ("weapons".equalsIgnoreCase(args[0])) {
                return filter(List.of("status", "reset"), args[1]);
            }
            if ("metrics".equalsIgnoreCase(args[0])) {
                return filter(List.of("status", "dump", "open"), args[1]);
            }
            if ("revive".equalsIgnoreCase(args[0])) {
                List<String> options = new ArrayList<>();
                options.add("all");
                options.addAll(onlineNames());
                return filter(options, args[1]);
            }
        }

        if (args.length == 3 && "kills".equalsIgnoreCase(args[0])) {
            String action = args[1].toLowerCase(Locale.ROOT);
            if ("set".equals(action) || "reset".equals(action)) {
                return filter(teamIds(), args[2]);
            }
        }

        return List.of();
    }

    private List<String> teamIds() {
        return TeamRegistry.teams().getAllTeams().stream()
                .map(TeamDefinition::getId)
                .collect(Collectors.toList());
    }

    private List<String> onlineNames() {
        return Bukkit.getOnlinePlayers().stream()
                .map(Player::getName)
                .collect(Collectors.toList());
    }

    private List<String> filter(List<String> options, String prefix) {
        String lower = prefix.toLowerCase(Locale.ROOT);
        return options.stream()
                .filter(s -> s.toLowerCase(Locale.ROOT).startsWith(lower))
                .collect(Collectors.toList());
    }
}
