package org.lseixas.mineguerra_plugins.clientaudit;

import java.util.List;

/**
 * Payload v1 do canal {@code mineguerra:client_audit}.
 */
public record ClientAuditPayload(
        int protocol,
        String mcVersion,
        String loaderVersion,
        List<ModEntry> mods,
        List<PackEntry> packs,
        String shader
) {

    public static final int PROTOCOL = 1;

    public record ModEntry(String id, String version) {
    }

    public record PackEntry(String id, byte[] sha1) {
    }
}
