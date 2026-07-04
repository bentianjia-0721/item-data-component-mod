package com.itemdatacomp.client.data;

import java.util.*;

/**
 * 组件注册表
 * 定义所有数据组件及其元数据
 */
public class ComponentRegistry {

    public enum ComponentType {
        STRING,           // 简单字符串输入
        INT,              // 整数输入
        BOOL,             // 布尔复选框
        ENUM,             // 枚举选择器
        JSON_TEXT,        // JSON文本编辑器（带颜色/格式）
        LORE_ARRAY,       // Lore行列表编辑器
        ENCHANTMENTS,     // 附魔选择器
        ATTRIBUTES,       // 属性修饰符编辑器
        TRIM,             // 盔甲纹饰编辑器
        DYED_COLOR,       // 染色RGB编辑器
        POTION,           // 药水编辑器
        ENTITY_DATA,      // 实体数据编辑器
        FOOD,             // 食物属性编辑器
        CONSUMABLE,       // 消耗行为编辑器
        EQUIPPABLE,       // 装备属性编辑器
        FIREWORKS,        // 烟花编辑器
        FIREWORK_STAR,    // 烟花之星编辑器
        UNIT,             // 存在即生效的空对象组件
        JSON              // 通用JSON编辑器
    }

    public record ComponentDef(
        String id,
        String group,
        ComponentType type,
        MinecraftVersion addedIn,
        MinecraftVersion removedIn
    ) {}

    private static final List<ComponentDef> COMPONENTS = new ArrayList<>();
    private static final Map<String, ComponentDef> COMPONENT_MAP = new HashMap<>();

    static {
        // 显示与名称组
        addComponent("minecraft:custom_name", "display", ComponentType.JSON_TEXT, MinecraftVersion.V1_21_0, null);
        addComponent("minecraft:item_name", "display", ComponentType.JSON_TEXT, MinecraftVersion.V1_21_0, null);
        addComponent("minecraft:lore", "display", ComponentType.LORE_ARRAY, MinecraftVersion.V1_21_0, null);
        addComponent("minecraft:rarity", "display", ComponentType.ENUM, MinecraftVersion.V1_21_0, null);
        addComponent("minecraft:custom_model_data", "display", ComponentType.JSON, MinecraftVersion.V1_21_0, null);
        addComponent("minecraft:item_model", "display", ComponentType.STRING, MinecraftVersion.V1_21_2, null);
        addComponent("minecraft:tooltip_style", "display", ComponentType.STRING, MinecraftVersion.V1_21_2, null);
        addComponent("minecraft:enchantment_glint_override", "display", ComponentType.BOOL, MinecraftVersion.V1_21_0, null);

        // 提示框控制组
        addComponent("minecraft:hide_tooltip", "tooltip", ComponentType.UNIT, MinecraftVersion.V1_21_0, MinecraftVersion.V1_21_5);
        addComponent("minecraft:hide_additional_tooltip", "tooltip", ComponentType.UNIT, MinecraftVersion.V1_21_0, MinecraftVersion.V1_21_5);

        // 附魔组
        addComponent("minecraft:enchantments", "enchantments", ComponentType.ENCHANTMENTS, MinecraftVersion.V1_21_0, null);
        addComponent("minecraft:stored_enchantments", "enchantments", ComponentType.ENCHANTMENTS, MinecraftVersion.V1_21_0, null);
        addComponent("minecraft:enchantable", "enchantments", ComponentType.INT, MinecraftVersion.V1_21_2, null);

        // 耐久与修复组
        addComponent("minecraft:damage", "durability", ComponentType.INT, MinecraftVersion.V1_21_0, null);
        addComponent("minecraft:max_damage", "durability", ComponentType.INT, MinecraftVersion.V1_21_0, null);
        addComponent("minecraft:max_stack_size", "durability", ComponentType.INT, MinecraftVersion.V1_21_0, null);
        addComponent("minecraft:unbreakable", "durability", ComponentType.UNIT, MinecraftVersion.V1_21_0, null);
        addComponent("minecraft:damage_resistant", "durability", ComponentType.STRING, MinecraftVersion.V1_21_2, null);
        addComponent("minecraft:fire_resistant", "durability", ComponentType.UNIT, MinecraftVersion.V1_21_0, MinecraftVersion.V1_21_2);
        addComponent("minecraft:repair_cost", "durability", ComponentType.INT, MinecraftVersion.V1_21_0, null);
        addComponent("minecraft:repairable", "durability", ComponentType.STRING, MinecraftVersion.V1_21_2, null);

        // 食物与消耗组
        addComponent("minecraft:food", "food", ComponentType.FOOD, MinecraftVersion.V1_21_0, null);
        addComponent("minecraft:consumable", "food", ComponentType.CONSUMABLE, MinecraftVersion.V1_21_2, null);
        addComponent("minecraft:use_cooldown", "food", ComponentType.JSON, MinecraftVersion.V1_21_2, null);
        addComponent("minecraft:use_remainder", "food", ComponentType.STRING, MinecraftVersion.V1_21_2, null);
        addComponent("minecraft:death_protection", "food", ComponentType.JSON, MinecraftVersion.V1_21_2, null);

        // 属性修饰符组
        addComponent("minecraft:attribute_modifiers", "attributes", ComponentType.ATTRIBUTES, MinecraftVersion.V1_21_0, null);

        // 盔甲纹饰组
        addComponent("minecraft:trim", "armor_trim", ComponentType.TRIM, MinecraftVersion.V1_21_0, null);
        addComponent("minecraft:dyed_color", "armor_trim", ComponentType.DYED_COLOR, MinecraftVersion.V1_21_0, null);

        // 药水组
        addComponent("minecraft:potion_contents", "potions", ComponentType.POTION, MinecraftVersion.V1_21_0, null);
        addComponent("minecraft:suspicious_stew_effects", "potions", ComponentType.JSON, MinecraftVersion.V1_21_0, null);
        addComponent("minecraft:ominous_bottle_amplifier", "potions", ComponentType.INT, MinecraftVersion.V1_21_0, null);

        // 容器与存储组
        addComponent("minecraft:container", "container", ComponentType.JSON, MinecraftVersion.V1_21_0, null);
        addComponent("minecraft:bundle_contents", "container", ComponentType.JSON, MinecraftVersion.V1_21_0, null);
        addComponent("minecraft:container_loot", "container", ComponentType.JSON, MinecraftVersion.V1_21_0, null);
        addComponent("minecraft:lock", "container", ComponentType.JSON, MinecraftVersion.V1_21_0, null);

        // 方块交互组
        addComponent("minecraft:can_break", "blocks", ComponentType.JSON, MinecraftVersion.V1_21_0, null);
        addComponent("minecraft:can_place_on", "blocks", ComponentType.JSON, MinecraftVersion.V1_21_0, null);
        addComponent("minecraft:block_entity_data", "blocks", ComponentType.JSON, MinecraftVersion.V1_21_0, null);
        addComponent("minecraft:block_state", "blocks", ComponentType.JSON, MinecraftVersion.V1_21_0, null);
        addComponent("minecraft:base_color", "blocks", ComponentType.STRING, MinecraftVersion.V1_21_0, null);
        addComponent("minecraft:banner_patterns", "blocks", ComponentType.JSON, MinecraftVersion.V1_21_0, null);
        addComponent("minecraft:bees", "blocks", ComponentType.JSON, MinecraftVersion.V1_21_0, null);
        addComponent("minecraft:note_block_sound", "blocks", ComponentType.STRING, MinecraftVersion.V1_21_0, null);

        // 投射物组
        addComponent("minecraft:charged_projectiles", "projectiles", ComponentType.JSON, MinecraftVersion.V1_21_0, null);
        addComponent("minecraft:intangible_projectile", "projectiles", ComponentType.UNIT, MinecraftVersion.V1_21_0, null);

        // 工具组
        addComponent("minecraft:tool", "tools", ComponentType.JSON, MinecraftVersion.V1_21_0, null);

        // 装备组
        addComponent("minecraft:equippable", "equipment", ComponentType.EQUIPPABLE, MinecraftVersion.V1_21_2, null);
        addComponent("minecraft:glider", "equipment", ComponentType.UNIT, MinecraftVersion.V1_21_2, null);

        // 实体数据组
        addComponent("minecraft:entity_data", "entity", ComponentType.ENTITY_DATA, MinecraftVersion.V1_21_0, null);
        addComponent("minecraft:bucket_entity_data", "entity", ComponentType.ENTITY_DATA, MinecraftVersion.V1_21_0, null);

        // 地图与指南针组
        addComponent("minecraft:map_id", "maps", ComponentType.INT, MinecraftVersion.V1_21_0, null);
        addComponent("minecraft:map_color", "maps", ComponentType.INT, MinecraftVersion.V1_21_0, null);
        addComponent("minecraft:map_decorations", "maps", ComponentType.JSON, MinecraftVersion.V1_21_0, null);
        addComponent("minecraft:lodestone_tracker", "maps", ComponentType.JSON, MinecraftVersion.V1_21_0, null);

        // 烟花组
        addComponent("minecraft:fireworks", "fireworks", ComponentType.FIREWORKS, MinecraftVersion.V1_21_0, null);
        addComponent("minecraft:firework_explosion", "fireworks", ComponentType.FIREWORK_STAR, MinecraftVersion.V1_21_0, null);

        // 特殊物品组
        addComponent("minecraft:profile", "special", ComponentType.JSON, MinecraftVersion.V1_21_0, null);
        addComponent("minecraft:custom_data", "special", ComponentType.JSON, MinecraftVersion.V1_21_0, null);
        addComponent("minecraft:debug_stick_state", "special", ComponentType.JSON, MinecraftVersion.V1_21_0, null);
        addComponent("minecraft:writable_book_content", "special", ComponentType.JSON, MinecraftVersion.V1_21_0, null);
        addComponent("minecraft:written_book_content", "special", ComponentType.JSON, MinecraftVersion.V1_21_0, null);
        addComponent("minecraft:pot_decorations", "special", ComponentType.JSON, MinecraftVersion.V1_21_0, null);
        addComponent("minecraft:recipes", "special", ComponentType.JSON, MinecraftVersion.V1_21_0, null);
        addComponent("minecraft:jukebox_playable", "special", ComponentType.STRING, MinecraftVersion.V1_21_0, null);
        addComponent("minecraft:instrument", "special", ComponentType.STRING, MinecraftVersion.V1_21_0, null);

        // 26.1 新增 (Tiny Takeover Update)
        addComponent("minecraft:additional_trade_cost", "special", ComponentType.INT, MinecraftVersion.V26_1, null);
        addComponent("minecraft:dye", "special", ComponentType.STRING, MinecraftVersion.V26_1, null);

        // 26.2 新增 (Chaos Cubed Update)
        addComponent("minecraft:sulfur_cube_content", "special", ComponentType.STRING, MinecraftVersion.V26_2, null);
    }

    private static void addComponent(String id, String group, ComponentType type, MinecraftVersion addedIn, MinecraftVersion removedIn) {
        ComponentDef def = new ComponentDef(id, group, type, addedIn, removedIn);
        COMPONENTS.add(def);
        COMPONENT_MAP.put(id, def);
    }

    public static List<ComponentDef> getAllComponents() {
        return Collections.unmodifiableList(COMPONENTS);
    }

    public static ComponentDef getComponent(String id) {
        return COMPONENT_MAP.get(id);
    }

    /**
     * 获取指定版本可用的组件列表
     */
    public static List<ComponentDef> getComponentsForVersion(MinecraftVersion version) {
        List<ComponentDef> result = new ArrayList<>();
        for (ComponentDef comp : COMPONENTS) {
            if (version.hasComponent(comp.id)) {
                result.add(comp);
            }
        }
        return result;
    }

    /**
     * 按组分组获取组件
     */
    public static Map<String, List<ComponentDef>> getComponentsByGroup(MinecraftVersion version) {
        Map<String, List<ComponentDef>> grouped = new LinkedHashMap<>();
        for (ComponentDef comp : getComponentsForVersion(version)) {
            grouped.computeIfAbsent(comp.group, k -> new ArrayList<>()).add(comp);
        }
        return grouped;
    }

    /**
     * 按首字母分组获取组件
     */
    public static Map<String, List<ComponentDef>> getComponentsByLetter(MinecraftVersion version) {
        Map<String, List<ComponentDef>> grouped = new TreeMap<>();
        for (ComponentDef comp : getComponentsForVersion(version)) {
            String letter = comp.id.replace("minecraft:", "").substring(0, 1).toUpperCase();
            grouped.computeIfAbsent(letter, k -> new ArrayList<>()).add(comp);
        }
        return grouped;
    }

    public static String[] getGroups() {
        return new String[]{
            "display", "tooltip", "enchantments", "durability", "food", "attributes",
            "armor_trim", "potions", "container", "blocks", "projectiles",
            "tools", "equipment", "entity", "maps", "fireworks", "special"
        };
    }
}
