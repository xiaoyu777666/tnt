# FKTNT 插件使用教程

作者: xiaoyu  
版本: 1.0.0  
支持版本: 1.8.x - 26.2  

---

## 目录
1. [插件安装](#插件安装)
2. [区域保护功能](#区域保护功能)
3. [方块保护功能](#方块保护功能)
4. [命令列表](#命令列表)
5. [权限配置](#权限配置)
6. [配置文件说明](#配置文件说明)
7. [常见问题](#常见问题)

---

## 插件安装

1. 将 `FKTNT-1.0.0.jar` 文件放入服务器的 `plugins/` 目录
2. 启动服务器，插件会自动加载
3. 首次加载会自动生成配置文件 `plugins/FKTNT/config.yml`

### 启动日志示例
```
========================================
FKTNT 插件已成功启用!
版本: 1.0.0
作者: xiaoyu
支持版本: 1.8.x - 26.2
========================================
```

---

## 区域保护功能

### 使用步骤

1. **获取区域选择工具**
   ```
   /tnt get
   ```
   执行后会获得一把金斧头，这是区域选择工具。

2. **选择起点**
   - 手持金斧头，**左键点击**任意方块
   - 系统会提示：`已设置起点: world (x, y, z)`

3. **选择终点**
   - 手持金斧头，**右键点击**任意方块
   - 系统会提示：`已设置终点: world (x, y, z)`

4. **创建保护区域**
   ```
   /tnt create <区域名称>
   ```
   示例：`/tnt create my_base`

5. **完成保护**
   - 区域创建成功后，该区域内的所有方块都不会被TNT爆炸破坏

### 区域选择提示

- 起点和终点必须在同一个世界
- 区域会自动计算两点之间的最小包围盒（三维空间）
- 支持跨高度选择（Y轴）

### 管理保护区域

```
# 查看当前选择
/tnt selection

# 清除选择
/tnt clear

# 删除保护区域
/tnt remove <区域名称>

# 列出所有区域
/tnt list

# 查看区域详情
/tnt info <区域名称>
```

---

## 方块保护功能

### 使用步骤

1. **获取防爆方块**
   ```
   /tnt hq [方块ID]
   ```
   
   - 如果不指定方块ID，默认给予钻石块
   - 示例：`/tnt hq IRON_BLOCK`

2. **放置防爆方块**
   - 将获得的防爆方块放置在任意位置
   - 放置后该方块具有TNT防爆属性

3. **效果**
   - 当TNT爆炸时，防爆方块不会被破坏
   - 其他玩家无法破坏你放置的防爆方块（除非具有管理员权限）

### 支持的方块ID

常见方块ID示例：
- `DIAMOND_BLOCK` - 钻石块
- `IRON_BLOCK` - 铁块
- `GOLD_BLOCK` - 金块
- `EMERALD_BLOCK` - 绿宝石块
- `OBSIDIAN` - 黑曜石
- `BEDROCK` - 基岩
- `CHEST` - 箱子
- `ENDER_CHEST` - 末影箱
- `ANVIL` - 铁砧

---

## 命令列表

### 基础命令

| 命令 | 说明 | 权限 |
|------|------|------|
| `/tnt help` | 显示帮助信息 | 所有玩家 |
| `/tnt get` | 获取区域选择工具 | `tntprotection.use` |

### 区域操作

| 命令 | 说明 | 权限 |
|------|------|------|
| `/tnt pos1` | 设置区域起点 | `tntprotection.use` |
| `/tnt pos2` | 设置区域终点 | `tntprotection.use` |
| `/tnt selection` | 查看当前选择 | `tntprotection.use` |
| `/tnt clear` | 清除当前选择 | `tntprotection.use` |
| `/tnt create <名称>` | 创建保护区域 | `tntprotection.use` |
| `/tnt remove <名称>` | 删除保护区域 | `tntprotection.use` |
| `/tnt list` | 列出所有区域 | `tntprotection.use` |
| `/tnt info <名称>` | 查看区域详情 | `tntprotection.use` |

### 方块保护

| 命令 | 说明 | 权限 |
|------|------|------|
| `/tnt hq [方块ID]` | 获取防爆方块 | `tntprotection.hq` |

### 管理命令

| 命令 | 说明 | 权限 |
|------|------|------|
| `/tnt addblock <方块ID>` | 添加全局防爆方块 | `tntprotection.admin` |
| `/tnt removeblock <方块ID>` | 移除全局防爆方块 | `tntprotection.admin` |
| `/tnt listblocks` | 查看已保护方块列表 | `tntprotection.use` |
| `/tnt reload` | 重载配置文件 | `tntprotection.admin` |

---

## 权限配置

### 权限节点

| 权限节点 | 说明 | 默认值 |
|----------|------|--------|
| `tntprotection.use` | 允许使用基础命令 | true |
| `tntprotection.admin` | 管理员权限 | op |
| `tntprotection.bypass` | 绕过TNT保护限制 | op |
| `tntprotection.limit.*` | 无限制创建区域 | op |
| `tntprotection.hq` | 使用防爆方块命令 | true |

### 示例权限组配置

在 `plugins/PermissionsEx/groups.yml` 中：

```yaml
admin:
  permissions:
    - tntprotection.admin
    - tntprotection.bypass
    - tntprotection.limit.*
member:
  permissions:
    - tntprotection.use
    - tntprotection.hq
guest:
  permissions:
    - tntprotection.use
```

---

## 配置文件说明

配置文件位置：`plugins/FKTNT/config.yml`

### 区域设置

```yaml
region:
  max-regions-per-player: 10    # 单个玩家最大区域数量 (-1=无限制)
  enabled: true                  # 是否启用区域保护
  selection-tool: GOLDEN_AXE     # 区域选择工具类型
  selection-tool-name: "&6[区域选择斧]&r"  # 工具名称
```

### 方块保护设置

```yaml
block-protection:
  enabled: true                  # 是否启用方块保护
  default-protected-blocks:      # 默认防爆方块
    - DIAMOND_BLOCK
    - EMERALD_BLOCK
    - GOLD_BLOCK
    - IRON_BLOCK
    - OBSIDIAN
    - BEDROCK
    - ENDER_CHEST
    - ANVIL
    - CHEST
    - TRAPPED_CHEST
```

### 爆炸设置

```yaml
explosion:
  cancel-explosion: false        # 是否完全取消爆炸
  explosion-radius: 0            # 爆炸半径 (0=无破坏)
  show-protection-message: true  # 是否显示保护消息
  log-explosions: true           # 是否记录爆炸日志
```

### 高级设置

```yaml
advanced:
  event-priority: HIGH           # 事件处理优先级
  play-sound: true               # 是否播放声音提示
  success-color: "&a"            # 成功消息颜色
  error-color: "&c"              # 错误消息颜色
```

---

## 常见问题

### Q1: 区域保护不生效？

**可能原因：**
1. 区域未正确创建
2. 起点和终点在不同世界
3. 玩家具有 `tntprotection.bypass` 权限

**解决方法：**
- 使用 `/tnt selection` 检查选择是否正确
- 使用 `/tnt list` 确认区域已创建
- 检查权限配置

### Q2: 防爆方块被破坏了？

**可能原因：**
1. 方块不是通过 `/tnt hq` 获取的
2. 破坏者具有管理员权限

**解决方法：**
- 确保使用 `/tnt hq` 命令获取防爆方块
- 检查破坏者权限

### Q3: 如何设置无限区域数量？

**方法：**
修改 `config.yml`：
```yaml
region:
  max-regions-per-player: -1
```

或授予玩家权限：`tntprotection.limit.*`

### Q4: 如何完全禁用TNT爆炸？

**方法：**
修改 `config.yml`：
```yaml
explosion:
  cancel-explosion: true
```

### Q5: 插件支持哪些版本？

**支持版本：** 1.8.x ~ 26.2

---

## 更新日志

### v1.0.0
- 实现区域保护功能
- 实现方块保护功能
- 支持 `/tnt` 命令系统
- 实现权限控制
- 添加配置文件系统
- 支持版本 1.8.x ~ 26.2

---

如有问题或建议，请联系作者 xiaoyu

---

*FKTNT - Minecraft TNT防爆保护插件*
