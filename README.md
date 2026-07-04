# Item Data Component Generator - Fabric Mod

> [中文文档](README_ZH.md) | **English**

A Minecraft 1.21-26.2 Fabric mod - In-game item data component command generator

## Features

- ✅ **GUI Editor** - Press `U` and `O` keys simultaneously to open the graphical interface
- ✅ **Version Support** - Supports Java Edition 1.21.0 to 26.2
- ✅ **Smart Search** - Fuzzy search with Chinese, English, and Pinyin initials
- ✅ **Real-time Preview** - Display actual item rendering and hover tooltips
- ✅ **Bilingual UI** - Auto-switch between English and Chinese based on client language
- ✅ **Command Generation** - Generate ready-to-use `/give` commands
- ✅ **Import/Export** - Support importing from commands and exporting to JSON
- ✅ **ModMenu Integration** - Open from ModMenu config screen

## Version Compatibility

| Minecraft Version | Components | Major Changes |
|-------------------|-----------|---------------|
| 1.21.0            | 53        | Base components |
| 1.21.2            | 63        | Added 12 components (consumable, equippable, etc.), removed fire_resistant |
| 1.21.4            | 63        | Current stable version |
| 1.21.5            | 61        | Removed hide_tooltip, hide_additional_tooltip |
| 26.1              | 65        | Tiny Takeover - Added additional_trade_cost, dye |
| 26.2              | 66        | Chaos Cubed - Added sulfur_cube_content |

## Installation

### Requirements
- Minecraft Java Edition 1.21+ to 26.2
- Fabric Loader (any version)
- Fabric API
- Java 21+

### Steps

1. Download the latest `.jar` file
2. Place it in `.minecraft/mods` directory
3. Launch the game

### Build from Source

```bash
# Windows
gradlew.bat build

# Linux/Mac
./gradlew build
```

After building, the JAR file will be in `build/libs/` directory.

## Usage

### Opening the Editor

- **Keybinding**: Press `U` and `O` keys simultaneously
- **ModMenu**: Find this mod in ModMenu and click the config button

### Interface Layout

```
┌─────────────────────────────────────────────┐
│ Title Bar               [Version Selector]  │
├──────────────┬──────────────────────────────┤
│ Item Search  │  Item Preview                │
│ [Search Box] │  ┌──────────────┐           │
│              │  │ 🗡️ Diamond    │           │
│ Item List    │  │ Sword         │           │
│ □ Diamond    │  │ Sharpness V  │           │
│ ■ Iron Sword │  └──────────────┘           │
│ □ Stone      │                              │
│              │  Component List [Group][A-Z] │
│              │  [▼ Display & Names]         │
│              │  [▶ Enchantments]            │
│              │  ...                         │
├──────────────┴──────────────────────────────┤
│ Command: /give @p diamond_sword[...] 1     │
│ [Copy] [Export] [Import] [Clear]           │
└─────────────────────────────────────────────┘
```

### Search Tips

- **English ID**: Type `diamond` or `sword`
- **Chinese Name**: Type `钻石` or `剑`
- **Pinyin Initials**: Type `zsj` to find "钻石剑" (Diamond Sword)

### Component View Modes

- **Group Mode**: Categorized by function (Display, Enchantments, Durability, etc.)
- **A-Z Mode**: Sorted alphabetically by component ID

## Supported Components

### Display & Names (8 components)
- custom_name - Custom display name
- item_name - Item type name override
- lore - Item description
- rarity - Item rarity level
- custom_model_data - Custom model data
- item_model - Item model (1.21.2+)
- tooltip_style - Tooltip style (1.21.2+)
- enchantment_glint_override - Enchantment glint override

### Enchantments (3 components)
- enchantments - Item enchantments
- stored_enchantments - Stored enchantments (for books)
- enchantable - Enchantability level (1.21.2+)

### Durability & Repair (8 components)
- damage - Current damage value
- max_damage - Maximum durability
- max_stack_size - Maximum stack size
- unbreakable - Unbreakable flag
- damage_resistant - Damage resistance (1.21.2+)
- fire_resistant - Fire resistance (1.21.0-1.21.1 only)
- repair_cost - Anvil repair cost
- repairable - Repairable items (1.21.2+)

...and 48 more components

## Development

### Project Structure

```
src/main/java/com/itemdatacomp/
├── client/
│   ├── ItemDataComponentClient.java    # Client entrypoint
│   ├── data/
│   │   ├── ItemRegistry.java           # Item registry (276 items)
│   │   ├── ComponentRegistry.java      # Component registry (72 components)
│   │   └── MinecraftVersion.java       # Version management (1.21-26.2)
│   ├── screen/
│   │   ├── ComponentEditorScreen.java  # Main UI screen
│   │   └── editor/                     # Specialized editors
│   │       ├── ItemSelectorEditorScreen.java  # Item selector
│   │       ├── EnchantmentEditorScreen.java   # Enchantment editor
│   │       ├── AttributeEditorScreen.java     # Attribute editor
│   │       └── ... (8+ editors)
│   ├── widget/
│   │   ├── ItemListWidget.java         # Item list widget
│   │   ├── ComponentListWidget.java    # Component list widget
│   │   └── AutoCompleteTextFieldWidget.java   # Auto-complete input
│   └── util/
│       ├── PinyinMatcher.java          # Pinyin fuzzy search
│       └── SNBTSerializer.java         # Command serialization
└── modmenu/
    └── ModMenuIntegration.java         # ModMenu integration
```

### Code Statistics

- **Total Lines**: 3200+ lines
- **Java Files**: 20+ files
- **Component Definitions**: 72 components (1.21-26.2)
- **Item Data**: 276 items (all 1.21-26.2 items)
- **Editors**: 9 specialized component editors

## Contributing

Contributions are welcome! Please ensure:

1. Follow existing code style
2. Add appropriate comments
3. Test all changes

## License

MIT License

## Credits

- Thanks to beizi for testing and feedback
- Thanks to Fabric team for excellent APIs
- Thanks to Minecraft Wiki for component documentation

## Contact

- GitHub: https://github.com/bentianjia/item-data-component-mod
- Issues: https://github.com/bentianjia/item-data-component-mod/issues
