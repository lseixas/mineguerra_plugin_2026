package org.lseixas.mineguerra_plugins.teams;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TeamOfflineAssignTest {

    private ServerMock server;
    private TeamsDataStore store;
    private TeamService teams;

    @BeforeEach
    void setUp() {
        server = MockBukkit.mock();
        JavaPlugin plugin = MockBukkit.createMockPlugin();
        store = new TeamsDataStore(plugin);
        teams = new TeamService(plugin, store);
        teams.createTeam("vermelho", "Vermelhos", ChatColor.RED);
    }

    @AfterEach
    void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    void joinByNickStoresPendingUntilLogin() {
        var result = teams.assignPlayerByName("LeoOffline", "vermelho");
        assertTrue(result.isPresent());
        assertTrue(result.get().pendingLogin());
        assertEquals("vermelho", store.getPendingByName().get("leooffline"));

        Player player = server.addPlayer("LeoOffline");
        teams.applyPlayerTeam(player);

        assertFalse(store.getPendingByName().containsKey("leooffline"));
        assertEquals("vermelho", store.getPlayerTeams().get(player.getUniqueId()));
        assertEquals("vermelho", teams.getTeamId(player));
    }

    @Test
    void leaveByNickRemovesPending() {
        teams.assignPlayerByName("Ghost", "vermelho");
        assertEquals(TeamService.RemoveResult.REMOVED_OFFLINE, teams.removePlayerByName("ghost"));
        assertTrue(store.getPendingByName().isEmpty());
    }

    @Test
    void clearConfirmWipesTeamsAndPending() {
        teams.assignPlayerByName("A", "vermelho");
        teams.createTeam("azul", "Azuis", ChatColor.BLUE);
        assertEquals(2, teams.clearAllTeams());
        assertTrue(store.getTeams().isEmpty());
        assertTrue(store.getPendingByName().isEmpty());
        assertTrue(store.getPlayerTeams().isEmpty());
    }
}
