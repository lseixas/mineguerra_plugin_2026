package org.lseixas.mineguerra_plugins.metrics;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

/**
 * Disk I/O for a metrics session: JSONL append + snapshot rewrite + active pointer.
 */
public final class MetricsSessionStore {

    private static final String ACTIVE_POINTER = "active-session.txt";
    private static final String EVENTS_FILE = "events.jsonl";
    private static final String SNAPSHOT_FILE = "snapshot.json";

    private final JavaPlugin plugin;
    private final Path root;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final Gson compactGson = new Gson();

    private Path sessionDir;
    private String sessionId;
    private final List<String> eventBuffer = new ArrayList<>();

    public MetricsSessionStore(JavaPlugin plugin) {
        this.plugin = plugin;
        this.root = plugin.getDataFolder().toPath().resolve("metrics");
    }

    public Path getRoot() {
        return root;
    }

    public Optional<String> getSessionId() {
        return Optional.ofNullable(sessionId);
    }

    public Optional<Path> getSessionDir() {
        return Optional.ofNullable(sessionDir);
    }

    public synchronized void openNewSession(String newSessionId) throws IOException {
        closeQuietly();
        Files.createDirectories(root);
        this.sessionId = newSessionId;
        this.sessionDir = root.resolve(newSessionId);
        Files.createDirectories(sessionDir);
        if (!Files.exists(sessionDir.resolve(EVENTS_FILE))) {
            Files.createFile(sessionDir.resolve(EVENTS_FILE));
        }
        writeActivePointer(newSessionId);
    }

    public synchronized boolean openExistingSession(String existingSessionId) throws IOException {
        Path dir = root.resolve(existingSessionId);
        if (!Files.isDirectory(dir)) {
            return false;
        }
        closeQuietly();
        this.sessionId = existingSessionId;
        this.sessionDir = dir;
        if (!Files.exists(dir.resolve(EVENTS_FILE))) {
            Files.createFile(dir.resolve(EVENTS_FILE));
        }
        writeActivePointer(existingSessionId);
        return true;
    }

    public synchronized Optional<String> readActivePointer() {
        Path pointer = root.resolve(ACTIVE_POINTER);
        if (!Files.isRegularFile(pointer)) {
            return Optional.empty();
        }
        try {
            String id = Files.readString(pointer, StandardCharsets.UTF_8).trim();
            return id.isEmpty() ? Optional.empty() : Optional.of(id);
        } catch (IOException ex) {
            plugin.getLogger().log(Level.WARNING, "Failed to read metrics active-session pointer", ex);
            return Optional.empty();
        }
    }

    public synchronized Optional<MetricsSnapshot> loadSnapshot() {
        if (sessionDir == null) {
            return Optional.empty();
        }
        Path file = sessionDir.resolve(SNAPSHOT_FILE);
        if (!Files.isRegularFile(file)) {
            return Optional.empty();
        }
        try {
            String json = Files.readString(file, StandardCharsets.UTF_8);
            MetricsSnapshot snapshot = gson.fromJson(json, MetricsSnapshot.class);
            return Optional.ofNullable(snapshot);
        } catch (Exception ex) {
            plugin.getLogger().log(Level.WARNING, "Failed to load metrics snapshot", ex);
            return Optional.empty();
        }
    }

    public synchronized void appendEvent(Object event) {
        eventBuffer.add(compactGson.toJson(event));
        if (eventBuffer.size() >= 50) {
            flushEvents();
        }
    }

    public synchronized void flushEvents() {
        if (sessionDir == null || eventBuffer.isEmpty()) {
            return;
        }
        Path file = sessionDir.resolve(EVENTS_FILE);
        try (BufferedWriter writer = Files.newBufferedWriter(
                file, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            for (String line : eventBuffer) {
                writer.write(line);
                writer.newLine();
            }
            eventBuffer.clear();
        } catch (IOException ex) {
            plugin.getLogger().log(Level.WARNING, "Failed to flush metrics JSONL", ex);
        }
    }

    public synchronized void writeSnapshot(MetricsSnapshot snapshot) {
        if (sessionDir == null) {
            return;
        }
        flushEvents();
        Path file = sessionDir.resolve(SNAPSHOT_FILE);
        Path temp = sessionDir.resolve(SNAPSHOT_FILE + ".tmp");
        try {
            Files.writeString(temp, gson.toJson(snapshot), StandardCharsets.UTF_8);
            Files.move(temp, file, java.nio.file.StandardCopyOption.REPLACE_EXISTING,
                    java.nio.file.StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException ex) {
            try {
                Files.writeString(file, gson.toJson(snapshot), StandardCharsets.UTF_8);
            } catch (IOException nested) {
                plugin.getLogger().log(Level.WARNING, "Failed to write metrics snapshot", nested);
            }
        }
    }

    public synchronized void clearActivePointer() {
        Path pointer = root.resolve(ACTIVE_POINTER);
        try {
            Files.deleteIfExists(pointer);
        } catch (IOException ex) {
            plugin.getLogger().log(Level.WARNING, "Failed to clear metrics active-session pointer", ex);
        }
    }

    public synchronized void closeQuietly() {
        flushEvents();
        sessionDir = null;
        sessionId = null;
        eventBuffer.clear();
    }

    private void writeActivePointer(String id) throws IOException {
        Files.createDirectories(root);
        Files.writeString(root.resolve(ACTIVE_POINTER), id + System.lineSeparator(), StandardCharsets.UTF_8);
    }
}
