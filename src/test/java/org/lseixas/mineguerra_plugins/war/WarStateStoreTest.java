package org.lseixas.mineguerra_plugins.war;

import org.bukkit.plugin.java.JavaPlugin;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WarStateStoreTest {

    private JavaPlugin plugin;

    @BeforeEach
    void setUp() {
        MockBukkit.mock();
        plugin = MockBukkit.createMockPlugin();
    }

    @AfterEach
    void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    void freshStateHasPvpOnAndNothingApplied() {
        WarStateStore store = new WarStateStore(plugin);
        store.load();

        assertFalse(store.isRunning());
        assertTrue(store.isPvpEnabled());
        assertFalse(store.isHardcore());
        assertTrue(store.getAppliedPhases().isEmpty());
    }

    @Test
    void markAppliedIsIdempotent() {
        WarStateStore store = new WarStateStore(plugin);
        store.load();

        assertTrue(store.markApplied(WarPhase.INICIO));
        assertFalse(store.markApplied(WarPhase.INICIO));
        assertTrue(store.isApplied(WarPhase.INICIO));
        assertEquals(1, store.getAppliedPhases().size());
    }

    @Test
    void stateSurvivesReload() {
        WarStateStore store = new WarStateStore(plugin);
        store.load();
        store.setRunning(true);
        store.setPvpEnabled(false);
        store.setHardcore(true);
        store.markApplied(WarPhase.INICIO);
        store.markApplied(WarPhase.HARDCORE);
        store.save();

        WarStateStore reloaded = new WarStateStore(plugin);
        reloaded.load();

        assertTrue(reloaded.isRunning());
        assertFalse(reloaded.isPvpEnabled());
        assertTrue(reloaded.isHardcore());
        assertTrue(reloaded.isApplied(WarPhase.INICIO));
        assertTrue(reloaded.isApplied(WarPhase.HARDCORE));
        assertFalse(reloaded.isApplied(WarPhase.PVP_ON));
    }

    @Test
    void resetToDefaultsClearsEverything() {
        WarStateStore store = new WarStateStore(plugin);
        store.load();
        store.setRunning(true);
        store.setPvpEnabled(false);
        store.setHardcore(true);
        store.markApplied(WarPhase.INICIO);
        store.rememberTrapaceiro(java.util.UUID.randomUUID());
        java.util.UUID kitPlayer = java.util.UUID.randomUUID();
        assertTrue(store.markStarterKitReceived(kitPlayer));

        store.resetToDefaults();

        assertFalse(store.isRunning());
        assertTrue(store.isPvpEnabled());
        assertFalse(store.isHardcore());
        assertTrue(store.getAppliedPhases().isEmpty());
        assertTrue(store.getTrapaceiroEntityIds().isEmpty());
        assertFalse(store.hasReceivedStarterKit(kitPlayer));
    }

    @Test
    void starterKitReceivedSurvivesReloadAndIsIdempotent() {
        WarStateStore store = new WarStateStore(plugin);
        store.load();
        java.util.UUID playerId = java.util.UUID.randomUUID();

        assertTrue(store.markStarterKitReceived(playerId));
        assertFalse(store.markStarterKitReceived(playerId));
        assertTrue(store.hasReceivedStarterKit(playerId));
        store.save();

        WarStateStore reloaded = new WarStateStore(plugin);
        reloaded.load();
        assertTrue(reloaded.hasReceivedStarterKit(playerId));
        assertFalse(reloaded.markStarterKitReceived(playerId));
    }

    @Test
    void currentPhaseIsLatestAppliedInScheduleOrder() {
        WarStateStore store = new WarStateStore(plugin);
        store.load();

        assertTrue(store.getCurrentPhase().isEmpty());

        store.markApplied(WarPhase.INICIO);
        assertEquals(WarPhase.INICIO, store.getCurrentPhase().orElseThrow());

        store.markApplied(WarPhase.PVP_ON);
        store.markApplied(WarPhase.TRAPACEIRO);
        assertEquals(WarPhase.TRAPACEIRO, store.getCurrentPhase().orElseThrow());

        store.resetToDefaults();
        assertTrue(store.getCurrentPhase().isEmpty());
    }

    @Test
    void phaseKeysRoundTripThroughConfigNames() {
        for (WarPhase phase : WarPhase.values()) {
            assertEquals(phase, WarPhase.fromKey(phase.getConfigKey()).orElseThrow());
            assertEquals(phase, WarPhase.fromKey(phase.name()).orElseThrow());
        }
        assertTrue(WarPhase.fromKey("churrasco").isEmpty());
        assertTrue(WarPhase.fromKey(null).isEmpty());
    }
}
