package org.lseixas.mineguerra_plugins.teams.flag;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.lseixas.mineguerra_plugins.teams.TeamsDataStore;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * Área limpa e protegida ao redor de cada bandeira (cubo de raio {@link #RADIUS}).
 *
 * <p>A limpeza remove também o bloco de suporte abaixo do banner — quem mantém a
 * bandeira no lugar é o {@link FlagPhysicsListener}, cancelando a física do bloco.
 */
public class FlagAreaService {

    /** Raio em blocos, aplicado nos três eixos (cubo 7x7x7 com raio 3). */
    public static final int RADIUS = 3;

    /** Blocos que a limpeza nunca remove, para não estragar o mundo do evento. */
    private static final Set<Material> UNCLEARABLE = EnumSet.of(
            Material.BEDROCK,
            Material.BARRIER,
            Material.END_PORTAL,
            Material.END_PORTAL_FRAME,
            Material.END_GATEWAY,
            Material.NETHER_PORTAL,
            Material.COMMAND_BLOCK,
            Material.CHAIN_COMMAND_BLOCK,
            Material.REPEATING_COMMAND_BLOCK,
            Material.STRUCTURE_BLOCK,
            Material.STRUCTURE_VOID,
            Material.JIGSAW,
            Material.LIGHT
    );

    private final TeamsDataStore dataStore;
    private volatile List<FlagPos> positions = List.of();

    public FlagAreaService(TeamsDataStore dataStore) {
        this.dataStore = dataStore;
        refresh();
    }

    /** Recarrega o índice de posições. Chamar sempre que uma bandeira muda de lugar. */
    public void refresh() {
        List<FlagPos> rebuilt = new ArrayList<>();
        for (TeamFlag flag : dataStore.getFlags().values()) {
            rebuilt.add(new FlagPos(
                    flag.getWorldName(),
                    (int) Math.floor(flag.getX()),
                    (int) Math.floor(flag.getY()),
                    (int) Math.floor(flag.getZ())
            ));
        }
        positions = List.copyOf(rebuilt);
    }

    /** {@code true} se o bloco é exatamente o bloco de uma bandeira registrada. */
    public boolean isFlagBlock(Block block) {
        for (FlagPos pos : positions) {
            if (pos.matches(block)) {
                return true;
            }
        }
        return false;
    }

    /** {@code true} se o bloco está no cubo de alguma bandeira (incluindo o banner). */
    public boolean isInProtectedArea(Block block) {
        for (FlagPos pos : positions) {
            if (pos.withinRadius(block)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Limpa o cubo ao redor da bandeira, preservando o próprio banner.
     *
     * @return quantidade de blocos removidos
     */
    public int clearArea(Location flagLocation) {
        if (flagLocation == null || flagLocation.getWorld() == null) {
            return 0;
        }

        int centerX = flagLocation.getBlockX();
        int centerY = flagLocation.getBlockY();
        int centerZ = flagLocation.getBlockZ();
        int minY = Math.max(flagLocation.getWorld().getMinHeight(), centerY - RADIUS);
        int maxY = Math.min(flagLocation.getWorld().getMaxHeight() - 1, centerY + RADIUS);

        int cleared = 0;
        for (int x = centerX - RADIUS; x <= centerX + RADIUS; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = centerZ - RADIUS; z <= centerZ + RADIUS; z++) {
                    if (x == centerX && y == centerY && z == centerZ) {
                        continue;
                    }
                    Block block = flagLocation.getWorld().getBlockAt(x, y, z);
                    if (!isClearable(block.getType())) {
                        continue;
                    }
                    // applyPhysics=false evita cascata de queda/atualização nos vizinhos
                    block.setType(Material.AIR, false);
                    cleared++;
                }
            }
        }
        return cleared;
    }

    public static boolean isClearable(Material material) {
        return !material.isAir() && !UNCLEARABLE.contains(material);
    }

    /** Distância de Chebyshev (cubo), não euclidiana. */
    static boolean withinRadius(int x, int y, int z, int centerX, int centerY, int centerZ, int radius) {
        return Math.abs(x - centerX) <= radius
                && Math.abs(y - centerY) <= radius
                && Math.abs(z - centerZ) <= radius;
    }

    private record FlagPos(String worldName, int x, int y, int z) {

        boolean matches(Block block) {
            return block.getY() == y
                    && block.getX() == x
                    && block.getZ() == z
                    && block.getWorld().getName().equals(worldName);
        }

        boolean withinRadius(Block block) {
            return FlagAreaService.withinRadius(
                    block.getX(), block.getY(), block.getZ(), x, y, z, RADIUS)
                    && block.getWorld().getName().equals(worldName);
        }
    }
}
