package org.lseixas.mineguerra_plugins.teams;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import org.lseixas.mineguerra_plugins.teams.flag.TeamFlag;
import org.lseixas.mineguerra_plugins.weapons.WeaponClaim;
import org.lseixas.mineguerra_plugins.weapons.WeaponId;

import java.io.File;
import java.io.IOException;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

/**
 * Persistência YAML de times, membros, kills e flag do leaderboard.
 */
public class TeamsDataStore {

    private final JavaPlugin plugin;
    private final File file;

    private boolean leaderboardEnabled;
    private final Map<String, TeamDefinition> teams = new LinkedHashMap<>();
    private final Map<UUID, String> playerTeams = new HashMap<>();
    private final Map<String, Integer> kills = new HashMap<>();
    private final Map<String, TeamFlag> flags = new HashMap<>();
    private final Map<WeaponId, WeaponClaim> weaponClaims = new EnumMap<>(WeaponId.class);

    public TeamsDataStore(JavaPlugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "teams-data.yml");
    }

    public void load() {
        teams.clear();
        playerTeams.clear();
        kills.clear();
        flags.clear();
        weaponClaims.clear();

        if (!file.exists()) {
            leaderboardEnabled = false;
            return;
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        leaderboardEnabled = config.getBoolean("leaderboardEnabled", false);

        if (config.isConfigurationSection("teams")) {
            for (String teamId : config.getConfigurationSection("teams").getKeys(false)) {
                String path = "teams." + teamId;
                String displayName = config.getString(path + ".displayName", teamId);
                String colorName = config.getString(path + ".color", "WHITE");
                String prefix = config.getString(path + ".prefix", "[" + displayName + "] ");

                ChatColor color = parseColor(colorName);
                teams.put(teamId, new TeamDefinition(teamId, displayName, color, prefix));
            }
        }

        if (config.isConfigurationSection("playerTeams")) {
            for (String uuidStr : config.getConfigurationSection("playerTeams").getKeys(false)) {
                try {
                    UUID uuid = UUID.fromString(uuidStr);
                    String teamId = config.getString("playerTeams." + uuidStr);
                    if (teamId != null && teams.containsKey(teamId)) {
                        playerTeams.put(uuid, teamId);
                    }
                } catch (IllegalArgumentException ignored) {
                    // UUID inválido no arquivo — ignorar
                }
            }
        }

        if (config.isConfigurationSection("kills")) {
            for (String teamId : config.getConfigurationSection("kills").getKeys(false)) {
                if (teams.containsKey(teamId)) {
                    kills.put(teamId, config.getInt("kills." + teamId, 0));
                }
            }
        }

        for (String teamId : teams.keySet()) {
            kills.putIfAbsent(teamId, 0);
        }

        if (config.isConfigurationSection("flags")) {
            for (String teamId : config.getConfigurationSection("flags").getKeys(false)) {
                if (!teams.containsKey(teamId)) {
                    continue;
                }
                String path = "flags." + teamId;
                String world = config.getString(path + ".world");
                if (world == null) {
                    continue;
                }
                double x = config.getDouble(path + ".x");
                double y = config.getDouble(path + ".y");
                double z = config.getDouble(path + ".z");
                float yaw = (float) config.getDouble(path + ".yaw", 0);
                boolean alive = config.getBoolean(path + ".alive", true);
                flags.put(teamId, new TeamFlag(teamId, world, x, y, z, yaw, alive));
            }
        }

        for (WeaponId weaponId : WeaponId.values()) {
            weaponClaims.put(weaponId, new WeaponClaim());
        }
        if (config.isConfigurationSection("weaponClaims")) {
            for (String weaponName : config.getConfigurationSection("weaponClaims").getKeys(false)) {
                try {
                    WeaponId weaponId = WeaponId.valueOf(weaponName);
                    String owner = config.getString("weaponClaims." + weaponName + ".ownerTeamId");
                    if (owner != null && !owner.isBlank() && teams.containsKey(owner)) {
                        weaponClaims.put(weaponId, new WeaponClaim(owner));
                    }
                } catch (IllegalArgumentException ignored) {
                    // nome de arma inválido no YAML
                }
            }
        }
    }

    public void save() {
        if (!plugin.getDataFolder().exists() && !plugin.getDataFolder().mkdirs()) {
            plugin.getLogger().warning("Nao foi possivel criar pasta de dados do plugin.");
        }

        FileConfiguration config = new YamlConfiguration();
        config.set("leaderboardEnabled", leaderboardEnabled);

        for (TeamDefinition team : teams.values()) {
            String path = "teams." + team.getId();
            config.set(path + ".displayName", team.getDisplayName());
            config.set(path + ".color", team.getColor().name());
            config.set(path + ".prefix", team.getPrefix());
        }

        for (Map.Entry<UUID, String> entry : playerTeams.entrySet()) {
            config.set("playerTeams." + entry.getKey(), entry.getValue());
        }

        for (Map.Entry<String, Integer> entry : kills.entrySet()) {
            config.set("kills." + entry.getKey(), entry.getValue());
        }

        for (Map.Entry<String, TeamFlag> entry : flags.entrySet()) {
            TeamFlag flag = entry.getValue();
            String path = "flags." + entry.getKey();
            config.set(path + ".world", flag.getWorldName());
            config.set(path + ".x", flag.getX());
            config.set(path + ".y", flag.getY());
            config.set(path + ".z", flag.getZ());
            config.set(path + ".yaw", flag.getYaw());
            config.set(path + ".alive", flag.isAlive());
        }

        for (Map.Entry<WeaponId, WeaponClaim> entry : weaponClaims.entrySet()) {
            WeaponClaim claim = entry.getValue();
            if (claim.hasOwner()) {
                config.set("weaponClaims." + entry.getKey().name() + ".ownerTeamId", claim.getOwnerTeamId());
            }
        }

        try {
            config.save(file);
        } catch (IOException ex) {
            plugin.getLogger().log(Level.SEVERE, "Falha ao salvar teams-data.yml", ex);
        }
    }

    private static ChatColor parseColor(String name) {
        try {
            return ChatColor.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException ex) {
            return ChatColor.WHITE;
        }
    }

    public boolean isLeaderboardEnabled() {
        return leaderboardEnabled;
    }

    public void setLeaderboardEnabled(boolean leaderboardEnabled) {
        this.leaderboardEnabled = leaderboardEnabled;
    }

    public Map<String, TeamDefinition> getTeams() {
        return teams;
    }

    public Map<UUID, String> getPlayerTeams() {
        return playerTeams;
    }

    public Map<String, Integer> getKills() {
        return kills;
    }

    public Map<String, TeamFlag> getFlags() {
        return flags;
    }

    public Map<WeaponId, WeaponClaim> getWeaponClaims() {
        return weaponClaims;
    }
}
