package org.lseixas.mineguerra_plugins.clientaudit;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.ArrayList;
import java.util.HexFormat;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

/**
 * Allowlist estrita (mode=exact) após filtrar mods internos do Fabric.
 */
public final class ClientAllowlist {

    private final boolean enabled;
    private final long timeoutTicks;
    private final String bypassPermission;
    private final String expectedMcVersion;
    private final Set<String> requiredMods;
    private final Set<String> allowedMods;
    private final Set<String> ignoredModIds;
    private final List<String> ignoredModIdPrefixes;
    private final Set<String> allowedPackSha1;
    private final Set<String> allowedShaders;
    private final List<String> bannedShaderSubstrings;

    public ClientAllowlist(
            boolean enabled,
            long timeoutTicks,
            String bypassPermission,
            String expectedMcVersion,
            Set<String> requiredMods,
            Set<String> allowedMods,
            Set<String> ignoredModIds,
            List<String> ignoredModIdPrefixes,
            Set<String> allowedPackSha1,
            Set<String> allowedShaders,
            List<String> bannedShaderSubstrings
    ) {
        this.enabled = enabled;
        this.timeoutTicks = timeoutTicks;
        this.bypassPermission = bypassPermission;
        this.expectedMcVersion = expectedMcVersion;
        this.requiredMods = Set.copyOf(requiredMods);
        this.allowedMods = Set.copyOf(allowedMods);
        this.ignoredModIds = Set.copyOf(ignoredModIds);
        this.ignoredModIdPrefixes = List.copyOf(ignoredModIdPrefixes);
        this.allowedPackSha1 = Set.copyOf(allowedPackSha1);
        this.allowedShaders = Set.copyOf(allowedShaders);
        this.bannedShaderSubstrings = List.copyOf(bannedShaderSubstrings);
    }

    public static ClientAllowlist fromConfig(FileConfiguration config) {
        return new ClientAllowlist(
                config.getBoolean("enabled", false),
                config.getLong("timeoutTicks", 100L),
                config.getString("bypassPermission", "mineguerra.admin"),
                config.getString("expectedMcVersion", "1.21.8"),
                new LinkedHashSet<>(config.getStringList("requiredMods")),
                new LinkedHashSet<>(config.getStringList("allowedMods")),
                new LinkedHashSet<>(config.getStringList("ignoredModIds")),
                config.getStringList("ignoredModIdPrefixes"),
                lowercaseSet(config.getStringList("allowedPackSha1")),
                lowercaseSet(config.getStringList("allowedShaders")),
                config.getStringList("bannedShaderSubstrings")
        );
    }

    public static ClientAllowlist fromYaml(String yaml) {
        return fromConfig(YamlConfiguration.loadConfiguration(new java.io.StringReader(yaml)));
    }

    public boolean enabled() {
        return enabled;
    }

    public long timeoutTicks() {
        return timeoutTicks;
    }

    public String bypassPermission() {
        return bypassPermission;
    }

    public Optional<String> rejectReason(ClientAuditPayload payload) {
        if (payload.protocol() != ClientAuditPayload.PROTOCOL) {
            return Optional.of("Protocolo de auditoria invalido.");
        }
        if (expectedMcVersion != null && !expectedMcVersion.isBlank()
                && !expectedMcVersion.equals(payload.mcVersion())) {
            return Optional.of("Versao de Minecraft nao permitida.");
        }

        Set<String> reported = new TreeSet<>();
        for (ClientAuditPayload.ModEntry mod : payload.mods()) {
            if (mod.id() == null || mod.id().isBlank()) {
                return Optional.of("Mod com id vazio.");
            }
            if (!isIgnored(mod.id())) {
                reported.add(mod.id());
            }
        }

        for (String required : requiredMods) {
            if (!reported.contains(required)) {
                return Optional.of("Mod obrigatorio ausente: " + required);
            }
        }

        Set<String> extra = new TreeSet<>(reported);
        extra.removeAll(allowedMods);
        if (!extra.isEmpty()) {
            return Optional.of("Mods nao permitidos: " + String.join(", ", extra));
        }

        Set<String> missing = new TreeSet<>(allowedMods);
        missing.removeAll(reported);
        if (!missing.isEmpty()) {
            return Optional.of("Faltam mods da allowlist: " + String.join(", ", missing));
        }

        for (ClientAuditPayload.PackEntry pack : payload.packs()) {
            if (isVanillaPack(pack.id())) {
                continue;
            }
            String hex = HexFormat.of().formatHex(pack.sha1() == null ? new byte[20] : pack.sha1())
                    .toLowerCase(Locale.ROOT);
            if (!allowedPackSha1.contains(hex)) {
                return Optional.of("Resource pack nao permitido: " + pack.id());
            }
        }

        String shader = payload.shader() == null ? "" : payload.shader().trim();
        if (!shader.isEmpty()) {
            String lower = shader.toLowerCase(Locale.ROOT);
            for (String banned : bannedShaderSubstrings) {
                if (!banned.isBlank() && lower.contains(banned.toLowerCase(Locale.ROOT))) {
                    return Optional.of("Shader nao permitido.");
                }
            }
            if (!allowedShaders.isEmpty() && !allowedShaders.contains(lower)) {
                return Optional.of("Shader nao permitido.");
            }
        }

        return Optional.empty();
    }

    boolean isIgnored(String modId) {
        if (ignoredModIds.contains(modId)) {
            return true;
        }
        for (String prefix : ignoredModIdPrefixes) {
            if (!prefix.isBlank() && modId.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }

    static boolean isVanillaPack(String packId) {
        if (packId == null || packId.isBlank()) {
            return true;
        }
        String id = packId.toLowerCase(Locale.ROOT);
        return id.equals("vanilla")
                || id.equals("server")
                || id.equals("minecraft")
                || id.startsWith("vanilla/")
                || id.startsWith("minecraft/");
    }

    private static Set<String> lowercaseSet(List<String> values) {
        Set<String> out = new LinkedHashSet<>();
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                out.add(value.toLowerCase(Locale.ROOT));
            }
        }
        return out;
    }

    public List<String> allowedModIds() {
        return new ArrayList<>(allowedMods);
    }
}
