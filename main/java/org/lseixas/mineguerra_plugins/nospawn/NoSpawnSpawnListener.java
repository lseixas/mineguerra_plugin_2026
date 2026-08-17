package org.lseixas.mineguerra_plugins.nospawn;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

/**
 * Cancela spawn de criaturas dentro de zonas no-spawn.
 */
public class NoSpawnSpawnListener implements Listener {

    private final NoSpawnZoneService zoneService;

    public NoSpawnSpawnListener(NoSpawnZoneService zoneService) {
        this.zoneService = zoneService;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        CreatureSpawnEvent.SpawnReason reason = event.getSpawnReason();
        if (isAllowedReason(reason)) {
            return;
        }
        if (zoneService.isNoSpawn(event.getLocation())) {
            event.setCancelled(true);
        }
    }

    private static boolean isAllowedReason(CreatureSpawnEvent.SpawnReason reason) {
        return switch (reason) {
            case CUSTOM, COMMAND, SPAWNER_EGG -> true;
            default -> false;
        };
    }
}
