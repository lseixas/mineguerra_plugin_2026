package org.lseixas.mineguerra_plugins.nobreak;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;

/**
 * Contorno de cuboide com partículas (feedback visual do delimitador).
 */
public final class NoBreakParticleHelper {

    private static final Particle.DustOptions SELECTION =
            new Particle.DustOptions(Color.fromRGB(255, 200, 40), 1.2f);
    private static final Particle.DustOptions EXISTING =
            new Particle.DustOptions(Color.fromRGB(180, 80, 255), 1.0f);
    private static final Particle.DustOptions CONFIRM =
            new Particle.DustOptions(Color.fromRGB(80, 255, 120), 1.4f);

    private NoBreakParticleHelper() {
    }

    public static void drawSelection(Player viewer, int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        drawBox(viewer, minX, minY, minZ, maxX, maxY, maxZ, SELECTION, 0.5);
    }

    public static void drawExisting(Player viewer, NoBreakZone zone) {
        drawBox(viewer, zone.minX(), zone.minY(), zone.minZ(), zone.maxX(), zone.maxY(), zone.maxZ(), EXISTING, 1.0);
    }

    public static void flashConfirm(World world, NoBreakZone zone) {
        if (world == null) {
            return;
        }
        drawBoxWorld(world, zone.minX(), zone.minY(), zone.minZ(), zone.maxX(), zone.maxY(), zone.maxZ(), CONFIRM, 0.35);
    }

    private static void drawBox(
            Player viewer,
            int minX,
            int minY,
            int minZ,
            int maxX,
            int maxY,
            int maxZ,
            Particle.DustOptions dust,
            double step
    ) {
        World world = viewer.getWorld();
        double x1 = minX;
        double y1 = minY;
        double z1 = minZ;
        double x2 = maxX + 1.0;
        double y2 = maxY + 1.0;
        double z2 = maxZ + 1.0;
        spawnEdges(viewer, world, x1, y1, z1, x2, y2, z2, dust, step);
    }

    private static void drawBoxWorld(
            World world,
            int minX,
            int minY,
            int minZ,
            int maxX,
            int maxY,
            int maxZ,
            Particle.DustOptions dust,
            double step
    ) {
        double x1 = minX;
        double y1 = minY;
        double z1 = minZ;
        double x2 = maxX + 1.0;
        double y2 = maxY + 1.0;
        double z2 = maxZ + 1.0;
        spawnEdgesWorld(world, x1, y1, z1, x2, y2, z2, dust, step);
    }

    private static void spawnEdges(
            Player viewer,
            World world,
            double x1, double y1, double z1,
            double x2, double y2, double z2,
            Particle.DustOptions dust,
            double step
    ) {
        spawnEdge(viewer, world, x1, y1, z1, x2, y1, z1, dust, step);
        spawnEdge(viewer, world, x1, y1, z2, x2, y1, z2, dust, step);
        spawnEdge(viewer, world, x1, y2, z1, x2, y2, z1, dust, step);
        spawnEdge(viewer, world, x1, y2, z2, x2, y2, z2, dust, step);

        spawnEdge(viewer, world, x1, y1, z1, x1, y1, z2, dust, step);
        spawnEdge(viewer, world, x2, y1, z1, x2, y1, z2, dust, step);
        spawnEdge(viewer, world, x1, y2, z1, x1, y2, z2, dust, step);
        spawnEdge(viewer, world, x2, y2, z1, x2, y2, z2, dust, step);

        spawnEdge(viewer, world, x1, y1, z1, x1, y2, z1, dust, step);
        spawnEdge(viewer, world, x2, y1, z1, x2, y2, z1, dust, step);
        spawnEdge(viewer, world, x1, y1, z2, x1, y2, z2, dust, step);
        spawnEdge(viewer, world, x2, y1, z2, x2, y2, z2, dust, step);
    }

    private static void spawnEdgesWorld(
            World world,
            double x1, double y1, double z1,
            double x2, double y2, double z2,
            Particle.DustOptions dust,
            double step
    ) {
        spawnEdgeWorld(world, x1, y1, z1, x2, y1, z1, dust, step);
        spawnEdgeWorld(world, x1, y1, z2, x2, y1, z2, dust, step);
        spawnEdgeWorld(world, x1, y2, z1, x2, y2, z1, dust, step);
        spawnEdgeWorld(world, x1, y2, z2, x2, y2, z2, dust, step);

        spawnEdgeWorld(world, x1, y1, z1, x1, y1, z2, dust, step);
        spawnEdgeWorld(world, x2, y1, z1, x2, y1, z2, dust, step);
        spawnEdgeWorld(world, x1, y2, z1, x1, y2, z2, dust, step);
        spawnEdgeWorld(world, x2, y2, z1, x2, y2, z2, dust, step);

        spawnEdgeWorld(world, x1, y1, z1, x1, y2, z1, dust, step);
        spawnEdgeWorld(world, x2, y1, z1, x2, y2, z1, dust, step);
        spawnEdgeWorld(world, x1, y1, z2, x1, y2, z2, dust, step);
        spawnEdgeWorld(world, x2, y1, z2, x2, y2, z2, dust, step);
    }

    private static void spawnEdge(
            Player viewer,
            World world,
            double x1, double y1, double z1,
            double x2, double y2, double z2,
            Particle.DustOptions dust,
            double step
    ) {
        double dx = x2 - x1;
        double dy = y2 - y1;
        double dz = z2 - z1;
        double length = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (length < 1e-6) {
            viewer.spawnParticle(Particle.DUST, new Location(world, x1, y1, z1), 1, 0, 0, 0, 0, dust);
            return;
        }
        int points = Math.max(1, (int) Math.ceil(length / step));
        for (int i = 0; i <= points; i++) {
            double t = (double) i / points;
            viewer.spawnParticle(
                    Particle.DUST,
                    new Location(world, x1 + dx * t, y1 + dy * t, z1 + dz * t),
                    1, 0, 0, 0, 0, dust
            );
        }
    }

    private static void spawnEdgeWorld(
            World world,
            double x1, double y1, double z1,
            double x2, double y2, double z2,
            Particle.DustOptions dust,
            double step
    ) {
        double dx = x2 - x1;
        double dy = y2 - y1;
        double dz = z2 - z1;
        double length = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (length < 1e-6) {
            world.spawnParticle(Particle.DUST, new Location(world, x1, y1, z1), 1, 0, 0, 0, 0, dust);
            return;
        }
        int points = Math.max(1, (int) Math.ceil(length / step));
        for (int i = 0; i <= points; i++) {
            double t = (double) i / points;
            world.spawnParticle(
                    Particle.DUST,
                    new Location(world, x1 + dx * t, y1 + dy * t, z1 + dz * t),
                    1, 0, 0, 0, 0, dust
            );
        }
    }
}
