package com.itemdatacomp.client.widget.suggestion;

import com.itemdatacomp.client.util.PinyinMatcher;
import java.util.ArrayList;
import java.util.List;

/**
 * 方块ID自动完成提供器
 * 包含所有 minecraft:* 方块，支持拼音搜索
 */
public class BlockSuggestionProvider {
    private static final List<BlockEntry> BLOCKS = new ArrayList<>();

    static {
        // 基础方块
        addBlock("minecraft:stone", "石头");
        addBlock("minecraft:granite", "花岗岩");
        addBlock("minecraft:diorite", "闪长岩");
        addBlock("minecraft:andesite", "安山岩");
        addBlock("minecraft:dirt", "泥土");
        addBlock("minecraft:coarse_dirt", "粗泥土");
        addBlock("minecraft:grass_block", "草方块");
        addBlock("minecraft:sand", "沙子");
        addBlock("minecraft:red_sand", "红沙");
        addBlock("minecraft:gravel", "沙砾");
        addBlock("minecraft:oak_log", "橡木原木");
        addBlock("minecraft:spruce_log", "云杉原木");
        addBlock("minecraft:birch_log", "桦木原木");
        addBlock("minecraft:jungle_log", "丛林原木");
        addBlock("minecraft:acacia_log", "相思木原木");
        addBlock("minecraft:dark_oak_log", "深色橡木原木");
        addBlock("minecraft:mangrove_log", "红树原木");
        addBlock("minecraft:cherry_log", "樱花原木");
        addBlock("minecraft:pale_oak_log", "淡色橡木原木");

        // 木料相关
        addBlock("minecraft:oak_wood", "橡木木料");
        addBlock("minecraft:spruce_wood", "云杉木料");
        addBlock("minecraft:birch_wood", "桦木木料");
        addBlock("minecraft:jungle_wood", "丛林木料");
        addBlock("minecraft:acacia_wood", "相思木料");
        addBlock("minecraft:dark_oak_wood", "深色橡木木料");
        addBlock("minecraft:mangrove_wood", "红树木料");
        addBlock("minecraft:cherry_wood", "樱花木料");
        addBlock("minecraft:pale_oak_wood", "淡色橡木木料");

        // 木板
        addBlock("minecraft:oak_planks", "橡木板");
        addBlock("minecraft:spruce_planks", "云杉木板");
        addBlock("minecraft:birch_planks", "桦木板");
        addBlock("minecraft:jungle_planks", "丛林木板");
        addBlock("minecraft:acacia_planks", "相思木板");
        addBlock("minecraft:dark_oak_planks", "深色橡木板");
        addBlock("minecraft:mangrove_planks", "红树木板");
        addBlock("minecraft:cherry_planks", "樱花木板");
        addBlock("minecraft:pale_oak_planks", "淡色橡木板");

        // 矿石
        addBlock("minecraft:coal_ore", "煤矿");
        addBlock("minecraft:copper_ore", "铜矿");
        addBlock("minecraft:iron_ore", "铁矿");
        addBlock("minecraft:gold_ore", "金矿");
        addBlock("minecraft:diamond_ore", "钻石矿");
        addBlock("minecraft:emerald_ore", "绿宝石矿");
        addBlock("minecraft:lapis_ore", "青金石矿");
        addBlock("minecraft:redstone_ore", "红石矿");
        addBlock("minecraft:nether_gold_ore", "下界金矿");
        addBlock("minecraft:nether_quartz_ore", "下界石英矿");
        addBlock("minecraft:deepslate_coal_ore", "深层煤矿");
        addBlock("minecraft:deepslate_copper_ore", "深层铜矿");
        addBlock("minecraft:deepslate_iron_ore", "深层铁矿");
        addBlock("minecraft:deepslate_gold_ore", "深层金矿");
        addBlock("minecraft:deepslate_diamond_ore", "深层钻石矿");
        addBlock("minecraft:deepslate_emerald_ore", "深层绿宝石矿");
        addBlock("minecraft:deepslate_lapis_ore", "深层青金石矿");
        addBlock("minecraft:deepslate_redstone_ore", "深层红石矿");

        // 储存块
        addBlock("minecraft:coal_block", "煤块");
        addBlock("minecraft:copper_block", "铜块");
        addBlock("minecraft:iron_block", "铁块");
        addBlock("minecraft:gold_block", "金块");
        addBlock("minecraft:diamond_block", "钻石块");
        addBlock("minecraft:emerald_block", "绿宝石块");
        addBlock("minecraft:lapis_block", "青金石块");
        addBlock("minecraft:redstone_block", "红石块");
        addBlock("minecraft:netherite_block", "下界合金块");
        addBlock("minecraft:quartz_block", "石英块");
        addBlock("minecraft:raw_copper_block", "粗铜块");
        addBlock("minecraft:raw_iron_block", "粗铁块");
        addBlock("minecraft:raw_gold_block", "粗金块");

        // 建筑方块
        addBlock("minecraft:cobblestone", "圆石");
        addBlock("minecraft:mossy_cobblestone", "苔藓圆石");
        addBlock("minecraft:stone_bricks", "石砖");
        addBlock("minecraft:mossy_stone_bricks", "苔藓石砖");
        addBlock("minecraft:cracked_stone_bricks", "裂纹石砖");
        addBlock("minecraft:chiseled_stone_bricks", "凿制石砖");
        addBlock("minecraft:deepslate", "深板岩");
        addBlock("minecraft:deepslate_bricks", "深板岩砖");
        addBlock("minecraft:deepslate_tiles", "深板岩瓷砖");
        addBlock("minecraft:brick", "砖块");
        addBlock("minecraft:bricks", "砖");
        addBlock("minecraft:mud_bricks", "泥砖");
        addBlock("minecraft:sandstone", "砂岩");
        addBlock("minecraft:chiseled_sandstone", "凿制砂岩");
        addBlock("minecraft:cut_sandstone", "切制砂岩");
        addBlock("minecraft:red_sandstone", "红砂岩");
        addBlock("minecraft:chiseled_red_sandstone", "凿制红砂岩");
        addBlock("minecraft:cut_red_sandstone", "切制红砂岩");

        // 玻璃
        addBlock("minecraft:glass", "玻璃");
        addBlock("minecraft:white_stained_glass", "白色染色玻璃");
        addBlock("minecraft:orange_stained_glass", "橙色染色玻璃");
        addBlock("minecraft:magenta_stained_glass", "品红色染色玻璃");
        addBlock("minecraft:light_blue_stained_glass", "浅蓝色染色玻璃");
        addBlock("minecraft:yellow_stained_glass", "黄色染色玻璃");
        addBlock("minecraft:lime_stained_glass", "黄绿色染色玻璃");
        addBlock("minecraft:pink_stained_glass", "粉红色染色玻璃");
        addBlock("minecraft:gray_stained_glass", "灰色染色玻璃");
        addBlock("minecraft:light_gray_stained_glass", "浅灰色染色玻璃");
        addBlock("minecraft:cyan_stained_glass", "青色染色玻璃");
        addBlock("minecraft:purple_stained_glass", "紫色染色玻璃");
        addBlock("minecraft:blue_stained_glass", "蓝色染色玻璃");
        addBlock("minecraft:brown_stained_glass", "棕色染色玻璃");
        addBlock("minecraft:green_stained_glass", "绿色染色玻璃");
        addBlock("minecraft:red_stained_glass", "红色染色玻璃");
        addBlock("minecraft:black_stained_glass", "黑色染色玻璃");

        // 羊毛
        addBlock("minecraft:white_wool", "白色羊毛");
        addBlock("minecraft:orange_wool", "橙色羊毛");
        addBlock("minecraft:magenta_wool", "品红色羊毛");
        addBlock("minecraft:light_blue_wool", "浅蓝色羊毛");
        addBlock("minecraft:yellow_wool", "黄色羊毛");
        addBlock("minecraft:lime_wool", "黄绿色羊毛");
        addBlock("minecraft:pink_wool", "粉红色羊毛");
        addBlock("minecraft:gray_wool", "灰色羊毛");
        addBlock("minecraft:light_gray_wool", "浅灰色羊毛");
        addBlock("minecraft:cyan_wool", "青色羊毛");
        addBlock("minecraft:purple_wool", "紫色羊毛");
        addBlock("minecraft:blue_wool", "蓝色羊毛");
        addBlock("minecraft:brown_wool", "棕色羊毛");
        addBlock("minecraft:green_wool", "绿色羊毛");
        addBlock("minecraft:red_wool", "红色羊毛");
        addBlock("minecraft:black_wool", "黑色羊毛");

        // 功能方块
        addBlock("minecraft:furnace", "熔炉");
        addBlock("minecraft:smoker", "烟熏炉");
        addBlock("minecraft:blast_furnace", "高炉");
        addBlock("minecraft:crafting_table", "工作台");
        addBlock("minecraft:anvil", "铁砧");
        addBlock("minecraft:enchanting_table", "附魔台");
        addBlock("minecraft:brewing_stand", "酿造台");
        addBlock("minecraft:cauldron", "炼药锅");
        addBlock("minecraft:beacon", "信标");
        addBlock("minecraft:grindstone", "砂轮");
        addBlock("minecraft:cartography_table", "制图台");
        addBlock("minecraft:loom", "织布机");
        addBlock("minecraft:smithing_table", "锻造台");
        addBlock("minecraft:chest", "箱子");
        addBlock("minecraft:trapped_chest", "陷阱箱");
        addBlock("minecraft:ender_chest", "末影箱");
        addBlock("minecraft:dispenser", "发射器");
        addBlock("minecraft:dropper", "投掷器");
        addBlock("minecraft:hopper", "漏斗");
        addBlock("minecraft:composter", "堆肥桶");

        // 红石方块
        addBlock("minecraft:redstone_wire", "红石");
        addBlock("minecraft:repeater", "中继器");
        addBlock("minecraft:comparator", "比较器");
        addBlock("minecraft:redstone_lamp", "红石灯");
        addBlock("minecraft:observer", "观察者");
        addBlock("minecraft:piston", "活塞");
        addBlock("minecraft:sticky_piston", "粘性活塞");

        // 其他常用方块
        addBlock("minecraft:grass", "草");
        addBlock("minecraft:seagrass", "水草");
        addBlock("minecraft:tall_grass", "高草");
        addBlock("minecraft:water", "水");
        addBlock("minecraft:lava", "岩浆");
        addBlock("minecraft:ice", "冰");
        addBlock("minecraft:packed_ice", "浮冰");
        addBlock("minecraft:blue_ice", "蓝冰");
        addBlock("minecraft:magma_block", "岩浆块");
        addBlock("minecraft:soul_sand", "灵魂沙");
        addBlock("minecraft:soul_soil", "灵魂土");
        addBlock("minecraft:nether_bricks", "下界砖");
        addBlock("minecraft:red_nether_bricks", "红色下界砖");
        addBlock("minecraft:warped_nether_bricks", "诡异下界砖");
        addBlock("minecraft:crimson_nether_bricks", "绯红下界砖");
        addBlock("minecraft:obsidian", "黑曜石");
        addBlock("minecraft:end_stone", "末地石");
        addBlock("minecraft:end_stone_bricks", "末地石砖");
        addBlock("minecraft:crying_obsidian", "哭泣的黑曜石");
        addBlock("minecraft:purpur_block", "紫水晶块");
        addBlock("minecraft:purpur_pillar", "紫水晶柱");
    }

    private static void addBlock(String id, String name) {
        BLOCKS.add(new BlockEntry(id, name));
    }

    public static List<String> getSuggestions(String query) {
        List<String> results = new ArrayList<>();
        if (query == null || query.isEmpty()) {
            return results;
        }

        String lowerQuery = query.toLowerCase();
        for (BlockEntry block : BLOCKS) {
            if (block.id.toLowerCase().contains(lowerQuery) ||
                block.name.toLowerCase().contains(lowerQuery) ||
                PinyinMatcher.getPinyinInitials(block.name).contains(lowerQuery)) {
                results.add(block.id);
            }
            if (results.size() >= 10) break;
        }

        return results;
    }

    private static class BlockEntry {
        final String id;
        final String name;

        BlockEntry(String id, String name) {
            this.id = id;
            this.name = name;
        }
    }
}
