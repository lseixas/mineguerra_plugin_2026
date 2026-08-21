package org.lseixas.mineguerra_plugins.metrics;

import org.bukkit.Material;

/**
 * Buckets for block break/place aggregates in the snapshot.
 * Name-based heuristics only (no Bukkit {@code Tag}) so classification works
 * in unit tests without a live server.
 */
public enum BlockCategory {
    ORE,
    STONE,
    WOOD,
    DIRT,
    OTHER;

    public static BlockCategory of(Material material) {
        if (material == null) {
            return OTHER;
        }
        String name = material.name();
        if (name.contains("ORE") || name.equals("ANCIENT_DEBRIS") || name.equals("RAW_IRON_BLOCK")
                || name.equals("RAW_GOLD_BLOCK") || name.equals("RAW_COPPER_BLOCK")
                || name.equals("AMETHYST_CLUSTER") || name.equals("BUDDING_AMETHYST")) {
            return ORE;
        }
        if (name.contains("_LOG") || name.contains("_WOOD") || name.contains("STEM")
                || name.contains("HYPHAE") || name.contains("PLANKS") || name.endsWith("_SAPLING")) {
            return WOOD;
        }
        if (material == Material.DIRT || material == Material.GRASS_BLOCK || material == Material.COARSE_DIRT
                || material == Material.ROOTED_DIRT || material == Material.PODZOL
                || material == Material.MYCELIUM || material == Material.MUD
                || material == Material.FARMLAND || material == Material.DIRT_PATH
                || material == Material.SAND || material == Material.RED_SAND
                || material == Material.GRAVEL || material == Material.CLAY
                || material == Material.SOUL_SAND || material == Material.SOUL_SOIL) {
            return DIRT;
        }
        if (name.contains("STONE") || name.contains("DEEPSLATE") || name.contains("GRANITE")
                || name.contains("DIORITE") || name.contains("ANDESITE") || name.contains("TUFF")
                || name.contains("BLACKSTONE") || name.contains("BASALT") || name.contains("NETHERRACK")
                || name.contains("COBBLE") || name.contains("BRICK") || name.contains("PRISMARINE")
                || name.contains("END_STONE") || name.contains("TERRACOTTA") || name.contains("CONCRETE")) {
            return STONE;
        }
        return OTHER;
    }
}
