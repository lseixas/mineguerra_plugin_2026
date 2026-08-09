package org.lseixas.mineguerra_plugins.weapons;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantInventory;
import org.lseixas.mineguerra_plugins.teams.TeamRegistry;

import java.util.Iterator;
import java.util.Optional;

public class LegendaryWeaponListener implements Listener {

    private final WeaponItemService itemService;

    public LegendaryWeaponListener(WeaponItemService itemService) {
        this.itemService = itemService;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onMerchantTrade(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        if (!(event.getInventory() instanceof MerchantInventory)) {
            return;
        }
        if (event.getSlotType() != InventoryType.SlotType.RESULT) {
            return;
        }
        if (event.getRawSlot() != 2) {
            return;
        }
        if (event.getCurrentItem() == null || event.getCurrentItem().getType().isAir()) {
            return;
        }

        Optional<WeaponId> weaponId = itemService.identify(event.getCurrentItem());
        if (weaponId.isEmpty()) {
            return;
        }

        String teamId = TeamRegistry.teams().getTeamId(player);
        if (teamId == null) {
            return;
        }

        TeamRegistry.weapons().claim(weaponId.get(), teamId);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPickup(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        Optional<WeaponId> weaponId = itemService.identify(event.getItem().getItemStack());
        if (weaponId.isEmpty()) {
            return;
        }

        String teamId = TeamRegistry.teams().getTeamId(player);
        if (teamId == null) {
            return;
        }

        TeamRegistry.weapons().claim(weaponId.get(), teamId);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDrop(PlayerDropItemEvent event) {
        Optional<WeaponId> weaponId = itemService.identify(event.getItemDrop().getItemStack());
        if (weaponId.isPresent()) {
            TeamRegistry.leaderboard().refreshAll();
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();

        Iterator<ItemStack> dropIterator = event.getDrops().iterator();
        while (dropIterator.hasNext()) {
            ItemStack stack = dropIterator.next();
            if (itemService.identify(stack).isPresent()) {
                dropIterator.remove();
            }
        }

        itemService.stripFromPlayer(player);
        TeamRegistry.weapons().rescanAll();
    }
}
