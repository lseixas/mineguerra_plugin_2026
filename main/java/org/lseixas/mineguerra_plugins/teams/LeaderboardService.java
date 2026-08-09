package org.lseixas.mineguerra_plugins.teams;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.lseixas.mineguerra_plugins.weapons.WeaponId;
import org.lseixas.mineguerra_plugins.weapons.WeaponOwnershipService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LeaderboardService {

    private static final String OBJECTIVE_NAME = "mg_kills";
    private static final String OBJECTIVE_DISPLAY = "§6§lMineGuerra";
    private static final int MAX_SIDEBAR_LINES = 15;

    private final TeamsDataStore dataStore;
    private final TeamService teamService;
    private final KillStatsService killStatsService;
    private final WeaponOwnershipService weaponOwnership;

    public LeaderboardService(
            TeamsDataStore dataStore,
            TeamService teamService,
            KillStatsService killStatsService,
            WeaponOwnershipService weaponOwnership
    ) {
        this.dataStore = dataStore;
        this.teamService = teamService;
        this.killStatsService = killStatsService;
        this.weaponOwnership = weaponOwnership;
    }

    public boolean isEnabled() {
        return dataStore.isLeaderboardEnabled();
    }

    public void setEnabled(boolean enabled) {
        dataStore.setLeaderboardEnabled(enabled);
        dataStore.save();

        if (enabled) {
            refreshAll();
        } else {
            hideAll();
        }
    }

    public void refreshAll() {
        if (!dataStore.isLeaderboardEnabled()) {
            return;
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            refreshPlayer(player);
        }
    }

    public void refreshPlayer(Player player) {
        if (!dataStore.isLeaderboardEnabled()) {
            return;
        }

        Scoreboard board = teamService.getOrCreatePlayerScoreboard(player);
        player.setScoreboard(board);

        Objective existing = board.getObjective(OBJECTIVE_NAME);
        if (existing != null) {
            existing.unregister();
        }

        Objective objective = board.registerNewObjective(
                OBJECTIVE_NAME,
                org.bukkit.scoreboard.Criteria.DUMMY,
                OBJECTIVE_DISPLAY
        );
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        List<TeamDefinition> teams = killStatsService.getTeamsSortedByKills();
        int score = MAX_SIDEBAR_LINES;
        Set<String> usedEntries = new HashSet<>();

        String killsHeader = uniqueEntry(usedEntries, "§7— Kills —", score);
        objective.getScore(killsHeader).setScore(score);
        score--;

        for (TeamDefinition teamDef : teams) {
            if (score <= 0) {
                break;
            }
            String line = formatTeamKillLine(teamDef);
            String entry = uniqueEntry(usedEntries, line, score);
            objective.getScore(entry).setScore(score);
            score--;
        }

        if (score > 0) {
            String weaponsHeader = uniqueEntry(usedEntries, "§7— Armas —", score);
            objective.getScore(weaponsHeader).setScore(score);
            score--;
        }

        for (TeamDefinition teamDef : teams) {
            if (score <= 0) {
                break;
            }
            String weaponLine = formatTeamWeaponLine(teamDef);
            String entry = uniqueEntry(usedEntries, weaponLine, score);
            objective.getScore(entry).setScore(score);
            score--;
        }

        for (WeaponId weaponId : WeaponId.values()) {
            if (score <= 0) {
                break;
            }
            String status = weaponOwnership.isAvailable(weaponId)
                    ? "§alivre"
                    : "§clockada";
            String line = "§7" + weaponId.getShortName() + " §7— " + status;
            String entry = uniqueEntry(usedEntries, line, score);
            objective.getScore(entry).setScore(score);
            score--;
        }

        if (score > 0) {
            String footer = uniqueEntry(usedEntries, "§7Atualizado ao vivo", score);
            objective.getScore(footer).setScore(score);
        }
    }

    public void hideAll() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            hidePlayer(player);
        }
    }

    public void hidePlayer(Player player) {
        Scoreboard board = player.getScoreboard();
        Objective objective = board.getObjective(OBJECTIVE_NAME);
        if (objective != null) {
            objective.unregister();
        }
    }

    private String formatTeamKillLine(TeamDefinition team) {
        int kills = killStatsService.getKills(team.getId());
        String killLabel = kills == 1 ? "kill" : "kills";
        return team.getColor() + team.getDisplayName() + " §7— §f" + kills + " " + killLabel;
    }

    private String formatTeamWeaponLine(TeamDefinition team) {
        String weaponDisplay = weaponOwnership.getTeamWeaponDisplayName(team.getId());
        return team.getColor() + team.getDisplayName() + " §7— " + weaponDisplay;
    }

    private String uniqueEntry(Set<String> used, String display, int score) {
        String entry = display;
        int suffix = 0;
        while (used.contains(entry)) {
            suffix++;
            entry = display + "§r§" + Integer.toHexString(suffix % 16);
        }
        if (entry.length() > 40) {
            entry = entry.substring(0, 40);
        }
        used.add(entry);
        return entry;
    }
}
