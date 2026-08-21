package org.lseixas.mineguerra_plugins.metrics;

import java.util.HashMap;
import java.util.Map;

/** Aggregated counters for a team (sum of member players at flush time). */
public final class TeamMetrics {

    private long kills;
    private long deaths;
    private long mobKills;
    private long blocksBroken;
    private long blocksPlaced;
    private long cropsPlanted;
    private long cropsHarvested;
    private long emeraldsGained;
    private long emeraldsSpent;
    private long flagCaptures;
    private long playTimeMs;
    private long distanceCm;
    private int players;
    private final Map<String, Long> blocksBrokenByCategory = new HashMap<>();
    private final Map<String, Long> blocksPlacedByCategory = new HashMap<>();
    private final Map<String, Long> itemsGained = new HashMap<>();
    private final Map<String, Long> itemsSpent = new HashMap<>();

    public void add(PlayerMetrics player) {
        players++;
        kills += player.getKills();
        deaths += player.getDeaths();
        mobKills += player.getMobKills();
        blocksBroken += player.getBlocksBroken();
        blocksPlaced += player.getBlocksPlaced();
        cropsPlanted += player.getCropsPlanted();
        cropsHarvested += player.getCropsHarvested();
        emeraldsGained += player.getEmeraldsGained();
        emeraldsSpent += player.getEmeraldsSpent();
        flagCaptures += player.getFlagCaptures();
        playTimeMs += player.getPlayTimeMs();
        distanceCm += player.getDistanceCm();
        merge(blocksBrokenByCategory, player.getBlocksBrokenByCategory());
        merge(blocksPlacedByCategory, player.getBlocksPlacedByCategory());
        merge(itemsGained, player.getItemsGained());
        merge(itemsSpent, player.getItemsSpent());
    }

    private static void merge(Map<String, Long> target, Map<String, Long> source) {
        for (Map.Entry<String, Long> entry : source.entrySet()) {
            target.merge(entry.getKey(), entry.getValue(), Long::sum);
        }
    }

    public long getKills() {
        return kills;
    }

    public long getDeaths() {
        return deaths;
    }

    public long getMobKills() {
        return mobKills;
    }

    public long getBlocksBroken() {
        return blocksBroken;
    }

    public long getBlocksPlaced() {
        return blocksPlaced;
    }

    public long getCropsPlanted() {
        return cropsPlanted;
    }

    public long getCropsHarvested() {
        return cropsHarvested;
    }

    public long getEmeraldsGained() {
        return emeraldsGained;
    }

    public long getEmeraldsSpent() {
        return emeraldsSpent;
    }

    public long getFlagCaptures() {
        return flagCaptures;
    }

    public long getPlayTimeMs() {
        return playTimeMs;
    }

    public long getDistanceCm() {
        return distanceCm;
    }

    public int getPlayers() {
        return players;
    }

    public Map<String, Long> getBlocksBrokenByCategory() {
        return blocksBrokenByCategory;
    }

    public Map<String, Long> getBlocksPlacedByCategory() {
        return blocksPlacedByCategory;
    }

    public Map<String, Long> getItemsGained() {
        return itemsGained;
    }

    public Map<String, Long> getItemsSpent() {
        return itemsSpent;
    }
}
