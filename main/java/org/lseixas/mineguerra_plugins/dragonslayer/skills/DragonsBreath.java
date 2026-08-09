package org.lseixas.mineguerra_plugins.dragonslayer.skills;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.lseixas.mineguerra_plugins.weapons.AbilityCooldown;
import org.lseixas.mineguerra_plugins.weapons.VanillaCooldownSync;
import org.lseixas.mineguerra_plugins.weapons.WeaponId;
import org.lseixas.mineguerra_plugins.weapons.WeaponMessages;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class DragonsBreath {

    private static final String ABILITY_NAME = "Dragon's Breath";
    private static final long COOLDOWN_MS = 35_000;

    private final JavaPlugin plugin;
    private final AbilityCooldown cooldown = new AbilityCooldown(COOLDOWN_MS);

    public DragonsBreath(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean isBlockedByCooldown(Player player) {
        if (cooldown.isOnCooldown(player)) {
            WeaponMessages.sendCooldown(
                    player,
                    WeaponId.DRAGON_SLAYER,
                    ABILITY_NAME,
                    cooldown.getRemainingSeconds(player));
            return true;
        }
        cooldown.commit(player);
        VanillaCooldownSync.apply(player, WeaponId.DRAGON_SLAYER, COOLDOWN_MS);
        return false;
    }

    public void activateDragonsBreath(Player player) {
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1.5f, 0.8f);
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 60, 3, false, false, false));

        Particle.DustOptions darkPurple = new Particle.DustOptions(Color.fromRGB(100, 0, 150), 2.0f);
        Particle.DustOptions brightPurple = new Particle.DustOptions(Color.fromRGB(180, 0, 255), 1.5f);

        Set<UUID> recentlyHit = new HashSet<>();

        new BukkitRunnable() {
            int ticks = 0;
            final int DURATION = 60;

            @Override
            public void run() {
                if (ticks >= DURATION || !player.isOnline() || player.isDead()) {
                    recentlyHit.clear();
                    cancel();
                    return;
                }

                if (ticks % 10 == 0) {
                    player.getWorld().playSound(player.getLocation(), Sound.ENTITY_BLAZE_SHOOT, 0.5f, 0.6f);
                }

                Location eyeLoc = player.getEyeLocation();
                Vector direction = eyeLoc.getDirection().normalize();

                Vector right = direction.clone().getCrossProduct(new Vector(0, 1, 0)).normalize();
                if (right.lengthSquared() == 0) {
                    right = new Vector(1, 0, 0);
                }
                Vector up = right.clone().getCrossProduct(direction).normalize();

                for (double dist = 0.5; dist <= 5.0; dist += 0.5) {
                    double coneRadius = dist * 0.3;
                    int particles = (int) (5 + dist * 2);

                    for (int i = 0; i < particles; i++) {
                        double angle = Math.random() * 2 * Math.PI;
                        double radius = Math.sqrt(Math.random()) * coneRadius;

                        double offsetX = Math.cos(angle) * radius;
                        double offsetY = Math.sin(angle) * radius;

                        Location particleLoc = eyeLoc.clone()
                                .add(direction.clone().multiply(dist))
                                .add(right.clone().multiply(offsetX))
                                .add(up.clone().multiply(offsetY));

                        player.getWorld().spawnParticle(Particle.DUST, particleLoc, 1, 0, 0, 0, 0, darkPurple);

                        if (Math.random() > 0.3) {
                            player.getWorld().spawnParticle(Particle.DUST, particleLoc, 1, 0, 0, 0, 0, brightPurple);
                        }

                        if (Math.random() > 0.85) {
                            player.getWorld().spawnParticle(Particle.FLAME, particleLoc, 1, 0, 0, 0, 0.01);
                        }

                        damageEntitiesAt(player, particleLoc, recentlyHit);
                    }
                }

                if (ticks % 10 == 0) {
                    recentlyHit.clear();
                }

                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 1L);

        WeaponMessages.sendActivated(player, WeaponId.DRAGON_SLAYER, ABILITY_NAME);
    }

    private void damageEntitiesAt(Player shooter, Location loc, Set<UUID> recentlyHit) {
        for (Entity entity : loc.getWorld().getNearbyEntities(loc, 1.0, 1.0, 1.0)) {
            if (entity.equals(shooter)) {
                continue;
            }
            if (!(entity instanceof LivingEntity living)) {
                continue;
            }

            if (recentlyHit.contains(living.getUniqueId())) {
                continue;
            }

            living.damage(2.0, shooter);
            living.setFireTicks(100);
            recentlyHit.add(living.getUniqueId());

            living.getWorld().spawnParticle(Particle.LAVA, living.getEyeLocation(), 5, 0.2, 0.2, 0.2, 0.1);
            living.getWorld().playSound(living.getLocation(), Sound.ENTITY_GENERIC_BURN, 0.3f, 1.2f);
        }
    }
}
