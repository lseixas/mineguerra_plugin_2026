package org.lseixas.mineguerra_plugins.clientaudit;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

/**
 * Handshake de mods/packs do cliente Fabric.
 */
public final class ClientAuditRegistry {

    private static JavaPlugin plugin;
    private static ClientAllowlist allowlist;
    private static ClientAuditListener listener;

    private ClientAuditRegistry() {
    }

    public static void init(JavaPlugin plugin) {
        ClientAuditRegistry.plugin = plugin;
        plugin.saveResource("client-allowlist.yml", false);
        File file = new File(plugin.getDataFolder(), "client-allowlist.yml");
        allowlist = ClientAllowlist.fromConfig(
                org.bukkit.configuration.file.YamlConfiguration.loadConfiguration(file)
        );

        listener = new ClientAuditListener(plugin, allowlist);
        plugin.getServer().getPluginManager().registerEvents(listener, plugin);

        var messenger = plugin.getServer().getMessenger();
        messenger.registerIncomingPluginChannel(plugin, ClientAuditCodec.CHANNEL, listener);
        messenger.registerOutgoingPluginChannel(plugin, ClientAuditCodec.CHANNEL);

        if (allowlist.enabled()) {
            plugin.getLogger().info("Client audit ATIVO (exact, timeout " + allowlist.timeoutTicks() + " ticks).");
        } else {
            plugin.getLogger().info("Client audit desligado (enabled: false).");
        }
    }

    public static void shutdown() {
        if (listener != null) {
            listener.shutdown();
        }
        if (plugin != null) {
            var messenger = plugin.getServer().getMessenger();
            messenger.unregisterIncomingPluginChannel(plugin, ClientAuditCodec.CHANNEL);
            messenger.unregisterOutgoingPluginChannel(plugin, ClientAuditCodec.CHANNEL);
        }
    }

    public static ClientAllowlist allowlist() {
        return allowlist;
    }
}
