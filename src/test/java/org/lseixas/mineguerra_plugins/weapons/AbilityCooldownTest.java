package org.lseixas.mineguerra_plugins.weapons;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AbilityCooldownTest {

    @Test
    void commitBlocksUntilDurationElapses() {
        AbilityCooldown cooldown = new AbilityCooldown(1_000);
        UUID playerId = UUID.randomUUID();

        assertFalse(cooldown.isOnCooldown(playerId));
        cooldown.commit(playerId);
        assertTrue(cooldown.isOnCooldown(playerId));
        assertTrue(cooldown.getRemainingMillis(playerId) > 0);

        cooldown.commitAt(playerId, System.currentTimeMillis() - 1_500);
        assertFalse(cooldown.isOnCooldown(playerId));
        assertEquals(0, cooldown.getRemainingMillis(playerId));
    }

    @Test
    void clearRemovesCooldown() {
        AbilityCooldown cooldown = new AbilityCooldown(5_000);
        UUID playerId = UUID.randomUUID();
        cooldown.commit(playerId);
        cooldown.clear(playerId);
        assertFalse(cooldown.isOnCooldown(playerId));
    }
}
