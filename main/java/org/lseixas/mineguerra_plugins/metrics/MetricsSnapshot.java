package org.lseixas.mineguerra_plugins.metrics;

import java.util.LinkedHashMap;
import java.util.Map;

/** Serializable snapshot written to {@code snapshot.json}. */
public final class MetricsSnapshot {

    private String sessionId;
    private String startedAt;
    private String updatedAt;
    private boolean recording;
    private long eventCount;
    private Map<String, PlayerMetrics> players = new LinkedHashMap<>();
    private Map<String, TeamMetrics> teams = new LinkedHashMap<>();

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(String startedAt) {
        this.startedAt = startedAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public boolean isRecording() {
        return recording;
    }

    public void setRecording(boolean recording) {
        this.recording = recording;
    }

    public long getEventCount() {
        return eventCount;
    }

    public void setEventCount(long eventCount) {
        this.eventCount = eventCount;
    }

    public Map<String, PlayerMetrics> getPlayers() {
        return players;
    }

    public void setPlayers(Map<String, PlayerMetrics> players) {
        this.players = players != null ? players : new LinkedHashMap<>();
    }

    public Map<String, TeamMetrics> getTeams() {
        return teams;
    }

    public void setTeams(Map<String, TeamMetrics> teams) {
        this.teams = teams != null ? teams : new LinkedHashMap<>();
    }
}
