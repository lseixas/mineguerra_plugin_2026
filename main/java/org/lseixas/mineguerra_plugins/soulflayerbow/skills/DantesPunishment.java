package org.lseixas.mineguerra_plugins.soulflayerbow.skills;

import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Random;

public class DantesPunishment {

    private final JavaPlugin plugin;
    private final Random random = new Random();

    public DantesPunishment(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void applyPurpleEffect(LivingEntity target) {
        Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromRGB(148, 0, 211), 1.5f);

        for (int i = 0; i < 20; i++) {
            double offsetX = (random.nextDouble() * 1.5) - 0.75;
            double offsetY = (random.nextDouble() * 2) + 0.5;
            double offsetZ = (random.nextDouble() * 1.5) - 0.75;

            target.getWorld().spawnParticle(Particle.DUST, target.getLocation().add(offsetX, offsetY, offsetZ), 5, dustOptions);
            target.getWorld().spawnParticle(Particle.DRAGON_BREATH, target.getLocation().add(offsetX, offsetY, offsetZ), 10, 0, 0, 0, 0.05);
        }
    }


}
