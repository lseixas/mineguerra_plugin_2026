package org.lseixas.mineguerra_plugins.teams.flag;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

/**
 * Localização e estado da bandeira de um time.
 */
public class TeamFlag {

    private final String teamId;
    private final String worldName;
    private final double x;
    private final double y;
    private final double z;
    private final float yaw;
    private boolean alive;

    public TeamFlag(String teamId, Location location, boolean alive) {
        this.teamId = teamId;
        this.worldName = location.getWorld().getName();
        this.x = location.getX();
        this.y = location.getY();
        this.z = location.getZ();
        this.yaw = location.getYaw();
        this.alive = alive;
    }

    public TeamFlag(String teamId, String worldName, double x, double y, double z, float yaw, boolean alive) {
        this.teamId = teamId;
        this.worldName = worldName;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.alive = alive;
    }

    public String getTeamId() {
        return teamId;
    }

    public String getWorldName() {
        return worldName;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public float getYaw() {
        return yaw;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public Location toLocation() {
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            return null;
        }
        Location location = new Location(world, x, y, z);
        location.setYaw(yaw);
        location.setPitch(0);
        return location;
    }
}
