package com.tntprotection.commands;

import com.tntprotection.TNTProtectionPlugin;
import com.tntprotection.data.RegionData;
import com.tntprotection.managers.ConfigManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.SimpleDateFormat;
import java.util.*;

public class TNTCommand implements CommandExecutor, TabCompleter {

    private final TNTProtectionPlugin plugin;
    private final Map<UUID, String> pendingRegionNames;

    public TNTCommand(TNTProtectionPlugin plugin) {
        this.plugin = plugin;
        this.pendingRegionNames = new HashMap<>();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(colorize("&c此命令只能由玩家执行!"));
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            sendHelp(player);
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "help":
                sendHelp(player);
                break;
            case "get":
            case "axe":
                handleGetAxe(player);
                break;
            case "pos1":
                handlePos1(player);
                break;
            case "pos2":
                handlePos2(player);
                break;
            case "selection":
            case "sel":
                handleSelection(player);
                break;
            case "clear":
                handleClear(player);
                break;
            case "create":
            case "add":
                if (args.length < 2) {
                    sendMessage(player, getErrorColor() + "请指定区域名称! 用法: /tnt create <区域名称>");
                } else {
                    handleCreate(player, args[1]);
                }
                break;
            case "remove":
            case "delete":
                if (args.length < 2) {
                    sendMessage(player, getErrorColor() + "请指定区域名称! 用法: /tnt remove <区域名称>");
                } else {
                    handleRemove(player, args[1]);
                }
                break;
            case "list":
                handleList(player);
                break;
            case "hq":
                handleHQ(player, args);
                break;
            case "info":
                if (args.length < 2) {
                    sendMessage(player, getErrorColor() + "请指定区域名称! 用法: /tnt info <区域名称>");
                } else {
                    handleInfo(player, args[1]);
                }
                break;
            case "reload":
                handleReload(player);
                break;
            case "addblock":
            case "addprotected":
                if (args.length < 2) {
                    sendMessage(player, getErrorColor() + "请指定方块ID! 用法: /tnt addblock <方块ID>");
                } else {
                    handleAddBlock(player, args[1]);
                }
                break;
            case "removeblock":
            case "removeprotected":
                if (args.length < 2) {
                    sendMessage(player, getErrorColor() + "请指定方块ID! 用法: /tnt removeblock <方块ID>");
                } else {
                    handleRemoveBlock(player, args[1]);
                }
                break;
            case "listblocks":
                handleListBlocks(player);
                break;
            default:
                sendMessage(player, getErrorColor() + "未知命令! 输入 /tnt help 查看帮助");
                break;
        }

        return true;
    }

    private void sendHelp(Player player) {
        sendMessage(player, getSuccessColor() + "========== TNTProtection 帮助 ==========");
        sendMessage(player, getSuccessColor() + "/tnt get" + getErrorColor() + " - 获取区域选择工具(金斧头)");
        sendMessage(player, getSuccessColor() + "/tnt pos1" + getErrorColor() + " - 设置区域起点");
        sendMessage(player, getSuccessColor() + "/tnt pos2" + getErrorColor() + " - 设置区域终点");
        sendMessage(player, getSuccessColor() + "/tnt selection" + getErrorColor() + " - 查看当前选择");
        sendMessage(player, getSuccessColor() + "/tnt clear" + getErrorColor() + " - 清除当前选择");
        sendMessage(player, getSuccessColor() + "/tnt create <名称>" + getErrorColor() + " - 创建保护区域");
        sendMessage(player, getSuccessColor() + "/tnt remove <名称>" + getErrorColor() + " - 删除保护区域");
        sendMessage(player, getSuccessColor() + "/tnt list" + getErrorColor() + " - 查看所有保护区域");
        sendMessage(player, getSuccessColor() + "/tnt info <名称>" + getErrorColor() + " - 查看区域详情");
        sendMessage(player, getSuccessColor() + "/tnt hq [方块ID]" + getErrorColor() + " - 获取防爆方块");
        sendMessage(player, getSuccessColor() + "/tnt addblock <方块ID>" + getErrorColor() + " - 添加防爆方块");
        sendMessage(player, getSuccessColor() + "/tnt removeblock <方块ID>" + getErrorColor() + " - 移除防爆方块");
        sendMessage(player, getSuccessColor() + "/tnt listblocks" + getErrorColor() + " - 查看已保护方块");
        sendMessage(player, getSuccessColor() + "/tnt reload" + getErrorColor() + " - 重载配置文件");
        sendMessage(player, getSuccessColor() + "=========================================");
    }

    private void handleGetAxe(Player player) {
        if (!player.hasPermission("tntprotection.use")) {
            sendMessage(player, getErrorColor() + "你没有权限使用此命令!");
            return;
        }

        if (!plugin.getConfigManager().isRegionEnabled()) {
            sendMessage(player, getErrorColor() + "区域保护功能已被禁用!");
            return;
        }

        Material tool = plugin.getSelectionTool();
        ItemStack item = new ItemStack(tool, 1);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(plugin.getSelectionToolName());
            List<String> lore = new ArrayList<>();
            lore.add(colorize("&7左键点击设置起点"));
            lore.add(colorize("&7右键点击设置终点"));
            meta.setLore(lore);
            item.setItemMeta(meta);
        }

        player.getInventory().addItem(item);
        sendMessage(player, getSuccessColor() + "你已获得区域选择工具!");
        sendMessage(player, getErrorColor() + "提示: " + getSuccessColor() + "左键点击设置起点，右键点击设置终点");
    }

    private void handlePos1(Player player) {
        if (!player.hasPermission("tntprotection.use")) {
            sendMessage(player, getErrorColor() + "你没有权限使用此命令!");
            return;
        }

        if (!plugin.getRegionManager().isSelectionTool(player)) {
            sendMessage(player, getErrorColor() + "你必须手持区域选择工具!");
            return;
        }

        Location loc = player.getLocation();
        plugin.getRegionManager().setPlayerPos1(player, loc);
        sendMessage(player, getSuccessColor() + "已设置起点: " +
                loc.getWorld().getName() + " (" + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ() + ")");
    }

    private void handlePos2(Player player) {
        if (!player.hasPermission("tntprotection.use")) {
            sendMessage(player, getErrorColor() + "你没有权限使用此命令!");
            return;
        }

        if (!plugin.getRegionManager().isSelectionTool(player)) {
            sendMessage(player, getErrorColor() + "你必须手持区域选择工具!");
            return;
        }

        Location loc = player.getLocation();
        plugin.getRegionManager().setPlayerPos2(player, loc);
        sendMessage(player, getSuccessColor() + "已设置终点: " +
                loc.getWorld().getName() + " (" + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ() + ")");
    }

    private void handleSelection(Player player) {
        if (!player.hasPermission("tntprotection.use")) {
            sendMessage(player, getErrorColor() + "你没有权限使用此命令!");
            return;
        }

        Location pos1 = plugin.getRegionManager().getPlayerPos1(player);
        Location pos2 = plugin.getRegionManager().getPlayerPos2(player);

        sendMessage(player, getSuccessColor() + "========== 当前选择 ==========");

        if (pos1 != null) {
            sendMessage(player, getSuccessColor() + "起点: " + getErrorColor() +
                    pos1.getWorld().getName() + " (" + pos1.getBlockX() + ", " + pos1.getBlockY() + ", " + pos1.getBlockZ() + ")");
        } else {
            sendMessage(player, getSuccessColor() + "起点: " + getErrorColor() + "未设置");
        }

        if (pos2 != null) {
            sendMessage(player, getSuccessColor() + "终点: " + getErrorColor() +
                    pos2.getWorld().getName() + " (" + pos2.getBlockX() + ", " + pos2.getBlockY() + ", " + pos2.getBlockZ() + ")");
        } else {
            sendMessage(player, getSuccessColor() + "终点: " + getErrorColor() + "未设置");
        }

        if (pos1 != null && pos2 != null) {
            if (pos1.getWorld().equals(pos2.getWorld())) {
                int volume = (Math.abs(pos2.getBlockX() - pos1.getBlockX()) + 1) *
                            (Math.abs(pos2.getBlockY() - pos1.getBlockY()) + 1) *
                            (Math.abs(pos2.getBlockZ() - pos1.getBlockZ()) + 1);
                sendMessage(player, getSuccessColor() + "区域体积: " + getErrorColor() + volume + " 方块");
            } else {
                sendMessage(player, getErrorColor() + "起点和终点不在同一个世界!");
            }
        }
        sendMessage(player, getSuccessColor() + "==============================");
    }

    private void handleClear(Player player) {
        plugin.getRegionManager().clearPlayerSelection(player);
        sendMessage(player, getSuccessColor() + "已清除当前选择!");
    }

    private void handleCreate(Player player, String regionName) {
        if (!player.hasPermission("tntprotection.use")) {
            sendMessage(player, getErrorColor() + "你没有权限使用此命令!");
            return;
        }

        if (!plugin.getConfigManager().isRegionEnabled()) {
            sendMessage(player, getErrorColor() + "区域保护功能已被禁用!");
            return;
        }

        if (!plugin.getRegionManager().canCreateRegion(player)) {
            int maxRegions = plugin.getConfigManager().getMaxRegionsPerPlayer();
            sendMessage(player, getErrorColor() + "你已达到最大区域数量限制 (" + maxRegions + ")!");
            return;
        }

        if (!plugin.getRegionManager().hasSelectedBothPositions(player)) {
            sendMessage(player, getErrorColor() + "请先选择两个点! 使用 /tnt pos1 和 /tnt pos2 设置起点和终点");
            return;
        }

        Location pos1 = plugin.getRegionManager().getPlayerPos1(player);
        Location pos2 = plugin.getRegionManager().getPlayerPos2(player);

        if (!pos1.getWorld().equals(pos2.getWorld())) {
            sendMessage(player, getErrorColor() + "起点和终点必须在同一个世界!");
            return;
        }

        // 检查是否与现有区域重叠
        for (RegionData region : plugin.getRegionManager().getAllRegions()) {
            if (region.getWorld().equals(pos1.getWorld().getName())) {
                if (regionsOverlap(pos1, pos2, region)) {
                    sendMessage(player, getErrorColor() + "此区域与现有保护区域 '" + region.getName() + "' 重叠!");
                    return;
                }
            }
        }

        RegionData region = new RegionData(regionName, player.getUniqueId(), pos1, pos2);
        plugin.getRegionManager().addRegion(region);

        sendMessage(player, getSuccessColor() + "保护区域 '" + regionName + "' 创建成功!");
        sendMessage(player, getSuccessColor() + "区域大小: " +
                region.getSizeX() + "x" + region.getSizeY() + "x" + region.getSizeZ() +
                " (" + region.getVolume() + " 方块)");
    }

    private boolean regionsOverlap(Location pos1, Location pos2, RegionData existing) {
        int minX = Math.min(pos1.getBlockX(), pos2.getBlockX());
        int maxX = Math.max(pos1.getBlockX(), pos2.getBlockX());
        int minY = Math.min(pos1.getBlockY(), pos2.getBlockY());
        int maxY = Math.max(pos1.getBlockY(), pos2.getBlockY());
        int minZ = Math.min(pos1.getBlockZ(), pos2.getBlockZ());
        int maxZ = Math.max(pos1.getBlockZ(), pos2.getBlockZ());

        return !(maxX < existing.getMinX() || minX > existing.getMaxX() ||
                 maxY < existing.getMinY() || minY > existing.getMaxY() ||
                 maxZ < existing.getMinZ() || minZ > existing.getMaxZ());
    }

    private void handleRemove(Player player, String regionName) {
        if (!player.hasPermission("tntprotection.use") && !player.hasPermission("tntprotection.admin")) {
            sendMessage(player, getErrorColor() + "你没有权限使用此命令!");
            return;
        }

        List<RegionData> regions = plugin.getRegionManager().getPlayerRegions(player.getUniqueId());
        RegionData toRemove = null;

        for (RegionData region : regions) {
            if (region.getName().equalsIgnoreCase(regionName)) {
                toRemove = region;
                break;
            }
        }

        if (toRemove == null) {
            if (player.hasPermission("tntprotection.admin")) {
                for (RegionData region : plugin.getRegionManager().getAllRegions()) {
                    if (region.getName().equalsIgnoreCase(regionName)) {
                        toRemove = region;
                        break;
                    }
                }
            }
        }

        if (toRemove == null) {
            sendMessage(player, getErrorColor() + "未找到名为 '" + regionName + "' 的保护区域!");
            return;
        }

        plugin.getRegionManager().removeRegion(toRemove.getName(), toRemove.getOwner());
        sendMessage(player, getSuccessColor() + "保护区域 '" + regionName + "' 已删除!");
    }

    private void handleList(Player player) {
        if (!player.hasPermission("tntprotection.use")) {
            sendMessage(player, getErrorColor() + "你没有权限使用此命令!");
            return;
        }

        List<RegionData> regions = plugin.getRegionManager().getAllRegions();

        sendMessage(player, getSuccessColor() + "========== 保护区域列表 ==========");

        if (regions.isEmpty()) {
            sendMessage(player, getErrorColor() + "当前没有任何保护区域!");
        } else {
            for (RegionData region : regions) {
                String ownerName = plugin.getServer().getPlayer(region.getOwner()) != null ?
                        plugin.getServer().getPlayer(region.getOwner()).getName() : "离线玩家";
                String info = getSuccessColor() + "- " + region.getName() +
                        getErrorColor() + " | 所有者: " + getSuccessColor() + ownerName +
                        getErrorColor() + " | 大小: " + getSuccessColor() + region.getVolume() + " 方块";
                sendMessage(player, info);
            }
        }

        sendMessage(player, getSuccessColor() + "总计: " + regions.size() + " 个区域");
        sendMessage(player, getSuccessColor() + "====================================");
    }

    private void handleHQ(Player player, String[] args) {
        if (!player.hasPermission("tntprotection.hq")) {
            sendMessage(player, getErrorColor() + "你没有权限使用此命令!");
            return;
        }

        if (!plugin.getConfigManager().isBlockProtectionEnabled()) {
            sendMessage(player, getErrorColor() + "方块保护功能已被禁用!");
            return;
        }

        Material material;

        if (args.length < 2) {
            // 默认给予钻石块
            material = Material.DIAMOND_BLOCK;
        } else {
            try {
                material = Material.valueOf(args[1].toUpperCase());
            } catch (IllegalArgumentException e) {
                sendMessage(player, getErrorColor() + "无效的方块ID: " + args[1]);
                sendMessage(player, getErrorColor() + "示例: /tnt hq DIAMOND_BLOCK");
                return;
            }
        }

        if (!material.isBlock()) {
            sendMessage(player, getErrorColor() + material.name() + " 不是方块类型!");
            return;
        }

        if (plugin.getBlockProtectionManager().giveProtectedBlock(player, material, 1)) {
            sendMessage(player, getSuccessColor() + "你获得了1个防爆方块: " + material.name());
            sendMessage(player, getSuccessColor() + "放置此方块后，它将具有TNT防爆属性!");
        } else {
            int maxBlocks = plugin.getConfigManager().getMaxProtectedBlocksPerPlayer();
            sendMessage(player, getErrorColor() + "你已达到最大防爆方块数量限制 (" + maxBlocks + ")!");
        }
    }

    private void handleInfo(Player player, String regionName) {
        if (!player.hasPermission("tntprotection.use")) {
            sendMessage(player, getErrorColor() + "你没有权限使用此命令!");
            return;
        }

        RegionData region = null;
        for (RegionData r : plugin.getRegionManager().getAllRegions()) {
            if (r.getName().equalsIgnoreCase(regionName)) {
                region = r;
                break;
            }
        }

        if (region == null) {
            sendMessage(player, getErrorColor() + "未找到名为 '" + regionName + "' 的保护区域!");
            return;
        }

        String ownerName = plugin.getServer().getPlayer(region.getOwner()) != null ?
                plugin.getServer().getPlayer(region.getOwner()).getName() : "离线玩家";

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        sendMessage(player, getSuccessColor() + "========== 区域信息 ==========");
        sendMessage(player, getSuccessColor() + "名称: " + getErrorColor() + region.getName());
        sendMessage(player, getSuccessColor() + "所有者: " + getErrorColor() + ownerName);
        sendMessage(player, getSuccessColor() + "世界: " + getErrorColor() + region.getWorld());
        sendMessage(player, getSuccessColor() + "范围:");
        sendMessage(player, getErrorColor() + "  X: " + region.getMinX() + " ~ " + region.getMaxX());
        sendMessage(player, getErrorColor() + "  Y: " + region.getMinY() + " ~ " + region.getMaxY());
        sendMessage(player, getErrorColor() + "  Z: " + region.getMinZ() + " ~ " + region.getMaxZ());
        sendMessage(player, getSuccessColor() + "大小: " + getErrorColor() +
                region.getSizeX() + "x" + region.getSizeY() + "x" + region.getSizeZ() +
                " (" + region.getVolume() + " 方块)");
        sendMessage(player, getSuccessColor() + "创建时间: " + getErrorColor() + sdf.format(new Date(region.getCreatedTime())));
        sendMessage(player, getSuccessColor() + "================================");
    }

    private void handleReload(Player player) {
        if (!player.hasPermission("tntprotection.admin")) {
            sendMessage(player, getErrorColor() + "你没有权限使用此命令!");
            return;
        }

        plugin.getConfigManager().loadConfiguration();
        plugin.getRegionManager().loadRegions();
        plugin.getBlockProtectionManager().loadProtectedBlocks();

        sendMessage(player, getSuccessColor() + "配置文件已重载!");
    }

    private void handleAddBlock(Player player, String blockId) {
        if (!player.hasPermission("tntprotection.admin")) {
            sendMessage(player, getErrorColor() + "你没有权限使用此命令!");
            return;
        }

        try {
            Material material = Material.valueOf(blockId.toUpperCase());
            if (!material.isBlock()) {
                sendMessage(player, getErrorColor() + material.name() + " 不是方块类型!");
                return;
            }

            plugin.getBlockProtectionManager().addProtectedBlock(material);
            sendMessage(player, getSuccessColor() + "已将 " + material.name() + " 添加到全局防爆列表!");
        } catch (IllegalArgumentException e) {
            sendMessage(player, getErrorColor() + "无效的方块ID: " + blockId);
        }
    }

    private void handleRemoveBlock(Player player, String blockId) {
        if (!player.hasPermission("tntprotection.admin")) {
            sendMessage(player, getErrorColor() + "你没有权限使用此命令!");
            return;
        }

        try {
            Material material = Material.valueOf(blockId.toUpperCase());
            plugin.getBlockProtectionManager().removeProtectedBlock(material);
            sendMessage(player, getSuccessColor() + "已将 " + material.name() + " 从全局防爆列表移除!");
        } catch (IllegalArgumentException e) {
            sendMessage(player, getErrorColor() + "无效的方块ID: " + blockId);
        }
    }

    private void handleListBlocks(Player player) {
        if (!player.hasPermission("tntprotection.use")) {
            sendMessage(player, getErrorColor() + "你没有权限使用此命令!");
            return;
        }

        Set<Material> blocks = plugin.getBlockProtectionManager().getProtectedBlocks();

        sendMessage(player, getSuccessColor() + "========== 全局防爆方块 ==========");

        if (blocks.isEmpty()) {
            sendMessage(player, getErrorColor() + "当前没有全局防爆方块!");
        } else {
            int count = 0;
            StringBuilder sb = new StringBuilder();
            for (Material material : blocks) {
                sb.append(getErrorColor()).append(material.name()).append(getSuccessColor()).append(", ");
                count++;
                if (count % 5 == 0) {
                    sendMessage(player, sb.toString());
                    sb = new StringBuilder();
                }
            }
            if (count % 5 != 0) {
                sendMessage(player, sb.toString().substring(0, sb.length() - 2));
            }
            sendMessage(player, getSuccessColor() + "总计: " + getErrorColor() + blocks.size() + getSuccessColor() + " 种方块");
        }

        sendMessage(player, getSuccessColor() + "==================================");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            List<String> subCommands = Arrays.asList(
                "help", "get", "pos1", "pos2", "selection", "clear",
                "create", "remove", "list", "info", "hq",
                "addblock", "removeblock", "listblocks", "reload"
            );
            String input = args[0].toLowerCase();
            for (String sub : subCommands) {
                if (sub.startsWith(input)) {
                    completions.add(sub);
                }
            }
        } else if (args.length == 2) {
            String subCommand = args[0].toLowerCase();
            if (subCommand.equals("remove") || subCommand.equals("info")) {
                for (RegionData region : plugin.getRegionManager().getAllRegions()) {
                    completions.add(region.getName());
                }
            } else if (subCommand.equals("hq") || subCommand.equals("addblock") || subCommand.equals("removeblock")) {
                for (Material material : Material.values()) {
                    if (material.isBlock() && material.name().toLowerCase().contains(args[1].toLowerCase())) {
                        completions.add(material.name());
                    }
                }
            }
        }

        return completions;
    }

    private String colorize(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    private void sendMessage(Player player, String message) {
        player.sendMessage(colorize(message));
    }

    private String getSuccessColor() {
        return plugin.getConfigManager().getSuccessColor();
    }

    private String getErrorColor() {
        return plugin.getConfigManager().getErrorColor();
    }
}
