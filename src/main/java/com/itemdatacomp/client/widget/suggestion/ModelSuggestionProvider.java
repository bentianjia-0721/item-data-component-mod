package com.itemdatacomp.client.widget.suggestion;

import com.itemdatacomp.client.util.PinyinMatcher;
import java.util.ArrayList;
import java.util.List;

/**
 * 物品模型自动完成提供器
 * 包含所有 minecraft:item/* 模型
 */
public class ModelSuggestionProvider {
    private static final List<String> MODELS = new ArrayList<>();

    static {
        // 工具模型
        addModel("minecraft:item/wooden_pickaxe", "木镐");
        addModel("minecraft:item/stone_pickaxe", "石镐");
        addModel("minecraft:item/iron_pickaxe", "铁镐");
        addModel("minecraft:item/golden_pickaxe", "金镐");
        addModel("minecraft:item/diamond_pickaxe", "钻石镐");
        addModel("minecraft:item/netherite_pickaxe", "下界合金镐");

        addModel("minecraft:item/wooden_axe", "木斧");
        addModel("minecraft:item/stone_axe", "石斧");
        addModel("minecraft:item/iron_axe", "铁斧");
        addModel("minecraft:item/golden_axe", "金斧");
        addModel("minecraft:item/diamond_axe", "钻石斧");
        addModel("minecraft:item/netherite_axe", "下界合金斧");

        addModel("minecraft:item/wooden_shovel", "木锹");
        addModel("minecraft:item/stone_shovel", "石锹");
        addModel("minecraft:item/iron_shovel", "铁锹");
        addModel("minecraft:item/golden_shovel", "金锹");
        addModel("minecraft:item/diamond_shovel", "钻石锹");
        addModel("minecraft:item/netherite_shovel", "下界合金锹");

        addModel("minecraft:item/wooden_hoe", "木锄");
        addModel("minecraft:item/stone_hoe", "石锄");
        addModel("minecraft:item/iron_hoe", "铁锄");
        addModel("minecraft:item/golden_hoe", "金锄");
        addModel("minecraft:item/diamond_hoe", "钻石锄");
        addModel("minecraft:item/netherite_hoe", "下界合金锄");

        addModel("minecraft:item/wooden_sword", "木剑");
        addModel("minecraft:item/stone_sword", "石剑");
        addModel("minecraft:item/iron_sword", "铁剑");
        addModel("minecraft:item/golden_sword", "金剑");
        addModel("minecraft:item/diamond_sword", "钻石剑");
        addModel("minecraft:item/netherite_sword", "下界合金剑");

        // 盔甲模型
        addModel("minecraft:item/leather_helmet", "皮革头盔");
        addModel("minecraft:item/leather_chestplate", "皮革胸甲");
        addModel("minecraft:item/leather_leggings", "皮革护腿");
        addModel("minecraft:item/leather_boots", "皮革靴子");

        addModel("minecraft:item/chainmail_helmet", "锁链头盔");
        addModel("minecraft:item/chainmail_chestplate", "锁链胸甲");
        addModel("minecraft:item/chainmail_leggings", "锁链护腿");
        addModel("minecraft:item/chainmail_boots", "锁链靴子");

        addModel("minecraft:item/iron_helmet", "铁头盔");
        addModel("minecraft:item/iron_chestplate", "铁胸甲");
        addModel("minecraft:item/iron_leggings", "铁护腿");
        addModel("minecraft:item/iron_boots", "铁靴子");

        addModel("minecraft:item/golden_helmet", "金头盔");
        addModel("minecraft:item/golden_chestplate", "金胸甲");
        addModel("minecraft:item/golden_leggings", "金护腿");
        addModel("minecraft:item/golden_boots", "金靴子");

        addModel("minecraft:item/diamond_helmet", "钻石头盔");
        addModel("minecraft:item/diamond_chestplate", "钻石胸甲");
        addModel("minecraft:item/diamond_leggings", "钻石护腿");
        addModel("minecraft:item/diamond_boots", "钻石靴子");

        addModel("minecraft:item/netherite_helmet", "下界合金头盔");
        addModel("minecraft:item/netherite_chestplate", "下界合金胸甲");
        addModel("minecraft:item/netherite_leggings", "下界合金护腿");
        addModel("minecraft:item/netherite_boots", "下界合金靴子");

        // 其他常用模型
        addModel("minecraft:item/bow", "弓");
        addModel("minecraft:item/crossbow", "弩");
        addModel("minecraft:item/trident", "三叉戟");
        addModel("minecraft:item/mace", "重锤");
        addModel("minecraft:item/shield", "盾牌");
        addModel("minecraft:item/elytra", "鞘翅");
        addModel("minecraft:item/totem_of_undying", "不死图腾");
        addModel("minecraft:item/apple", "苹果");
        addModel("minecraft:item/golden_apple", "金苹果");
        addModel("minecraft:item/enchanted_golden_apple", "附魔金苹果");
        addModel("minecraft:item/bread", "面包");
        addModel("minecraft:item/cooked_porkchop", "熟猪排");
        addModel("minecraft:item/cooked_beef", "熟牛肉");
        addModel("minecraft:item/cooked_chicken", "熟鸡肉");
        addModel("minecraft:item/cooked_mutton", "熟羊肉");
        addModel("minecraft:item/cooked_salmon", "熟鲑鱼");
        addModel("minecraft:item/cooked_cod", "熟鳕鱼");
        addModel("minecraft:item/golden_carrot", "金胡萝卜");
        addModel("minecraft:item/carrot", "胡萝卜");
        addModel("minecraft:item/baked_potato", "烤土豆");
        addModel("minecraft:item/potato", "土豆");
        addModel("minecraft:item/beetroot", "甜菜根");
        addModel("minecraft:item/pumpkin_pie", "南瓜派");
        addModel("minecraft:item/cake", "蛋糕");
        addModel("minecraft:item/cookie", "曲奇");
        addModel("minecraft:item/melon_slice", "西瓜");
        addModel("minecraft:item/glow_berries", "发光浆果");
        addModel("minecraft:item/sweet_berries", "甜浆果");
        addModel("minecraft:item/honey_bottle", "蜂蜜瓶");
        addModel("minecraft:item/diamond", "钻石");
        addModel("minecraft:item/emerald", "绿宝石");
        addModel("minecraft:item/iron_ingot", "铁锭");
        addModel("minecraft:item/gold_ingot", "金锭");
        addModel("minecraft:item/netherite_ingot", "下界合金锭");
        addModel("minecraft:item/copper_ingot", "铜锭");
        addModel("minecraft:item/ender_pearl", "末影珍珠");
        addModel("minecraft:item/ender_eye", "末影之眼");
        addModel("minecraft:item/nether_star", "下界之星");
        addModel("minecraft:item/experience_bottle", "经验瓶");
        addModel("minecraft:item/glowstone_dust", "荧石粉");
        addModel("minecraft:item/redstone", "红石");
        addModel("minecraft:item/coal", "煤炭");
        addModel("minecraft:item/charcoal", "木炭");
    }

    private static void addModel(String model, String desc) {
        MODELS.add(model);
    }

    public static List<String> getSuggestions(String query) {
        List<String> results = new ArrayList<>();
        if (query == null || query.isEmpty()) {
            return results;
        }

        String lowerQuery = query.toLowerCase();
        for (String model : MODELS) {
            if (model.toLowerCase().contains(lowerQuery)) {
                results.add(model);
            }
            if (results.size() >= 10) break;
        }

        return results;
    }
}
