package org.lseixas.mineguerra_plugins.weapons;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Cooldown em memória por jogador (reset no restart do servidor).
 */
public class AbilityCooldown {

    private final Map<UUID, Long> startTimes = new HashMap<>();
    private final long durationMillis;

    public AbilityCooldown(long durationMillis) {
        this.durationMillis = durationMillis;
    }

    public long getDurationMillis() {
        return durationMillis;
    }

    public boolean isOnCooldown(Player player) {
        return isOnCooldown(player.getUniqueId());
    }

    public boolean isOnCooldown(UUID playerId) {
        return getRemainingMillis(playerId) > 0;
    }

    public long getRemainingMillis(Player player) {
        return getRemainingMillis(player.getUniqueId());
    }

    public long getRemainingMillis(UUID playerId) {
        Long start = startTimes.get(playerId);
        if (start == null) {
            return 0;
        }
        long elapsed = System.currentTimeMillis() - start;
        long remaining = durationMillis - elapsed;
        return Math.max(0, remaining);
    }

    public long getRemainingSeconds(Player player) {
        return (getRemainingMillis(player) + 999) / 1000;
    }

    /**
     * @return {@code true} se a habilidade pode ser usada (não está em cooldown)
     */
    public boolean tryUse(Player player) {
        return !isOnCooldown(player);
    }

    /** Grava o início do cooldown (chamar conforme {@link CooldownStart}). */
    public void commit(Player player) {
        commit(player.getUniqueId());
    }

    public void commit(UUID playerId) {
        startTimes.put(playerId, System.currentTimeMillis());
    }

    /** For tests: commit as if {@code elapsedMillis} already passed. */
    public void commitAt(UUID playerId, long startEpochMillis) {
        startTimes.put(playerId, startEpochMillis);
    }

    public void clear(Player player) {
        clear(player.getUniqueId());
    }

    public void clear(UUID playerId) {
        startTimes.remove(playerId);
    }
}
