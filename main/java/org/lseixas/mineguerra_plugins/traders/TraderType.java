package org.lseixas.mineguerra_plugins.traders;

import org.bukkit.Location;
import org.bukkit.inventory.Merchant;

import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;

/**
 * Tipos de villagers/comerciantes do evento MineGuerra.
 */
public enum TraderType {

    OCEANO("oceano", "Explorador do Oceano", true),
    PROFUNDEZAS("profundezas", "Explorador das Profundezas", true),
    NETHER("nether", "Explorador do Nether", true),
    END("end", "Explorador do End", true),

    BIBLIOTECARIO("bibliotecatio", "Bibliotecário", false),
    MONSTROS("monstros", "Caçador de Monstros", false),
    ACOUGUEIRO("açougueiro", "Açougueiro", false),
    FERREIRO("ferreiro", "Ferreiro", false),
    ENGENHEIRO("engenheiro", "Engenheiro", false),
    PESCADOR("pescador", "Pescador", false),
    MINEIRO("mineiro", "Mineiro", false),
    FAZENDEIRO("fazendeiro", "Fazendeiro", false);

    private final String id;
    private final String displayName;
    private final boolean weaponExplorer;

    TraderType(String id, String displayName, boolean weaponExplorer) {
        this.id = id;
        this.displayName = displayName;
        this.weaponExplorer = weaponExplorer;
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isWeaponExplorer() {
        return weaponExplorer;
    }

    public void spawn(Location location) {
        VillagerSpawner.spawn(this, location);
    }

    public boolean configure(Merchant merchant) {
        return VillagerSpawner.configureMerchant(this, merchant);
    }

    public static Optional<TraderType> fromId(String rawId) {
        if (rawId == null || rawId.isBlank()) {
            return Optional.empty();
        }
        String normalized = rawId.toLowerCase(Locale.ROOT);
        return Arrays.stream(values())
                .filter(type -> type.id.equals(normalized))
                .findFirst();
    }

    public static String allIds() {
        StringBuilder builder = new StringBuilder();
        for (TraderType type : values()) {
            if (builder.length() > 0) {
                builder.append(", ");
            }
            builder.append(type.id);
        }
        return builder.toString();
    }
}
