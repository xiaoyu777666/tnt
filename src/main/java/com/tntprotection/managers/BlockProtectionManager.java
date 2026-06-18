package com.tntprotection.managers;

import com.tntprotection.TNTProtectionPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class BlockProtectionManager {

    private final TNTProtectionPlugin plugin;
    private final Set<Material> protectedBlocks;
    private final Map<UUID, Set<Material>> playerCustomProtectedBlocks;
    private File protectedBlocksFile;
    private FileConfiguration protectedBlocksConfig;

    public BlockProtectionManager(TNTProtectionPlugin plugin) {
        this.plugin = plugin;
        this.protectedBlocks = new HashSet<>();
        this.playerCustomProtectedBlocks = new HashMap<>();
        this.protectedBlocksFile = new File(plugin.getDataFolder(), "protected-blocks.yml");
    }

    public void loadProtectedBlocks() {
        // 添加默认防爆方块
        protectedBlocks.addAll(plugin.getConfigManager().getDefaultProtectedBlocks());

        // 从文件加载额外保护的方块
        if (!protectedBlocksFile.exists()) {
            try {
                protectedBlocksFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("无法创建protected-blocks.yml文件: " + e.getMessage());
            }
        }

        protectedBlocksConfig = YamlConfiguration.loadConfiguration(protectedBlocksFile);

        // 加载全局保护的方块
        if (protectedBlocksConfig.contains("global-protected-blocks")) {
            List<String> blocks = protectedBlocksConfig.getStringList("global-protected-blocks");
            for (String block : blocks) {
                try {
                    Material material = Material.valueOf(block);
                    protectedBlocks.add(material);
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().warning("无效的方块ID: " + block);
                }
            }
        }

        // 加载玩家自定义保护的方块
        if (protectedBlocksConfig.contains("player-protected-blocks")) {
            Map<String, Object> playerBlocks = protectedBlocksConfig.getConfigurationSection("player-protected-blocks").getValues(false);
            for (Map.Entry<String, Object> entry : playerBlocks.entrySet()) {
                UUID playerId = UUID.fromString(entry.getKey());
                Set<Material> materials = new HashSet<>();
                if (entry.getValue() instanceof List) {
                    @SuppressWarnings("unchecked")
                    List<String> blockList = (List<String>) entry.getValue();
                    for (String block : blockList) {
                        try {
                            Material material = Material.valueOf(block);
                            materials.add(material);
                        } catch (IllegalArgumentException e) {
                            plugin.getLogger().warning("无效的方块ID: " + block);
                        }
                    }
                }
                playerCustomProtectedBlocks.put(playerId, materials);
            }
        }

        plugin.getLogger().info("已加载 " + protectedBlocks.size() + " 个全局防爆方块");
    }

    public void saveProtectedBlocks() {
        if (protectedBlocksConfig == null) {
            protectedBlocksConfig = YamlConfiguration.loadConfiguration(protectedBlocksFile);
        }

        // 保存全局保护的方块
        List<String> globalBlocks = new ArrayList<>();
        for (Material material : protectedBlocks) {
            globalBlocks.add(material.name());
        }
        protectedBlocksConfig.set("global-protected-blocks", globalBlocks);

        // 保存玩家自定义保护的方块
        Map<String, List<String>> playerBlocksMap = new HashMap<>();
        for (Map.Entry<UUID, Set<Material>> entry : playerCustomProtectedBlocks.entrySet()) {
            List<String> materials = new ArrayList<>();
            for (Material material : entry.getValue()) {
                materials.add(material.name());
            }
            playerBlocksMap.put(entry.getKey().toString(), materials);
        }
        protectedBlocksConfig.set("player-protected-blocks", playerBlocksMap);

        try {
            protectedBlocksConfig.save(protectedBlocksFile);
        } catch (IOException e) {
            plugin.getLogger().severe("无法保存protected-blocks.yml文件: " + e.getMessage());
        }
    }

    public boolean isBlockProtected(Material material) {
        return protectedBlocks.contains(material);
    }

    public boolean isBlockProtectedForPlayer(Material material, UUID playerId) {
        if (protectedBlocks.contains(material)) return true;

        Set<Material> playerBlocks = playerCustomProtectedBlocks.get(playerId);
        return playerBlocks != null && playerBlocks.contains(material);
    }

    public void addProtectedBlock(Material material) {
        protectedBlocks.add(material);
        saveProtectedBlocks();
    }

    public void removeProtectedBlock(Material material) {
        protectedBlocks.remove(material);
        saveProtectedBlocks();
    }

    public void addProtectedBlockForPlayer(Material material, UUID playerId) {
        playerCustomProtectedBlocks
            .computeIfAbsent(playerId, k -> new HashSet<>())
            .add(material);
        saveProtectedBlocks();
    }

    public void removeProtectedBlockForPlayer(Material material, UUID playerId) {
        Set<Material> playerBlocks = playerCustomProtectedBlocks.get(playerId);
        if (playerBlocks != null) {
            playerBlocks.remove(material);
            saveProtectedBlocks();
        }
    }

    public Set<Material> getProtectedBlocks() {
        return new HashSet<>(protectedBlocks);
    }

    public Set<Material> getPlayerProtectedBlocks(UUID playerId) {
        return new HashSet<>(playerCustomProtectedBlocks.getOrDefault(playerId, new HashSet<>()));
    }

    public Set<Material> getAllProtectedBlocksForPlayer(UUID playerId) {
        Set<Material> allBlocks = new HashSet<>(protectedBlocks);
        Set<Material> playerBlocks = playerCustomProtectedBlocks.get(playerId);
        if (playerBlocks != null) {
            allBlocks.addAll(playerBlocks);
        }
        return allBlocks;
    }

    public ItemStack createProtectedBlockItem(Material material, int amount) {
        ItemStack item = new ItemStack(material, amount);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(plugin.getConfigManager().colorize("&6防爆方块 &7(" + material.name() + ")"));
            List<String> lore = new ArrayList<>();
            lore.add(plugin.getConfigManager().colorize("&7此方块具有TNT防爆属性"));
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }

    public boolean giveProtectedBlock(Player player, Material material, int amount) {
        int maxAmount = plugin.getConfigManager().getMaxProtectedBlocksPerPlayer();
        if (maxAmount != -1 && !player.hasPermission("tntprotection.limit.*")) {
            int currentCount = getPlayerProtectedBlocks(player.getUniqueId()).size();
            if (currentCount >= maxAmount) {
                return false;
            }
        }

        ItemStack item = createProtectedBlockItem(material, amount);
        HashMap<Integer, ItemStack> overflow = player.getInventory().addItem(item);
        if (!overflow.isEmpty()) {
            player.getWorld().dropItemNaturally(player.getLocation(), item);
        }

        addProtectedBlockForPlayer(material, player.getUniqueId());
        return true;
    }
}
