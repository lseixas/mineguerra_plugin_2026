package org.lseixas.mineguerra_plugins.weapons;

import org.bukkit.entity.Player;

/**
 * Espelha o cooldown da habilidade na barra vanilla do item na hotbar.
 */
public final class VanillaCooldownSync {

    private VanillaCooldownSync() {
    }

    public static void apply(Player player, WeaponId weapon, long durationMillis) {
        int ticks = (int) Math.max(1, durationMillis / 50L);
        player.setCooldown(weapon.getMaterial(), ticks);
    }
}
