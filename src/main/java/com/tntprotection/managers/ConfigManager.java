package com.tntprotection.managers;

import com.tntprotection.TNTProtectionPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public class ConfigManager {

    private final TNTProtectionPlugin plugin;
    private int maxRegionsPerPlayer;
    private boolean regionEnabled;
    private Material selectionTool;
    private String selectionToolName;
    private boolean blockProtectionEnabled;
    private List<Material> defaultProtectedBlocks;
    private boolean cancelExplosion;
    private int explosionRadius;
    private boolean showProtectionMessage;
    private boolean logExplosions;
    private int maxProtectedBlocksPerPlayer;
    private boolean economyEnabled;
    private double regionCreationCost;
    private double blockProtectionCost;
    private String eventPriority;
    private boolean playSound;
    private String successColor;
    private String errorColor;
    private String locale;

    public ConfigManager(TNTProtectionPlugin plugin) {
        this.plugin = plugin;
    }

    public void loadConfiguration() {
        plugin.reloadConfig();

        // 区域设置
        maxRegionsPerPlayer = plugin.getConfig().getInt("region.max-regions-per-player", 10);
        regionEnabled = plugin.getConfig().getBoolean("region.enabled", true);

        String toolName = plugin.getConfig().getString("region.selection-tool", "GOLDEN_AXE");
        try {
            selectionTool = Material.valueOf(toolName);
        } catch (IllegalArgumentException e) {
            plugin.getLogger().warning("无效的选择工具: " + toolName + "，使用默认值 GOLDEN_AXE");
            selectionTool = Material.GOLDEN_AXE;
        }

        selectionToolName = colorize(plugin.getConfig().getString("region.selection-tool-name", "&6[区域选择斧]&r"));

        // 方块保护设置
        blockProtectionEnabled = plugin.getConfig().getBoolean("block-protection.enabled", true);
        defaultProtectedBlocks = new ArrayList<>();

        List<String> blockList = plugin.getConfig().getStringList("block-protection.default-protected-blocks");
        for (String block : blockList) {
            try {
                Material material = Material.valueOf(block);
                defaultProtectedBlocks.add(material);
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("无效的方块ID: " + block);
            }
        }

        // 爆炸设置
        cancelExplosion = plugin.getConfig().getBoolean("explosion.cancel-explosion", false);
        explosionRadius = plugin.getConfig().getInt("explosion.explosion-radius", 0);
        showProtectionMessage = plugin.getConfig().getBoolean("explosion.show-protection-message", true);
        logExplosions = plugin.getConfig().getBoolean("explosion.log-explosions", true);

        // 权限设置
        maxProtectedBlocksPerPlayer = plugin.getConfig().getInt("permissions.max-protected-blocks-per-player", -1);

        // 经济设置
        economyEnabled = plugin.getConfig().getBoolean("economy.enabled", false);
        regionCreationCost = plugin.getConfig().getDouble("economy.region-creation-cost", 0.0);
        blockProtectionCost = plugin.getConfig().getDouble("economy.block-protection-cost", 0.0);

        // 高级设置
        eventPriority = plugin.getConfig().getString("advanced.event-priority", "HIGH");
        playSound = plugin.getConfig().getBoolean("advanced.play-sound", true);
        successColor = colorize(plugin.getConfig().getString("advanced.success-color", "&a"));
        errorColor = colorize(plugin.getConfig().getString("advanced.error-color", "&c"));
        locale = plugin.getConfig().getString("locale", "zh_CN");

        plugin.getLogger().info("配置文件加载完成!");
    }

    public void saveConfiguration() {
        plugin.saveConfig();
    }

    public String colorize(String message) {
        if (message == null) return "";
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    // Getters
    public int getMaxRegionsPerPlayer() {
        return maxRegionsPerPlayer;
    }

    public boolean isRegionEnabled() {
        return regionEnabled;
    }

    public Material getSelectionTool() {
        return selectionTool;
    }

    public String getSelectionToolName() {
        return selectionToolName;
    }

    public boolean isBlockProtectionEnabled() {
        return blockProtectionEnabled;
    }

    public List<Material> getDefaultProtectedBlocks() {
        return defaultProtectedBlocks;
    }

    public boolean isCancelExplosion() {
        return cancelExplosion;
    }

    public int getExplosionRadius() {
        return explosionRadius;
    }

    public boolean isShowProtectionMessage() {
        return showProtectionMessage;
    }

    public boolean isLogExplosions() {
        return logExplosions;
    }

    public int getMaxProtectedBlocksPerPlayer() {
        return maxProtectedBlocksPerPlayer;
    }

    public boolean isEconomyEnabled() {
        return economyEnabled;
    }

    public double getRegionCreationCost() {
        return regionCreationCost;
    }

    public double getBlockProtectionCost() {
        return blockProtectionCost;
    }

    public String getEventPriority() {
        return eventPriority;
    }

    public boolean isPlaySound() {
        return playSound;
    }

    public String getSuccessColor() {
        return successColor;
    }

    public String getErrorColor() {
        return errorColor;
    }

    public String getLocale() {
        return locale;
    }
}
