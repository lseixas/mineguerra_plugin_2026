package org.lseixas.mineguerra_plugins.teams;

import org.bukkit.ChatColor;

/**
 * Definição imutável de um time do evento.
 */
public class TeamDefinition {

    private final String id;
    private final String displayName;
    private final ChatColor color;
    private final String prefix;

    public TeamDefinition(String id, String displayName, ChatColor color, String prefix) {
        this.id = id;
        this.displayName = displayName;
        this.color = color;
        this.prefix = prefix;
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public ChatColor getColor() {
        return color;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getColoredPrefix() {
        return color + prefix;
    }
}
