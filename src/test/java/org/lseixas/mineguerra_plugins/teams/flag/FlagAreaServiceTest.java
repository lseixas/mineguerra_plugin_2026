package org.lseixas.mineguerra_plugins.teams.flag;

import org.bukkit.Material;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FlagAreaServiceTest {

    private static final int CENTER = 0;

    private static boolean within(int x, int y, int z) {
        return FlagAreaService.withinRadius(
                x, y, z, CENTER, CENTER, CENTER, FlagAreaService.RADIUS);
    }

    @Test
    void radiusIsThree() {
        assertTrue(FlagAreaService.RADIUS == 3);
    }

    @Test
    void centerAndFacesAreInside() {
        assertTrue(within(0, 0, 0));
        assertTrue(within(3, 0, 0));
        assertTrue(within(-3, 0, 0));
        assertTrue(within(0, 3, 0));
        assertTrue(within(0, -3, 0));
        assertTrue(within(0, 0, 3));
        assertTrue(within(0, 0, -3));
    }

    @Test
    void cornersAreInsideBecauseAreaIsACube() {
        assertTrue(within(3, 3, 3));
        assertTrue(within(-3, -3, -3));
    }

    @Test
    void oneBlockPastTheEdgeIsOutside() {
        assertFalse(within(4, 0, 0));
        assertFalse(within(0, 4, 0));
        assertFalse(within(0, 0, -4));
        assertFalse(within(4, 4, 4));
    }

    @Test
    void terrainAndBuildBlocksAreClearable() {
        assertTrue(FlagAreaService.isClearable(Material.STONE));
        assertTrue(FlagAreaService.isClearable(Material.DIRT));
        assertTrue(FlagAreaService.isClearable(Material.OAK_LOG));
        assertTrue(FlagAreaService.isClearable(Material.OBSIDIAN));
        assertTrue(FlagAreaService.isClearable(Material.WATER));
    }

    @Test
    void airIsSkipped() {
        assertFalse(FlagAreaService.isClearable(Material.AIR));
        assertFalse(FlagAreaService.isClearable(Material.CAVE_AIR));
    }

    @Test
    void worldCriticalBlocksAreNeverCleared() {
        assertFalse(FlagAreaService.isClearable(Material.BEDROCK));
        assertFalse(FlagAreaService.isClearable(Material.BARRIER));
        assertFalse(FlagAreaService.isClearable(Material.END_PORTAL));
        assertFalse(FlagAreaService.isClearable(Material.END_PORTAL_FRAME));
        assertFalse(FlagAreaService.isClearable(Material.NETHER_PORTAL));
        assertFalse(FlagAreaService.isClearable(Material.COMMAND_BLOCK));
    }
}
