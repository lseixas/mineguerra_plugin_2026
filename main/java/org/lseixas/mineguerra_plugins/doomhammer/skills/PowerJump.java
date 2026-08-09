package org.lseixas.mineguerra_plugins.doomhammer.skills;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.lseixas.mineguerra_plugins.weapons.AbilityCooldown;
import org.lseixas.mineguerra_plugins.weapons.PlayerFeedback;
import org.lseixas.mineguerra_plugins.weapons.VanillaCooldownSync;
import org.lseixas.mineguerra_plugins.weapons.WeaponId;
import org.lseixas.mineguerra_plugins.weapons.WeaponMessages;

public class PowerJump {

    private static final String ABILITY_NAME = "Power Jump";
    private static final long COOLDOWN_MS = 90_000;

    private final AbilityCooldown cooldown = new AbilityCooldown(COOLDOWN_MS);

    public void activatePowerJump(Player player) {
        if (cooldown.isOnCooldown(player)) {
            WeaponMessages.sendCooldown(player, WeaponId.DOOM_HAMMER, ABILITY_NAME, cooldown.getRemainingSeconds(player));
            return;
        }

        Vector velocity = player.getLocation().getDirection().multiply(0.8);
        velocity.setY(1.5);
        player.setVelocity(velocity);

        PlayerFeedback.playSound(player, Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1f, 1f);
        WeaponMessages.sendActivated(player, WeaponId.DOOM_HAMMER, ABILITY_NAME);

        cooldown.commit(player);
        VanillaCooldownSync.apply(player, WeaponId.DOOM_HAMMER, COOLDOWN_MS);
    }
}
