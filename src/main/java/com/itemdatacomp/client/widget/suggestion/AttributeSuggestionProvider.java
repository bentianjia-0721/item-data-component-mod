package com.itemdatacomp.client.widget.suggestion;

import com.itemdatacomp.client.util.PinyinMatcher;
import java.util.ArrayList;
import java.util.List;

/**
 * 属性ID自动完成提供器
 * 包含所有 minecraft:* 属性修饰符，支持拼音搜索
 */
public class AttributeSuggestionProvider {
    private static final List<AttributeEntry> ATTRIBUTES = new ArrayList<>();

    static {
        // 通用属性
        addAttribute("minecraft:generic.max_health", "最大生命值");
        addAttribute("minecraft:generic.knockback_resistance", "击退抗性");
        addAttribute("minecraft:generic.movement_speed", "移动速度");
        addAttribute("minecraft:generic.flying_speed", "飞行速度");
        addAttribute("minecraft:generic.attack_damage", "攻击伤害");
        addAttribute("minecraft:generic.attack_knockback", "攻击击退");
        addAttribute("minecraft:generic.attack_speed", "攻击速度");
        addAttribute("minecraft:generic.armor", "护甲");
        addAttribute("minecraft:generic.armor_toughness", "护甲韧性");
        addAttribute("minecraft:generic.luck", "幸运");
        addAttribute("minecraft:generic.scale", "缩放");

        // 玩家属性
        addAttribute("minecraft:player.block_break_speed", "方块破坏速度");
        addAttribute("minecraft:player.block_interaction_range", "方块交互范围");
        addAttribute("minecraft:player.entity_interaction_range", "实体交互范围");

        // 其他属性
        addAttribute("minecraft:zombie.spawn_reinforcements", "僵尸增援");
    }

    private static void addAttribute(String id, String chineseName) {
        ATTRIBUTES.add(new AttributeEntry(id, chineseName));
    }

    public static List<String> getSuggestions(String query) {
        List<String> results = new ArrayList<>();
        if (query == null || query.isEmpty()) {
            return results;
        }

        String lowerQuery = query.toLowerCase();
        for (AttributeEntry attr : ATTRIBUTES) {
            // 精确匹配 ID
            if (attr.id.toLowerCase().contains(lowerQuery)) {
                results.add(attr.id);
            }
            // 匹配中文名
            else if (attr.chineseName.contains(query)) {
                results.add(attr.id);
            }
            // 拼音首字母匹配
            else if (PinyinMatcher.getPinyinInitials(attr.chineseName).contains(lowerQuery)) {
                results.add(attr.id);
            }

            if (results.size() >= 10) break;
        }

        return results;
    }

    private static class AttributeEntry {
        final String id;
        final String chineseName;

        AttributeEntry(String id, String chineseName) {
            this.id = id;
            this.chineseName = chineseName;
        }
    }
}
