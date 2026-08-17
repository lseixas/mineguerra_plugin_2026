package org.lseixas.mineguerra_plugins.nospawn;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * CRUD e consulta de zonas no-spawn.
 */
public class NoSpawnZoneService {

    private final NoSpawnZoneStore store;
    private final AtomicInteger nextId;

    public NoSpawnZoneService(NoSpawnZoneStore store) {
        this.store = store;
        int max = 0;
        for (NoSpawnZone zone : store.all()) {
            if (zone.id().startsWith("zone-")) {
                try {
                    max = Math.max(max, Integer.parseInt(zone.id().substring("zone-".length())));
                } catch (NumberFormatException ignored) {
                    // ids customizados — ok
                }
            }
        }
        this.nextId = new AtomicInteger(max + 1);
    }

    public Collection<NoSpawnZone> all() {
        return store.all();
    }

    public boolean isNoSpawn(Location location) {
        if (location == null || location.getWorld() == null) {
            return false;
        }
        for (NoSpawnZone zone : store.inWorld(location.getWorld().getUID())) {
            if (zone.contains(location)) {
                return true;
            }
        }
        return false;
    }

    public Optional<NoSpawnZone> findAt(Location location) {
        if (location == null || location.getWorld() == null) {
            return Optional.empty();
        }
        for (NoSpawnZone zone : store.inWorld(location.getWorld().getUID())) {
            if (zone.contains(location)) {
                return Optional.of(zone);
            }
        }
        return Optional.empty();
    }

    public Optional<NoSpawnZone> findAt(Block block) {
        if (block == null) {
            return Optional.empty();
        }
        return findAt(block.getLocation());
    }

    public NoSpawnZone create(Block cornerA, Block cornerB) {
        World world = cornerA.getWorld();
        if (!world.equals(cornerB.getWorld())) {
            throw new IllegalArgumentException("Cantos em mundos diferentes");
        }

        String id = "zone-" + nextId.getAndIncrement();
        NoSpawnZone zone = new NoSpawnZone(
                id,
                world.getUID(),
                world.getName(),
                cornerA.getX(), cornerA.getY(), cornerA.getZ(),
                cornerB.getX(), cornerB.getY(), cornerB.getZ()
        );
        store.put(zone);
        store.save();
        return zone;
    }

    public boolean remove(NoSpawnZone zone) {
        boolean removed = store.remove(zone.id());
        if (removed) {
            store.save();
        }
        return removed;
    }

    public int clearAll() {
        int count = store.all().size();
        store.clear();
        store.save();
        return count;
    }
}
