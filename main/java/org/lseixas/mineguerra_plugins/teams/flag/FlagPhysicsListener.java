package org.lseixas.mineguerra_plugins.teams.flag;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.lseixas.mineguerra_plugins.teams.TeamRegistry;

/**
 * Mantém o banner da bandeira no lugar depois que a limpeza do raio removeu o
 * bloco de suporte. Sem isso o banner cai por física logo após o
 * {@code /team flag set}.
 */
public class FlagPhysicsListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPhysics(BlockPhysicsEvent event) {
        FlagAreaService area = TeamRegistry.flags().area();
        if (area.isFlagBlock(event.getBlock())) {
            event.setCancelled(true);
        }
    }
}
