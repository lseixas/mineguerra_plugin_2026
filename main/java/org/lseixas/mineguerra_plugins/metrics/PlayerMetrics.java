package org.lseixas.mineguerra_plugins.metrics;

import java.util.HashMap;
import java.util.Map;

/**
 * Mutable per-player counters. Safe for single-writer (main thread) use.
 */
public final class PlayerMetrics {

    private String name = "";
    private String teamId;
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
    private final Map<String, Long> blocksBrokenByCategory = new HashMap<>();
    private final Map<String, Long> blocksPlacedByCategory = new HashMap<>();
    private final Map<String, Long> itemsGained = new HashMap<>();
    private final Map<String, Long> itemsSpent = new HashMap<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name != null) {
            this.name = name;
        }
    }

    public String getTeamId() {
        return teamId;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
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

    public void setPlayTimeMs(long playTimeMs) {
        this.playTimeMs = Math.max(0L, playTimeMs);
    }

    public long getDistanceCm() {
        return distanceCm;
    }

    public void setDistanceCm(long distanceCm) {
        this.distanceCm = Math.max(0L, distanceCm);
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

    public void addKill(long amount) {
        kills += amount;
    }

    public void addDeath(long amount) {
        deaths += amount;
    }

    public void addMobKill(long amount) {
        mobKills += amount;
    }

    public void addBlockBroken(BlockCategory category, long amount) {
        blocksBroken += amount;
        blocksBrokenByCategory.merge(category.name(), amount, Long::sum);
    }

    public void addBlockPlaced(BlockCategory category, long amount) {
        blocksPlaced += amount;
        blocksPlacedByCategory.merge(category.name(), amount, Long::sum);
    }

    public void addCropPlanted(long amount) {
        cropsPlanted += amount;
    }

    public void addCropHarvested(long amount) {
        cropsHarvested += amount;
    }

    public void addEmeraldsGained(long amount) {
        emeraldsGained += amount;
    }

    public void addEmeraldsSpent(long amount) {
        emeraldsSpent += amount;
    }

    public void addFlagCapture(long amount) {
        flagCaptures += amount;
    }

    public void addItemGained(String material, long amount) {
        itemsGained.merge(material, amount, Long::sum);
    }

    public void addItemSpent(String material, long amount) {
        itemsSpent.merge(material, amount, Long::sum);
    }

    public PlayerMetrics copy() {
        PlayerMetrics copy = new PlayerMetrics();
        copy.name = name;
        copy.teamId = teamId;
        copy.kills = kills;
        copy.deaths = deaths;
        copy.mobKills = mobKills;
        copy.blocksBroken = blocksBroken;
        copy.blocksPlaced = blocksPlaced;
        copy.cropsPlanted = cropsPlanted;
        copy.cropsHarvested = cropsHarvested;
        copy.emeraldsGained = emeraldsGained;
        copy.emeraldsSpent = emeraldsSpent;
        copy.flagCaptures = flagCaptures;
        copy.playTimeMs = playTimeMs;
        copy.distanceCm = distanceCm;
        copy.blocksBrokenByCategory.putAll(blocksBrokenByCategory);
        copy.blocksPlacedByCategory.putAll(blocksPlacedByCategory);
        copy.itemsGained.putAll(itemsGained);
        copy.itemsSpent.putAll(itemsSpent);
        return copy;
    }

    /** Delta of high-volume counters since {@code baseline} (for tick_delta). */
    public Map<String, Long> deltaFrom(PlayerMetrics baseline) {
        Map<String, Long> delta = new HashMap<>();
        putIfPositive(delta, "kills", kills - baseline.kills);
        putIfPositive(delta, "deaths", deaths - baseline.deaths);
        putIfPositive(delta, "mobKills", mobKills - baseline.mobKills);
        putIfPositive(delta, "blocksBroken", blocksBroken - baseline.blocksBroken);
        putIfPositive(delta, "blocksPlaced", blocksPlaced - baseline.blocksPlaced);
        putIfPositive(delta, "cropsPlanted", cropsPlanted - baseline.cropsPlanted);
        putIfPositive(delta, "cropsHarvested", cropsHarvested - baseline.cropsHarvested);
        putIfPositive(delta, "emeraldsGained", emeraldsGained - baseline.emeraldsGained);
        putIfPositive(delta, "emeraldsSpent", emeraldsSpent - baseline.emeraldsSpent);
        putIfPositive(delta, "flagCaptures", flagCaptures - baseline.flagCaptures);
        return delta;
    }

    private static void putIfPositive(Map<String, Long> map, String key, long value) {
        if (value > 0) {
            map.put(key, value);
        }
    }
}
