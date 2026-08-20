package org.lseixas.mineguerra_plugins.war;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Ticker de 1s que compara o wall-clock com o cronograma e dispara as fases.
 */
public class WarScheduler {

    private static final long TICK_PERIOD = 20L;
    /** Minutos restantes que geram aviso antes de cada fase. */
    private static final List<Long> WARN_MINUTES = List.of(60L, 10L, 5L, 1L);

    private final JavaPlugin plugin;
    private final WarService warService;
    private final WarStateStore state;
    private final Set<String> sentWarnings = new HashSet<>();

    private BukkitTask task;

    public WarScheduler(JavaPlugin plugin, WarService warService, WarStateStore state) {
        this.plugin = plugin;
        this.warService = warService;
        this.state = state;
    }

    public boolean isRunning() {
        return task != null;
    }

    public void start() {
        if (task != null) {
            return;
        }
        task = Bukkit.getScheduler().runTaskTimer(plugin, this::tick, TICK_PERIOD, TICK_PERIOD);
    }

    public void stop() {
        if (task == null) {
            return;
        }
        task.cancel();
        task = null;
        sentWarnings.clear();
    }

    private void tick() {
        if (!state.isRunning()) {
            return;
        }
        if (!warService.advance().isEmpty()) {
            return;
        }
        announceCountdown();
    }

    private void announceCountdown() {
        ZonedDateTime now = warService.now();
        WarSchedule schedule = warService.getSchedule();
        Optional<WarPhase> nextOpt = schedule.getNextPhase(now);
        if (nextOpt.isEmpty()) {
            return;
        }

        WarPhase next = nextOpt.get();
        ZonedDateTime at = schedule.getTime(next).orElse(null);
        if (at == null) {
            return;
        }

        long secondsLeft = Duration.between(now, at).getSeconds();
        for (long minutes : WARN_MINUTES) {
            long target = minutes * 60;
            if (secondsLeft > target || secondsLeft <= target - 60) {
                continue;
            }
            String key = next.getConfigKey() + ":" + minutes;
            if (!sentWarnings.add(key)) {
                return;
            }
            Bukkit.broadcastMessage("§e§l[MineGuerra] §f" + next.getDisplayName()
                    + " §7em §f" + minutes + " min§7.");
            return;
        }
    }
}
