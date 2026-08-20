package org.lseixas.mineguerra_plugins.war;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.lseixas.mineguerra_plugins.teams.TeamRegistry;

/**
 * No hardcore, qualquer morte elimina de vez — não importa se a bandeira do time
 * ainda está de pé. Reaproveita a eliminação das bandeiras (spectator + revive).
 */
public class HardcoreDeathListener implements Listener {

    private final WarStateStore state;

    public HardcoreDeathListener(WarStateStore state) {
        this.state = state;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onDeath(PlayerDeathEvent event) {
        if (!state.isHardcore()) {
            return;
        }

        Player player = event.getEntity();
        if (TeamRegistry.flags().isEliminated(player)) {
            return;
        }

        TeamRegistry.flags().eliminatePlayer(player);
        TeamRegistry.weapons().rescanAll();
    }
}
