package org.lseixas.mineguerra_plugins.war;

import java.util.Locale;
import java.util.Optional;

/**
 * Fases do cronograma do evento, na ordem em que acontecem.
 *
 * <p>O {@code configKey} é a chave usada em {@code war-schedule.yml}.
 */
public enum WarPhase {

    INICIO("inicio", "Abertura", "§a§lA GUERRA COMECOU", "§7PvP off. Kit inicial entregue. Montem as bases."),
    PVP_ON("pvp-on", "PvP liberado", "§c§lPVP LIBERADO", "§7As bandeiras precisam estar postas."),
    TRAPACEIRO("trapaceiro", "Trapaceiro", "§6§lTRAPACEIRO CHEGOU", "§7Ele nao negocia de graca."),
    JULGAMENTO("julgamento", "Julgamento", "§e§lJULGAMENTO", "§7Contagem das bandeiras de pe."),
    HARDCORE("hardcore", "Hardcore", "§4§lHARDCORE", "§7Morrer agora e para sempre."),
    FECHAR_CENTRO("fechar-centro", "Centro fechando", "§c§lO CENTRO ESTA FECHANDO", "§7Corram para o meio.");

    private final String configKey;
    private final String displayName;
    private final String title;
    private final String subtitle;

    WarPhase(String configKey, String displayName, String title, String subtitle) {
        this.configKey = configKey;
        this.displayName = displayName;
        this.title = title;
        this.subtitle = subtitle;
    }

    public static Optional<WarPhase> fromKey(String key) {
        if (key == null) {
            return Optional.empty();
        }
        String normalized = key.trim().toLowerCase(Locale.ROOT);
        for (WarPhase phase : values()) {
            if (phase.configKey.equals(normalized) || phase.name().toLowerCase(Locale.ROOT).equals(normalized)) {
                return Optional.of(phase);
            }
        }
        return Optional.empty();
    }

    public String getConfigKey() {
        return configKey;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }
}
