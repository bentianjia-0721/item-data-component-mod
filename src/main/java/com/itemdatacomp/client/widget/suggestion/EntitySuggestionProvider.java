package com.itemdatacomp.client.widget.suggestion;

import com.itemdatacomp.client.util.PinyinMatcher;
import java.util.ArrayList;
import java.util.List;

/**
 * 实体类型自动完成提供器
 * 包含所有 minecraft:* 实体，按类别分组
 */
public class EntitySuggestionProvider {
    private static final List<EntityEntry> ENTITIES = new ArrayList<>();

    static {
        // 敌对生物
        addEntity("minecraft:blaze", "烈焰人", "hostile");
        addEntity("minecraft:cave_spider", "洞穴蜘蛛", "hostile");
        addEntity("minecraft:creeper", "苦力怕", "hostile");
        addEntity("minecraft:drowned", "溺水僵尸", "hostile");
        addEntity("minecraft:elder_guardian", "远古守卫者", "hostile");
        addEntity("minecraft:ender_dragon", "末影龙", "hostile");
        addEntity("minecraft:enderman", "末影人", "hostile");
        addEntity("minecraft:endermite", "末影螨", "hostile");
        addEntity("minecraft:evoker", "唤魔者", "hostile");
        addEntity("minecraft:ghast", "恶魂", "hostile");
        addEntity("minecraft:giant", "巨人", "hostile");
        addEntity("minecraft:guardian", "守卫者", "hostile");
        addEntity("minecraft:hoglin", "疣猪兽", "hostile");
        addEntity("minecraft:husk", "沙漠僵尸", "hostile");
        addEntity("minecraft:magma_cube", "岩浆怪", "hostile");
        addEntity("minecraft:phantom", "幻翼", "hostile");
        addEntity("minecraft:piglin_brute", "蛮猪人", "hostile");
        addEntity("minecraft:pillager", "掠夺者", "hostile");
        addEntity("minecraft:ravager", "掠夺兽", "hostile");
        addEntity("minecraft:shulker", "潜影贝", "hostile");
        addEntity("minecraft:silverfish", "蠹虫", "hostile");
        addEntity("minecraft:skeleton", "骷髅", "hostile");
        addEntity("minecraft:slime", "史莱姆", "hostile");
        addEntity("minecraft:spider", "蜘蛛", "hostile");
        addEntity("minecraft:stray", "流浪者", "hostile");
        addEntity("minecraft:vex", "恼鬼", "hostile");
        addEntity("minecraft:vindicator", "卫道士", "hostile");
        addEntity("minecraft:warden", "监守者", "hostile");
        addEntity("minecraft:witch", "女巫", "hostile");
        addEntity("minecraft:wither", "凋零", "hostile");
        addEntity("minecraft:wither_skeleton", "凋零骷髅", "hostile");
        addEntity("minecraft:zoglin", "疣猪兽僵尸", "hostile");
        addEntity("minecraft:zombie", "僵尸", "hostile");
        addEntity("minecraft:zombie_villager", "僵尸村民", "hostile");
        addEntity("minecraft:zombified_piglin", "僵尸猪人", "hostile");

        // 中立生物
        addEntity("minecraft:armor_stand", "盔甲架", "neutral");
        addEntity("minecraft:axolotl", "美西螈", "neutral");
        addEntity("minecraft:bat", "蝙蝠", "neutral");
        addEntity("minecraft:bee", "蜜蜂", "neutral");
        addEntity("minecraft:camel", "骆驼", "neutral");
        addEntity("minecraft:cat", "猫", "neutral");
        addEntity("minecraft:cave_spider", "洞穴蜘蛛", "neutral");
        addEntity("minecraft:chicken", "鸡", "neutral");
        addEntity("minecraft:cow", "牛", "neutral");
        addEntity("minecraft:dolphin", "海豚", "neutral");
        addEntity("minecraft:donkey", "驴", "neutral");
        addEntity("minecraft:fox", "狐狸", "neutral");
        addEntity("minecraft:frog", "青蛙", "neutral");
        addEntity("minecraft:goat", "山羊", "neutral");
        addEntity("minecraft:horse", "马", "neutral");
        addEntity("minecraft:iron_golem", "铁傀儡", "neutral");
        addEntity("minecraft:llama", "羊驼", "neutral");
        addEntity("minecraft:mule", "骡", "neutral");
        addEntity("minecraft:ocelot", "豹猫", "neutral");
        addEntity("minecraft:panda", "熊猫", "neutral");
        addEntity("minecraft:parrot", "鹦鹉", "neutral");
        addEntity("minecraft:pig", "猪", "neutral");
        addEntity("minecraft:piglin", "猪人", "neutral");
        addEntity("minecraft:polar_bear", "北极熊", "neutral");
        addEntity("minecraft:rabbit", "兔子", "neutral");
        addEntity("minecraft:sheep", "羊", "neutral");
        addEntity("minecraft:snow_golem", "雪傀儡", "neutral");
        addEntity("minecraft:strider", "炽足兽", "neutral");
        addEntity("minecraft:tadpole", "蝌蚪", "neutral");
        addEntity("minecraft:villager", "村民", "neutral");
        addEntity("minecraft:wandering_trader", "流浪商人", "neutral");
        addEntity("minecraft:wolf", "狼", "neutral");

        // 友好生物/其他
        addEntity("minecraft:allay", "悦灵", "friendly");
        addEntity("minecraft:item_frame", "物品展示框", "friendly");
        addEntity("minecraft:painting", "画", "friendly");
        addEntity("minecraft:player", "玩家", "friendly");
        addEntity("minecraft:elder_guardian", "远古守卫者", "friendly");
    }

    private static void addEntity(String id, String name, String category) {
        ENTITIES.add(new EntityEntry(id, name, category));
    }

    public static List<String> getSuggestions(String query) {
        List<String> results = new ArrayList<>();
        if (query == null || query.isEmpty()) {
            return results;
        }

        String lowerQuery = query.toLowerCase();
        for (EntityEntry entity : ENTITIES) {
            if (entity.id.toLowerCase().contains(lowerQuery) ||
                entity.name.toLowerCase().contains(lowerQuery) ||
                PinyinMatcher.getPinyinInitials(entity.name).contains(lowerQuery)) {
                results.add(entity.id);
            }
            if (results.size() >= 10) break;
        }

        return results;
    }

    private static class EntityEntry {
        final String id;
        final String name;
        final String category;

        EntityEntry(String id, String name, String category) {
            this.id = id;
            this.name = name;
            this.category = category;
        }
    }
}
