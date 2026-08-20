package org.lseixas.mineguerra_plugins.teams.flag;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.lseixas.mineguerra_plugins.teams.TeamRegistry;

/**
 * Mantém o raio ao redor da bandeira limpo: ninguém constrói nem cava perto dela.
 *
 * <p>O bloco do banner em si continua sob as regras de {@link FlagBreakListener}
 * (aliado não quebra, inimigo captura).
 */
public class FlagAreaListener implements Listener {

    private static final String BYPASS_PERMISSION = "mineguerra.team";

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPlace(BlockPlaceEvent event) {
        if (isBlocked(event.getBlock(), event.getPlayer())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(
                    "§c§l[MineGuerra] §7Nao da para construir perto de uma bandeira.");
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onBreak(BlockBreakEvent event) {
        FlagAreaService area = TeamRegistry.flags().area();
        if (area.isFlagBlock(event.getBlock())) {
            return;
        }
        if (isBlocked(event.getBlock(), event.getPlayer())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(
                    "§c§l[MineGuerra] §7Nao da para cavar perto de uma bandeira.");
        }
    }

    private boolean isBlocked(Block block, Player player) {
        if (player.hasPermission(BYPASS_PERMISSION)) {
            return false;
        }
        return TeamRegistry.flags().area().isInProtectedArea(block);
    }
}
