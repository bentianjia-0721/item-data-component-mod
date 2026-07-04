package com.itemdatacomp.client.widget.suggestion;

import com.itemdatacomp.client.util.PinyinMatcher;
import java.util.ArrayList;
import java.util.List;

/**
 * 药水效果自动完成提供器
 * 包含所有 minecraft:* 药水效果，支持拼音搜索
 */
public class PotionEffectSuggestionProvider {
    private static final List<PotionEffectEntry> EFFECTS = new ArrayList<>();

    static {
        // 正面效果
        addEffect("minecraft:speed", "速度");
        addEffect("minecraft:haste", "急迫");
        addEffect("minecraft:strength", "力量");
        addEffect("minecraft:instant_health", "瞬间治疗");
        addEffect("minecraft:jump_boost", "跳跃提升");
        addEffect("minecraft:regeneration", "生命恢复");
        addEffect("minecraft:resistance", "抗性");
        addEffect("minecraft:fire_resistance", "防火");
        addEffect("minecraft:water_breathing", "水下呼吸");
        addEffect("minecraft:invisibility", "隐身");
        addEffect("minecraft:night_vision", "夜视");
        addEffect("minecraft:saturation", "饱和");
        addEffect("minecraft:glowing", "发光");
        addEffect("minecraft:luck", "幸运");
        addEffect("minecraft:slow_falling", "缓降");
        addEffect("minecraft:conduit_power", "潮涌能量");
        addEffect("minecraft:dolphins_grace", "海豚的恩惠");
        addEffect("minecraft:hero_of_the_village", "村庄英雄");
        addEffect("minecraft:wind_charged", "带电");
        addEffect("minecraft:weaving", "编织");

        // 负面效果
        addEffect("minecraft:slowness", "缓慢");
        addEffect("minecraft:mining_fatigue", "挖掘疲劳");
        addEffect("minecraft:instant_damage", "瞬间伤害");
        addEffect("minecraft:nausea", "恶心");
        addEffect("minecraft:blindness", "失明");
        addEffect("minecraft:hunger", "饥饿");
        addEffect("minecraft:weakness", "虚弱");
        addEffect("minecraft:poison", "中毒");
        addEffect("minecraft:wither", "凋零");
        addEffect("minecraft:unluck", "厄运");
        addEffect("minecraft:bad_omen", "不祥之兆");
        addEffect("minecraft:trial_omen", "试炼不祥");
        addEffect("minecraft:raid_omen", "掠夺不祥");
        addEffect("minecraft:darkness", "黑暗");
        addEffect("minecraft:oozing", "滑液");
        addEffect("minecraft:infested", "虫蚀");
        addEffect("minecraft:levitation", "浮地");
    }

    private static void addEffect(String id, String chineseName) {
        EFFECTS.add(new PotionEffectEntry(id, chineseName));
    }

    public static List<String> getSuggestions(String query) {
        List<String> results = new ArrayList<>();
        if (query == null || query.isEmpty()) {
            return results;
        }

        String lowerQuery = query.toLowerCase();
        for (PotionEffectEntry effect : EFFECTS) {
            // 精确匹配 ID
            if (effect.id.toLowerCase().contains(lowerQuery)) {
                results.add(effect.id);
            }
            // 匹配中文名
            else if (effect.chineseName.contains(query)) {
                results.add(effect.id);
            }
            // 拼音首字母匹配
            else if (PinyinMatcher.getPinyinInitials(effect.chineseName).contains(lowerQuery)) {
                results.add(effect.id);
            }

            if (results.size() >= 10) break;
        }

        return results;
    }

    private static class PotionEffectEntry {
        final String id;
        final String chineseName;

        PotionEffectEntry(String id, String chineseName) {
            this.id = id;
            this.chineseName = chineseName;
        }
    }
}
