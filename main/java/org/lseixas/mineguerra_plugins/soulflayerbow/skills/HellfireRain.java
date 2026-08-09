package org.lseixas.mineguerra_plugins.soulflayerbow.skills;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.WitherSkull;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HellfireRain {

    private final JavaPlugin plugin;
    private final Random random = new Random();

    public HellfireRain(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void spawnHellfireRain(Location center) {
        center.getWorld().playSound(center, Sound.ENTITY_WITHER_AMBIENT, 1f, 0.5f);

        List<WitherSkull> skulls = new ArrayList<>();

        // Spawna as caveiras no céu
        new BukkitRunnable() {
            int count = 0;
            final int TOTAL_SKULLS = 10;

            @Override
            public void run() {
                if (count >= TOTAL_SKULLS) {
                    this.cancel();

                    // Após spawnar todas, lança elas para baixo
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            for (WitherSkull skull : skulls) {
                                if (skull.isValid() && !skull.isDead()) {
                                    launchSkull(skull);
                                    skull.getWorld().playSound(skull.getLocation(), Sound.ENTITY_GHAST_SHOOT, 0.5f, 0.5f);
                                }
                            }
                        }
                    }.runTaskLater(plugin, 15L);

                    return;
                }

                WitherSkull skull = spawnSingleSkull(center);
                skulls.add(skull);
                count++;
            }
        }.runTaskTimer(plugin, 0L, 4L);
    }

    private WitherSkull spawnSingleSkull(Location center) {
        // Spawna em posição aleatória no céu
        double offsetX = (random.nextDouble() * 20) - 10;
        double offsetZ = (random.nextDouble() * 20) - 10;

        Location spawnLoc = center.clone().add(offsetX, 20, offsetZ);

        WitherSkull skull = (WitherSkull) center.getWorld().spawnEntity(spawnLoc, EntityType.WITHER_SKULL);

        // Configurações para não explodir nem causar fogo
        skull.setIsIncendiary(false); // Remove fogo ao atingir
        skull.setYield(0F); // Remove explosão

        // Parado no ar inicialmente
        skull.setDirection(new Vector(0, 0, 0));
        skull.setVelocity(new Vector(0, 0, 0));

        return skull;
    }

    private void launchSkull(WitherSkull skull) {
        Vector direction = new Vector(0, -1, 0);
        skull.setDirection(direction);
        skull.setVelocity(direction.multiply(1.5));

        startParticleTrail(skull);
    }

    private void startParticleTrail(WitherSkull skull) {
        final Vector downVector = new Vector(0, -3.0, 0);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (skull.isDead() || !skull.isValid()) {
                    // Efeitos visuais ao atingir o chão (sem explosão/fogo)
                    skull.getWorld().spawnParticle(Particle.LAVA, skull.getLocation(), 20);
                    skull.getWorld().spawnParticle(Particle.FLAME, skull.getLocation(), 30, 1, 0.5, 1);
                    skull.getWorld().playSound(skull.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 0.5f, 0.8f);
                    this.cancel();
                    return;
                }

                skull.setVelocity(downVector);

                Location loc = skull.getLocation();

                // Trail de partículas
                skull.getWorld().spawnParticle(Particle.FLAME, loc, 5, 0.2, 0.2, 0.2, 0.05);
                skull.getWorld().spawnParticle(Particle.END_ROD, loc, 3, 0.1, 0.1, 0.1, 0.05);
                skull.getWorld().spawnParticle(Particle.LARGE_SMOKE, loc, 2, 0.1, 0.1, 0.1, 0.01);
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }
}