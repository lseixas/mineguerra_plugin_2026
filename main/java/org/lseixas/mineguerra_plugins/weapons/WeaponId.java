package org.lseixas.mineguerra_plugins.weapons;

import org.bukkit.Material;
import org.lseixas.mineguerra_plugins.traders.TraderType;

import java.util.List;
import java.util.Map;

/**
 * Identidade canônica de cada arma customizada do evento.
 * Lore de CD/chance deve bater com docs/BALANCE.md e skills.
 */
public enum WeaponId {

    SOULFLAYER_BOW(
            Material.BOW,
            10001,
            "§7§lSoulflayer Bow",
            "§7§l[Soulflayer Bow]",
            List.of(
                    "§7Forjado no calor do Nether, este arco canaliza",
                    "§7chamas ancestrais e a corrupção do §5Wither§7.",
                    "§7Cada flecha pode ruir como um sopro infernal.",
                    "",
                    "§eHellfire Rain §7— Agachar + F, próximo tiro",
                    "§7Cooldown: §c75s",
                    "§5Dante's Punishment §7— 12% ao acertar flecha",
                    "§8Multishot I §7— 3 flechas por disparo"
            ),
            TraderType.NETHER
    ),

    DRAGON_SLAYER(
            Material.NETHERITE_SWORD,
            10002,
            "§5§lDragon Slayer",
            "§5§l[Dragon Slayer]",
            List.of(
                    "§5Empunhada com o fogo ancestral dos dragões,",
                    "§5esta lâmina libera o §dDragon's Breath§5",
                    "§5e desperta o §dRage of the Dragon§5.",
                    "",
                    "§dDragon's Breath §7— Clique direito",
                    "§7Cooldown: §c35s",
                    "§dRage of the Dragon §7— ≤10% vida (passiva)",
                    "§7Cooldown: §c100s §8(8s de efeitos)"
            ),
            TraderType.END
    ),

    STORM_RIDER(
            Material.TRIDENT,
            10003,
            "§b§lStorm Rider",
            "§b§l[Storm Rider]",
            List.of(
                    "§7Tridente forjado nas tempestades do oceano.",
                    "§7Canaliza raios e o furor do mar.",
                    "",
                    "§eThunder Teleport §7— Agachar + F, arremesso",
                    "§7Cooldown: §c25s",
                    "§8(8 raios reais caem ao redor do pouso)"
            ),
            TraderType.OCEANO
    ),

    DOOM_HAMMER(
            Material.MACE,
            10004,
            "§6§lDoom Hammer",
            "§6§l[Doom Hammer]",
            List.of(
                    "§7Martelo forjado nas profundezas da terra.",
                    "§7O impacto ecoa como trovão nas cavernas.",
                    "",
                    "§ePower Jump §7— Clique direito",
                    "§7Cooldown: §c80s",
                    "§7Passiva: imunidade a dano de queda"
            ),
            TraderType.PROFUNDEZAS
    );

    private final Material material;
    private final int customModelData;
    private final String displayName;
    private final String messagePrefix;
    private final List<String> loreLines;
    private final TraderType explorerTraderType;

    WeaponId(
            Material material,
            int customModelData,
            String displayName,
            String messagePrefix,
            List<String> loreLines,
            TraderType explorerTraderType
    ) {
        this.material = material;
        this.customModelData = customModelData;
        this.displayName = displayName;
        this.messagePrefix = messagePrefix;
        this.loreLines = loreLines;
        this.explorerTraderType = explorerTraderType;
    }

    public static WeaponId forExplorer(TraderType type) {
        for (WeaponId id : values()) {
            if (id.explorerTraderType == type) {
                return id;
            }
        }
        return null;
    }

    public Material getMaterial() {
        return material;
    }

    public int getCustomModelData() {
        return customModelData;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getMessagePrefix() {
        return messagePrefix;
    }

    public List<String> getLoreLines() {
        return loreLines;
    }

    public TraderType getExplorerTraderType() {
        return explorerTraderType;
    }

    /**
     * Enchantments aplicados na criação do item, por chave vanilla (namespace minecraft).
     * Chaves em vez de {@link org.bukkit.enchantments.Enchantment} para não tocar o
     * registry do Bukkit durante a inicialização deste enum.
     */
    public Map<String, Integer> getDefaultEnchantments() {
        return switch (this) {
            case SOULFLAYER_BOW -> Map.of("multishot", 1);
            default -> Map.of();
        };
    }

    public String getShortName() {
        return displayName.replaceAll("§.", "");
    }
}
