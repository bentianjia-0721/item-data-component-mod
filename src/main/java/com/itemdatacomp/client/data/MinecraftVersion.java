package com.itemdatacomp.client.data;

/**
 * Minecraft版本枚举
 * 支持 Minecraft Java Edition 1.21.0 到 1.21.5
 * 以及快照版 26.1 到 26.2
 */
public enum MinecraftVersion {
    V1_21_0("1.21", "1.21", 0),
    V1_21_1("1.21.1", "1.21.1", 1),
    V1_21_2("1.21.2", "1.21.2 (Bundles of Bravery)", 2),
    V1_21_3("1.21.3", "1.21.3", 3),
    V1_21_4("1.21.4", "1.21.4", 4),
    V1_21_5("1.21.5", "1.21.5", 5),
    V26_1("26.1", "26.1", 100),
    V26_2("26.2", "26.2", 103);

    private final String id;
    private final String displayName;
    private final int order;

    MinecraftVersion(String id, String displayName, int order) {
        this.id = id;
        this.displayName = displayName;
        this.order = order;
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getOrder() {
        return order;
    }

    /**
     * 检查组件是否在此版本中可用
     */
    public boolean hasComponent(String componentId) {
        return switch (componentId) {
            // 1.21.2 新增的组件
            case "minecraft:consumable",
                 "minecraft:damage_resistant",
                 "minecraft:death_protection",
                 "minecraft:enchantable",
                 "minecraft:equippable",
                 "minecraft:glider",
                 "minecraft:item_model",
                 "minecraft:repairable",
                 "minecraft:tooltip_style",
                 "minecraft:use_cooldown",
                 "minecraft:use_remainder" -> this.order >= 2;

            // 1.21.0-1.21.1 存在但在 1.21.2 被移除
            case "minecraft:fire_resistant" -> this.order < 2;

            // 1.21.0-1.21.4 存在但在 1.21.5 被移除
            case "minecraft:hide_tooltip",
                 "minecraft:hide_additional_tooltip" -> this.order < 5;

            // 26.1 新增的组件
            case "minecraft:additional_trade_cost",
                 "minecraft:dye" -> this.order >= 100;

            // 26.2 新增的组件
            case "minecraft:sulfur_cube_content" -> this.order >= 103;

            // 其他所有基础组件在所有版本都可用
            default -> true;
        };
    }

    public static MinecraftVersion fromId(String id) {
        for (MinecraftVersion version : values()) {
            if (version.id.equals(id)) {
                return version;
            }
        }
        return V1_21_4; // 默认返回当前运行版本
    }
}
