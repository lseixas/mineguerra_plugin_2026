package org.lseixas.mineguerra_plugins.teams.flag;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.TileState;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.RayTraceResult;
import org.lseixas.mineguerra_plugins.metrics.MetricsRegistry;
import org.lseixas.mineguerra_plugins.teams.TeamDefinition;
import org.lseixas.mineguerra_plugins.teams.TeamRegistry;
import org.lseixas.mineguerra_plugins.teams.TeamService;
import org.lseixas.mineguerra_plugins.teams.TeamsDataStore;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class FlagService {

    private final TeamsDataStore dataStore;
    private final TeamService teamService;
    private final FlagAreaService areaService;
    private final org.bukkit.NamespacedKey flagKey;
    private final Set<UUID> eliminatedPlayers = new HashSet<>();

    public FlagService(
            JavaPlugin plugin,
            TeamsDataStore dataStore,
            TeamService teamService,
            FlagAreaService areaService
    ) {
        this.dataStore = dataStore;
        this.teamService = teamService;
        this.areaService = areaService;
        this.flagKey = FlagConstants.teamFlagKey(plugin);
    }

    public FlagAreaService area() {
        return areaService;
    }

    public Optional<TeamFlag> getFlag(String teamId) {
        return Optional.ofNullable(dataStore.getFlags().get(teamId));
    }

    public Optional<String> getTeamIdAtBlock(Block block) {
        if (block == null) {
            return Optional.empty();
        }
        BlockState state = block.getState();
        if (!(state instanceof TileState tile)) {
            return Optional.empty();
        }
        PersistentDataContainer pdc = tile.getPersistentDataContainer();
        if (!pdc.has(flagKey, PersistentDataType.STRING)) {
            return Optional.empty();
        }
        String teamId = pdc.get(flagKey, PersistentDataType.STRING);
        return teamId != null ? Optional.of(teamId) : Optional.empty();
    }

    public boolean isTeamFlagBlock(Block block) {
        return getTeamIdAtBlock(block).isPresent();
    }

    public SetResult setFlag(String teamId, Player staff) {
        Optional<TeamDefinition> teamOpt = teamService.getTeam(teamId);
        if (teamOpt.isEmpty()) {
            return SetResult.TEAM_NOT_FOUND;
        }

        Block target = resolveTargetBlock(staff);
        if (target == null) {
            return SetResult.NO_TARGET_BLOCK;
        }

        TeamDefinition team = teamOpt.get();
        Material banner = bannerMaterialFor(team.getColor());
        if (banner == null) {
            return SetResult.UNSUPPORTED_COLOR;
        }

        removeExistingFlagBlock(teamId);

        Location loc = target.getLocation().add(0.5, 0, 0.5);
        loc.setYaw(staff.getLocation().getYaw());
        TeamFlag flag = new TeamFlag(teamId, loc, true);
        dataStore.getFlags().put(teamId, flag);
        dataStore.save();
        areaService.refresh();

        areaService.clearArea(target.getLocation());
        placeFlagBanner(teamId, target, banner);
        return SetResult.SUCCESS;
    }

    public boolean removeFlag(String teamId) {
        if (!dataStore.getFlags().containsKey(teamId)) {
            return false;
        }
        removeExistingFlagBlock(teamId);
        dataStore.getFlags().remove(teamId);
        dataStore.save();
        areaService.refresh();
        return true;
    }

    public RepairResult repairFlag(String teamId) {
        TeamFlag flag = dataStore.getFlags().get(teamId);
        if (flag == null) {
            return RepairResult.NO_FLAG;
        }

        Optional<TeamDefinition> teamOpt = teamService.getTeam(teamId);
        if (teamOpt.isEmpty()) {
            return RepairResult.TEAM_NOT_FOUND;
        }

        Location loc = flag.toLocation();
        if (loc == null) {
            return RepairResult.WORLD_UNLOADED;
        }

        Material banner = bannerMaterialFor(teamOpt.get().getColor());
        if (banner == null) {
            return RepairResult.UNSUPPORTED_COLOR;
        }

        areaService.refresh();
        areaService.clearArea(loc);
        placeFlagBanner(teamId, loc.getBlock(), banner);
        flag.setAlive(true);
        dataStore.save();
        return RepairResult.SUCCESS;
    }

    /**
     * Reaplica a limpeza do raio de uma bandeira já registrada, sem mexer no banner.
     *
     * @return blocos removidos, ou -1 se a bandeira não existe / mundo não carregado
     */
    public int clearArea(String teamId) {
        TeamFlag flag = dataStore.getFlags().get(teamId);
        if (flag == null) {
            return -1;
        }
        Location loc = flag.toLocation();
        if (loc == null) {
            return -1;
        }
        areaService.refresh();
        return areaService.clearArea(loc);
    }

    public void onFlagDestroyed(String teamId, Player breaker) {
        TeamFlag flag = dataStore.getFlags().get(teamId);
        if (flag == null || !flag.isAlive()) {
            return;
        }
        flag.setAlive(false);
        dataStore.save();

        if (MetricsRegistry.service() != null) {
            MetricsRegistry.service().recordFlagCapture(breaker, teamId);
        }

        String breakerName = breaker != null ? breaker.getName() : "???";
        for (Player member : teamService.getOnlineMembers(teamId)) {
            member.sendTitle(
                    "§c§lBANDEIRA CAPTURADA",
                    "§7Sua bandeira foi destruída por §f" + breakerName,
                    10, 70, 20
            );
            member.sendMessage("§c§l[MineGuerra] §7Sua bandeira caiu! A próxima morte elimina você.");
        }
    }

    public void eliminatePlayer(Player player) {
        eliminatedPlayers.add(player.getUniqueId());
        player.setGameMode(GameMode.SPECTATOR);
        player.sendMessage("§c§l[MineGuerra] §7Você foi eliminado. Só pode espectar aliados do seu time.");
        player.sendMessage("§7Shift = próximo aliado.");
        TeamRegistry.eliminatedSpectators().attachSoon(player);
    }

    public boolean isEliminated(Player player) {
        return eliminatedPlayers.contains(player.getUniqueId());
    }

    public boolean revivePlayer(UUID uuid) {
        if (!eliminatedPlayers.remove(uuid)) {
            return false;
        }
        Player player = Bukkit.getPlayer(uuid);
        if (player != null) {
            TeamRegistry.eliminatedSpectators().onRevived(player);
            player.setGameMode(GameMode.SURVIVAL);
            player.sendMessage("§a§l[MineGuerra] §7Voce foi revivido por um admin.");
        }
        return true;
    }

    public int reviveAll() {
        Set<UUID> toRevive = new HashSet<>(eliminatedPlayers);
        clearEliminated();
        for (UUID uuid : toRevive) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                TeamRegistry.eliminatedSpectators().onRevived(player);
                player.setGameMode(GameMode.SURVIVAL);
                player.sendMessage("§a§l[MineGuerra] §7Voce foi revivido por um admin.");
            }
        }
        return toRevive.size();
    }

    public void clearEliminated() {
        eliminatedPlayers.clear();
    }

    public void applyEliminatedState(Player player) {
        if (eliminatedPlayers.contains(player.getUniqueId())) {
            player.setGameMode(GameMode.SPECTATOR);
            TeamRegistry.eliminatedSpectators().attachSoon(player);
        }
    }

    private void placeFlagBanner(String teamId, Block block, Material banner) {
        block.setType(banner);
        BlockState state = block.getState();
        if (state instanceof TileState tile) {
            tile.getPersistentDataContainer().set(flagKey, PersistentDataType.STRING, teamId);
            tile.update(true, false);
        }
    }

    private void removeExistingFlagBlock(String teamId) {
        TeamFlag existing = dataStore.getFlags().get(teamId);
        if (existing == null) {
            return;
        }
        Location loc = existing.toLocation();
        if (loc == null) {
            return;
        }
        Block block = loc.getBlock();
        if (isTeamFlagBlock(block) && teamId.equals(getTeamIdAtBlock(block).orElse(null))) {
            block.setType(Material.AIR);
        }
    }

    private Block resolveTargetBlock(Player player) {
        RayTraceResult trace = player.getWorld().rayTraceBlocks(
                player.getEyeLocation(),
                player.getEyeLocation().getDirection(),
                5.0
        );
        if (trace != null && trace.getHitBlock() != null) {
            return trace.getHitBlock();
        }
        Block feet = player.getLocation().getBlock();
        if (feet.getType().isAir()) {
            return feet.getRelative(0, -1, 0);
        }
        return feet;
    }

    private static Material bannerMaterialFor(ChatColor color) {
        return switch (color) {
            case RED -> Material.RED_BANNER;
            case BLUE -> Material.BLUE_BANNER;
            case GREEN -> Material.GREEN_BANNER;
            case YELLOW -> Material.YELLOW_BANNER;
            case AQUA -> Material.CYAN_BANNER;
            case LIGHT_PURPLE -> Material.MAGENTA_BANNER;
            case GOLD -> Material.ORANGE_BANNER;
            case GRAY -> Material.GRAY_BANNER;
            case DARK_GRAY -> Material.BLACK_BANNER;
            case DARK_RED -> Material.RED_BANNER;
            case DARK_BLUE -> Material.BLUE_BANNER;
            case DARK_GREEN -> Material.GREEN_BANNER;
            case DARK_AQUA -> Material.CYAN_BANNER;
            case DARK_PURPLE -> Material.PURPLE_BANNER;
            default -> Material.WHITE_BANNER;
        };
    }

    public enum SetResult {
        SUCCESS,
        TEAM_NOT_FOUND,
        NO_TARGET_BLOCK,
        UNSUPPORTED_COLOR
    }

    public enum RepairResult {
        SUCCESS,
        NO_FLAG,
        TEAM_NOT_FOUND,
        WORLD_UNLOADED,
        UNSUPPORTED_COLOR
    }
}
