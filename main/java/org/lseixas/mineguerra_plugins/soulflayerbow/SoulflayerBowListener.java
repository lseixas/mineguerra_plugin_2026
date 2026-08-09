package org.lseixas.mineguerra_plugins.soulflayerbow;

import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.lseixas.mineguerra_plugins.soulflayerbow.skills.DantesPunishment;
import org.lseixas.mineguerra_plugins.soulflayerbow.skills.HellfireRain;
import org.lseixas.mineguerra_plugins.teams.TeamRegistry;
import org.lseixas.mineguerra_plugins.weapons.AbilityCooldown;
import org.lseixas.mineguerra_plugins.weapons.PlayerFeedback;
import org.lseixas.mineguerra_plugins.weapons.VanillaCooldownSync;
import org.lseixas.mineguerra_plugins.weapons.WeaponId;
import org.lseixas.mineguerra_plugins.weapons.WeaponMessages;
import org.lseixas.mineguerra_plugins.weapons.WeaponRegistry;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

public class SoulflayerBowListener implements Listener {

    private static final String HELLFIRE_ABILITY = "Hellfire Rain";
    private static final String DANTE_ABILITY = "Dante's Punishment";
    private static final long HELLFIRE_COOLDOWN_MS = 90_000;

    private final JavaPlugin plugin;
    private final AbilityCooldown hellfireCooldown = new AbilityCooldown(HELLFIRE_COOLDOWN_MS);
    private final Set<UUID> ultimateReady = new HashSet<>();
    private final Random random = new Random();
    private final double DANTE_CHANCE = 0.2;

    private final HellfireRain hellfireRain;
    private final DantesPunishment dantesPunishment;

    public SoulflayerBowListener(JavaPlugin plugin) {
        this.plugin = plugin;
        this.hellfireRain = new HellfireRain(plugin);
        this.dantesPunishment = new DantesPunishment(plugin);
    }

    @EventHandler
    public void onSwapHands(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();

        if (!player.isSneaking()) {
            return;
        }
        if (!WeaponRegistry.items().isInMainHand(player, WeaponId.SOULFLAYER_BOW)) {
            return;
        }

        event.setCancelled(true);

        UUID playerId = player.getUniqueId();
        WeaponId weapon = WeaponId.SOULFLAYER_BOW;

        if (ultimateReady.contains(playerId)) {
            ultimateReady.remove(playerId);
            WeaponMessages.sendModeDisabled(player, weapon, HELLFIRE_ABILITY);
            PlayerFeedback.playSound(player, Sound.UI_BUTTON_CLICK, 0.2f, 1.0f);
        } else {
            if (hellfireCooldown.isOnCooldown(player)) {
                WeaponMessages.sendCooldown(player, weapon, HELLFIRE_ABILITY, hellfireCooldown.getRemainingSeconds(player));
                return;
            }

            ultimateReady.add(playerId);
            WeaponMessages.sendModeEnabled(player, weapon, HELLFIRE_ABILITY, "Proxima flecha invoca a chuva infernal.");
            PlayerFeedback.playSound(player, Sound.BLOCK_BEACON_ACTIVATE, 1f, 2f);
        }
    }

    @EventHandler
    public void onShoot(EntityShootBowEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        if (!WeaponRegistry.items().isInMainHand(player, WeaponId.SOULFLAYER_BOW)) {
            return;
        }
        if (!(event.getProjectile() instanceof Arrow arrow)) {
            return;
        }

        arrow.setMetadata("is_dante_arrow", new FixedMetadataValue(plugin, true));

        if (ultimateReady.contains(player.getUniqueId())) {
            ultimateReady.remove(player.getUniqueId());
            hellfireCooldown.commit(player);
            VanillaCooldownSync.apply(player, WeaponId.SOULFLAYER_BOW, HELLFIRE_COOLDOWN_MS);

            arrow.setMetadata("is_ultimate_arrow", new FixedMetadataValue(plugin, true));
            arrow.setVelocity(arrow.getVelocity().multiply(2.5));
            arrow.setCritical(true);
            arrow.setFireTicks(100);

            WeaponMessages.sendActivated(player, WeaponId.SOULFLAYER_BOW, HELLFIRE_ABILITY);
            PlayerFeedback.playSound(player, Sound.ENTITY_GENERIC_EXPLODE, 1f, 1f);
        }
    }

    @EventHandler
    public void onArrowHit(ProjectileHitEvent event) {
        if (!(event.getEntity() instanceof Arrow arrow)) {
            return;
        }

        if (arrow.hasMetadata("is_ultimate_arrow") && arrow.getShooter() instanceof Player shooter) {
            hellfireRain.spawnHellfireRain(arrow.getLocation(), shooter);
        }
    }

    @EventHandler
    public void onHellfireSkullDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof WitherSkull skull)) {
            return;
        }
        if (!(skull.getShooter() instanceof Player shooter)) {
            return;
        }
        if (!(event.getEntity() instanceof Player victim)) {
            return;
        }

        String shooterTeam = TeamRegistry.teams().getTeamId(shooter);
        String victimTeam = TeamRegistry.teams().getTeamId(victim);
        if (shooterTeam != null && shooterTeam.equals(victimTeam)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onArrowDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Arrow arrow)) {
            return;
        }
        if (!(event.getEntity() instanceof LivingEntity target)) {
            return;
        }

        if (!arrow.hasMetadata("is_dante_arrow")) {
            return;
        }

        if (random.nextDouble() <= DANTE_CHANCE) {
            double rawDamage = event.getDamage();
            double health = target.getHealth() - rawDamage;
            if (health <= 0) {
                health = 0;
            }

            event.setDamage(0);
            target.setHealth(health);
            target.setFireTicks(100);
            dantesPunishment.applyPurpleEffect(target);
            target.getWorld().playSound(target.getLocation(), Sound.BLOCK_PORTAL_TRAVEL, 1f, 1f);

            if (arrow.getShooter() instanceof Player shooter) {
                WeaponMessages.sendActivated(shooter, WeaponId.SOULFLAYER_BOW, DANTE_ABILITY);
            }
        }
    }
}
