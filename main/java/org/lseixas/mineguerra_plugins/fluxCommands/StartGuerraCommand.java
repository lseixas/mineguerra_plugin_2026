package org.lseixas.mineguerra_plugins.fluxCommands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.lseixas.mineguerra_plugins.Mineguerra_plugins;
import org.lseixas.mineguerra_plugins.war.WarPhase;
import org.lseixas.mineguerra_plugins.war.WarRegistry;
import org.lseixas.mineguerra_plugins.war.WarSchedule;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Orquestra o cronograma do evento. Ver docs/WAR_SCHEDULE.md.
 */
public class StartGuerraCommand implements CommandExecutor, TabCompleter {

    private static final List<String> SUBCOMMANDS =
            List.of("start", "stop", "status", "phase", "reload");
    private static final DateTimeFormatter DISPLAY =
            DateTimeFormatter.ofPattern("EEE dd/MM HH:mm");

    public StartGuerraCommand(Mineguerra_plugins plugin) {
        // Estado vive no WarRegistry; o comando é só a interface de staff.
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("mineguerra.admin")) {
            sender.sendMessage("§cVoce nao tem permissao (mineguerra.admin).");
            return true;
        }

        if (args.length == 0) {
            sendUsage(sender);
            return true;
        }

        switch (args[0].toLowerCase(Locale.ROOT)) {
            case "start" -> handleStart(sender);
            case "stop" -> handleStop(sender);
            case "status" -> handleStatus(sender);
            case "phase" -> handlePhase(sender, args);
            case "reload" -> handleReload(sender);
            default -> sendUsage(sender);
        }
        return true;
    }

    private void handleStart(CommandSender sender) {
        if (WarRegistry.state().isRunning()) {
            sender.sendMessage("§e§l[MineGuerra] §7O cronograma ja esta rodando.");
            return;
        }
        WarRegistry.state().setRunning(true);
        WarRegistry.state().save();
        WarRegistry.scheduler().start();
        WarRegistry.service().advance();
        sender.sendMessage("§a§l[MineGuerra] §7Cronograma iniciado.");
        handleStatus(sender);
    }

    private void handleStop(CommandSender sender) {
        if (!WarRegistry.state().isRunning()) {
            sender.sendMessage("§e§l[MineGuerra] §7O cronograma nao esta rodando.");
            return;
        }
        WarRegistry.state().setRunning(false);
        WarRegistry.state().save();
        WarRegistry.scheduler().stop();
        sender.sendMessage("§c§l[MineGuerra] §7Cronograma pausado. Fases nao vao mais disparar.");
    }

    private void handleStatus(CommandSender sender) {
        WarSchedule schedule = WarRegistry.service().getSchedule();
        ZonedDateTime now = WarRegistry.service().now();

        sender.sendMessage("§6§l[MineGuerra] §7Status do evento:");
        sender.sendMessage("§7Cronograma: " + (WarRegistry.state().isRunning() ? "§arodando" : "§cparado"));
        sender.sendMessage("§7PvP: " + (WarRegistry.state().isPvpEnabled() ? "§aligado" : "§cdesligado"));
        sender.sendMessage("§7Hardcore: " + (WarRegistry.state().isHardcore() ? "§cativo" : "§7inativo"));
        sender.sendMessage("§7Agora: §f" + DISPLAY.format(now) + " §8(" + schedule.getZone() + ")");

        List<WarPhase> phases = schedule.getScheduledPhases();
        if (phases.isEmpty()) {
            sender.sendMessage("§cNenhuma fase agendada — confira war-schedule.yml.");
            return;
        }

        for (WarPhase phase : phases) {
            ZonedDateTime at = schedule.getTime(phase).orElse(null);
            if (at == null) {
                continue;
            }
            String mark = WarRegistry.state().isApplied(phase) ? "§a✔" : "§8○";
            String when = DISPLAY.format(at);
            String remaining = at.isAfter(now) ? " §8(em " + formatDuration(Duration.between(now, at)) + ")" : "";
            sender.sendMessage(mark + " §f" + phase.getDisplayName() + " §7— §f" + when + remaining);
        }

        schedule.getNextPhase(now).ifPresent(next -> sender.sendMessage(
                "§7Proxima: §f" + next.getDisplayName()));
    }

    private void handlePhase(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("§cUso: /startGuerra phase <" + String.join("|", phaseKeys()) + ">");
            return;
        }
        Optional<WarPhase> phaseOpt = WarPhase.fromKey(args[1]);
        if (phaseOpt.isEmpty()) {
            sender.sendMessage("§cFase desconhecida. Opcoes: §f" + String.join(", ", phaseKeys()));
            return;
        }
        WarPhase phase = phaseOpt.get();
        WarRegistry.service().forcePhase(phase);
        sender.sendMessage("§a§l[MineGuerra] §7Fase forcada: §f" + phase.getDisplayName());
    }

    private void handleReload(CommandSender sender) {
        WarRegistry.reloadSchedule();
        WarSchedule schedule = WarRegistry.service().getSchedule();
        sender.sendMessage("§a§l[MineGuerra] §7war-schedule.yml recarregado: §f"
                + schedule.getScheduledPhases().size() + " §7fases.");
        for (String warning : schedule.getWarnings()) {
            sender.sendMessage("§e- " + warning);
        }
    }

    private void sendUsage(CommandSender sender) {
        sender.sendMessage("§6§l[MineGuerra] §7Cronograma do evento:");
        sender.sendMessage("§e/startGuerra start §7— liga o cronograma");
        sender.sendMessage("§e/startGuerra stop §7— pausa o cronograma");
        sender.sendMessage("§e/startGuerra status §7— fases, PvP e hardcore");
        sender.sendMessage("§e/startGuerra phase <fase> §7— forca uma fase");
        sender.sendMessage("§e/startGuerra reload §7— recarrega war-schedule.yml");
    }

    private static String formatDuration(Duration duration) {
        long totalMinutes = Math.max(0, duration.toMinutes());
        long hours = totalMinutes / 60;
        long minutes = totalMinutes % 60;
        if (hours == 0) {
            return minutes + "min";
        }
        return hours + "h" + String.format("%02d", minutes);
    }

    private static List<String> phaseKeys() {
        List<String> keys = new ArrayList<>();
        for (WarPhase phase : WarPhase.values()) {
            keys.add(phase.getConfigKey());
        }
        return keys;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!sender.hasPermission("mineguerra.admin")) {
            return List.of();
        }
        if (args.length == 1) {
            return filterPrefix(SUBCOMMANDS, args[0]);
        }
        if (args.length == 2 && "phase".equalsIgnoreCase(args[0])) {
            return filterPrefix(phaseKeys(), args[1]);
        }
        return List.of();
    }

    private static List<String> filterPrefix(List<String> options, String prefix) {
        String lower = prefix.toLowerCase(Locale.ROOT);
        return options.stream()
                .filter(option -> option.toLowerCase(Locale.ROOT).startsWith(lower))
                .toList();
    }
}
