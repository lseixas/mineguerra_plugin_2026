package org.lseixas.mineguerra_plugins.metrics;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.Material;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlayerMetricsAggregationTest {

    @Test
    void emeraldBlockCountsAsNineEmeralds() {
        assertEquals(9, TrackedMaterials.toEmeraldUnits(Material.EMERALD_BLOCK, 1));
        assertEquals(18, TrackedMaterials.toEmeraldUnits(Material.EMERALD_BLOCK, 2));
        assertEquals(5, TrackedMaterials.toEmeraldUnits(Material.EMERALD, 5));
        assertEquals(0, TrackedMaterials.toEmeraldUnits(Material.DIAMOND, 3));
    }

    @Test
    void playerCountersAccumulateAndCopy() {
        PlayerMetrics metrics = new PlayerMetrics();
        metrics.setName("leo");
        metrics.setTeamId("vermelho");
        metrics.addKill(2);
        metrics.addDeath(1);
        metrics.addBlockBroken(BlockCategory.ORE, 10);
        metrics.addBlockBroken(BlockCategory.STONE, 5);
        metrics.addItemGained("EMERALD", 3);
        metrics.addEmeraldsGained(3);

        PlayerMetrics copy = metrics.copy();
        assertEquals(2, copy.getKills());
        assertEquals(1, copy.getDeaths());
        assertEquals(15, copy.getBlocksBroken());
        assertEquals(10L, copy.getBlocksBrokenByCategory().get("ORE"));
        assertEquals(3L, copy.getItemsGained().get("EMERALD"));
        assertEquals(3, copy.getEmeraldsGained());

        metrics.addKill(1);
        assertEquals(2, copy.getKills());
        assertEquals(3, metrics.getKills());
    }

    @Test
    void teamMetricsSumsPlayers() {
        PlayerMetrics a = new PlayerMetrics();
        a.setTeamId("azul");
        a.addKill(3);
        a.addEmeraldsGained(9);
        a.addBlockBroken(BlockCategory.WOOD, 4);

        PlayerMetrics b = new PlayerMetrics();
        b.setTeamId("azul");
        b.addKill(1);
        b.addEmeraldsGained(2);
        b.addBlockBroken(BlockCategory.WOOD, 1);

        TeamMetrics team = new TeamMetrics();
        team.add(a);
        team.add(b);

        assertEquals(2, team.getPlayers());
        assertEquals(4, team.getKills());
        assertEquals(11, team.getEmeraldsGained());
        assertEquals(5L, team.getBlocksBrokenByCategory().get("WOOD"));
    }

    @Test
    void deltaFromIgnoresZeros() {
        PlayerMetrics baseline = new PlayerMetrics();
        baseline.addBlockBroken(BlockCategory.STONE, 100);

        PlayerMetrics current = baseline.copy();
        current.addBlockBroken(BlockCategory.STONE, 20);
        current.addKill(1);

        Map<String, Long> delta = current.deltaFrom(baseline);
        assertEquals(20L, delta.get("blocksBroken"));
        assertEquals(1L, delta.get("kills"));
        assertTrue(!delta.containsKey("deaths"));
    }

    @Test
    void snapshotRoundTripsThroughGson() {
        Gson gson = new GsonBuilder().create();
        MetricsSnapshot snapshot = new MetricsSnapshot();
        snapshot.setSessionId("20260821T180000Z");
        snapshot.setStartedAt("2026-08-21T18:00:00Z");
        snapshot.setUpdatedAt("2026-08-21T18:30:00Z");
        snapshot.setRecording(true);
        snapshot.setEventCount(42);

        PlayerMetrics player = new PlayerMetrics();
        player.setName("leo");
        player.setTeamId("vermelho");
        player.addEmeraldsGained(18);
        player.addBlockBroken(BlockCategory.ORE, 7);
        String uuid = UUID.randomUUID().toString();
        snapshot.getPlayers().put(uuid, player);

        TeamMetrics team = new TeamMetrics();
        team.add(player);
        snapshot.getTeams().put("vermelho", team);

        String json = gson.toJson(snapshot);
        MetricsSnapshot loaded = gson.fromJson(json, MetricsSnapshot.class);

        assertEquals("20260821T180000Z", loaded.getSessionId());
        assertEquals(42, loaded.getEventCount());
        assertEquals(18, loaded.getPlayers().get(uuid).getEmeraldsGained());
        assertEquals(7L, loaded.getPlayers().get(uuid).getBlocksBrokenByCategory().get("ORE"));
        assertEquals(18, loaded.getTeams().get("vermelho").getEmeraldsGained());
    }

    @Test
    void blockCategoriesClassifyOresAndCrops() {
        assertEquals(BlockCategory.ORE, BlockCategory.of(Material.DIAMOND_ORE));
        assertEquals(BlockCategory.ORE, BlockCategory.of(Material.ANCIENT_DEBRIS));
        assertEquals(BlockCategory.DIRT, BlockCategory.of(Material.DIRT));
        assertTrue(CropMaterials.isPlantable(Material.WHEAT_SEEDS));
        assertTrue(CropMaterials.isHarvestable(Material.WHEAT));
        assertTrue(TrackedMaterials.isTracked(Material.EMERALD));
        assertTrue(!TrackedMaterials.isTracked(Material.COBBLESTONE));
    }
}
