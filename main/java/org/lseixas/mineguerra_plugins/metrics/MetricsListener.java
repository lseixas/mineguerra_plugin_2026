package org.lseixas.mineguerra_plugins.metrics;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantInventory;
import org.lseixas.mineguerra_plugins.teams.TeamRegistry;
import org.lseixas.mineguerra_plugins.teams.TeamService;

/**
 * MONITOR listeners for gameplay metrics. Only records while the war metrics session is active.
 */
public final class MetricsListener implements Listener {

    private final MetricsService metrics;

    public MetricsListener(MetricsService metrics) {
        this.metrics = metrics;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (!metrics.isRecording()) {
            return;
        }
        Player victim = event.getEntity();
        Player killer = victim.getKiller();
        boolean validPvp = isValidPvpKill(killer, victim);
        metrics.recordDeath(victim, killer, validPvp);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDeath(EntityDeathEvent event) {
        if (!metrics.isRecording()) {
            return;
        }
        LivingEntity entity = event.getEntity();
        if (entity instanceof Player) {
            return;
        }
        Player killer = entity.getKiller();
        if (killer == null) {
            return;
        }
        metrics.recordMobKill(killer, entity.getType().name());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        if (!metrics.isRecording()) {
            return;
        }
        metrics.recordBlockBreak(event.getPlayer(), event.getBlock().getType());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (!metrics.isRecording()) {
            return;
        }
        metrics.recordBlockPlace(event.getPlayer(), event.getBlockPlaced().getType());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPickup(EntityPickupItemEvent event) {
        if (!metrics.isRecording()) {
            return;
        }
        Entity entity = event.getEntity();
        if (!(entity instanceof Player player)) {
            return;
        }
        ItemStack stack = event.getItem().getItemStack();
        metrics.recordItemGain(player, stack.getType(), stack.getAmount(), "pickup");
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onCraft(CraftItemEvent event) {
        if (!metrics.isRecording()) {
            return;
        }
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        ItemStack result = event.getCurrentItem();
        if (result == null || result.getType() == Material.AIR) {
            return;
        }
        int amount = result.getAmount();
        if (event.isShiftClick()) {
            // Approximate: shift-craft can produce multiple; use max craftable if available
            ItemStack cursorResult = event.getInventory().getResult();
            if (cursorResult != null) {
                amount = Math.max(amount, cursorResult.getAmount());
            }
        }
        metrics.recordItemGain(player, result.getType(), amount, "craft");
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onMerchantClick(InventoryClickEvent event) {
        if (!metrics.isRecording()) {
            return;
        }
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        if (!(event.getInventory() instanceof MerchantInventory merchant)) {
            return;
        }
        if (event.getSlotType() != InventoryType.SlotType.RESULT) {
            return;
        }
        if (event.getClick() == ClickType.NUMBER_KEY
                || event.getAction() == InventoryAction.NOTHING
                || event.getAction() == InventoryAction.UNKNOWN) {
            return;
        }

        ItemStack result = event.getCurrentItem();
        if (result == null || result.getType() == Material.AIR) {
            return;
        }

        var recipe = merchant.getSelectedRecipe();
        if (recipe != null) {
            for (ItemStack cost : recipe.getIngredients()) {
                if (cost != null && cost.getType() != Material.AIR && cost.getAmount() > 0) {
                    metrics.recordItemSpend(player, cost.getType(), cost.getAmount(), "trade");
                }
            }
        }

        metrics.recordItemGain(player, result.getType(), result.getAmount(), "trade");
    }

    private static boolean isValidPvpKill(Player killer, Player victim) {
        if (killer == null || killer.equals(victim)) {
            return false;
        }
        TeamService teamService = TeamRegistry.teams();
        String killerTeamId = teamService.getTeamId(killer);
        String victimTeamId = teamService.getTeamId(victim);
        if (killerTeamId == null || victimTeamId == null) {
            return false;
        }
        return !killerTeamId.equals(victimTeamId);
    }
}
