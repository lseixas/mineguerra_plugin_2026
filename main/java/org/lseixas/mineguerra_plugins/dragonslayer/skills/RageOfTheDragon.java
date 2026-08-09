package org.lseixas.mineguerra_plugins.dragonslayer.skills;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.lseixas.mineguerra_plugins.weapons.AbilityCooldown;
import org.lseixas.mineguerra_plugins.weapons.WeaponId;
import org.lseixas.mineguerra_plugins.weapons.WeaponMessages;

public class RageOfTheDragon {

    private static final String ABILITY_NAME = "Rage of the Dragon";
    private static final long COOLDOWN_MS = 90_000;

    private final AbilityCooldown cooldown = new AbilityCooldown(COOLDOWN_MS);

    public void activateRageOfTheDragon(Player player) {
        if (cooldown.isOnCooldown(player)) {
            return;
        }

        cooldown.commit(player);

        player.addPotionEffect(new PotionEffect(PotionEffectType.HASTE, 200, 3, false, false, true));
        player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 200, 3, false, false, true));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 200, 3, false, false, true));
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 200, 3, false, false, true));

        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1.5f, 0.8f);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_DEATH, 1.5f, 0.8f);

        WeaponMessages.sendActivated(player, WeaponId.DRAGON_SLAYER, ABILITY_NAME);
    }
}
