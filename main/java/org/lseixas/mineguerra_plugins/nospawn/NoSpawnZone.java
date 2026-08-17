package org.lseixas.mineguerra_plugins.nospawn;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.Objects;
import java.util.UUID;

/**
 * Cuboide inclusivo (coordenadas de bloco) onde mobs não podem spawnar.
 */
public final class NoSpawnZone {

    private final String id;
    private final UUID worldId;
    private final String worldName;
    private final int minX;
    private final int minY;
    private final int minZ;
    private final int maxX;
    private final int maxY;
    private final int maxZ;

    public NoSpawnZone(
            String id,
            UUID worldId,
            String worldName,
            int x1,
            int y1,
            int z1,
            int x2,
            int y2,
            int z2
    ) {
        this.id = Objects.requireNonNull(id, "id");
        this.worldId = Objects.requireNonNull(worldId, "worldId");
        this.worldName = Objects.requireNonNull(worldName, "worldName");
        this.minX = Math.min(x1, x2);
        this.minY = Math.min(y1, y2);
        this.minZ = Math.min(z1, z2);
        this.maxX = Math.max(x1, x2);
        this.maxY = Math.max(y1, y2);
        this.maxZ = Math.max(z1, z2);
    }

    public String id() {
        return id;
    }

    public UUID worldId() {
        return worldId;
    }

    public String worldName() {
        return worldName;
    }

    public int minX() {
        return minX;
    }

    public int minY() {
        return minY;
    }

    public int minZ() {
        return minZ;
    }

    public int maxX() {
        return maxX;
    }

    public int maxY() {
        return maxY;
    }

    public int maxZ() {
        return maxZ;
    }

    public long volumeBlocks() {
        return (long) (maxX - minX + 1) * (maxY - minY + 1) * (maxZ - minZ + 1);
    }

    public boolean contains(Location location) {
        if (location == null || location.getWorld() == null) {
            return false;
        }
        if (!worldId.equals(location.getWorld().getUID())) {
            return false;
        }
        return containsBlock(location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    public boolean containsBlock(int x, int y, int z) {
        return x >= minX && x <= maxX
                && y >= minY && y <= maxY
                && z >= minZ && z <= maxZ;
    }

    public World world() {
        World byId = Bukkit.getWorld(worldId);
        if (byId != null) {
            return byId;
        }
        return Bukkit.getWorld(worldName);
    }

    public String sizeLabel() {
        int dx = maxX - minX + 1;
        int dy = maxY - minY + 1;
        int dz = maxZ - minZ + 1;
        return dx + "x" + dy + "x" + dz;
    }
}
