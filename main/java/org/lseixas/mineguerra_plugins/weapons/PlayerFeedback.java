package org.lseixas.mineguerra_plugins.weapons;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

/**
 * Atalhos para feedback sonoro/visual comum.
 */
public final class PlayerFeedback {

    private PlayerFeedback() {
    }

    public static void playSound(Player player, Sound sound, float volume, float pitch) {
        player.playSound(player.getLocation(), sound, volume, pitch);
    }

    public static void playSoundAt(Location location, Sound sound, float volume, float pitch) {
        location.getWorld().playSound(location, sound, volume, pitch);
    }
}
