package org.lseixas.mineguerra_plugins.teams;

import org.bukkit.plugin.java.JavaPlugin;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TeamIdValidationTest {

    private ServerMock server;
    private TeamService teams;

    @BeforeEach
    void setUp() {
        server = MockBukkit.mock();
        JavaPlugin plugin = MockBukkit.createMockPlugin();
        TeamsDataStore store = new TeamsDataStore(plugin);
        teams = new TeamService(plugin, store);
    }

    @AfterEach
    void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    void acceptsLowercaseIdsWithDashUnderscore() {
        assertTrue(teams.isValidTeamId("red"));
        assertTrue(teams.isValidTeamId("red_team"));
        assertTrue(teams.isValidTeamId("red-team"));
        assertEquals("red-team", teams.normalizeTeamId("Red-Team"));
    }

    @Test
    void rejectsInvalidIds() {
        assertFalse(teams.isValidTeamId(null));
        assertFalse(teams.isValidTeamId(""));
        assertFalse(teams.isValidTeamId("Red Team"));
        assertFalse(teams.isValidTeamId("time!"));
        assertFalse(teams.isValidTeamId("a".repeat(33)));
    }
}
