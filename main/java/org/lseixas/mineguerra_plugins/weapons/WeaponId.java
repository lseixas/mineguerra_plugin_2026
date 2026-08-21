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
                    "§7Forjado no Nether: canaliza chamas e a",
                    "§7corrupção do §5Wither§7 em cada flecha.",
                    "",
                    "§e§lATIVA §eHellfire Rain",
                    "§7Chove caveiras de Wither no ponto do tiro.",
                    "§7Dano em inimigos; não quebra blocos.",
                    "§8Agachar + F, próximo tiro · CD §c45s",
                    "",
                    "§5§lPASSIVA §5Dante's Punishment",
                    "§712% de chance: o hit ignora armadura e",
                    "§7ainda põe o alvo em chamas.",
                    "§8Rola por flecha (Multishot I = 3 flechas)"
            ),
            TraderType.NETHER
    ),

    DRAGON_SLAYER(
            Material.NETHERITE_SWORD,
            10002,
            "§5§lDragon Slayer",
            "§5§l[Dragon Slayer]",
            List.of(
                    "§5Lâmina do End: o fôlego do dragão e a",
                    "§5fúria quando a morte está perto.",
                    "",
                    "§d§lATIVA §dDragon's Breath",
                    "§7Sopro à frente que causa dano contínuo",
                    "§7nas entidades atingidas (canal curto).",
                    "§8Clique direito · CD §c35s",
                    "",
                    "§d§lPASSIVA §dRage of the Dragon",
                    "§7Com ≤10% de vida: Força, Pressa, Velocidade",
                    "§7e Regeneração IV por 8s.",
                    "§8Dispara sozinha · CD §c100s"
            ),
            TraderType.END
    ),

    STORM_RIDER(
            Material.TRIDENT,
            10003,
            "§b§lStorm Rider",
            "§b§l[Storm Rider]",
            List.of(
                    "§bTridente do oceano: raios no pouso,",
                    "§bsem mudar o clima do mundo.",
                    "",
                    "§e§lATIVA §eThunder Teleport",
                    "§7O arremesso te teleporta até o ponto de",
                    "§7impacto. Antes, 8 raios caem em círculo",
                    "§7(raio 5) — dano vanilla real.",
                    "§8Agachar + F, arremesso · CD §c25s"
            ),
            TraderType.OCEANO
    ),

    DOOM_HAMMER(
            Material.MACE,
            10004,
            "§6§lDoom Hammer",
            "§6§l[Doom Hammer]",
            List.of(
                    "§6Martelo das profundezas: impulso brutal",
                    "§6e queda sem medo.",
                    "",
                    "§e§lATIVA §ePower Jump",
                    "§7Te lança com força para frente/cima,",
                    "§7bom para gap close ou fuga.",
                    "§8Clique direito · CD §c80s",
                    "",
                    "§6§lPASSIVA §6Queda Blindada",
                    "§7Segurando o martelo na mão principal,",
                    "§7você não toma dano de queda."
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
