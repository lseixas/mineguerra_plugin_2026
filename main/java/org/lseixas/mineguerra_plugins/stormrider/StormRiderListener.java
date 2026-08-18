package org.lseixas.mineguerra_plugins.stormrider;

import org.bukkit.entity.Player;
import org.bukkit.entity.Trident;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.lseixas.mineguerra_plugins.stormrider.skills.ThunderTeleport;
import org.lseixas.mineguerra_plugins.weapons.WeaponId;
import org.lseixas.mineguerra_plugins.weapons.WeaponMessages;
import org.lseixas.mineguerra_plugins.weapons.WeaponRegistry;

import java.util.*;

public class StormRiderListener implements Listener {

    private static final String TELEPORT_ABILITY = "Thunder Teleport";
    /** ~1 minute storm+thunder after a Storm Rider throw. */
    private static final int WEATHER_DURATION_TICKS = 1200;

    private final JavaPlugin plugin;
    private final ThunderTeleport thunderTeleport;
    private final Map<UUID, UUID> tridentOwners = new HashMap<>();
    private final Set<UUID> activeModeEnabled = new HashSet<>();

    public StormRiderListener(JavaPlugin plugin) {
        this.plugin = plugin;
        this.thunderTeleport = new ThunderTeleport(plugin);
    }

    @EventHandler
    public void onSwapHands(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();

        if (!player.isSneaking()) {
            return;
        }
        if (!WeaponRegistry.items().isInMainHand(player, WeaponId.STORM_RIDER)) {
            return;
        }

        event.setCancelled(true);

        UUID playerId = player.getUniqueId();
        WeaponId weapon = WeaponId.STORM_RIDER;

        if (activeModeEnabled.contains(playerId)) {
            activeModeEnabled.remove(playerId);
            WeaponMessages.sendModeDisabled(player, weapon, TELEPORT_ABILITY);
        } else {
            if (thunderTeleport.isOnCooldown(player)) {
                thunderTeleport.sendCooldownMessage(player);
                return;
            }

            activeModeEnabled.add(playerId);
            WeaponMessages.sendModeEnabled(player, weapon, TELEPORT_ABILITY, "Proximo arremesso teleporta.");
        }
    }

    @EventHandler
    public void onTridentLaunch(ProjectileLaunchEvent event) {
        if (!(event.getEntity() instanceof Trident trident)) {
            return;
        }
        if (!(trident.getShooter() instanceof Player player)) {
            return;
        }

        // Main-hand can already be empty after consume; also match thrown item meta.
        if (!isStormRiderTrident(player, trident)) {
            return;
        }

        UUID playerId = player.getUniqueId();
        if (!activeModeEnabled.contains(playerId)) {
            return;
        }

        boolean wasClear = !player.getWorld().hasStorm();

        player.getWorld().setStorm(true);
        player.getWorld().setWeatherDuration(WEATHER_DURATION_TICKS);
        player.getWorld().setThundering(true);
        player.getWorld().setThunderDuration(WEATHER_DURATION_TICKS);

        if (wasClear) {
            WeaponMessages.sendInfo(player, WeaponId.STORM_RIDER, "A tempestade foi invocada.");
        }

        tridentOwners.put(trident.getUniqueId(), playerId);
    }

    private boolean isStormRiderTrident(Player player, Trident trident) {
        if (WeaponRegistry.items().isInMainHand(player, WeaponId.STORM_RIDER)) {
            return true;
        }
        return WeaponRegistry.items().matches(trident.getItem(), WeaponId.STORM_RIDER);
    }

    @EventHandler
    public void onTridentHit(ProjectileHitEvent event) {
        if (!(event.getEntity() instanceof Trident trident)) {
            return;
        }

        UUID tridentId = trident.getUniqueId();
        if (!tridentOwners.containsKey(tridentId)) {
            return;
        }

        UUID playerId = tridentOwners.get(tridentId);
        Player player = plugin.getServer().getPlayer(playerId);

        if (player == null || !player.isOnline()) {
            tridentOwners.remove(tridentId);
            activeModeEnabled.remove(playerId);
            return;
        }

        if (activeModeEnabled.contains(playerId)) {
            thunderTeleport.activateThunderTeleport(player, trident);
            activeModeEnabled.remove(playerId);
        }

        tridentOwners.remove(tridentId);
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (WeaponRegistry.items().matches(event.getItemDrop().getItemStack(), WeaponId.STORM_RIDER)) {
            activeModeEnabled.remove(event.getPlayer().getUniqueId());
        }
    }
}
