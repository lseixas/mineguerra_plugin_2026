package org.lseixas.mineguerra_plugins.traders;

import org.bukkit.inventory.Merchant;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class WeaponTraderRefreshListener implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void onInteract(PlayerInteractEntityEvent event) {
        if (!(event.getRightClicked() instanceof Merchant merchant)) {
            return;
        }

        TraderType type = TraderTypeDetector.fromMerchant(merchant);
        if (type == null || !type.isWeaponExplorer()) {
            return;
        }

        type.configure(merchant);
    }
}
