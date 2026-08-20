package org.lseixas.mineguerra_plugins.war;

import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PotionSplashEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Bloqueia dano entre jogadores enquanto o PvP do evento está desligado.
 */
public class PvpToggleListener implements Listener {

    private static final long MESSAGE_COOLDOWN_MS = 3_000;

    private final WarStateStore state;
    private final Map<UUID, Long> lastMessage = new HashMap<>();

    public PvpToggleListener(WarStateStore state) {
        this.state = state;
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onDamage(EntityDamageByEntityEvent event) {
        if (state.isPvpEnabled()) {
            return;
        }
        if (!(event.getEntity() instanceof Player victim)) {
            return;
        }

        Player attacker = resolveAttacker(event.getDamager());
        if (attacker == null || attacker.equals(victim)) {
            return;
        }

        event.setCancelled(true);
        warn(attacker);
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPotionSplash(PotionSplashEvent event) {
        if (state.isPvpEnabled()) {
            return;
        }
        if (!(event.getPotion().getShooter() instanceof Player thrower)) {
            return;
        }
        for (var affected : event.getAffectedEntities()) {
            if (affected instanceof Player target && !target.equals(thrower)) {
                event.setIntensity(target, 0);
            }
        }
    }

    private void warn(Player player) {
        long now = System.currentTimeMillis();
        Long previous = lastMessage.get(player.getUniqueId());
        if (previous != null && now - previous < MESSAGE_COOLDOWN_MS) {
            return;
        }
        lastMessage.put(player.getUniqueId(), now);
        player.sendMessage("§c§l[MineGuerra] §7PvP ainda esta desligado.");
    }

    private Player resolveAttacker(org.bukkit.entity.Entity damager) {
        if (damager instanceof Player player) {
            return player;
        }
        if (damager instanceof Projectile projectile && projectile.getShooter() instanceof Player shooter) {
            return shooter;
        }
        return null;
    }
}
