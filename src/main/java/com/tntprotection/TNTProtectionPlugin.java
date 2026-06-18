package com.tntprotection;

import com.tntprotection.commands.TNTCommand;
import com.tntprotection.listeners.BlockPlaceListener;
import com.tntprotection.listeners.ExplosionListener;
import com.tntprotection.listeners.PlayerInteractListener;
import com.tntprotection.managers.BlockProtectionManager;
import com.tntprotection.managers.ConfigManager;
import com.tntprotection.managers.RegionManager;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

public class TNTProtectionPlugin extends JavaPlugin {

    private static TNTProtectionPlugin instance;
    private ConfigManager configManager;
    private RegionManager regionManager;
    private BlockProtectionManager blockProtectionManager;
    private BlockPlaceListener blockPlaceListener;

    @Override
    public void onEnable() {
        instance = this;

        // 保存默认配置文件
        saveDefaultConfig();

        // 初始化配置管理器
        configManager = new ConfigManager(this);
        configManager.loadConfiguration();

        // 初始化区域管理器
        regionManager = new RegionManager(this);
        regionManager.loadRegions();

        // 初始化方块保护管理器
        blockProtectionManager = new BlockProtectionManager(this);
        blockProtectionManager.loadProtectedBlocks();

        // 注册指令
        TNTCommand tntCommand = new TNTCommand(this);
        getCommand("tnt").setExecutor(tntCommand);
        getCommand("tnt").setTabCompleter(tntCommand);

        // 注册事件监听器
        blockPlaceListener = new BlockPlaceListener(this);
        getServer().getPluginManager().registerEvents(blockPlaceListener, this);
        getServer().getPluginManager().registerEvents(new ExplosionListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerInteractListener(this), this);

        // 检查版本兼容性
        checkVersionCompatibility();

        getLogger().info("========================================");
        getLogger().info("FKTNT 插件已成功启用!");
        getLogger().info("版本: " + getDescription().getVersion());
        getLogger().info("作者: xiaoyu");
        getLogger().info("支持版本: 1.8.x - 26.2");
        getLogger().info("========================================");
    }

    @Override
    public void onDisable() {
        // 保存区域数据
        if (regionManager != null) {
            regionManager.saveRegions();
        }

        // 保存配置
        if (configManager != null) {
            configManager.saveConfiguration();
        }

        getLogger().info("FKTNT 插件已禁用!");
    }

    private void checkVersionCompatibility() {
        String version = getServer().getVersion();
        getLogger().info("检测到服务器版本: " + version);
        if (version.contains("1.8") || version.contains("1.9") ||
            version.contains("1.10") || version.contains("1.11") ||
            version.contains("1.12") || version.contains("1.13") ||
            version.contains("1.14") || version.contains("1.15") ||
            version.contains("1.16") || version.contains("1.17") ||
            version.contains("1.18") || version.contains("1.19") ||
            version.contains("1.20") || version.contains("1.21") ||
            version.contains("1.22") || version.contains("1.23") ||
            version.contains("1.24") || version.contains("1.25") ||
            version.contains("1.26") || version.contains("26.2")) {
            getLogger().info("版本兼容性检查通过!");
        } else {
            getLogger().warning("未测试的服务器版本，可能存在兼容性问题!");
        }
    }

    public Material getSelectionTool() {
        return configManager.getSelectionTool();
    }

    public String getSelectionToolName() {
        return configManager.getSelectionToolName();
    }

    public static TNTProtectionPlugin getInstance() {
        return instance;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public RegionManager getRegionManager() {
        return regionManager;
    }

    public BlockProtectionManager getBlockProtectionManager() {
        return blockProtectionManager;
    }

    public BlockPlaceListener getBlockPlaceListener() {
        return blockPlaceListener;
    }
}
