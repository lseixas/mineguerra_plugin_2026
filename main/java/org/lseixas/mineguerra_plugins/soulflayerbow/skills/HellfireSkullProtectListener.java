package org.lseixas.mineguerra_plugins.soulflayerbow.skills;

import org.bukkit.entity.WitherSkull;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;

/**
 * Paper/Spigot often still explode wither skulls even after {@code setYield(0)}.
 * Subclassing the NMS entity isn't available on the Spigot API we use, so we strip
 * <strong>block</strong> damage (and fire) from Hellfire Rain skulls at the Bukkit
 * event layer — without touching the explosion radius, so players still take damage.
 *
 * <p>Creative mode is unrelated: these skulls are spawned by the plugin, not fired
 * as a creative projectile.
 */
public class HellfireSkullProtectListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPrime(ExplosionPrimeEvent event) {
        if (!(event.getEntity() instanceof WitherSkull skull)) {
            return;
        }
        if (!HellfireRain.isHellfireSkull(skull)) {
            return;
        }
        // Só tira fogo. NÃO zerar o raio — isso remove o dano em jogadores.
        event.setFire(false);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onExplode(EntityExplodeEvent event) {
        if (!(event.getEntity() instanceof WitherSkull skull)) {
            return;
        }
        if (!HellfireRain.isHellfireSkull(skull)) {
            return;
        }
        // Limpa blocos; entidades continuam levando dano da explosão.
        event.blockList().clear();
        event.setYield(0F);
    }
}
