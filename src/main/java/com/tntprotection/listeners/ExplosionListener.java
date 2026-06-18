package com.tntprotection.listeners;

import com.tntprotection.TNTProtectionPlugin;
import com.tntprotection.data.RegionData;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ExplosionListener implements Listener {

    private final TNTProtectionPlugin plugin;

    public ExplosionListener(TNTProtectionPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = false)
    public void onExplosion(EntityExplodeEvent event) {
        Entity entity = event.getEntity();

        // 只处理TNT爆炸
        if (!(entity instanceof TNTPrimed)) {
            // 也检查是否是其他来源的爆炸
            String explosionType = entity != null ? entity.getType().name() : "UNKNOWN";
            if (plugin.getConfigManager().isLogExplosions()) {
                plugin.getLogger().info("检测到非TNT爆炸: " + explosionType);
            }
            return;
        }

        Location location = entity.getLocation();
        String worldName = location.getWorld().getName();

        if (plugin.getConfigManager().isLogExplosions()) {
            plugin.getLogger().info("TNT爆炸发生在: " + worldName + " (" +
                    location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ() + ")");
        }

        // 检查是否有玩家拥有绕过权限
        // 如果配置为完全取消爆炸
        if (plugin.getConfigManager().isCancelExplosion()) {
            // 检查是否有bypass权限的玩家在场
            if (!hasBypassPlayerNearby(location)) {
                if (plugin.getConfigManager().isShowProtectionMessage()) {
                    // 向附近玩家发送消息
                    broadcastProtectionMessage(location, "TNT爆炸已被取消");
                }
                event.setCancelled(true);
                return;
            }
        }

        // 获取要保护的位置列表
        List<org.bukkit.block.Block> blocksToProtect = new ArrayList<>();
        List<org.bukkit.block.Block> blocksToRemove = new ArrayList<>();

        Iterator<org.bukkit.block.Block> iterator = event.blockList().iterator();
        while (iterator.hasNext()) {
            org.bukkit.block.Block block = iterator.next();

            // 检查区域保护
            if (plugin.getRegionManager().isLocationProtected(block.getLocation())) {
                RegionData region = plugin.getRegionManager().getRegionAtLocation(block.getLocation());
                if (region != null && plugin.getConfigManager().isLogExplosions()) {
                    plugin.getLogger().info("区域保护: " + block.getType() + " 在区域 " + region.getName() + " 中被保护");
                }
                blocksToProtect.add(block);
                iterator.remove();
                continue;
            }

            // 检查方块保护
            if (plugin.getBlockProtectionManager().isBlockProtected(block.getType())) {
                if (plugin.getConfigManager().isLogExplosions()) {
                    plugin.getLogger().info("方块保护: " + block.getType() + " 被保护");
                }
                blocksToProtect.add(block);
                iterator.remove();
                continue;
            }

            // 检查玩家自定义的防爆方块
            if (isPlayerPlacedProtectedBlock(block)) {
                if (plugin.getConfigManager().isLogExplosions()) {
                    plugin.getLogger().info("玩家防爆方块: " + block.getType() + " 被保护");
                }
                blocksToProtect.add(block);
                iterator.remove();
            }
        }

        // 如果有受保护的方块且配置了显示消息
        if (!blocksToProtect.isEmpty() && plugin.getConfigManager().isShowProtectionMessage()) {
            String message = "保护了 " + blocksToProtect.size() + " 个方块免受TNT爆炸破坏";
            broadcastProtectionMessage(location, message);
        }

        // 如果不是完全取消爆炸，设置较小的爆炸半径
        if (!plugin.getConfigManager().isCancelExplosion() && plugin.getConfigManager().getExplosionRadius() >= 0) {
            int radius = plugin.getConfigManager().getExplosionRadius();
            if (radius == 0) {
                // 完全取消破坏力但不取消爆炸视觉效果
                event.blockList().clear();
            }
            // 注: setRadius在1.13+被移除，使用Bukkit.getServer().createExplosion()控制半径
        }
    }

    // 处理苦力怕改变方块事件
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = false)
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        Entity entity = event.getEntity();

        // 检查是否是爆炸导致的方块变化
        if (entity instanceof TNTPrimed) {
            Location blockLoc = event.getBlock().getLocation();

            // 检查区域保护
            if (plugin.getRegionManager().isLocationProtected(blockLoc)) {
                event.setCancelled(true);
                return;
            }

            // 检查方块保护
            if (plugin.getBlockProtectionManager().isBlockProtected(event.getBlock().getType())) {
                event.setCancelled(true);
            }
        }
    }

    private boolean hasBypassPlayerNearby(Location location) {
        int radius = 50; // 检查50格范围内的玩家
        return location.getWorld().getPlayers().stream()
                .filter(player -> player.hasPermission("tntprotection.bypass"))
                .anyMatch(player -> player.getLocation().distance(location) <= radius);
    }

    private void broadcastProtectionMessage(Location location, String message) {
        int radius = 100;
        location.getWorld().getPlayers().stream()
                .filter(player -> player.getLocation().distance(location) <= radius)
                .forEach(player -> player.sendMessage(
                        plugin.getConfigManager().colorize("&6[TNT Protection] &a" + message)));
    }

    private boolean isPlayerPlacedProtectedBlock(org.bukkit.block.Block block) {
        Material type = block.getType();

        // 检查是否是玩家放置的受保护方块
        // 使用BlockPlaceListener来追踪
        BlockPlaceListener blockPlaceListener = plugin.getBlockPlaceListener();
        if (blockPlaceListener == null) {
            return false;
        }

        // 检查这个方块是否被BlockPlaceListener记录
        return blockPlaceListener.getBlockPlacer(block.getLocation()) != null;
    }
}
