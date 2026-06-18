# FKTNT 插件使用教程

作者: xiaoyu
版本: 1.0.0
支持版本: 1.8.x - 26.2
许可证: MIT / 开源可商用

---

## 目录
1. [插件简介](#插件简介)
2. [开源说明](#开源说明)
3. [插件安装](#插件安装)
4. [区域保护功能](#区域保护功能)
5. [方块保护功能](#方块保护功能)
6. [命令列表](#命令列表)
7. [权限配置](#权限配置)
8. [配置文件说明](#配置文件说明)
9. [注意事项](#注意事项)

---

## 插件简介

FKTNT 是一款 Minecraft TNT 防爆保护插件，核心功能包括：

- **区域保护** — 通过选择工具标记区域，该区域内所有方块免受TNT爆炸破坏
- **方块保护** — 特定方块具有永久防爆属性

> 本插件完全开源，支持商业使用。

---

## 开源说明

- 本插件采用 MIT 许可证
- 可自由使用、修改、再分发
- 允许商用，无需告知作者
- 源代码可基于本项目进行二次开发

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

> ⚠️ 区域选择斧功能暂不可用（功能设计说明）

### 预期使用步骤

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

### 手动设置坐标（备选方案）

如区域选择斧无法使用，可通过命令手动设置坐标：

```
# 设置起点坐标
/tnt pos1

# 设置终点坐标
/tnt pos2
```

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

> ⚠️ 防爆方块功能暂不可用（功能设计说明）

### 预期使用步骤

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

## 注意事项

### ⚠️ 区域选择斧

区域选择斧（金斧头左键/右键点击记录坐标）功能暂不可用。如需选择区域，请使用 `/tnt pos1` 和 `/tnt pos2` 命令在当前位置手动设置坐标。

### ⚠️ 防爆方块

通过 `/tnt hq` 获取的防爆方块功能暂不可用。该功能为预留设计，当前版本中方块保护机制尚未完全实现。

### ⚠️ 生电兼容性

本插件对**生电模组**（Create模组及相关电气机械模组）的支持有限。生电环境中大量TNT爆炸、旋转机械、动力传递等场景可能与插件的爆炸保护逻辑产生冲突或不可预期的行为。

如服务器主要使用生电玩法，建议：
- 单独测试后再部署到生产环境
- 考虑配合其他兼容性更好的防爆插件使用
- 酌情评估是否适合当前服务器需求

---

## 更新日志

### v1.0.0
- 实现区域保护功能框架
- 实现方块保护功能框架
- 支持 `/tnt` 命令系统
- 实现权限控制系统
- 添加配置文件系统
- 支持版本 1.8.x ~ 26.2
- 开源可商用 (MIT)

---

*FKTNT - Minecraft TNT防爆保护插件*
*作者: xiaoyu | MIT License | 开源可商用*
