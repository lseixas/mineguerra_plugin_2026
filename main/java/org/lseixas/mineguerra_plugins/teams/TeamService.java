package org.lseixas.mineguerra_plugins.teams;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.lseixas.mineguerra_plugins.teams.flag.FlagService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

public class TeamService {

    private static final Pattern TEAM_ID_PATTERN = Pattern.compile("[a-z0-9_-]+");
    private static final int MAX_TEAMS = 12;

    private final TeamsDataStore dataStore;

    public TeamService(JavaPlugin plugin, TeamsDataStore dataStore) {
        this.dataStore = dataStore;
    }

    public Collection<TeamDefinition> getAllTeams() {
        return dataStore.getTeams().values();
    }

    public Optional<TeamDefinition> getTeam(String teamId) {
        return Optional.ofNullable(dataStore.getTeams().get(teamId));
    }

    public String getTeamId(Player player) {
        return dataStore.getPlayerTeams().get(player.getUniqueId());
    }

    public List<Player> getOnlineMembers(String teamId) {
        List<Player> members = new ArrayList<>();
        for (Map.Entry<UUID, String> entry : dataStore.getPlayerTeams().entrySet()) {
            if (!teamId.equals(entry.getValue())) {
                continue;
            }
            Player online = Bukkit.getPlayer(entry.getKey());
            if (online != null) {
                members.add(online);
            }
        }
        return members;
    }

    public int countMembers(String teamId) {
        int count = 0;
        for (String id : dataStore.getPlayerTeams().values()) {
            if (teamId.equals(id)) {
                count++;
            }
        }
        return count;
    }

    public boolean isValidTeamId(String rawId) {
        if (rawId == null) {
            return false;
        }
        String id = rawId.toLowerCase(Locale.ROOT);
        return TEAM_ID_PATTERN.matcher(id).matches() && id.length() <= 32;
    }

    public String normalizeTeamId(String rawId) {
        return rawId.toLowerCase(Locale.ROOT);
    }

    public CreateResult createTeam(String rawId, String displayName, ChatColor color) {
        if (!isValidTeamId(rawId)) {
            return CreateResult.INVALID_ID;
        }

        String id = normalizeTeamId(rawId);
        if (dataStore.getTeams().containsKey(id)) {
            return CreateResult.ALREADY_EXISTS;
        }

        if (dataStore.getTeams().size() >= MAX_TEAMS) {
            return CreateResult.TOO_MANY;
        }

        if (displayName == null || displayName.isBlank()) {
            displayName = id;
        }

        String prefix = "[" + displayName + "] ";
        TeamDefinition definition = new TeamDefinition(id, displayName, color, prefix);
        dataStore.getTeams().put(id, definition);
        dataStore.getKills().put(id, 0);

        for (Player online : Bukkit.getOnlinePlayers()) {
            ensureScoreboardTeam(online.getScoreboard(), definition);
        }

        dataStore.save();
        return CreateResult.SUCCESS;
    }

    public boolean deleteTeam(String teamId) {
        if (!dataStore.getTeams().containsKey(teamId)) {
            return false;
        }

        FlagService flags = TeamRegistry.flags();
        if (flags != null) {
            flags.removeFlag(teamId);
        }

        dataStore.getPlayerTeams().entrySet().removeIf(e -> teamId.equals(e.getValue()));
        dataStore.getTeams().remove(teamId);
        dataStore.getKills().remove(teamId);

        for (Player online : Bukkit.getOnlinePlayers()) {
            Team boardTeam = online.getScoreboard().getTeam(scoreboardTeamName(teamId));
            if (boardTeam != null) {
                boardTeam.unregister();
            }
        }

        dataStore.save();
        return true;
    }

    public boolean assignPlayer(Player player, String teamId) {
        if (!dataStore.getTeams().containsKey(teamId)) {
            return false;
        }

        removePlayerFromBoardTeam(player);
        dataStore.getPlayerTeams().put(player.getUniqueId(), teamId);
        applyTeamToPlayer(player, dataStore.getTeams().get(teamId));
        dataStore.save();
        return true;
    }

    public boolean removePlayer(Player player) {
        if (!dataStore.getPlayerTeams().containsKey(player.getUniqueId())) {
            return false;
        }

        removePlayerFromBoardTeam(player);
        dataStore.getPlayerTeams().remove(player.getUniqueId());
        dataStore.save();
        return true;
    }

    public void syncAllOnlinePlayers() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            applyPlayerTeam(player);
        }
    }

    public void applyPlayerTeam(Player player) {
        Scoreboard board = getOrCreatePlayerScoreboard(player);
        player.setScoreboard(board);

        String teamId = getTeamId(player);
        if (teamId == null) {
            removePlayerFromBoardTeam(player);
            return;
        }

        TeamDefinition definition = dataStore.getTeams().get(teamId);
        if (definition == null) {
            dataStore.getPlayerTeams().remove(player.getUniqueId());
            removePlayerFromBoardTeam(player);
            return;
        }

        applyTeamToPlayer(player, definition);
    }

    public Scoreboard getOrCreatePlayerScoreboard(Player player) {
        Scoreboard board = player.getScoreboard();
        if (board == Bukkit.getScoreboardManager().getMainScoreboard()) {
            board = Bukkit.getScoreboardManager().getNewScoreboard();
            player.setScoreboard(board);
        }

        for (TeamDefinition definition : dataStore.getTeams().values()) {
            ensureScoreboardTeam(board, definition);
        }

        // Joining / board rebuild: every online member must appear on this board
        // so the local client can render their nametag/tab prefixes.
        populateOnlineTeamEntries(board);
        return board;
    }

    private void applyTeamToPlayer(Player player, TeamDefinition definition) {
        getOrCreatePlayerScoreboard(player);
        propagatePlayerTeamEntry(player, definition);
    }

    /**
     * Adds every online teamed player's entry onto the given scoreboard.
     */
    private void populateOnlineTeamEntries(Scoreboard board) {
        for (Player online : Bukkit.getOnlinePlayers()) {
            String teamId = getTeamId(online);
            if (teamId == null) {
                continue;
            }
            TeamDefinition definition = dataStore.getTeams().get(teamId);
            if (definition == null) {
                continue;
            }
            addEntryOnBoard(board, online.getName(), definition);
        }
    }

    /**
     * Ensures this player's team entry exists on every online player's scoreboard.
     */
    private void propagatePlayerTeamEntry(Player player, TeamDefinition definition) {
        for (Player viewer : Bukkit.getOnlinePlayers()) {
            Scoreboard board = getOrCreatePlayerScoreboard(viewer);
            addEntryOnBoard(board, player.getName(), definition);
        }
    }

    private void addEntryOnBoard(Scoreboard board, String entryName, TeamDefinition definition) {
        removeEntryFromOtherTeams(board, entryName, definition.getId());
        Team boardTeam = ensureScoreboardTeam(board, definition);
        if (!boardTeam.hasEntry(entryName)) {
            boardTeam.addEntry(entryName);
        }
    }

    private void removePlayerFromBoardTeam(Player player) {
        String entryName = player.getName();
        for (Player viewer : Bukkit.getOnlinePlayers()) {
            Scoreboard board = viewer.getScoreboard();
            for (TeamDefinition definition : dataStore.getTeams().values()) {
                Team team = board.getTeam(scoreboardTeamName(definition.getId()));
                if (team != null) {
                    team.removeEntry(entryName);
                }
            }
        }
    }

    private void removeEntryFromOtherTeams(Scoreboard board, String entryName, String keepTeamId) {
        for (TeamDefinition definition : dataStore.getTeams().values()) {
            if (definition.getId().equals(keepTeamId)) {
                continue;
            }
            Team team = board.getTeam(scoreboardTeamName(definition.getId()));
            if (team != null) {
                team.removeEntry(entryName);
            }
        }
    }

    private Team ensureScoreboardTeam(Scoreboard board, TeamDefinition definition) {
        String name = scoreboardTeamName(definition.getId());
        Team team = board.getTeam(name);
        if (team == null) {
            team = board.registerNewTeam(name);
        }

        team.setDisplayName(definition.getDisplayName());
        team.setPrefix(definition.getColoredPrefix());
        team.setColor(definition.getColor());
        team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.ALWAYS);
        team.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
        return team;
    }

    /**
     * Bukkit team names max 16 chars. Hash the full id so {@code team-a} and
     * {@code team_a} (or truncated ids) never collapse to the same name.
     */
    private static String scoreboardTeamName(String teamId) {
        return "mg_" + String.format("%08x", teamId.hashCode() & 0xffffffffL);
    }

    public enum CreateResult {
        SUCCESS,
        INVALID_ID,
        ALREADY_EXISTS,
        TOO_MANY
    }
}
