package org.lseixas.mineguerra_plugins.war;

import org.bukkit.configuration.ConfigurationSection;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Cronograma do evento lido de {@code war-schedule.yml}.
 *
 * <p>Horários são wall-clock (data e hora reais), então o cronograma sobrevive a
 * restart do servidor sem recalcular offsets.
 */
public class WarSchedule {

    private static final DateTimeFormatter FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm[:ss]");
    private static final String DEFAULT_ZONE = "America/Sao_Paulo";
    private static final String DEFAULT_WORLD = "world";

    private final ZoneId zone;
    private final String worldName;
    private final Map<WarPhase, ZonedDateTime> times;
    private final TraderSpawn traderSpawn;
    private final BorderSettings border;
    private final List<String> warnings;

    private WarSchedule(
            ZoneId zone,
            String worldName,
            Map<WarPhase, ZonedDateTime> times,
            TraderSpawn traderSpawn,
            BorderSettings border,
            List<String> warnings
    ) {
        this.zone = zone;
        this.worldName = worldName;
        this.times = times;
        this.traderSpawn = traderSpawn;
        this.border = border;
        this.warnings = warnings;
    }

    public static WarSchedule fromConfig(ConfigurationSection config) {
        List<String> warnings = new ArrayList<>();
        ZoneId zone = parseZone(config.getString("timezone", DEFAULT_ZONE), warnings);
        String worldName = config.getString("world", DEFAULT_WORLD);

        Map<WarPhase, ZonedDateTime> times = new EnumMap<>(WarPhase.class);
        ConfigurationSection phases = config.getConfigurationSection("phases");
        if (phases == null) {
            warnings.add("Secao 'phases' ausente — nenhuma fase agendada.");
            return new WarSchedule(zone, worldName, times, null, null, warnings);
        }

        TraderSpawn traderSpawn = null;
        BorderSettings border = null;

        for (String key : phases.getKeys(false)) {
            Optional<WarPhase> phaseOpt = WarPhase.fromKey(key);
            if (phaseOpt.isEmpty()) {
                warnings.add("Fase desconhecida em phases: '" + key + "'.");
                continue;
            }
            WarPhase phase = phaseOpt.get();
            ConfigurationSection section = phases.getConfigurationSection(key);
            if (section == null) {
                warnings.add("Fase '" + key + "' sem configuracao.");
                continue;
            }

            String at = section.getString("at");
            Optional<ZonedDateTime> parsed = parseInstant(at, zone);
            if (parsed.isEmpty()) {
                warnings.add("Fase '" + key + "' com data invalida: '" + at
                        + "' (esperado yyyy-MM-dd HH:mm).");
                continue;
            }
            times.put(phase, parsed.get());

            if (phase == WarPhase.TRAPACEIRO) {
                traderSpawn = new TraderSpawn(
                        section.getDouble("x"),
                        section.getDouble("y"),
                        section.getDouble("z"));
            }
            if (phase == WarPhase.FECHAR_CENTRO) {
                border = new BorderSettings(
                        section.getDouble("centerX"),
                        section.getDouble("centerZ"),
                        section.getDouble("fromSize", 3000),
                        section.getDouble("toSize", 200),
                        section.getLong("durationSeconds", 7200));
            }
        }

        return new WarSchedule(zone, worldName, times, traderSpawn, border, warnings);
    }

    private static ZoneId parseZone(String value, List<String> warnings) {
        try {
            return ZoneId.of(value);
        } catch (DateTimeException ex) {
            warnings.add("Timezone invalida '" + value + "', usando " + DEFAULT_ZONE + ".");
            return ZoneId.of(DEFAULT_ZONE);
        }
    }

    private static Optional<ZonedDateTime> parseInstant(String value, ZoneId zone) {
        if (value == null || value.isBlank()) {
            return Optional.empty();
        }
        try {
            return Optional.of(LocalDateTime.parse(value.trim(), FORMAT).atZone(zone));
        } catch (DateTimeParseException ex) {
            return Optional.empty();
        }
    }

    public ZoneId getZone() {
        return zone;
    }

    public String getWorldName() {
        return worldName;
    }

    public Optional<ZonedDateTime> getTime(WarPhase phase) {
        return Optional.ofNullable(times.get(phase));
    }

    /** Fases agendadas em ordem cronológica. */
    public List<WarPhase> getScheduledPhases() {
        return times.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .toList();
    }

    /** Fases cujo horário já passou, em ordem cronológica. */
    public List<WarPhase> getDuePhases(ZonedDateTime now) {
        return times.entrySet().stream()
                .filter(entry -> !entry.getValue().isAfter(now))
                .sorted(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .toList();
    }

    /** Próxima fase ainda não vencida. */
    public Optional<WarPhase> getNextPhase(ZonedDateTime now) {
        return times.entrySet().stream()
                .filter(entry -> entry.getValue().isAfter(now))
                .min(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey);
    }

    public Optional<TraderSpawn> getTraderSpawn() {
        return Optional.ofNullable(traderSpawn);
    }

    public Optional<BorderSettings> getBorder() {
        return Optional.ofNullable(border);
    }

    public List<String> getWarnings() {
        return List.copyOf(warnings);
    }

    public record TraderSpawn(double x, double y, double z) {
    }

    public record BorderSettings(
            double centerX,
            double centerZ,
            double fromSize,
            double toSize,
            long durationSeconds
    ) {
    }
}
