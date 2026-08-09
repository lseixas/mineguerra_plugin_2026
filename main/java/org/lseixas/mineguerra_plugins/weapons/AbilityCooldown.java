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
        return getRemainingMillis(player) > 0;
    }

    public long getRemainingMillis(Player player) {
        Long start = startTimes.get(player.getUniqueId());
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
        startTimes.put(player.getUniqueId(), System.currentTimeMillis());
    }

    public void clear(Player player) {
        startTimes.remove(player.getUniqueId());
    }
}
