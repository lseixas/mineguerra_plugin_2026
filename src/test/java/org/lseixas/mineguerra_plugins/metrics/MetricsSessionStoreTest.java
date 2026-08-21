package org.lseixas.mineguerra_plugins.metrics;

import org.bukkit.plugin.java.JavaPlugin;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MetricsSessionStoreTest {

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
    void openSessionWritesJsonlAndSnapshot() throws Exception {
        MetricsSessionStore store = new MetricsSessionStore(plugin);
        store.openNewSession("test-session");

        store.appendEvent(Map.of("type", "session_start", "sessionId", "test-session"));
        store.flushEvents();

        MetricsSnapshot snapshot = new MetricsSnapshot();
        snapshot.setSessionId("test-session");
        snapshot.setEventCount(1);
        store.writeSnapshot(snapshot);

        Path dir = store.getSessionDir().orElseThrow();
        assertTrue(Files.isRegularFile(dir.resolve("events.jsonl")));
        assertTrue(Files.isRegularFile(dir.resolve("snapshot.json")));
        assertEquals("test-session", store.readActivePointer().orElseThrow());

        String events = Files.readString(dir.resolve("events.jsonl"));
        assertTrue(events.contains("session_start"));

        MetricsSnapshot loaded = store.loadSnapshot().orElseThrow();
        assertEquals("test-session", loaded.getSessionId());
        assertEquals(1, loaded.getEventCount());
    }
}
