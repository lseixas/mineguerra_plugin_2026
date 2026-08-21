package org.lseixas.mineguerra_plugins.metrics;

import org.bukkit.Material;

import java.util.EnumSet;
import java.util.Set;

/**
 * Valuable materials that emit {@code item_gain}/{@code item_spend} JSONL events.
 */
public final class TrackedMaterials {

    private static final Set<Material> TRACKED = EnumSet.of(
            Material.EMERALD,
            Material.EMERALD_BLOCK,
            Material.DIAMOND,
            Material.DIAMOND_BLOCK,
            Material.DIAMOND_ORE,
            Material.DEEPSLATE_DIAMOND_ORE,
            Material.GOLD_INGOT,
            Material.GOLD_BLOCK,
            Material.GOLD_NUGGET,
            Material.RAW_GOLD,
            Material.IRON_INGOT,
            Material.IRON_BLOCK,
            Material.RAW_IRON,
            Material.NETHERITE_INGOT,
            Material.NETHERITE_SCRAP,
            Material.NETHERITE_BLOCK,
            Material.ANCIENT_DEBRIS,
            Material.LAPIS_LAZULI,
            Material.LAPIS_BLOCK,
            Material.REDSTONE,
            Material.REDSTONE_BLOCK,
            Material.COAL,
            Material.COAL_BLOCK,
            Material.COPPER_INGOT,
            Material.RAW_COPPER,
            Material.QUARTZ,
            Material.AMETHYST_SHARD,
            Material.ECHO_SHARD,
            Material.TOTEM_OF_UNDYING,
            Material.ENCHANTED_GOLDEN_APPLE,
            Material.GOLDEN_APPLE,
            Material.NETHER_STAR,
            Material.ELYTRA,
            Material.SHULKER_SHELL,
            Material.DRAGON_EGG,
            Material.HEART_OF_THE_SEA,
            Material.NAUTILUS_SHELL,
            Material.TRIDENT,
            Material.WITHER_SKELETON_SKULL,
            Material.ENDER_PEARL,
            Material.BLAZE_ROD,
            Material.GHAST_TEAR,
            Material.GUNPOWDER,
            Material.ARROW,
            Material.SPECTRAL_ARROW,
            Material.TIPPED_ARROW,
            Material.OBSIDIAN,
            Material.CRYING_OBSIDIAN,
            Material.END_CRYSTAL
    );

    private TrackedMaterials() {
    }

    public static boolean isTracked(Material material) {
        return material != null && TRACKED.contains(material);
    }

    /** Converts blocks/ingots to emerald-equivalent units for the emeraldsGained counter. */
    public static int toEmeraldUnits(Material material, int amount) {
        if (material == Material.EMERALD) {
            return amount;
        }
        if (material == Material.EMERALD_BLOCK) {
            return amount * 9;
        }
        return 0;
    }
}
