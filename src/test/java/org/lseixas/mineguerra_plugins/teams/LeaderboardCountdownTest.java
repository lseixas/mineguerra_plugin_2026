package org.lseixas.mineguerra_plugins.teams;

import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LeaderboardCountdownTest {

    @Test
    void formatsHoursAndMinutes() {
        assertEquals("2h18m", LeaderboardService.formatCountdown(Duration.ofHours(2).plusMinutes(18)));
    }

    @Test
    void formatsMinutesAndSeconds() {
        assertEquals("5m09s", LeaderboardService.formatCountdown(Duration.ofMinutes(5).plusSeconds(9)));
    }

    @Test
    void formatsSecondsOnly() {
        assertEquals("42s", LeaderboardService.formatCountdown(Duration.ofSeconds(42)));
    }

    @Test
    void clampsNegativeToZero() {
        assertEquals("0s", LeaderboardService.formatCountdown(Duration.ofSeconds(-3)));
    }
}
