# Item Data Component Generator - Fabric模组

> **中文** | [English](README.md)

Minecraft 1.21.4 Fabric模组 - 游戏内物品数据组件命令生成器

## 功能特性

- ✅ **UI化编辑器** - 同时按下 `U` 和 `O` 键打开图形界面
- ✅ **版本支持** - 支持 1.21.0 到 1.21.11 以及 26.1 到 26.2
- ✅ **智能搜索** - 支持中文、英文、拼音首字母模糊搜索
- ✅ **实时预览** - 显示物品真实渲染效果和悬停提示框
- ✅ **双语界面** - 根据客户端语言自动切换中英文
- ✅ **命令生成** - 生成可直接使用的 `/give` 命令
- ✅ **导入导出** - 支持从命令导入和导出JSON格式
- ✅ **ModMenu集成** - 可从ModMenu配置菜单打开

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

...以及其他48个组件

## 开发说明

### 项目结构

```
src/main/java/com/itemdatacomp/
├── client/
│   ├── ItemDataComponentClient.java    # 客户端入口
│   ├── data/
│   │   ├── ItemRegistry.java           # 物品注册表（262个物品）
│   │   ├── ComponentRegistry.java      # 组件注册表（67个组件）
│   │   └── MinecraftVersion.java       # 版本管理（1.21-26.2）
│   ├── screen/
│   │   ├── ComponentEditorScreen.java  # 主界面
│   │   └── editor/                     # 8个专业编辑器
│   ├── widget/
│   │   ├── ItemListWidget.java         # 物品列表
│   │   └── ComponentListWidget.java    # 组件列表
│   └── util/
│       ├── PinyinMatcher.java          # 拼音搜索
│       └── SNBTSerializer.java         # 命令序列化
└── modmenu/
    └── ModMenuIntegration.java         # ModMenu集成
```

### 代码统计

- **总代码行数**: 2905+ 行
- **Java文件**: 18个
- **组件定义**: 67个（1.21-26.2）
- **物品数据**: 262个
- **编辑器**: 8个专业组件编辑器

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

- GitHub: https://github.com/bentianjia/item-data-component-mod
- Issues: https://github.com/bentianjia/item-data-component-mod/issues
