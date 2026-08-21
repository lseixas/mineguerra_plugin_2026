package org.lseixas.mineguerra_plugins.metrics;

import org.bukkit.Material;

import java.util.EnumSet;
import java.util.Set;

public final class CropMaterials {

    private static final Set<Material> PLANTABLE = EnumSet.of(
            Material.WHEAT_SEEDS,
            Material.BEETROOT_SEEDS,
            Material.PUMPKIN_SEEDS,
            Material.MELON_SEEDS,
            Material.TORCHFLOWER_SEEDS,
            Material.PITCHER_POD,
            Material.CARROT,
            Material.POTATO,
            Material.NETHER_WART,
            Material.COCOA_BEANS,
            Material.SUGAR_CANE,
            Material.BAMBOO,
            Material.CACTUS,
            Material.KELP,
            Material.SWEET_BERRIES,
            Material.GLOW_BERRIES,
            Material.CHORUS_FLOWER
    );

    private static final Set<Material> HARVESTABLE = EnumSet.of(
            Material.WHEAT,
            Material.BEETROOTS,
            Material.POTATOES,
            Material.CARROTS,
            Material.NETHER_WART,
            Material.COCOA,
            Material.SUGAR_CANE,
            Material.BAMBOO,
            Material.CACTUS,
            Material.KELP,
            Material.KELP_PLANT,
            Material.SWEET_BERRY_BUSH,
            Material.CAVE_VINES,
            Material.CAVE_VINES_PLANT,
            Material.PUMPKIN,
            Material.MELON,
            Material.TORCHFLOWER,
            Material.PITCHER_CROP,
            Material.CHORUS_PLANT,
            Material.CHORUS_FLOWER
    );

    private CropMaterials() {
    }

    public static boolean isPlantable(Material material) {
        return material != null && PLANTABLE.contains(material);
    }

    public static boolean isHarvestable(Material material) {
        return material != null && HARVESTABLE.contains(material);
    }
}
