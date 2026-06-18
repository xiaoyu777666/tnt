package com.tntprotection.listeners;

import com.tntprotection.TNTProtectionPlugin;
import com.tntprotection.managers.ConfigManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class PlayerInteractListener implements Listener {

    private final TNTProtectionPlugin plugin;

    public PlayerInteractListener(TNTProtectionPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = false)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item == null) {
            return;
        }

        // 检查是否是选择工具
        if (!isSelectionTool(item)) {
            return;
        }

        // 检查权限
        if (!player.hasPermission("tntprotection.use")) {
            return;
        }

        // 检查区域保护是否启用
        if (!plugin.getConfigManager().isRegionEnabled()) {
            return;
        }

        Action action = event.getAction();

        if (action == Action.LEFT_CLICK_BLOCK) {
            // 左键点击 - 设置起点
            event.setCancelled(true);

            if (event.getClickedBlock() == null) {
                return;
            }

            Location loc = event.getClickedBlock().getLocation();
            plugin.getRegionManager().setPlayerPos1(player, loc);

            sendMessage(player, getSuccessColor() + "已设置起点: " +
                    loc.getWorld().getName() + " (" + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ() + ")");

            // 显示粒子效果或声音提示
            playEffect(player);

        } else if (action == Action.RIGHT_CLICK_BLOCK) {
            // 右键点击 - 设置终点
            event.setCancelled(true);

            if (event.getClickedBlock() == null) {
                return;
            }

            Location loc = event.getClickedBlock().getLocation();
            plugin.getRegionManager().setPlayerPos2(player, loc);

            sendMessage(player, getSuccessColor() + "已设置终点: " +
                    loc.getWorld().getName() + " (" + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ() + ")");

            // 检查是否两点都已选择
            if (plugin.getRegionManager().hasSelectedBothPositions(player)) {
                Location pos1 = plugin.getRegionManager().getPlayerPos1(player);
                Location pos2 = plugin.getRegionManager().getPlayerPos2(player);

                if (pos1.getWorld().equals(pos2.getWorld())) {
                    int volume = (Math.abs(pos2.getBlockX() - pos1.getBlockX()) + 1) *
                                (Math.abs(pos2.getBlockY() - pos1.getBlockY()) + 1) *
                                (Math.abs(pos2.getBlockZ() - pos1.getBlockZ()) + 1);

                    sendMessage(player, getSuccessColor() + "区域已选择完成! 体积: " + volume + " 方块");
                    sendMessage(player, getErrorColor() + "使用 /tnt create <名称> 来创建保护区域");
                } else {
                    sendMessage(player, getErrorColor() + "错误: 起点和终点必须在同一个世界!");
                }
            }

            // 显示粒子效果或声音提示
            playEffect(player);
        }
    }

    private boolean isSelectionTool(ItemStack item) {
        if (item == null) {
            return false;
        }

        Material type = item.getType();

        // 检查类型
        if (!type.equals(plugin.getConfigManager().getSelectionTool())) {
            return false;
        }

        // 检查名称（如果有的话）
        ItemMeta meta = item.getItemMeta();
        if (meta != null && meta.hasDisplayName()) {
            String displayName = meta.getDisplayName();
            String toolName = plugin.getConfigManager().getSelectionToolName();
            return ChatColor.translateAlternateColorCodes('&', toolName).equals(displayName) ||
                   displayName.contains("区域选择");
        }

        // 如果没有自定义名称，检查材质作为后备
        return true;
    }

    private void sendMessage(Player player, String message) {
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }

    private void playEffect(Player player) {
        if (plugin.getConfigManager().isPlaySound()) {
            try {
                player.playSound(player.getLocation(), org.bukkit.Sound.valueOf("WOOD_CLICK"), 1.0f, 1.0f);
            } catch (IllegalArgumentException e) {
                // 忽略不支持的声音
            }
        }
    }

    private String getSuccessColor() {
        return plugin.getConfigManager().getSuccessColor();
    }

    private String getErrorColor() {
        return plugin.getConfigManager().getErrorColor();
    }
}
