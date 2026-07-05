# Item Data Component Generator - Fabric Mod

> [中文文档](README_ZH.md) | **English**

A Minecraft 1.21-26.2 Fabric mod - In-game item data component command generator

## Features

- ✅ **GUI Editor** - Press `U` and `O` keys simultaneously to open the graphical interface
- ✅ **Version Support** - Supports Java Edition 1.21.0 to 1.21.5 and snapshots 26.1 to 26.2
- ✅ **Smart Search** - Fuzzy search for items with Chinese, English, and Pinyin initials
- ✅ **Real-time Preview** - Display actual item rendering and hover tooltips
- ✅ **Bilingual UI** - Auto-switch between English and Chinese based on client language
- ✅ **Command Generation** - Generate ready-to-use `/give` commands
- ✅ **Import/Export** - Support importing from commands and exporting to JSON
- ✅ **ModMenu Integration** - Open from ModMenu config screen
- ✅ **Specialized Editors** - 13 dedicated component editors (enchantments, attributes, potions, fireworks, etc.)
- ✅ **Auto-Complete** - Smart suggestions for item IDs, enchantments, potion effects, and more
- ✅ **Grouped View** - View components by functional groups or alphabetical order

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

### Food & Consumables (5 components)
- food - Food properties
- consumable - Consumable behavior (1.21.2+)
- use_cooldown - Use cooldown (1.21.2+)
- use_remainder - Item after use (1.21.2+)
- death_protection - Death protection (1.21.2+)

### Attribute Modifiers (1 component)
- attribute_modifiers - Attribute modifiers

### Armor Trim (2 components)
- trim - Armor trim
- dyed_color - Dyed color

### Potions (3 components)
- potion_contents - Potion contents
- suspicious_stew_effects - Suspicious stew effects
- ominous_bottle_amplifier - Ominous bottle amplifier

### Container & Storage (4 components)
- container - Container contents
- bundle_contents - Bundle contents
- container_loot - Container loot table
- lock - Lock key

### Block Interactions (8 components)
- can_break - Breakable blocks
- can_place_on - Placeable blocks
- block_entity_data - Block entity data
- block_state - Block state
- base_color - Base color
- banner_patterns - Banner patterns
- bees - Bee data
- note_block_sound - Note block sound

### Projectiles (2 components)
- charged_projectiles - Charged projectiles
- intangible_projectile - Intangible projectile

### Tools (1 component)
- tool - Tool properties

### Equipment (2 components)
- equippable - Equippable properties (1.21.2+)
- glider - Glider (1.21.2+)

### Entity Data (2 components)
- entity_data - Entity data
- bucket_entity_data - Bucket entity data

### Maps & Compass (4 components)
- map_id - Map ID
- map_color - Map color
- map_decorations - Map decorations
- lodestone_tracker - Lodestone tracker

### Fireworks (2 components)
- fireworks - Fireworks
- firework_explosion - Firework star

### Special Items (11 components)
- profile - Player head profile
- custom_data - Custom NBT data
- debug_stick_state - Debug stick state
- writable_book_content - Writable book content
- written_book_content - Written book content
- pot_decorations - Decorated pot sherds
- recipes - Crafting recipes
- jukebox_playable - Jukebox playable
- instrument - Instrument type
- additional_trade_cost - Additional trade cost (26.1+)
- dye - Dye color (26.1+)
- sulfur_cube_content - Sulfur cube content (26.2+)

**Total: 70 data components** covering versions 1.21.0 to 26.2

## Development

### Project Structure

```
src/main/java/com/itemdatacomp/
├── client/
│   ├── ItemDataComponentClient.java    # Client entrypoint
│   ├── data/
│   │   ├── ItemRegistry.java           # Item registry (295 items)
│   │   ├── ComponentRegistry.java      # Component registry (70 components)
│   │   └── MinecraftVersion.java       # Version management (1.21-26.2)
│   ├── screen/
│   │   ├── ItemDataEditorScreen.java   # Main UI screen
│   │   ├── ItemDataEditorScreenEnhanced.java  # Enhanced main UI
│   │   ├── ComponentEditorScreen.java  # Component editor base
│   │   ├── ParseErrorDialog.java       # Parse error dialog
│   │   └── editor/                     # 13 specialized editors
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
│   │   ├── ItemListWidget.java         # Item list widget
│   │   ├── ItemListWidgetEnhanced.java # Enhanced item list
│   │   ├── ComponentListWidget.java    # Component list widget
│   │   ├── ComponentListWidgetEnhanced.java # Enhanced component list
│   │   ├── PreviewPanelWidget.java     # Preview panel
│   │   ├── AutoCompleteTextFieldWidget.java  # Auto-complete input
│   │   ├── DropdownWidget.java         # Dropdown menu
│   │   ├── ColorPickerWidget.java      # Color picker
│   │   ├── ItemPreviewWidget.java      # Item preview
│   │   └── suggestion/                 # Auto-complete providers
│   │       ├── EnchantmentSuggestionProvider.java
│   │       ├── AttributeSuggestionProvider.java
│   │       ├── PotionEffectSuggestionProvider.java
│   │       ├── EntitySuggestionProvider.java
│   │       ├── SoundSuggestionProvider.java
│   │       └── BlockSuggestionProvider.java
│   ├── config/
│   │   └── ConfigManager.java          # Config manager
│   ├── font/
│   │   └── ChineseFontManager.java     # Chinese font manager
│   └── util/
│       ├── PinyinMatcher.java          # Pinyin fuzzy search
│       ├── SNBTSerializer.java         # Command serialization
│       └── CommandParser.java          # Command parser
└── modmenu/
    └── ModMenuIntegration.java         # ModMenu integration
```

### Specialized Editors

The mod provides **13 dedicated component editors** for intuitive editing of complex data components:

1. **TextInputEditorScreen** - General text input
2. **LoreEditorScreen** - Multi-line item description editor
3. **EnchantmentEditorScreen** - Enchantment selector (with search)
4. **AttributeEditorScreen** - Attribute modifier editor
5. **ArmorTrimEditorScreen** - Armor trim selector
6. **ColorPickerScreen** - RGB color picker
7. **PotionEditorScreen** - Potion effect editor
8. **FoodEditorScreen** - Food properties editor
9. **ConsumableEditorScreen** - Consumable behavior editor
10. **EquippableEditorScreen** - Equipment properties editor
11. **EntityDataEditorScreen** - Entity data editor
12. **FireworksEditorScreen** - Fireworks editor
13. **ItemSelectorEditorScreen** - Item selector

### Code Statistics

- **Total Lines**: 10,600+ lines
- **Java Files**: 44 files
- **Component Definitions**: 70 components (1.21-26.2)
- **Item Data**: 295 items (all 1.21-26.2 items)
- **Editors**: 13 specialized component editors
- **Smart Components**: 6 auto-complete providers (enchantments, attributes, potions, entities, sounds, blocks/items)

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
