package com.tntprotection.data;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@SerializableAs("RegionData")
public class RegionData implements ConfigurationSerializable {

    private String name;
    private UUID owner;
    private String world;
    private int minX, minY, minZ;
    private int maxX, maxY, maxZ;
    private long createdTime;

    public RegionData(String name, UUID owner, Location pos1, Location pos2) {
        this.name = name;
        this.owner = owner;
        this.world = pos1.getWorld().getName();

        this.minX = Math.min(pos1.getBlockX(), pos2.getBlockX());
        this.minY = Math.min(pos1.getBlockY(), pos2.getBlockY());
        this.minZ = Math.min(pos1.getBlockZ(), pos2.getBlockZ());

        this.maxX = Math.max(pos1.getBlockX(), pos2.getBlockX());
        this.maxY = Math.max(pos1.getBlockY(), pos2.getBlockY());
        this.maxZ = Math.max(pos1.getBlockZ(), pos2.getBlockZ());

        this.createdTime = System.currentTimeMillis();
    }

    public RegionData(String name, UUID owner, String world, int minX, int minY, int minZ,
                      int maxX, int maxY, int maxZ, long createdTime) {
        this.name = name;
        this.owner = owner;
        this.world = world;
        this.minX = minX;
        this.minY = minY;
        this.minZ = minZ;
        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;
        this.createdTime = createdTime;
    }

    public static RegionData deserialize(Map<String, Object> map) {
        try {
            String name = (String) map.get("name");
            UUID owner = UUID.fromString((String) map.get("owner"));
            String world = (String) map.get("world");
            int minX = (int) map.get("minX");
            int minY = (int) map.get("minY");
            int minZ = (int) map.get("minZ");
            int maxX = (int) map.get("maxX");
            int maxY = (int) map.get("maxY");
            int maxZ = (int) map.get("maxZ");
            long createdTime = ((Number) map.get("createdTime")).longValue();

            return new RegionData(name, owner, world, minX, minY, minZ, maxX, maxY, maxZ, createdTime);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("owner", owner.toString());
        map.put("world", world);
        map.put("minX", minX);
        map.put("minY", minY);
        map.put("minZ", minZ);
        map.put("maxX", maxX);
        map.put("maxY", maxY);
        map.put("maxZ", maxZ);
        map.put("createdTime", createdTime);
        return map;
    }

    public boolean contains(Location location) {
        if (!location.getWorld().getName().equals(world)) {
            return false;
        }

        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();

        return x >= minX && x <= maxX &&
               y >= minY && y <= maxY &&
               z >= minZ && z <= maxZ;
    }

    public String getName() {
        return name;
    }

    public UUID getOwner() {
        return owner;
    }

    public String getWorld() {
        return world;
    }

    public int getMinX() {
        return minX;
    }

    public int getMinY() {
        return minY;
    }

    public int getMinZ() {
        return minZ;
    }

    public int getMaxX() {
        return maxX;
    }

    public int getMaxY() {
        return maxY;
    }

    public int getMaxZ() {
        return maxZ;
    }

    public long getCreatedTime() {
        return createdTime;
    }

    public int getSizeX() {
        return maxX - minX + 1;
    }

    public int getSizeY() {
        return maxY - minY + 1;
    }

    public int getSizeZ() {
        return maxZ - minZ + 1;
    }

    public int getVolume() {
        return getSizeX() * getSizeY() * getSizeZ();
    }

    public Location getCenter() {
        World worldObj = Bukkit.getWorld(world);
        if (worldObj == null) return null;

        return new Location(worldObj,
            (minX + maxX) / 2.0,
            (minY + maxY) / 2.0,
            (minZ + maxZ) / 2.0);
    }

    @Override
    public String toString() {
        return "RegionData{" +
                "name='" + name + '\'' +
                ", owner=" + owner +
                ", world='" + world + '\'' +
                ", min=(" + minX + ", " + minY + ", " + minZ + ")" +
                ", max=(" + maxX + ", " + maxY + ", " + maxZ + ")" +
                '}';
    }
}
