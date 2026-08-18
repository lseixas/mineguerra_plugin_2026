package org.lseixas.mineguerra_plugins.nobreak;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;

/**
 * Persistência YAML das zonas no-break.
 */
public class NoBreakZoneStore {

    private final JavaPlugin plugin;
    private final File file;
    private final Map<String, NoBreakZone> zones = new LinkedHashMap<>();

    public NoBreakZoneStore(JavaPlugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "nobreak-zones.yml");
    }

    public void load() {
        zones.clear();
        if (!file.exists()) {
            return;
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection section = config.getConfigurationSection("zones");
        if (section == null) {
            return;
        }

        for (String id : section.getKeys(false)) {
            String path = "zones." + id;
            String worldName = config.getString(path + ".world");
            String worldUuidStr = config.getString(path + ".worldUuid");
            List<Integer> min = config.getIntegerList(path + ".min");
            List<Integer> max = config.getIntegerList(path + ".max");

            if (worldName == null || worldUuidStr == null || min.size() != 3 || max.size() != 3) {
                plugin.getLogger().warning("Zona no-break invalida ignorada: " + id);
                continue;
            }

            try {
                UUID worldId = UUID.fromString(worldUuidStr);
                zones.put(id, new NoBreakZone(
                        id,
                        worldId,
                        worldName,
                        min.get(0), min.get(1), min.get(2),
                        max.get(0), max.get(1), max.get(2)
                ));
            } catch (IllegalArgumentException ex) {
                plugin.getLogger().warning("UUID de mundo invalido na zona no-break: " + id);
            }
        }
    }

    public void save() {
        if (!plugin.getDataFolder().exists() && !plugin.getDataFolder().mkdirs()) {
            plugin.getLogger().warning("Nao foi possivel criar pasta de dados do plugin.");
            return;
        }

        FileConfiguration config = new YamlConfiguration();
        for (NoBreakZone zone : zones.values()) {
            String path = "zones." + zone.id();
            config.set(path + ".world", zone.worldName());
            config.set(path + ".worldUuid", zone.worldId().toString());
            config.set(path + ".min", List.of(zone.minX(), zone.minY(), zone.minZ()));
            config.set(path + ".max", List.of(zone.maxX(), zone.maxY(), zone.maxZ()));
        }

        try {
            config.save(file);
        } catch (IOException ex) {
            plugin.getLogger().log(Level.SEVERE, "Falha ao salvar nobreak-zones.yml", ex);
        }
    }

    public Collection<NoBreakZone> all() {
        return List.copyOf(zones.values());
    }

    public Optional<NoBreakZone> get(String id) {
        return Optional.ofNullable(zones.get(id));
    }

    public void put(NoBreakZone zone) {
        zones.put(zone.id(), zone);
    }

    public boolean remove(String id) {
        return zones.remove(id) != null;
    }

    public void clear() {
        zones.clear();
    }

    public List<NoBreakZone> inWorld(UUID worldId) {
        List<NoBreakZone> result = new ArrayList<>();
        for (NoBreakZone zone : zones.values()) {
            if (zone.worldId().equals(worldId)) {
                result.add(zone);
            }
        }
        return result;
    }
}
