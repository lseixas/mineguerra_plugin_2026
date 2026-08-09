package org.lseixas.mineguerra_plugins.traders;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.Merchant;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class TraderToolListener implements Listener {

    private final TraderToolService traderToolService;

    public TraderToolListener(JavaPlugin plugin) {
        this.traderToolService = new TraderToolService(plugin);
    }

    public TraderToolService getTraderToolService() {
        return traderToolService;
    }

    @EventHandler
    public void onEntityInteract(PlayerInteractEntityEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        var typeOpt = traderToolService.getType(item);
        if (typeOpt.isEmpty()) {
            return;
        }

        if (!(event.getRightClicked() instanceof Merchant merchant)) {
            player.sendMessage("§c§l[MineGuerra] §7Clique em um villager ou wandering trader.");
            return;
        }

        event.setCancelled(true);

        TraderType type = typeOpt.get();
        if (VillagerSpawner.configureMerchant(type, merchant)) {
            player.sendMessage("§a§l[MineGuerra] §7Trades de §f" + type.getDisplayName() + " §7aplicadas.");
        } else {
            player.sendMessage("§c§l[MineGuerra] §7Nao foi possivel configurar este NPC para §f" + type.getId() + "§7.");
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        ItemStack item = event.getItem();
        var typeOpt = traderToolService.getType(item);
        if (typeOpt.isEmpty()) {
            return;
        }

        if (event.getAction() != org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null) {
            return;
        }

        event.setCancelled(true);

        Player player = event.getPlayer();
        TraderType type = typeOpt.get();

        org.bukkit.Location spawnLocation = resolveSpawnLocation(clickedBlock, player);
        type.spawn(spawnLocation);
        player.sendMessage("§a§l[MineGuerra] §7" + type.getDisplayName() + " §7spawnado.");
    }

    private org.bukkit.Location resolveSpawnLocation(Block clickedBlock, Player player) {
        org.bukkit.Location loc = clickedBlock.getRelative(BlockFace.UP).getLocation().add(0.5, 0, 0.5);
        loc.setYaw(player.getLocation().getYaw());
        loc.setPitch(0);
        return loc;
    }
}
