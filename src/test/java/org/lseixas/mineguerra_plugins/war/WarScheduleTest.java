package org.lseixas.mineguerra_plugins.war;

import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;

import java.io.StringReader;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WarScheduleTest {

    private static final String YAML = """
            timezone: America/Sao_Paulo
            world: guerra
            phases:
              inicio:
                at: "2026-08-21 18:00"
              pvp-on:
                at: "2026-08-22 00:00"
              trapaceiro:
                at: "2026-08-22 18:00"
                x: 47
                y: 66
                z: -39
              julgamento:
                at: "2026-08-23 12:00"
              hardcore:
                at: "2026-08-23 18:00"
              fechar-centro:
                at: "2026-08-23 20:00"
                centerX: 47
                centerZ: -39
                fromSize: 3000
                toSize: 200
                durationSeconds: 7200
            """;

    private static WarSchedule parse(String yaml) {
        return WarSchedule.fromConfig(
                YamlConfiguration.loadConfiguration(new StringReader(yaml)));
    }

    private static ZonedDateTime saoPaulo(String isoLocal) {
        return ZonedDateTime.parse(isoLocal + "-03:00[America/Sao_Paulo]");
    }

    @Test
    void parsesAllPhasesInChronologicalOrder() {
        WarSchedule schedule = parse(YAML);

        assertTrue(schedule.getWarnings().isEmpty(), schedule.getWarnings().toString());
        assertEquals(ZoneId.of("America/Sao_Paulo"), schedule.getZone());
        assertEquals("guerra", schedule.getWorldName());
        assertEquals(
                List.of(
                        WarPhase.INICIO,
                        WarPhase.PVP_ON,
                        WarPhase.TRAPACEIRO,
                        WarPhase.JULGAMENTO,
                        WarPhase.HARDCORE,
                        WarPhase.FECHAR_CENTRO),
                schedule.getScheduledPhases());
    }

    @Test
    void resolvesTimesInConfiguredZone() {
        WarSchedule schedule = parse(YAML);
        assertEquals(saoPaulo("2026-08-21T18:00:00"), schedule.getTime(WarPhase.INICIO).orElseThrow());
    }

    @Test
    void duePhasesIncludeOnlyWhatAlreadyPassed() {
        WarSchedule schedule = parse(YAML);
        ZonedDateTime saturdayEvening = saoPaulo("2026-08-22T18:30:00");

        assertEquals(
                List.of(WarPhase.INICIO, WarPhase.PVP_ON, WarPhase.TRAPACEIRO),
                schedule.getDuePhases(saturdayEvening));
        assertEquals(WarPhase.JULGAMENTO, schedule.getNextPhase(saturdayEvening).orElseThrow());
    }

    @Test
    void phaseExactlyAtCurrentTimeCountsAsDue() {
        WarSchedule schedule = parse(YAML);
        assertEquals(List.of(WarPhase.INICIO), schedule.getDuePhases(saoPaulo("2026-08-21T18:00:00")));
    }

    @Test
    void noNextPhaseAfterLastOne() {
        WarSchedule schedule = parse(YAML);
        assertTrue(schedule.getNextPhase(saoPaulo("2026-08-24T00:00:00")).isEmpty());
    }

    @Test
    void readsTraderAndBorderOptions() {
        WarSchedule schedule = parse(YAML);

        WarSchedule.TraderSpawn spawn = schedule.getTraderSpawn().orElseThrow();
        assertEquals(47, spawn.x());
        assertEquals(66, spawn.y());
        assertEquals(-39, spawn.z());

        WarSchedule.BorderSettings border = schedule.getBorder().orElseThrow();
        assertEquals(47, border.centerX());
        assertEquals(-39, border.centerZ());
        assertEquals(3000, border.fromSize());
        assertEquals(200, border.toSize());
        assertEquals(7200, border.durationSeconds());
    }

    @Test
    void invalidDateIsReportedAndPhaseSkipped() {
        WarSchedule schedule = parse("""
                phases:
                  inicio:
                    at: "21/08/2026 18h"
                  hardcore:
                    at: "2026-08-23 18:00"
                """);

        assertEquals(List.of(WarPhase.HARDCORE), schedule.getScheduledPhases());
        assertEquals(1, schedule.getWarnings().size());
        assertTrue(schedule.getWarnings().get(0).contains("inicio"));
    }

    @Test
    void unknownPhaseKeyIsReported() {
        WarSchedule schedule = parse("""
                phases:
                  churrasco:
                    at: "2026-08-21 18:00"
                """);

        assertTrue(schedule.getScheduledPhases().isEmpty());
        assertTrue(schedule.getWarnings().get(0).contains("churrasco"));
    }

    @Test
    void invalidTimezoneFallsBackToDefault() {
        WarSchedule schedule = parse("""
                timezone: Marte/Olympus
                phases:
                  inicio:
                    at: "2026-08-21 18:00"
                """);

        assertEquals(ZoneId.of("America/Sao_Paulo"), schedule.getZone());
        assertFalse(schedule.getWarnings().isEmpty());
    }

    @Test
    void missingPhasesSectionYieldsEmptySchedule() {
        WarSchedule schedule = parse("timezone: America/Sao_Paulo\n");

        assertTrue(schedule.getScheduledPhases().isEmpty());
        assertTrue(schedule.getNextPhase(ZonedDateTime.now()).isEmpty());
        assertFalse(schedule.getWarnings().isEmpty());
    }
}
