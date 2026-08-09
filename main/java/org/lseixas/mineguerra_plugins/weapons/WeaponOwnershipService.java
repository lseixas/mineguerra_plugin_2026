package org.lseixas.mineguerra_plugins.weapons;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.lseixas.mineguerra_plugins.teams.TeamDefinition;
import org.lseixas.mineguerra_plugins.teams.TeamRegistry;
import org.lseixas.mineguerra_plugins.teams.TeamsDataStore;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class WeaponOwnershipService {

    private final TeamsDataStore dataStore;
    private final WeaponItemService itemService;

    public WeaponOwnershipService(TeamsDataStore dataStore, WeaponItemService itemService) {
        this.dataStore = dataStore;
        this.itemService = itemService;
    }

    public boolean isAvailable(WeaponId weaponId) {
        return !existsInWorld(weaponId);
    }

    public boolean existsInWorld(WeaponId weaponId) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (hasWeaponInInventory(player, weaponId)) {
                return true;
            }
        }
        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (entity instanceof Item item) {
                    if (itemService.matches(item.getItemStack(), weaponId)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void claim(WeaponId weaponId, String ownerTeamId) {
        WeaponClaim claim = dataStore.getWeaponClaims().computeIfAbsent(weaponId, id -> new WeaponClaim());
        claim.setOwnerTeamId(ownerTeamId);
        dataStore.save();
        refreshLeaderboard();
    }

    public void releaseIfGone(WeaponId weaponId) {
        if (!existsInWorld(weaponId)) {
            WeaponClaim claim = dataStore.getWeaponClaims().get(weaponId);
            if (claim != null) {
                claim.setOwnerTeamId(null);
            }
            dataStore.save();
            refreshLeaderboard();
        }
    }

    public void rescanAll() {
        for (WeaponId weaponId : WeaponId.values()) {
            releaseIfGone(weaponId);
        }
    }

    public void resetAll() {
        for (WeaponClaim claim : dataStore.getWeaponClaims().values()) {
            claim.setOwnerTeamId(null);
        }
        dataStore.save();
        refreshLeaderboard();
    }

    public Optional<String> getOwnerTeamId(WeaponId weaponId) {
        WeaponClaim claim = dataStore.getWeaponClaims().get(weaponId);
        if (claim == null || !claim.hasOwner()) {
            return Optional.empty();
        }
        return Optional.of(claim.getOwnerTeamId());
    }

    public Optional<WeaponId> findWeaponHeldByTeam(String teamId) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            String playerTeam = TeamRegistry.teams().getTeamId(player);
            if (!teamId.equals(playerTeam)) {
                continue;
            }
            for (WeaponId weaponId : WeaponId.values()) {
                if (hasWeaponInInventory(player, weaponId)) {
                    return Optional.of(weaponId);
                }
            }
        }
        return Optional.empty();
    }

    public String getTeamWeaponDisplayName(String teamId) {
        Optional<WeaponId> held = findWeaponHeldByTeam(teamId);
        if (held.isPresent()) {
            return held.get().getDisplayName();
        }
        for (Map.Entry<WeaponId, WeaponClaim> entry : dataStore.getWeaponClaims().entrySet()) {
            if (teamId.equals(entry.getValue().getOwnerTeamId()) && existsInWorld(entry.getKey())) {
                return entry.getKey().getDisplayName();
            }
        }
        return "§8(nenhuma)";
    }

    public List<MerchantRecipe> filterAvailableTrades(List<MerchantRecipe> recipes) {
        List<MerchantRecipe> filtered = new ArrayList<>();
        for (MerchantRecipe recipe : recipes) {
            ItemStack result = recipe.getResult();
            Optional<WeaponId> weaponId = itemService.identify(result);
            if (weaponId.isPresent() && !isAvailable(weaponId.get())) {
                continue;
            }
            filtered.add(recipe);
        }
        return filtered;
    }

    public Map<WeaponId, String> getStatusSummary() {
        Map<WeaponId, String> summary = new EnumMap<>(WeaponId.class);
        for (WeaponId weaponId : WeaponId.values()) {
            if (isAvailable(weaponId)) {
                summary.put(weaponId, "§alivre");
            } else {
                String owner = getOwnerTeamId(weaponId).orElse("?");
                TeamDefinition team = TeamRegistry.teams().getTeam(owner).orElse(null);
                String teamLabel = team != null ? team.getColor() + team.getDisplayName() : owner;
                summary.put(weaponId, "§c" + teamLabel);
            }
        }
        return summary;
    }

    private boolean hasWeaponInInventory(Player player, WeaponId weaponId) {
        for (ItemStack stack : player.getInventory().getContents()) {
            if (itemService.matches(stack, weaponId)) {
                return true;
            }
        }
        return false;
    }

    private void refreshLeaderboard() {
        if (TeamRegistry.leaderboard().isEnabled()) {
            TeamRegistry.leaderboard().refreshAll();
        }
    }
}
