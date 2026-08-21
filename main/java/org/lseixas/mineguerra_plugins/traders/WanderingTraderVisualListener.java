package org.lseixas.mineguerra_plugins.traders;

import org.bukkit.ChatColor;
import org.bukkit.entity.WanderingTrader;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * Mantém exploradores/trapaceiro (WanderingTrader) visíveis: sem poção de invisibilidade.
 */
public class WanderingTraderVisualListener implements Listener {

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPotion(EntityPotionEffectEvent event) {
        if (!(event.getEntity() instanceof WanderingTrader trader)) {
            return;
        }
        if (!isMineGuerraWanderingTrader(trader)) {
            return;
        }
        if (event.getNewEffect() == null
                || event.getNewEffect().getType() != PotionEffectType.INVISIBILITY) {
            return;
        }
        event.setCancelled(true);
        trader.setInvisible(false);
    }

    /** Limpa invisibilidade residual ao abrir o trader (NPCs já spawnados). */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInteract(PlayerInteractEntityEvent event) {
        if (!(event.getRightClicked() instanceof WanderingTrader trader)) {
            return;
        }
        if (!isMineGuerraWanderingTrader(trader)) {
            return;
        }
        try {
            trader.getClass().getMethod("setCanDrinkPotion", boolean.class).invoke(trader, false);
            trader.getClass().getMethod("setCanDrinkMilk", boolean.class).invoke(trader, false);
        } catch (ReflectiveOperationException ignored) {
            // Spigot
        }
        for (PotionEffect effect : trader.getActivePotionEffects()) {
            trader.removePotionEffect(effect.getType());
        }
        trader.setInvisible(false);
    }

    static boolean isMineGuerraWanderingTrader(WanderingTrader trader) {
        String name = trader.getCustomName();
        if (name == null) {
            return false;
        }
        String plain = ChatColor.stripColor(name);
        for (TraderType type : TraderType.values()) {
            if (plain.equalsIgnoreCase(type.getDisplayName())
                    || plain.contains(type.getDisplayName())) {
                return true;
            }
        }
        return false;
    }
}
