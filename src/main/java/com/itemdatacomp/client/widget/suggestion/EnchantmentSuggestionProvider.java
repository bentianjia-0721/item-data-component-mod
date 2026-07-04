package com.itemdatacomp.client.widget.suggestion;

import com.itemdatacomp.client.util.PinyinMatcher;
import java.util.ArrayList;
import java.util.List;

/**
 * 附魔ID自动完成提供器
 * 包含所有 minecraft:* 附魔，支持拼音搜索
 */
public class EnchantmentSuggestionProvider {
    private static final List<EnchantmentEntry> ENCHANTMENTS = new ArrayList<>();

    static {
        // 通用附魔
        addEnchantment("minecraft:unbreaking", "耐久");
        addEnchantment("minecraft:mending", "经验修补");
        addEnchantment("minecraft:binding_curse", "绑定诅咒");
        addEnchantment("minecraft:vanishing_curse", "消失诅咒");

        // 武器附魔
        addEnchantment("minecraft:sharpness", "锋利");
        addEnchantment("minecraft:smite", "亡灵杀手");
        addEnchantment("minecraft:bane_of_arthropods", "节肢生物杀手");
        addEnchantment("minecraft:knockback", "击退");
        addEnchantment("minecraft:fire_aspect", "火焰附加");
        addEnchantment("minecraft:looting", "抢夺");
        addEnchantment("minecraft:sweeping_edge", "扫过边缘");

        // 工具附魔
        addEnchantment("minecraft:efficiency", "效率");
        addEnchantment("minecraft:fortune", "财富");
        addEnchantment("minecraft:silk_touch", "精准采集");

        // 弓附魔
        addEnchantment("minecraft:power", "力量");
        addEnchantment("minecraft:punch", "冲击");
        addEnchantment("minecraft:flame", "火焰");
        addEnchantment("minecraft:infinity", "无限");

        // 钓鱼竿附魔
        addEnchantment("minecraft:luck_of_the_sea", "海之眷顾");
        addEnchantment("minecraft:lure", "饵");

        // 盔甲附魔
        addEnchantment("minecraft:protection", "保护");
        addEnchantment("minecraft:fire_protection", "火焰保护");
        addEnchantment("minecraft:feather_falling", "羽落");
        addEnchantment("minecraft:blast_protection", "爆炸保护");
        addEnchantment("minecraft:projectile_protection", "弹射物保护");
        addEnchantment("minecraft:thorns", "荆棘");
        addEnchantment("minecraft:aqua_affinity", "水下采掘");
        addEnchantment("minecraft:respiration", "呼吸");
        addEnchantment("minecraft:depth_strider", "深海探险");
        addEnchantment("minecraft:frost_walker", "冰霜漫步");
        addEnchantment("minecraft:soul_speed", "灵魂疾行");
        addEnchantment("minecraft:swift_sneak", "迅捷潜行");

        // 三叉戟附魔
        addEnchantment("minecraft:channeling", "引雷");
        addEnchantment("minecraft:riptide", "激流");
        addEnchantment("minecraft:impaling", "穿刺");
        addEnchantment("minecraft:loyalty", "忠诚");

        // 弩附魔
        addEnchantment("minecraft:multishot", "多重射击");
        addEnchantment("minecraft:quick_charge", "快速装填");
        addEnchantment("minecraft:piercing", "穿透");

        // 重锤附魔
        addEnchantment("minecraft:density", "密度");
        addEnchantment("minecraft:breach", "破甲");
        addEnchantment("minecraft:wind_burst", "风爆");
    }

    private static void addEnchantment(String id, String chineseName) {
        ENCHANTMENTS.add(new EnchantmentEntry(id, chineseName));
    }

    public static List<String> getSuggestions(String query) {
        List<String> results = new ArrayList<>();
        if (query == null || query.isEmpty()) {
            return results;
        }

        String lowerQuery = query.toLowerCase();
        for (EnchantmentEntry ench : ENCHANTMENTS) {
            // 精确匹配 ID
            if (ench.id.toLowerCase().contains(lowerQuery)) {
                results.add(ench.id);
            }
            // 匹配中文名
            else if (ench.chineseName.contains(query)) {
                results.add(ench.id);
            }
            // 拼音首字母匹配
            else if (PinyinMatcher.getPinyinInitials(ench.chineseName).contains(lowerQuery)) {
                results.add(ench.id);
            }

            if (results.size() >= 10) break;
        }

        return results;
    }

    public static String resolveToId(String query) {
        if (query == null || query.trim().isEmpty()) {
            return "";
        }

        String normalized = query.trim().toLowerCase();
        for (EnchantmentEntry ench : ENCHANTMENTS) {
            if (ench.id.equalsIgnoreCase(normalized)
                || ench.id.replace("minecraft:", "").equalsIgnoreCase(normalized)
                || ench.chineseName.equals(query.trim())) {
                return ench.id;
            }
        }

        List<String> suggestions = getSuggestions(query.trim());
        if (!suggestions.isEmpty()) {
            return suggestions.get(0);
        }

        return normalized.contains(":") ? normalized : "minecraft:" + normalized;
    }

    private static class EnchantmentEntry {
        final String id;
        final String chineseName;

        EnchantmentEntry(String id, String chineseName) {
            this.id = id;
            this.chineseName = chineseName;
        }
    }
}
