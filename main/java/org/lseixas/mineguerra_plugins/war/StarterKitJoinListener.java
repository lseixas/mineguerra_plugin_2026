package org.lseixas.mineguerra_plugins.war;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Late joiners recebem o kit de abertura se a fase {@code inicio} já rodou.
 */
final class StarterKitJoinListener implements Listener {

    private final WarService warService;
    private final WarStateStore state;

    StarterKitJoinListener(WarService warService, WarStateStore state) {
        this.warService = warService;
        this.state = state;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event) {
        if (warService.giveStarterKitIfNeeded(event.getPlayer())) {
            state.save();
        }
    }
}
