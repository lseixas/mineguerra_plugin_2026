package org.lseixas.mineguerra_plugins.stormrider.skills;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.Trident;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.lseixas.mineguerra_plugins.weapons.AbilityCooldown;
import org.lseixas.mineguerra_plugins.weapons.VanillaCooldownSync;
import org.lseixas.mineguerra_plugins.weapons.WeaponId;
import org.lseixas.mineguerra_plugins.weapons.WeaponMessages;

import java.util.ArrayList;
import java.util.List;

public class ThunderTeleport {

    private static final String ABILITY_NAME = "Thunder Teleport";
    private static final long COOLDOWN_MS = 25_000;

    private final JavaPlugin plugin;
    private final AbilityCooldown cooldown = new AbilityCooldown(COOLDOWN_MS);

    public ThunderTeleport(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean isOnCooldown(Player player) {
        return cooldown.isOnCooldown(player);
    }

    public void sendCooldownMessage(Player player) {
        WeaponMessages.sendCooldown(player, WeaponId.STORM_RIDER, ABILITY_NAME, cooldown.getRemainingSeconds(player));
    }

    public void activateThunderTeleport(Player player, Trident trident) {
        Location tridentLocation = trident.getLocation();
        Location teleportLocation = tridentLocation.clone();

        while (teleportLocation.getBlock().isEmpty()
                && teleportLocation.getY() > teleportLocation.getWorld().getMinHeight()) {
            teleportLocation.subtract(0, 1, 0);
        }
        teleportLocation.add(0, 1, 0);

        spawnLightningCircle(teleportLocation);

        Location finalTeleportLocation = teleportLocation.clone();
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline()) {
                    return;
                }

                player.getWorld().spawnParticle(Particle.CLOUD, player.getLocation().add(0, 1, 0), 30, 0.5, 0.5, 0.5, 0.1);
                player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 0.5f);

                player.teleport(finalTeleportLocation);

                player.getWorld().spawnParticle(
                        Particle.ELECTRIC_SPARK,
                        finalTeleportLocation.clone().add(0, 1, 0),
                        50, 1, 1, 1, 0.2);
                player.getWorld().playSound(finalTeleportLocation, Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1.0f, 1.2f);
                player.getWorld().playSound(finalTeleportLocation, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 0.5f);

                WeaponMessages.sendActivated(player, WeaponId.STORM_RIDER, ABILITY_NAME);
            }
        }.runTaskLater(plugin, 30L);

        cooldown.commit(player);
        VanillaCooldownSync.apply(player, WeaponId.STORM_RIDER, COOLDOWN_MS);
    }

    private void spawnLightningCircle(Location center) {
        List<Location> lightningLocations = new ArrayList<>();
        int radius = 5;
        int numberOfBolts = 8;

        for (int i = 0; i < numberOfBolts; i++) {
            double angle = 2 * Math.PI * i / numberOfBolts;
            double x = center.getX() + radius * Math.cos(angle);
            double z = center.getZ() + radius * Math.sin(angle);

            Location loc = new Location(center.getWorld(), x, center.getY(), z);
            while (loc.getBlock().isEmpty() && loc.getY() > loc.getWorld().getMinHeight()) {
                loc.subtract(0, 1, 0);
            }
            loc.add(0, 1, 0);
            lightningLocations.add(loc);
        }

        new BukkitRunnable() {
            int counter = 0;

            @Override
            public void run() {
                if (counter >= lightningLocations.size()) {
                    cancel();
                    return;
                }

                Location loc = lightningLocations.get(counter);
                Location particleLoc = loc.clone().add(0, 10, 0);
                center.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, particleLoc, 20, 0.5, 5, 0.5, 0.1);
                center.getWorld().strikeLightning(loc);

                counter++;
            }
        }.runTaskTimer(plugin, 0L, 3L);
    }
}
