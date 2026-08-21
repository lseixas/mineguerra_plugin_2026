package org.lseixas.mineguerra_plugins.world;

import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Desliga tick de fogo ({@code doFireTick}) em todos os mundos: o fogo não espalha
 * e não consome blocos sozinho. Reforço via {@link BlockIgniteEvent} causa SPREAD.
 */
public class FireSpreadGuard implements Listener {

    private final JavaPlugin plugin;

    public FireSpreadGuard(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void applyToAllWorlds() {
        for (World world : plugin.getServer().getWorlds()) {
            apply(world);
        }
    }

    private void apply(World world) {
        Boolean previous = world.getGameRuleValue(GameRule.DO_FIRE_TICK);
        if (Boolean.FALSE.equals(previous)) {
            return;
        }
        world.setGameRule(GameRule.DO_FIRE_TICK, false);
        plugin.getLogger().info("doFireTick=false em mundo " + world.getName());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onWorldLoad(WorldLoadEvent event) {
        apply(event.getWorld());
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onIgnite(BlockIgniteEvent event) {
        if (event.getCause() == BlockIgniteEvent.IgniteCause.SPREAD) {
            event.setCancelled(true);
        }
    }
}
