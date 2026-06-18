package com.tntprotection.managers;

import com.tntprotection.TNTProtectionPlugin;
import com.tntprotection.data.PlayerData;
import com.tntprotection.data.RegionData;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class RegionManager {

    private final TNTProtectionPlugin plugin;
    private final Map<UUID, PlayerData> playerDataMap;
    private final List<RegionData> protectedRegions;
    private File regionsFile;
    private FileConfiguration regionsConfig;

    public RegionManager(TNTProtectionPlugin plugin) {
        this.plugin = plugin;
        this.playerDataMap = new HashMap<>();
        this.protectedRegions = new ArrayList<>();
        this.regionsFile = new File(plugin.getDataFolder(), "regions.yml");
    }

    public void loadRegions() {
        if (!regionsFile.exists()) {
            try {
                regionsFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("无法创建regions.yml文件: " + e.getMessage());
            }
        }

        regionsConfig = YamlConfiguration.loadConfiguration(regionsFile);

        protectedRegions.clear();
        if (regionsConfig.contains("regions")) {
            List<?> regionsList = regionsConfig.getList("regions");
            if (regionsList != null) {
                for (Object obj : regionsList) {
                    if (obj instanceof Map) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> regionMap = (Map<String, Object>) obj;
                        RegionData region = RegionData.deserialize(regionMap);
                        if (region != null) {
                            protectedRegions.add(region);
                        }
                    }
                }
            }
        }

        plugin.getLogger().info("已加载 " + protectedRegions.size() + " 个保护区域");
    }

    public void saveRegions() {
        if (regionsConfig == null) {
            regionsConfig = YamlConfiguration.loadConfiguration(regionsFile);
        }

        List<Map<String, Object>> regionsList = new ArrayList<>();
        for (RegionData region : protectedRegions) {
            regionsList.add(region.serialize());
        }

        regionsConfig.set("regions", regionsList);

        try {
            regionsConfig.save(regionsFile);
            plugin.getLogger().info("已保存 " + protectedRegions.size() + " 个保护区域");
        } catch (IOException e) {
            plugin.getLogger().severe("无法保存regions.yml文件: " + e.getMessage());
        }
    }

    public void addRegion(RegionData region) {
        protectedRegions.add(region);
        saveRegions();
    }

    public void removeRegion(String regionName, UUID owner) {
        protectedRegions.removeIf(region ->
            region.getName().equals(regionName) && region.getOwner().equals(owner));
        saveRegions();
    }

    public boolean isLocationProtected(Location location) {
        for (RegionData region : protectedRegions) {
            if (region.contains(location)) {
                return true;
            }
        }
        return false;
    }

    public RegionData getRegionAtLocation(Location location) {
        for (RegionData region : protectedRegions) {
            if (region.contains(location)) {
                return region;
            }
        }
        return null;
    }

    public List<RegionData> getPlayerRegions(UUID playerId) {
        List<RegionData> playerRegions = new ArrayList<>();
        for (RegionData region : protectedRegions) {
            if (region.getOwner().equals(playerId)) {
                playerRegions.add(region);
            }
        }
        return playerRegions;
    }

    public int getPlayerRegionCount(UUID playerId) {
        return getPlayerRegions(playerId).size();
    }

    public boolean canCreateRegion(Player player) {
        int maxRegions = plugin.getConfigManager().getMaxRegionsPerPlayer();
        if (maxRegions == -1) return true;

        if (player.hasPermission("tntprotection.limit.*")) return true;

        return getPlayerRegionCount(player.getUniqueId()) < maxRegions;
    }

    public boolean isSelectionTool(Player player) {
        if (!player.getInventory().getItemInMainHand().getType().equals(plugin.getSelectionTool())) {
            return !player.getInventory().getItemInOffHand().getType().equals(plugin.getSelectionTool());
        }
        return true;
    }

    // Player Selection Methods
    public void setPlayerPos1(Player player, Location location) {
        PlayerData data = getPlayerData(player);
        data.setPos1(location);
    }

    public void setPlayerPos2(Player player, Location location) {
        PlayerData data = getPlayerData(player);
        data.setPos2(location);
    }

    public Location getPlayerPos1(Player player) {
        return getPlayerData(player).getPos1();
    }

    public Location getPlayerPos2(Player player) {
        return getPlayerData(player).getPos2();
    }

    public boolean hasSelectedBothPositions(Player player) {
        PlayerData data = getPlayerData(player);
        return data.getPos1() != null && data.getPos2() != null;
    }

    public void clearPlayerSelection(Player player) {
        PlayerData data = getPlayerData(player);
        data.clear();
    }

    public PlayerData getPlayerData(Player player) {
        return playerDataMap.computeIfAbsent(player.getUniqueId(), k -> new PlayerData());
    }

    public List<RegionData> getAllRegions() {
        return new ArrayList<>(protectedRegions);
    }
}
