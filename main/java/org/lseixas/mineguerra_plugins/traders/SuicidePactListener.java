package org.lseixas.mineguerra_plugins.traders;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Efeito do Peitoral do Pacto (trade do Trapaceiro): qualquer dano mata quem veste,
 * e se o dano veio de um jogador, esse jogador morre junto.
 *
 * <p>Necessário porque o vanilla faz clamp do fator de proteção entre 0 e 20, então
 * {@code Protection -9999} no item não tem efeito nenhum por si só.
 */
public class SuicidePactListener implements Listener {

    /** Alto o bastante para atravessar armadura, resistência e absorção. */
    private static final double PACT_DAMAGE = 10_000;

    private final JavaPlugin plugin;
    /** Evita reentrância: o dano do pacto dispara o mesmo evento novamente. */
    private final Set<UUID> resolving = new HashSet<>();

    public SuicidePactListener(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player victim)) {
            return;
        }
        if (!TrapaceiroItems.isPactChestplate(victim.getInventory().getChestplate())) {
            return;
        }
        if (victim.isDead() || resolving.contains(victim.getUniqueId())) {
            return;
        }

        Player attacker = event instanceof EntityDamageByEntityEvent byEntity
                ? resolveAttacker(byEntity.getDamager())
                : null;
        if (attacker != null && (attacker.equals(victim) || resolving.contains(attacker.getUniqueId()))) {
            attacker = null;
        }

        event.setDamage(0);
        triggerPact(victim, attacker);
    }

    private void triggerPact(Player victim, Player attacker) {
        resolving.add(victim.getUniqueId());
        if (attacker != null) {
            resolving.add(attacker.getUniqueId());
        }

        Player finalAttacker = attacker;
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            try {
                kill(victim, finalAttacker);
                if (finalAttacker != null) {
                    kill(finalAttacker, victim);
                    Bukkit.broadcastMessage("§4§l[Pacto] §f" + victim.getName()
                            + " §7levou §f" + finalAttacker.getName() + " §7junto.");
                }
            } finally {
                resolving.remove(victim.getUniqueId());
                if (finalAttacker != null) {
                    resolving.remove(finalAttacker.getUniqueId());
                }
            }
        });
    }

    private void kill(Player target, Player source) {
        if (target.isDead() || !target.isOnline()) {
            return;
        }
        target.setNoDamageTicks(0);
        if (source != null) {
            target.damage(PACT_DAMAGE, source);
        } else {
            target.damage(PACT_DAMAGE);
        }
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
