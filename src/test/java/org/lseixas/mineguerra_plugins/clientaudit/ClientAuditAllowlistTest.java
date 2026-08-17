package org.lseixas.mineguerra_plugins.clientaudit;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ClientAuditAllowlistTest {

    private static final String YAML = """
            enabled: true
            timeoutTicks: 100
            bypassPermission: mineguerra.admin
            expectedMcVersion: "1.21.8"
            requiredMods:
              - mineguerra-client-audit
            allowedMods:
              - mineguerra-client-audit
              - sodium
              - iris
            ignoredModIds:
              - minecraft
              - java
              - fabricloader
            ignoredModIdPrefixes:
              - fabric-
            allowedPackSha1: []
            allowedShaders: []
            bannedShaderSubstrings:
              - xray
            """;

    @Test
    void codecRoundTrip() throws IOException {
        ClientAuditPayload original = new ClientAuditPayload(
                1,
                "1.21.8",
                "0.16.0",
                List.of(new ClientAuditPayload.ModEntry("sodium", "0.6.0")),
                List.of(new ClientAuditPayload.PackEntry("vanilla", new byte[20])),
                "Complementary"
        );
        byte[] encoded = ClientAuditCodec.encode(original);
        ClientAuditPayload decoded = ClientAuditCodec.decode(encoded);
        assertEquals(original.protocol(), decoded.protocol());
        assertEquals(original.mcVersion(), decoded.mcVersion());
        assertEquals(original.loaderVersion(), decoded.loaderVersion());
        assertEquals(original.mods(), decoded.mods());
        assertEquals(original.shader(), decoded.shader());
        assertEquals("vanilla", decoded.packs().getFirst().id());
        assertEquals(20, decoded.packs().getFirst().sha1().length);
    }

    @Test
    void exactMatchAcceptsFilteredFabricInternals() {
        ClientAllowlist allowlist = ClientAllowlist.fromYaml(YAML);
        var reason = allowlist.rejectReason(payload(
                List.of(
                        mod("minecraft", "1.21.8"),
                        mod("fabric-api", "0.1"),
                        mod("fabric-networking-api-v1", "0.1"),
                        mod("mineguerra-client-audit", "0.1"),
                        mod("sodium", "0.6"),
                        mod("iris", "1.8")
                ),
                List.of(),
                ""
        ));
        assertTrue(reason.isEmpty(), reason.orElse(""));
    }

    @Test
    void extraModIsRejected() {
        ClientAllowlist allowlist = ClientAllowlist.fromYaml(YAML);
        var reason = allowlist.rejectReason(payload(
                List.of(
                        mod("mineguerra-client-audit", "0.1"),
                        mod("sodium", "0.6"),
                        mod("iris", "1.8"),
                        mod("jade", "1.0")
                ),
                List.of(),
                ""
        ));
        assertTrue(reason.isPresent());
        assertTrue(reason.get().contains("jade"));
    }

    @Test
    void missingRequiredIsRejected() {
        ClientAllowlist allowlist = ClientAllowlist.fromYaml(YAML);
        var reason = allowlist.rejectReason(payload(
                List.of(mod("sodium", "0.6"), mod("iris", "1.8")),
                List.of(),
                ""
        ));
        assertTrue(reason.isPresent());
        assertTrue(reason.get().contains("mineguerra-client-audit"));
    }

    @Test
    void bannedShaderIsRejected() {
        ClientAllowlist allowlist = ClientAllowlist.fromYaml(YAML);
        var reason = allowlist.rejectReason(payload(
                List.of(
                        mod("mineguerra-client-audit", "0.1"),
                        mod("sodium", "0.6"),
                        mod("iris", "1.8")
                ),
                List.of(),
                "Xray Cave Finder"
        ));
        assertTrue(reason.isPresent());
        assertTrue(reason.get().toLowerCase().contains("shader"));
    }

    @Test
    void unknownPackHashIsRejected() {
        ClientAllowlist allowlist = ClientAllowlist.fromYaml(YAML);
        byte[] sha1 = HexFormat.of().parseHex("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        var reason = allowlist.rejectReason(payload(
                List.of(
                        mod("mineguerra-client-audit", "0.1"),
                        mod("sodium", "0.6"),
                        mod("iris", "1.8")
                ),
                List.of(new ClientAuditPayload.PackEntry("file/xray", sha1)),
                ""
        ));
        assertTrue(reason.isPresent());
        assertTrue(reason.get().toLowerCase().contains("pack"));
    }

    private static ClientAuditPayload payload(
            List<ClientAuditPayload.ModEntry> mods,
            List<ClientAuditPayload.PackEntry> packs,
            String shader
    ) {
        return new ClientAuditPayload(1, "1.21.8", "0.16.0", new ArrayList<>(mods), new ArrayList<>(packs), shader);
    }

    private static ClientAuditPayload.ModEntry mod(String id, String version) {
        return new ClientAuditPayload.ModEntry(id, version);
    }
}
