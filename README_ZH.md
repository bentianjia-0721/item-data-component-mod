# Item Data Component Generator - Fabric模组

> **中文** | [English](README.md)

Minecraft 1.21.4 Fabric模组 - 游戏内物品数据组件命令生成器

## 功能特性

- ✅ **UI化编辑器** - 同时按下 `U` 和 `O` 键打开图形界面
- ✅ **版本支持** - 支持 1.21.0 到 1.21.5 以及快照版 26.1 到 26.2
- ✅ **智能搜索** - 支持中文、英文、拼音首字母模糊搜索物品
- ✅ **实时预览** - 显示物品真实渲染效果和悬停提示框
- ✅ **双语界面** - 根据客户端语言自动切换中英文
- ✅ **命令生成** - 生成可直接使用的 `/give` 命令
- ✅ **导入导出** - 支持从命令导入和导出JSON格式
- ✅ **ModMenu集成** - 可从ModMenu配置菜单打开
- ✅ **专业编辑器** - 13个专门的组件编辑器（附魔、属性、药水、烟花等）
- ✅ **智能补全** - 输入框支持自动补全物品ID、附魔、药水效果等
- ✅ **分组视图** - 按功能分组或字母排序查看组件

## 版本兼容性

| Minecraft版本 | 组件数量 | 主要变化 |
|--------------|---------|---------|
| 1.21.0       | 53个    | 基础组件 |
| 1.21.2       | 63个    | 新增12个组件（consumable, equippable等），移除fire_resistant |
| 1.21.4       | 63个    | 当前稳定版本 |
| 1.21.5       | 61个    | 移除hide_tooltip, hide_additional_tooltip |
| 26.1         | 65个    | Tiny Takeover - 新增additional_trade_cost, dye |
| 26.2         | 66个    | Chaos Cubed - 新增sulfur_cube_content |

## 安装方法

### 前置要求
- Minecraft 1.21+（已在1.21.4测试）
- Fabric Loader（任意版本）
- Fabric API
- Java 21+

### 安装步骤

1. 下载最新的 `.jar` 文件
2. 将文件放入 `.minecraft/mods` 目录
3. 启动游戏

### 从源码构建

```bash
# Windows
gradlew.bat build

# Linux/Mac
./gradlew build
```

构建完成后，JAR文件位于 `build/libs/` 目录。

## 使用方法

### 打开编辑器

- **快捷键**: 同时按下 `U` 和 `O` 键
- **ModMenu**: 从ModMenu找到本模组，点击配置按钮

### 界面布局

```
┌─────────────────────────────────────────────┐
│ 标题栏                      [版本选择器]    │
├──────────────┬──────────────────────────────┤
│ 物品搜索     │  物品预览                    │
│ [搜索框]     │  ┌──────────────┐           │
│              │  │ 🗡️ 钻石剑     │           │
│ 物品列表     │  │ 锋利 V        │           │
│ □ 钻石       │  └──────────────┘           │
│ ■ 铁剑       │                              │
│ □ 石头       │  组件列表 [分组] [A-Z]      │
│              │  [▼ 显示与名称]              │
│              │  [▶ 附魔]                    │
│              │  ...                         │
├──────────────┴──────────────────────────────┤
│ 命令: /give @p diamond_sword[...] 1        │
│ [复制] [导出] [导入] [清空]                │
└─────────────────────────────────────────────┘
```

### 搜索技巧

- **英文ID**: 输入 `diamond` 或 `sword`
- **中文名**: 输入 `钻石` 或 `剑`
- **拼音首字母**: 输入 `zsj` 查找"钻石剑"

### 组件视图模式

- **分组模式**: 按功能分类（显示、附魔、耐久等）
- **A-Z模式**: 按首字母排序

## 支持的组件

### 显示与名称 (8个)
- custom_name - 自定义名称
- item_name - 物品名称
- lore - 描述
- rarity - 稀有度
- custom_model_data - 自定义模型数据
- item_model - 物品模型 (1.21.2+)
- tooltip_style - 提示框样式 (1.21.2+)
- enchantment_glint_override - 附魔光效覆盖

### 附魔 (3个)
- enchantments - 附魔
- stored_enchantments - 储存的附魔
- enchantable - 可附魔等级 (1.21.2+)

### 耐久与修复 (8个)
- damage - 损坏值
- max_damage - 最大耐久
- max_stack_size - 最大堆叠数
- unbreakable - 无法破坏
- damage_resistant - 伤害抗性 (1.21.2+)
- fire_resistant - 火焰抗性 (仅1.21.0-1.21.1)
- repair_cost - 修复花费
- repairable - 可修复 (1.21.2+)

### 食物与消耗 (5个)
- food - 食物属性
- consumable - 消耗行为 (1.21.2+)
- use_cooldown - 使用冷却 (1.21.2+)
- use_remainder - 使用后物品 (1.21.2+)
- death_protection - 死亡保护 (1.21.2+)

### 属性修饰符 (1个)
- attribute_modifiers - 属性修饰符

### 盔甲纹饰 (2个)
- trim - 盔甲纹饰
- dyed_color - 染色

### 药水 (3个)
- potion_contents - 药水内容
- suspicious_stew_effects - 可疑的炖菜效果
- ominous_bottle_amplifier - 不祥之瓶强度

### 容器与存储 (4个)
- container - 容器内容
- bundle_contents - 包裹内容
- container_loot - 容器战利品表
- lock - 锁定

### 方块交互 (8个)
- can_break - 可破坏方块
- can_place_on - 可放置方块
- block_entity_data - 方块实体数据
- block_state - 方块状态
- base_color - 基础颜色
- banner_patterns - 旗帜图案
- bees - 蜜蜂数据
- note_block_sound - 音符盒音效

### 投射物 (2个)
- charged_projectiles - 已装填投射物
- intangible_projectile - 无形投射物

### 工具 (1个)
- tool - 工具属性

### 装备 (2个)
- equippable - 可装备属性 (1.21.2+)
- glider - 滑翔 (1.21.2+)

### 实体数据 (2个)
- entity_data - 实体数据
- bucket_entity_data - 桶中实体数据

### 地图与指南针 (4个)
- map_id - 地图ID
- map_color - 地图颜色
- map_decorations - 地图装饰
- lodestone_tracker - 磁石追踪

### 烟花 (2个)
- fireworks - 烟花
- firework_explosion - 烟花之星

### 特殊物品 (11个)
- profile - 玩家头颅档案
- custom_data - 自定义数据
- debug_stick_state - 调试棒状态
- writable_book_content - 可写书内容
- written_book_content - 成书内容
- pot_decorations - 饰纹陶罐图案
- recipes - 合成配方
- jukebox_playable - 唱片机可播放
- instrument - 乐器
- additional_trade_cost - 额外交易成本 (26.1+)
- dye - 染料 (26.1+)
- sulfur_cube_content - 硫磺立方体内容 (26.2+)

**总计：70个数据组件** 覆盖 1.21.0 到 26.2 所有版本

## 开发说明

### 项目结构

```
src/main/java/com/itemdatacomp/
├── client/
│   ├── ItemDataComponentClient.java    # 客户端入口
│   ├── data/
│   │   ├── ItemRegistry.java           # 物品注册表（295个物品）
│   │   ├── ComponentRegistry.java      # 组件注册表（70个组件）
│   │   └── MinecraftVersion.java       # 版本管理（1.21-26.2）
│   ├── screen/
│   │   ├── ItemDataEditorScreen.java   # 主界面
│   │   ├── ItemDataEditorScreenEnhanced.java  # 增强版主界面
│   │   ├── ComponentEditorScreen.java  # 组件编辑基类
│   │   ├── ParseErrorDialog.java       # 解析错误对话框
│   │   └── editor/                     # 13个专业编辑器
│   │       ├── TextInputEditorScreen.java
│   │       ├── LoreEditorScreen.java
│   │       ├── EnchantmentEditorScreen.java
│   │       ├── AttributeEditorScreen.java
│   │       ├── ArmorTrimEditorScreen.java
│   │       ├── ColorPickerScreen.java
│   │       ├── PotionEditorScreen.java
│   │       ├── FoodEditorScreen.java
│   │       ├── ConsumableEditorScreen.java
│   │       ├── EquippableEditorScreen.java
│   │       ├── EntityDataEditorScreen.java
│   │       ├── FireworksEditorScreen.java
│   │       └── ItemSelectorEditorScreen.java
│   ├── widget/
│   │   ├── ItemListWidget.java         # 物品列表
│   │   ├── ItemListWidgetEnhanced.java # 增强版物品列表
│   │   ├── ComponentListWidget.java    # 组件列表
│   │   ├── ComponentListWidgetEnhanced.java # 增强版组件列表
│   │   ├── PreviewPanelWidget.java     # 预览面板
│   │   ├── AutoCompleteTextFieldWidget.java  # 自动补全输入框
│   │   ├── DropdownWidget.java         # 下拉菜单
│   │   ├── ColorPickerWidget.java      # 颜色选择器
│   │   ├── ItemPreviewWidget.java      # 物品预览
│   │   └── suggestion/                 # 智能补全提供器
│   │       ├── EnchantmentSuggestionProvider.java
│   │       ├── AttributeSuggestionProvider.java
│   │       ├── PotionEffectSuggestionProvider.java
│   │       ├── EntitySuggestionProvider.java
│   │       ├── SoundSuggestionProvider.java
│   │       └── BlockSuggestionProvider.java
│   ├── config/
│   │   └── ConfigManager.java          # 配置管理
│   ├── font/
│   │   └── ChineseFontManager.java     # 中文字体管理
│   └── util/
│       ├── PinyinMatcher.java          # 拼音搜索
│       ├── SNBTSerializer.java         # 命令序列化
│       └── CommandParser.java          # 命令解析器
└── modmenu/
    └── ModMenuIntegration.java         # ModMenu集成
```

### 专业编辑器

模组提供了 **13个专门的组件编辑器**，让复杂数据组件的编辑更加直观：

1. **TextInputEditorScreen** - 通用文本输入
2. **LoreEditorScreen** - 物品描述多行编辑
3. **EnchantmentEditorScreen** - 附魔选择器（支持搜索）
4. **AttributeEditorScreen** - 属性修饰符编辑
5. **ArmorTrimEditorScreen** - 盔甲纹饰选择
6. **ColorPickerScreen** - RGB颜色选择器
7. **PotionEditorScreen** - 药水效果编辑
8. **FoodEditorScreen** - 食物属性编辑
9. **ConsumableEditorScreen** - 消耗行为编辑
10. **EquippableEditorScreen** - 装备属性编辑
11. **EntityDataEditorScreen** - 实体数据编辑
12. **FireworksEditorScreen** - 烟花编辑器
13. **ItemSelectorEditorScreen** - 物品选择器

### 代码统计

- **总代码行数**: 10,600+ 行
- **Java文件**: 44个
- **组件定义**: 70个（1.21-26.2）
- **物品数据**: 295个
- **编辑器**: 13个专业组件编辑器
- **智能组件**: 6个补全提供器（附魔、属性、药水、实体、音效、方块/物品）

## 贡献指南

欢迎贡献代码！请确保：

1. 遵循现有代码风格
2. 添加适当的注释
3. 测试所有更改

## 许可证

MIT License

## 致谢

- 感谢 beizi 为本模组提供测试
- 感谢 Fabric 团队提供的优秀 API
- 感谢 Minecraft Wiki 提供的组件文档

## 联系方式

- GitHub: https://github.com/bentianjia-0721/item-data-component-mod
- Issues: https://github.com/bentianjia-0721/item-data-component-mod/issues
