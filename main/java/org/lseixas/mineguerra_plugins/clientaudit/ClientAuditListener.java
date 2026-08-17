package org.lseixas.mineguerra_plugins.clientaudit;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

/**
 * Recebe o handshake e kicka se a allowlist falhar ou o timeout estourar.
 */
public class ClientAuditListener implements Listener, PluginMessageListener {

    private final JavaPlugin plugin;
    private final ClientAllowlist allowlist;
    private final Map<UUID, BukkitTask> pending = new ConcurrentHashMap<>();

    public ClientAuditListener(JavaPlugin plugin, ClientAllowlist allowlist) {
        this.plugin = plugin;
        this.allowlist = allowlist;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (!allowlist.enabled()) {
            return;
        }
        Player player = event.getPlayer();
        if (hasBypass(player)) {
            return;
        }
        UUID id = player.getUniqueId();
        BukkitTask task = plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            pending.remove(id);
            Player online = plugin.getServer().getPlayer(id);
            if (online != null && online.isOnline()) {
                online.kickPlayer("§cInstale o mod MineGuerra Audit.");
            }
        }, allowlist.timeoutTicks());
        pending.put(id, task);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        cancelPending(event.getPlayer().getUniqueId());
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (!ClientAuditCodec.CHANNEL.equals(channel)) {
            return;
        }
        if (!allowlist.enabled()) {
            return;
        }
        if (hasBypass(player)) {
            cancelPending(player.getUniqueId());
            return;
        }

        try {
            ClientAuditPayload payload = ClientAuditCodec.decode(message);
            var reason = allowlist.rejectReason(payload);
            cancelPending(player.getUniqueId());
            if (reason.isPresent()) {
                player.kickPlayer("§c" + reason.get());
            }
        } catch (IOException ex) {
            plugin.getLogger().log(Level.WARNING, "Payload de auditoria invalido de " + player.getName(), ex);
            cancelPending(player.getUniqueId());
            player.kickPlayer("§cPayload de auditoria invalido.");
        }
    }

    public void shutdown() {
        for (BukkitTask task : pending.values()) {
            task.cancel();
        }
        pending.clear();
    }

    private boolean hasBypass(Player player) {
        String perm = allowlist.bypassPermission();
        return perm != null && !perm.isBlank() && player.hasPermission(perm);
    }

    private void cancelPending(UUID id) {
        BukkitTask task = pending.remove(id);
        if (task != null) {
            task.cancel();
        }
    }
}
