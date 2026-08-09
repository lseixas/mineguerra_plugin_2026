package org.lseixas.mineguerra_plugins.teams;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class KillStatsService {

    private final TeamsDataStore dataStore;

    public KillStatsService(TeamsDataStore dataStore) {
        this.dataStore = dataStore;
    }

    public int getKills(String teamId) {
        return dataStore.getKills().getOrDefault(teamId, 0);
    }

    public void increment(String teamId) {
        if (!dataStore.getTeams().containsKey(teamId)) {
            return;
        }
        int current = dataStore.getKills().getOrDefault(teamId, 0);
        dataStore.getKills().put(teamId, current + 1);
        dataStore.save();
    }

    public boolean setKills(String teamId, int amount) {
        if (!dataStore.getTeams().containsKey(teamId)) {
            return false;
        }
        if (amount < 0) {
            amount = 0;
        }
        dataStore.getKills().put(teamId, amount);
        dataStore.save();
        return true;
    }

    public boolean resetKills(String teamId) {
        return setKills(teamId, 0);
    }

    public void resetAllKills() {
        for (String teamId : dataStore.getTeams().keySet()) {
            dataStore.getKills().put(teamId, 0);
        }
        dataStore.save();
    }

    /**
     * Times ordenados por kills (maior primeiro), depois por nome.
     */
    public List<TeamDefinition> getTeamsSortedByKills() {
        List<TeamDefinition> sorted = new ArrayList<>(dataStore.getTeams().values());
        sorted.sort(Comparator
                .comparingInt((TeamDefinition t) -> getKills(t.getId())).reversed()
                .thenComparing(TeamDefinition::getDisplayName, String.CASE_INSENSITIVE_ORDER));
        return sorted;
    }
}
