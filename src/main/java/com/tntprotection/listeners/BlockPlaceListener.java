package com.tntprotection.listeners;

import com.tntprotection.TNTProtectionPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BlockPlaceListener implements Listener {

    private final TNTProtectionPlugin plugin;
    // 存储玩家放置的防爆方块位置 (Location -> PlayerUUID)
    private final Map<String, UUID> placedProtectedBlocks;

    public BlockPlaceListener(TNTProtectionPlugin plugin) {
        this.plugin = plugin;
        this.placedProtectedBlocks = new HashMap<>();
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = false)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Material type = event.getBlock().getType();
        Location loc = event.getBlock().getLocation();

        // 检查是否是受保护的方块类型
        if (!plugin.getBlockProtectionManager().isBlockProtected(type)) {
            return;
        }

        // 检查玩家是否有自定义的防爆方块
        if (!plugin.getBlockProtectionManager().isBlockProtectedForPlayer(type, player.getUniqueId())) {
            return;
        }

        // 记录这个方块是此玩家放置的
        String key = locationToKey(loc);
        placedProtectedBlocks.put(key, player.getUniqueId());

        if (plugin.getConfigManager().isLogExplosions()) {
            plugin.getLogger().info("玩家 " + player.getName() + " 放置了防爆方块: " + type + " at " + key);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = false)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Location loc = event.getBlock().getLocation();
        String key = locationToKey(loc);

        // 检查这是否是玩家放置的防爆方块
        UUID placer = placedProtectedBlocks.get(key);

        if (placer != null) {
            // 如果是放置该方块的玩家打破，则移除记录
            if (placer.equals(player.getUniqueId())) {
                placedProtectedBlocks.remove(key);

                // 同步移除玩家受保护方块列表中的记录
                plugin.getBlockProtectionManager().removeProtectedBlockForPlayer(
                        event.getBlock().getType(), player.getUniqueId());

                if (plugin.getConfigManager().isLogExplosions()) {
                    plugin.getLogger().info("玩家 " + player.getName() + " 移除了防爆方块记录: " + event.getBlock().getType());
                }
            } else {
                // 其他玩家不能破坏
                if (!player.hasPermission("tntprotection.admin")) {
                    event.setCancelled(true);
                    player.sendMessage(plugin.getConfigManager().colorize(
                            "&c这是其他玩家放置的防爆方块，你不能破坏它!"));
                }
            }
        }
    }

    public boolean isPlayerPlacedProtectedBlock(Location loc, UUID playerId) {
        String key = locationToKey(loc);
        UUID placer = placedProtectedBlocks.get(key);

        if (placer == null) {
            return false;
        }

        return placer.equals(playerId);
    }

    public UUID getBlockPlacer(Location loc) {
        return placedProtectedBlocks.get(locationToKey(loc));
    }

    private String locationToKey(Location loc) {
        return loc.getWorld().getName() + ":" + loc.getBlockX() + ":" + loc.getBlockY() + ":" + loc.getBlockZ();
    }

    public void clearPlayerBlocks(UUID playerId) {
        placedProtectedBlocks.entrySet().removeIf(entry -> entry.getValue().equals(playerId));
    }

    public int getPlayerBlockCount(UUID playerId) {
        return (int) placedProtectedBlocks.values().stream()
                .filter(uuid -> uuid.equals(playerId))
                .count();
    }
}
