package org.lseixas.mineguerra_plugins.war;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

/**
 * Estado do evento entre reinícios: fases já aplicadas e chaves de gameplay.
 */
public class WarStateStore {

    private final JavaPlugin plugin;
    private final File file;

    private boolean running;
    private boolean pvpEnabled;
    private boolean hardcore;
    private final Set<WarPhase> appliedPhases = EnumSet.noneOf(WarPhase.class);
    private final List<UUID> trapaceiroEntityIds = new ArrayList<>();
    private final Set<UUID> starterKitReceived = new LinkedHashSet<>();

    public WarStateStore(JavaPlugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "war-state.yml");
    }

    public void load() {
        appliedPhases.clear();
        trapaceiroEntityIds.clear();
        starterKitReceived.clear();
        if (!file.exists()) {
            running = false;
            pvpEnabled = true;
            hardcore = false;
            return;
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        running = config.getBoolean("running", false);
        // Antes de qualquer fase o PvP segue vanilla; a fase INICIO é que desliga.
        pvpEnabled = config.getBoolean("pvpEnabled", true);
        hardcore = config.getBoolean("hardcore", false);

        for (String key : config.getStringList("appliedPhases")) {
            WarPhase.fromKey(key).ifPresent(appliedPhases::add);
        }
        for (String id : config.getStringList("trapaceiroEntityIds")) {
            try {
                trapaceiroEntityIds.add(UUID.fromString(id));
            } catch (IllegalArgumentException ignored) {
                // UUID inválido no YAML
            }
        }
        for (String id : config.getStringList("starterKitReceived")) {
            try {
                starterKitReceived.add(UUID.fromString(id));
            } catch (IllegalArgumentException ignored) {
                // UUID inválido no YAML
            }
        }
    }

    public void save() {
        if (!plugin.getDataFolder().exists() && !plugin.getDataFolder().mkdirs()) {
            plugin.getLogger().warning("Nao foi possivel criar pasta de dados do plugin.");
        }

        FileConfiguration config = new YamlConfiguration();
        config.set("running", running);
        config.set("pvpEnabled", pvpEnabled);
        config.set("hardcore", hardcore);
        config.set("appliedPhases", appliedPhases.stream().map(WarPhase::getConfigKey).toList());
        config.set(
                "trapaceiroEntityIds",
                trapaceiroEntityIds.stream().map(UUID::toString).toList());
        config.set(
                "starterKitReceived",
                starterKitReceived.stream().map(UUID::toString).toList());

        try {
            config.save(file);
        } catch (IOException ex) {
            plugin.getLogger().log(Level.SEVERE, "Falha ao salvar war-state.yml", ex);
        }
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public boolean isPvpEnabled() {
        return pvpEnabled;
    }

    public void setPvpEnabled(boolean pvpEnabled) {
        this.pvpEnabled = pvpEnabled;
    }

    public boolean isHardcore() {
        return hardcore;
    }

    public void setHardcore(boolean hardcore) {
        this.hardcore = hardcore;
    }

    public boolean isApplied(WarPhase phase) {
        return appliedPhases.contains(phase);
    }

    /** @return {@code true} se a fase ainda não tinha sido aplicada */
    public boolean markApplied(WarPhase phase) {
        return appliedPhases.add(phase);
    }

    public List<WarPhase> getAppliedPhases() {
        return List.copyOf(appliedPhases);
    }

    /** Última fase aplicada na ordem do cronograma. */
    public Optional<WarPhase> getCurrentPhase() {
        WarPhase current = null;
        for (WarPhase phase : WarPhase.values()) {
            if (appliedPhases.contains(phase)) {
                current = phase;
            }
        }
        return Optional.ofNullable(current);
    }

    public void clearAppliedPhases() {
        appliedPhases.clear();
    }

    public void rememberTrapaceiro(UUID uuid) {
        if (uuid != null && !trapaceiroEntityIds.contains(uuid)) {
            trapaceiroEntityIds.add(uuid);
        }
    }

    public List<UUID> getTrapaceiroEntityIds() {
        return List.copyOf(trapaceiroEntityIds);
    }

    public void clearTrapaceiroEntityIds() {
        trapaceiroEntityIds.clear();
    }

    /** @return {@code true} se o jogador ainda não tinha recebido o kit de abertura */
    public boolean markStarterKitReceived(UUID uuid) {
        return uuid != null && starterKitReceived.add(uuid);
    }

    public boolean hasReceivedStarterKit(UUID uuid) {
        return uuid != null && starterKitReceived.contains(uuid);
    }

    public void clearStarterKitReceived() {
        starterKitReceived.clear();
    }

    /** Volta o estado persistido ao ponto zero (antes de qualquer fase). */
    public void resetToDefaults() {
        running = false;
        pvpEnabled = true;
        hardcore = false;
        appliedPhases.clear();
        trapaceiroEntityIds.clear();
        starterKitReceived.clear();
    }
}
