package org.lseixas.mineguerra_plugins.nobreak;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * CRUD e consulta de zonas no-break.
 */
public class NoBreakZoneService {

    private final NoBreakZoneStore store;
    private final AtomicInteger nextId;

    public NoBreakZoneService(NoBreakZoneStore store) {
        this.store = store;
        int max = 0;
        for (NoBreakZone zone : store.all()) {
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

    public Collection<NoBreakZone> all() {
        return store.all();
    }

    public boolean isProtected(Location location) {
        if (location == null || location.getWorld() == null) {
            return false;
        }
        for (NoBreakZone zone : store.inWorld(location.getWorld().getUID())) {
            if (zone.contains(location)) {
                return true;
            }
        }
        return false;
    }

    public boolean isProtected(Block block) {
        if (block == null) {
            return false;
        }
        return isProtected(block.getLocation());
    }

    public Optional<NoBreakZone> findAt(Location location) {
        if (location == null || location.getWorld() == null) {
            return Optional.empty();
        }
        for (NoBreakZone zone : store.inWorld(location.getWorld().getUID())) {
            if (zone.contains(location)) {
                return Optional.of(zone);
            }
        }
        return Optional.empty();
    }

    public Optional<NoBreakZone> findAt(Block block) {
        if (block == null) {
            return Optional.empty();
        }
        return findAt(block.getLocation());
    }

    public NoBreakZone create(Block cornerA, Block cornerB) {
        World world = cornerA.getWorld();
        if (!world.equals(cornerB.getWorld())) {
            throw new IllegalArgumentException("Cantos em mundos diferentes");
        }

        String id = "zone-" + nextId.getAndIncrement();
        NoBreakZone zone = new NoBreakZone(
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

    public boolean remove(NoBreakZone zone) {
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
