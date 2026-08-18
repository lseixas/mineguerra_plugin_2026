package org.lseixas.mineguerra_plugins.nobreak;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Interações do delimitador + preview de partículas enquanto a ferramenta está na mão.
 */
public class NoBreakToolListener implements Listener {

    private final JavaPlugin plugin;
    private final NoBreakToolService toolService;
    private final NoBreakZoneService zoneService;
    private final Map<UUID, Block> pendingCorner = new HashMap<>();
    private final Map<UUID, Block> pendingCorner2 = new HashMap<>();
    private BukkitTask particleTask;

    public NoBreakToolListener(JavaPlugin plugin, NoBreakToolService toolService, NoBreakZoneService zoneService) {
        this.plugin = plugin;
        this.toolService = toolService;
        this.zoneService = zoneService;
        startParticleTask();
    }

    public NoBreakToolService getToolService() {
        return toolService;
    }

    private void startParticleTask() {
        particleTask = plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            for (Player player : plugin.getServer().getOnlinePlayers()) {
                if (!toolService.isNoBreakTool(player.getInventory().getItemInMainHand())) {
                    continue;
                }
                if (!player.hasPermission("mineguerra.nobreak")) {
                    continue;
                }

                Block c1 = pendingCorner.get(player.getUniqueId());
                Block c2 = pendingCorner2.get(player.getUniqueId());
                if (c1 != null && c2 != null && c1.getWorld().equals(c2.getWorld())) {
                    NoBreakParticleHelper.drawSelection(
                            player,
                            Math.min(c1.getX(), c2.getX()),
                            Math.min(c1.getY(), c2.getY()),
                            Math.min(c1.getZ(), c2.getZ()),
                            Math.max(c1.getX(), c2.getX()),
                            Math.max(c1.getY(), c2.getY()),
                            Math.max(c1.getZ(), c2.getZ())
                    );
                } else if (c1 != null) {
                    NoBreakParticleHelper.drawSelection(
                            player, c1.getX(), c1.getY(), c1.getZ(), c1.getX(), c1.getY(), c1.getZ()
                    );
                }

                for (NoBreakZone zone : zoneService.all()) {
                    if (!zone.worldId().equals(player.getWorld().getUID())) {
                        continue;
                    }
                    if (distanceSqApprox(player, zone) > 96 * 96) {
                        continue;
                    }
                    NoBreakParticleHelper.drawExisting(player, zone);
                }
            }
        }, 10L, 10L);
    }

    private static double distanceSqApprox(Player player, NoBreakZone zone) {
        double cx = (zone.minX() + zone.maxX() + 1) / 2.0;
        double cy = (zone.minY() + zone.maxY() + 1) / 2.0;
        double cz = (zone.minZ() + zone.maxZ() + 1) / 2.0;
        double dx = player.getLocation().getX() - cx;
        double dy = player.getLocation().getY() - cy;
        double dz = player.getLocation().getZ() - cz;
        return dx * dx + dy * dy + dz * dz;
    }

    public void shutdown() {
        if (particleTask != null) {
            particleTask.cancel();
            particleTask = null;
        }
        pendingCorner.clear();
        pendingCorner2.clear();
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        UUID id = event.getPlayer().getUniqueId();
        pendingCorner.remove(id);
        pendingCorner2.remove(id);
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (toolService.isNoBreakTool(player.getInventory().getItemInMainHand())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        if (!toolService.isNoBreakTool(item)) {
            return;
        }

        event.setCancelled(true);

        if (!player.hasPermission("mineguerra.nobreak")) {
            player.sendMessage("§cVoce nao tem permissao (mineguerra.nobreak).");
            return;
        }

        Action action = event.getAction();
        boolean shift = player.isSneaking();

        if (action == Action.LEFT_CLICK_AIR) {
            clearSelection(player);
            player.sendMessage("§e§l[MineGuerra] §7Selecao no-break limpa.");
            return;
        }

        if (action == Action.LEFT_CLICK_BLOCK && shift) {
            Block block = event.getClickedBlock();
            if (block == null) {
                return;
            }
            Optional<NoBreakZone> at = zoneService.findAt(block);
            if (at.isEmpty()) {
                player.sendMessage("§c§l[MineGuerra] §7Nenhuma zona no-break neste bloco.");
                return;
            }
            NoBreakZone zone = at.get();
            zoneService.remove(zone);
            clearSelection(player);
            player.sendMessage("§a§l[MineGuerra] §7Zona removida: §f" + zone.id()
                    + " §7(" + zone.sizeLabel() + ")");
            return;
        }

        if (action == Action.RIGHT_CLICK_BLOCK) {
            Block block = event.getClickedBlock();
            if (block == null) {
                return;
            }

            if (shift) {
                confirmZone(player, block);
                return;
            }

            UUID id = player.getUniqueId();
            if (!pendingCorner.containsKey(id)) {
                pendingCorner.put(id, block);
                pendingCorner2.remove(id);
                player.sendMessage("§a§l[MineGuerra] §7Canto 1: §f"
                        + block.getX() + ", " + block.getY() + ", " + block.getZ());
                player.sendMessage("§7Clique direito em outro bloco para o canto 2.");
                return;
            }

            Block first = pendingCorner.get(id);
            if (!first.getWorld().equals(block.getWorld())) {
                player.sendMessage("§c§l[MineGuerra] §7Os cantos precisam estar no mesmo mundo.");
                return;
            }

            pendingCorner2.put(id, block);
            int minX = Math.min(first.getX(), block.getX());
            int minY = Math.min(first.getY(), block.getY());
            int minZ = Math.min(first.getZ(), block.getZ());
            int maxX = Math.max(first.getX(), block.getX());
            int maxY = Math.max(first.getY(), block.getY());
            int maxZ = Math.max(first.getZ(), block.getZ());
            long volume = (long) (maxX - minX + 1) * (maxY - minY + 1) * (maxZ - minZ + 1);

            player.sendMessage("§a§l[MineGuerra] §7Canto 2: §f"
                    + block.getX() + ", " + block.getY() + ", " + block.getZ());
            player.sendMessage("§7Area: §f" + (maxX - minX + 1) + "x" + (maxY - minY + 1)
                    + "x" + (maxZ - minZ + 1) + " §7(" + volume + " blocos)");
            player.sendMessage("§eShift + clique direito §7para confirmar a zona.");
            return;
        }

        if (action == Action.RIGHT_CLICK_AIR && shift) {
            Block target = player.getTargetBlockExact(8);
            if (target != null) {
                confirmZone(player, target);
            } else {
                player.sendMessage("§c§l[MineGuerra] §7Olhe para um bloco (ou use os dois cantos) para confirmar.");
            }
        }
    }

    private void confirmZone(Player player, Block fallbackCorner) {
        UUID id = player.getUniqueId();
        Block c1 = pendingCorner.get(id);
        Block c2 = pendingCorner2.get(id);

        if (c1 == null) {
            player.sendMessage("§c§l[MineGuerra] §7Marque o canto 1 com clique direito primeiro.");
            return;
        }
        if (c2 == null) {
            c2 = fallbackCorner;
            if (!c1.getWorld().equals(c2.getWorld())) {
                player.sendMessage("§c§l[MineGuerra] §7Os cantos precisam estar no mesmo mundo.");
                return;
            }
        }

        NoBreakZone zone = zoneService.create(c1, c2);
        clearSelection(player);
        NoBreakParticleHelper.flashConfirm(zone.world(), zone);
        player.sendMessage("§a§l[MineGuerra] §7Zona no-break criada: §f" + zone.id()
                + " §7(" + zone.sizeLabel() + ", " + zone.volumeBlocks() + " blocos)");
        player.sendMessage("§7Blocos nao vao quebrar dentro desta area (staff com perm ainda pode).");
    }

    private void clearSelection(Player player) {
        UUID id = player.getUniqueId();
        pendingCorner.remove(id);
        pendingCorner2.remove(id);
    }
}
