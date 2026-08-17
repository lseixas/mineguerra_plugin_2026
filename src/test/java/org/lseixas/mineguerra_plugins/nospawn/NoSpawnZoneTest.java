package org.lseixas.mineguerra_plugins.nospawn;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NoSpawnZoneTest {

    @Test
    void normalizesCornersAndIncludesBounds() {
        UUID worldId = UUID.randomUUID();
        NoSpawnZone zone = new NoSpawnZone(
                "zone-1",
                worldId,
                "world",
                10, 64, 20,
                5, 70, 15
        );

        assertEquals(5, zone.minX());
        assertEquals(64, zone.minY());
        assertEquals(15, zone.minZ());
        assertEquals(10, zone.maxX());
        assertEquals(70, zone.maxY());
        assertEquals(20, zone.maxZ());
        assertEquals(6L * 7L * 6L, zone.volumeBlocks());
        assertEquals("6x7x6", zone.sizeLabel());

        assertTrue(zone.containsBlock(5, 64, 15));
        assertTrue(zone.containsBlock(10, 70, 20));
        assertTrue(zone.containsBlock(7, 67, 17));
        assertFalse(zone.containsBlock(4, 67, 17));
        assertFalse(zone.containsBlock(7, 71, 17));
        assertFalse(zone.containsBlock(7, 67, 21));
    }
}
