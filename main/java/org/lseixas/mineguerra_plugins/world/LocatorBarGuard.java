package org.lseixas.mineguerra_plugins.world;

import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Desliga a Locator Bar (bússola de jogadores na barra de XP, 1.21.6+).
 */
public class LocatorBarGuard implements Listener {

    private final JavaPlugin plugin;

    public LocatorBarGuard(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void applyToAllWorlds() {
        for (World world : plugin.getServer().getWorlds()) {
            apply(world);
        }
    }

    private void apply(World world) {
        Boolean previous = world.getGameRuleValue(GameRule.LOCATOR_BAR);
        if (Boolean.FALSE.equals(previous)) {
            return;
        }
        world.setGameRule(GameRule.LOCATOR_BAR, false);
        plugin.getLogger().info("locatorBar=false em mundo " + world.getName());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onWorldLoad(WorldLoadEvent event) {
        apply(event.getWorld());
    }
}
