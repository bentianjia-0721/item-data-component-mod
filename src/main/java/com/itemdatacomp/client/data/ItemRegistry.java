package com.itemdatacomp.client.data;

import com.itemdatacomp.client.util.PinyinMatcher;
import java.util.*;

/**
 * 物品注册表
 * 包含所有Minecraft物品及其分类、中文名和版本信息
 */
public class ItemRegistry {

    public record ItemData(
        String id,
        String zhName,
        String category,
        String pinyin,
        MinecraftVersion addedIn,
        MinecraftVersion removedIn
    ) {}

    private static final List<ItemData> ITEMS = new ArrayList<>();
    private static final Map<String, ItemData> ITEM_MAP = new HashMap<>();

    static {
        // 武器
        addItem("minecraft:wooden_sword", "木剑", "weapon", "mj", MinecraftVersion.V1_21_0, null);
        addItem("minecraft:stone_sword", "石剑", "weapon", "sj", MinecraftVersion.V1_21_0, null);
        addItem("minecraft:iron_sword", "铁剑", "weapon", "tj");
        addItem("minecraft:golden_sword", "金剑", "weapon", "jj");
        addItem("minecraft:diamond_sword", "钻石剑", "weapon", "zsj");
        addItem("minecraft:netherite_sword", "下界合金剑", "weapon", "xjhjj");
        addItem("minecraft:bow", "弓", "weapon", "g");
        addItem("minecraft:crossbow", "弩", "weapon", "n");
        addItem("minecraft:trident", "三叉戟", "weapon", "scj");
        addItem("minecraft:mace", "重锤", "weapon", "zc", MinecraftVersion.V1_21_0, null);
        addItem("minecraft:arrow", "箭", "weapon", "j");
        addItem("minecraft:spectral_arrow", "光灵箭", "weapon", "glj");
        addItem("minecraft:tipped_arrow", "药箭", "weapon");
        addItem("minecraft:shield", "盾牌", "weapon", "dp");
        addItem("minecraft:wind_charge", "风弹", "weapon", "fd", MinecraftVersion.V1_21_0, null);

        // 工具
        addItem("minecraft:wooden_pickaxe", "木镐", "tool", "mg");
        addItem("minecraft:stone_pickaxe", "石镐", "tool", "sg");
        addItem("minecraft:iron_pickaxe", "铁镐", "tool", "tg");
        addItem("minecraft:golden_pickaxe", "金镐", "tool", "jg");
        addItem("minecraft:diamond_pickaxe", "钻石镐", "tool", "zsg");
        addItem("minecraft:netherite_pickaxe", "下界合金镐", "tool", "xjhjg");

        addItem("minecraft:wooden_axe", "木斧", "tool", "mf");
        addItem("minecraft:stone_axe", "石斧", "tool", "sf");
        addItem("minecraft:iron_axe", "铁斧", "tool", "tf");
        addItem("minecraft:golden_axe", "金斧", "tool", "jf");
        addItem("minecraft:diamond_axe", "钻石斧", "tool", "zsf");
        addItem("minecraft:netherite_axe", "下界合金斧", "tool", "xjhjf");

        addItem("minecraft:wooden_shovel", "木锹", "tool", "mq");
        addItem("minecraft:stone_shovel", "石锹", "tool", "sq");
        addItem("minecraft:iron_shovel", "铁锹", "tool", "tq");
        addItem("minecraft:golden_shovel", "金锹", "tool", "jq");
        addItem("minecraft:diamond_shovel", "钻石锹", "tool", "zsq");
        addItem("minecraft:netherite_shovel", "下界合金锹", "tool", "xjhjq");

        addItem("minecraft:wooden_hoe", "木锄", "tool", "mc");
        addItem("minecraft:stone_hoe", "石锄", "tool", "sc");
        addItem("minecraft:iron_hoe", "铁锄", "tool", "tc");
        addItem("minecraft:golden_hoe", "金锄", "tool", "jc");
        addItem("minecraft:diamond_hoe", "钻石锄", "tool", "zsc");
        addItem("minecraft:netherite_hoe", "下界合金锄", "tool", "xjhjc");

        addItem("minecraft:shears", "剪刀", "tool", "jd");
        addItem("minecraft:fishing_rod", "钓鱼竿", "tool", "dyg");
        addItem("minecraft:flint_and_steel", "打火石", "tool", "dhs");
        addItem("minecraft:compass", "指南针", "tool", "znz");
        addItem("minecraft:recovery_compass", "追溯指针", "tool");
        addItem("minecraft:clock", "时钟", "tool", "sz");
        addItem("minecraft:spyglass", "望远镜", "tool", "wyj");
        addItem("minecraft:brush", "刷子", "tool", "sz");
        addItem("minecraft:warped_fungus_on_a_stick", "诡异菌钓竿", "tool");
        addItem("minecraft:carrot_on_a_stick", "胡萝卜钓竿", "tool");
        addItem("minecraft:lead", "拴绳", "tool");
        addItem("minecraft:name_tag", "命名牌", "tool");
        addItem("minecraft:bundle", "收纳袋", "tool");
        addItem("minecraft:saddle", "鞍", "tool");

        // 盔甲
        addItem("minecraft:leather_helmet", "皮革头盔", "armor", "pgtkm");
        addItem("minecraft:leather_chestplate", "皮革胸甲", "armor", "pgxj");
        addItem("minecraft:leather_leggings", "皮革护腿", "armor", "pght");
        addItem("minecraft:leather_boots", "皮革靴子", "armor", "pgxz");

        addItem("minecraft:chainmail_helmet", "锁链头盔", "armor", "sltkm");
        addItem("minecraft:chainmail_chestplate", "锁链胸甲", "armor", "slxj");
        addItem("minecraft:chainmail_leggings", "锁链护腿", "armor", "slht");
        addItem("minecraft:chainmail_boots", "锁链靴子", "armor", "slxz");

        addItem("minecraft:iron_helmet", "铁头盔", "armor", "ttkm");
        addItem("minecraft:iron_chestplate", "铁胸甲", "armor", "txj");
        addItem("minecraft:iron_leggings", "铁护腿", "armor", "tht");
        addItem("minecraft:iron_boots", "铁靴子", "armor", "txz");

        addItem("minecraft:golden_helmet", "金头盔", "armor", "jtkm");
        addItem("minecraft:golden_chestplate", "金胸甲", "armor", "jxj");
        addItem("minecraft:golden_leggings", "金护腿", "armor", "jht");
        addItem("minecraft:golden_boots", "金靴子", "armor", "jxz");

        addItem("minecraft:diamond_helmet", "钻石头盔", "armor", "zstkm");
        addItem("minecraft:diamond_chestplate", "钻石胸甲", "armor", "zsxj");
        addItem("minecraft:diamond_leggings", "钻石护腿", "armor", "zsht");
        addItem("minecraft:diamond_boots", "钻石靴子", "armor", "zsxz");

        addItem("minecraft:netherite_helmet", "下界合金头盔", "armor", "xjhjtkm");
        addItem("minecraft:netherite_chestplate", "下界合金胸甲", "armor", "xjhjxj");
        addItem("minecraft:netherite_leggings", "下界合金护腿", "armor", "xjhjht");
        addItem("minecraft:netherite_boots", "下界合金靴子", "armor", "xjhjxz");

        addItem("minecraft:turtle_helmet", "海龟壳", "armor", "hgk");
        addItem("minecraft:elytra", "鞘翅", "armor", "qc");

        // 食物
        addItem("minecraft:apple", "苹果", "food", "pg");
        addItem("minecraft:golden_apple", "金苹果", "food", "jpg");
        addItem("minecraft:enchanted_golden_apple", "附魔金苹果", "food", "fmjpg");
        addItem("minecraft:bread", "面包", "food", "mb");
        addItem("minecraft:cooked_beef", "牛排", "food", "np");
        addItem("minecraft:cooked_porkchop", "熟猪排", "food", "szp");
        addItem("minecraft:cooked_chicken", "熟鸡肉", "food", "sjr");
        addItem("minecraft:cooked_mutton", "熟羊肉", "food", "syr");
        addItem("minecraft:cooked_cod", "熟鳕鱼", "food");
        addItem("minecraft:cooked_salmon", "熟鲑鱼", "food");
        addItem("minecraft:cooked_rabbit", "熟兔肉", "food");
        addItem("minecraft:carrot", "胡萝卜", "food", "hlb");
        addItem("minecraft:golden_carrot", "金胡萝卜", "food", "jhlb");
        addItem("minecraft:potato", "马铃薯", "food", "mls");
        addItem("minecraft:baked_potato", "烤马铃薯", "food", "kmls");
        addItem("minecraft:melon_slice", "西瓜片", "food", "xgp");
        addItem("minecraft:sweet_berries", "甜浆果", "food", "tjg");
        addItem("minecraft:glow_berries", "发光浆果", "food", "fgjg");
        addItem("minecraft:cookie", "曲奇", "food", "qq");
        addItem("minecraft:pumpkin_pie", "南瓜派", "food", "ngp");
        addItem("minecraft:dried_kelp", "干海带", "food", "ghd");
        addItem("minecraft:chorus_fruit", "紫颂果", "food", "zsg");
        addItem("minecraft:suspicious_stew", "迷之炖菜", "food", "mzdc");
        addItem("minecraft:beetroot_soup", "甜菜汤", "food", "tct");
        addItem("minecraft:mushroom_stew", "蘑菇煲", "food", "mgb");
        addItem("minecraft:rabbit_stew", "兔肉煲", "food", "trb");
        addItem("minecraft:honey_bottle", "蜂蜜瓶", "food", "fmp");
        addItem("minecraft:cake", "蛋糕", "food");
        addItem("minecraft:milk_bucket", "牛奶桶", "food");

        // 方块（更多）
        addItem("minecraft:stone", "石头", "block", "st");
        addItem("minecraft:granite", "花岗岩", "block", "hgy");
        addItem("minecraft:diorite", "闪长岩", "block", "scy");
        addItem("minecraft:andesite", "安山岩", "block", "asy");
        addItem("minecraft:deepslate", "深板岩", "block", "sby");
        addItem("minecraft:dirt", "泥土", "block", "nt");
        addItem("minecraft:grass_block", "草方块", "block", "cfk");
        addItem("minecraft:cobblestone", "圆石", "block", "ys");
        addItem("minecraft:sand", "沙子", "block");
        addItem("minecraft:gravel", "沙砾", "block");
        addItem("minecraft:obsidian", "黑曜石", "block");
        addItem("minecraft:oak_log", "橡木原木", "block", "xmym");
        addItem("minecraft:oak_planks", "橡木木板", "block", "xmmb");
        addItem("minecraft:spruce_log", "云杉原木", "block", "ysym");
        addItem("minecraft:spruce_planks", "云杉木板", "block", "ysmb");
        addItem("minecraft:birch_log", "白桦原木", "block", "bhym");
        addItem("minecraft:birch_planks", "白桦木板", "block", "bhmb");
        addItem("minecraft:jungle_log", "丛林原木", "block", "clym");
        addItem("minecraft:jungle_planks", "丛林木板", "block", "clmb");
        addItem("minecraft:acacia_log", "金合欢原木", "block", "jhyym");
        addItem("minecraft:acacia_planks", "金合欢木板", "block", "jhymb");
        addItem("minecraft:dark_oak_log", "深色橡木原木", "block", "ssxmym");
        addItem("minecraft:dark_oak_planks", "深色橡木木板", "block", "ssxmmb");
        addItem("minecraft:cherry_log", "樱花原木", "block", "yhym");
        addItem("minecraft:cherry_planks", "樱花木板", "block", "yhmb");
        addItem("minecraft:bamboo_block", "竹块", "block", "zk");
        addItem("minecraft:bamboo_planks", "竹木板", "block", "zmb");
        addItem("minecraft:crimson_stem", "绯红菌柄", "block", "fhjb");
        addItem("minecraft:crimson_planks", "绯红木板", "block", "fhmb");
        addItem("minecraft:warped_stem", "诡异菌柄", "block", "gyjb");
        addItem("minecraft:warped_planks", "诡异木板", "block", "gymb");
        addItem("minecraft:mangrove_log", "红树原木", "block", "hsym");
        addItem("minecraft:mangrove_planks", "红树木板", "block", "hsmb");

        addItem("minecraft:glass", "玻璃", "block", "bl");
        addItem("minecraft:white_stained_glass", "白色染色玻璃", "block", "bsrsbl");
        addItem("minecraft:glowstone", "荧石", "block", "ys");
        addItem("minecraft:sea_lantern", "海晶灯", "block", "hjd");
        addItem("minecraft:redstone_lamp", "红石灯", "block", "hsd");

        addItem("minecraft:chest", "箱子", "block", "xz");
        addItem("minecraft:barrel", "桶", "block", "t");
        addItem("minecraft:shulker_box", "潜影盒", "block");
        addItem("minecraft:crafting_table", "工作台", "block", "gzt");
        addItem("minecraft:furnace", "熔炉", "block", "rl");
        addItem("minecraft:blast_furnace", "高炉", "block", "gl");
        addItem("minecraft:smoker", "烟熏炉", "block", "yxl");
        addItem("minecraft:anvil", "铁砧", "block", "tz");
        addItem("minecraft:grindstone", "砂轮", "block", "sl");
        addItem("minecraft:enchanting_table", "附魔台", "block", "fmt");
        addItem("minecraft:brewing_stand", "酿造台", "block", "nzt");
        addItem("minecraft:cauldron", "炼药锅", "block", "lyg");
        addItem("minecraft:beacon", "信标", "block", "xb");
        addItem("minecraft:conduit", "潮涌核心", "block", "cxhx");
        addItem("minecraft:respawn_anchor", "重生锚", "block", "zsm");
        addItem("minecraft:lodestone", "磁石", "block", "cs");
        addItem("minecraft:beehive", "蜂箱", "block");
        addItem("minecraft:bee_nest", "蜂巢", "block");
        addItem("minecraft:note_block", "音符盒", "block");
        addItem("minecraft:jukebox", "唱片机", "block");
        addItem("minecraft:end_portal_frame", "末地传送门框架", "block");
        addItem("minecraft:decorated_pot", "饰纹陶罐", "block");
        addItem("minecraft:white_banner", "白色旗帜", "block");
        addItem("minecraft:white_bed", "白色床", "block");
        addItem("minecraft:ender_chest", "末影箱", "block");
        addItem("minecraft:trapped_chest", "陷阱箱", "block");
        addItem("minecraft:hopper", "漏斗", "block");
        addItem("minecraft:dropper", "投掷器", "block");
        addItem("minecraft:dispenser", "发射器", "block");
        addItem("minecraft:tnt", "TNT", "block");
        addItem("minecraft:oak_sign", "橡木告示牌", "block");
        addItem("minecraft:oak_hanging_sign", "橡木悬挂告示牌", "block");
        addItem("minecraft:bell", "钟", "block", "z");
        addItem("minecraft:composter", "堆肥桶", "block", "dft");
        addItem("minecraft:lectern", "讲台", "block", "jt");
        addItem("minecraft:stonecutter", "切石机", "block", "qsj");
        addItem("minecraft:loom", "织布机", "block", "zbj");
        addItem("minecraft:cartography_table", "制图台", "block", "ztt");
        addItem("minecraft:smithing_table", "锻造台", "block", "dzt");
        addItem("minecraft:fletching_table", "制箭台", "block", "zjt");
        addItem("minecraft:vault", "金库", "block", "jk", MinecraftVersion.V1_21_0, null);
        addItem("minecraft:trial_spawner", "试炼刷怪笼", "block", "slsgl", MinecraftVersion.V1_21_0, null);

        // 材料（更多）
        addItem("minecraft:stick", "木棍", "material", "mg");
        addItem("minecraft:diamond", "钻石", "material", "zs");
        addItem("minecraft:iron_ingot", "铁锭", "material", "td");
        addItem("minecraft:gold_ingot", "金锭", "material", "jd");
        addItem("minecraft:netherite_ingot", "下界合金锭", "material", "xjhjd");
        addItem("minecraft:emerald", "绿宝石", "material", "lbs");
        addItem("minecraft:coal", "煤炭", "material", "mt");
        addItem("minecraft:redstone", "红石粉", "material", "hsf");
        addItem("minecraft:lapis_lazuli", "青金石", "material", "qjs");
        addItem("minecraft:quartz", "石英", "material", "sy");
        addItem("minecraft:amethyst_shard", "紫水晶碎片", "material", "zsjsp");
        addItem("minecraft:copper_ingot", "铜锭", "material", "td");
        addItem("minecraft:netherite_scrap", "下界合金碎片", "material", "xjhjsp");
        addItem("minecraft:glowstone_dust", "荧石粉", "material", "ysf");
        addItem("minecraft:blaze_rod", "烈焰棒", "material", "lyb");
        addItem("minecraft:blaze_powder", "烈焰粉", "material", "lyf");
        addItem("minecraft:slime_ball", "粘液球", "material", "nyq");
        addItem("minecraft:prismarine_shard", "海晶碎片", "material", "hjsp");
        addItem("minecraft:nautilus_shell", "鹦鹉螺壳", "material", "yylk");
        addItem("minecraft:heart_of_the_sea", "海洋之心", "material", "hyzx");
        addItem("minecraft:echo_shard", "回响碎片", "material", "hxsp");
        addItem("minecraft:string", "线", "material", "x");
        addItem("minecraft:feather", "羽毛", "material", "ym");
        addItem("minecraft:leather", "皮革", "material", "pg");
        addItem("minecraft:gunpowder", "火药", "material", "hy");
        addItem("minecraft:bone", "骨头", "material", "gt");
        addItem("minecraft:bone_meal", "骨粉", "material", "gf");
        addItem("minecraft:nether_star", "下界之星", "material", "xjzx");
        addItem("minecraft:dragon_breath", "龙息", "material", "lx");
        addItem("minecraft:netherite_upgrade_smithing_template", "下界合金升级锻造模板", "material");
        addItem("minecraft:paper", "纸", "material");
        addItem("minecraft:phantom_membrane", "幻翼膜", "material");
        addItem("minecraft:rabbit_hide", "兔子皮", "material");
        addItem("minecraft:scute", "鳞甲", "material");
        addItem("minecraft:armadillo_scute", "犰狳鳞甲", "material");
        addItem("minecraft:breeze_rod", "旋风棒", "material");
        addItem("minecraft:heavy_core", "沉重核心", "material");
        addItem("minecraft:prismarine_crystals", "海晶砂粒", "material");
        addItem("minecraft:firework_rocket", "烟花火箭", "material");
        addItem("minecraft:firework_star", "烟花之星", "material");
        addItem("minecraft:fire_charge", "火焰弹", "material");
        addItem("minecraft:trial_key", "试炼钥匙", "material", "slys", MinecraftVersion.V1_21_0, null);
        addItem("minecraft:ominous_trial_key", "不祥试炼钥匙", "material", "bxslys", MinecraftVersion.V1_21_0, null);

        // 药水
        addItem("minecraft:potion", "药水", "potion", "ys");
        addItem("minecraft:splash_potion", "喷溅药水", "potion", "pjys");
        addItem("minecraft:lingering_potion", "滞留药水", "potion", "zlys");
        addItem("minecraft:glass_bottle", "玻璃瓶", "potion", "blp");
        addItem("minecraft:experience_bottle", "附魔之瓶", "potion", "fmzp");
        addItem("minecraft:ominous_bottle", "不祥之瓶", "potion");

        // 刷怪蛋
        addItem("minecraft:zombie_spawn_egg", "僵尸刷怪蛋", "spawn_egg", "jssgd");
        addItem("minecraft:skeleton_spawn_egg", "骷髅刷怪蛋", "spawn_egg", "klsgd");
        addItem("minecraft:spider_spawn_egg", "蜘蛛刷怪蛋", "spawn_egg");
        addItem("minecraft:creeper_spawn_egg", "苦力怕刷怪蛋", "spawn_egg", "klpsgd");
        addItem("minecraft:slime_spawn_egg", "史莱姆刷怪蛋", "spawn_egg");
        addItem("minecraft:ghast_spawn_egg", "恶魂刷怪蛋", "spawn_egg");
        addItem("minecraft:zombified_piglin_spawn_egg", "僵尸猪灵刷怪蛋", "spawn_egg");
        addItem("minecraft:blaze_spawn_egg", "烈焰人刷怪蛋", "spawn_egg");
        addItem("minecraft:witch_spawn_egg", "女巫刷怪蛋", "spawn_egg");
        addItem("minecraft:wither_skeleton_spawn_egg", "凋灵骷髅刷怪蛋", "spawn_egg");
        addItem("minecraft:enderman_spawn_egg", "末影人刷怪蛋", "spawn_egg", "myrsgd");
        addItem("minecraft:pig_spawn_egg", "猪刷怪蛋", "spawn_egg", "zsgd");
        addItem("minecraft:cow_spawn_egg", "牛刷怪蛋", "spawn_egg", "nsgd");
        addItem("minecraft:villager_spawn_egg", "村民刷怪蛋", "spawn_egg", "cmsgd");
        addItem("minecraft:breeze_spawn_egg", "旋风刷怪蛋", "spawn_egg", "xfsgd", MinecraftVersion.V1_21_0, null);
        addItem("minecraft:armadillo_spawn_egg", "犰狳刷怪蛋", "spawn_egg", "qysgd", MinecraftVersion.V1_21_0, null);
        addItem("minecraft:warden_spawn_egg", "监守者刷怪蛋", "spawn_egg", "jszsgd", MinecraftVersion.V1_21_0, null);

        // 杂项
        addItem("minecraft:ender_pearl", "末影珍珠", "misc", "myzz");
        addItem("minecraft:ender_eye", "末影之眼", "misc", "myzy");
        addItem("minecraft:book", "书", "misc", "s");
        addItem("minecraft:enchanted_book", "附魔书", "misc", "fms");
        addItem("minecraft:writable_book", "书与笔", "misc", "syb");
        addItem("minecraft:written_book", "成书", "misc", "cs");
        addItem("minecraft:player_head", "玩家头颅", "misc");
        addItem("minecraft:filled_map", "已探索地图", "misc");
        addItem("minecraft:map", "空地图", "misc");
        addItem("minecraft:totem_of_undying", "不死图腾", "misc");
        addItem("minecraft:goat_horn", "山羊角", "misc");
        addItem("minecraft:debug_stick", "调试棒", "misc");
        addItem("minecraft:knowledge_book", "知识之书", "misc");
        addItem("minecraft:bucket", "铁桶", "misc");
        addItem("minecraft:water_bucket", "水桶", "misc");
        addItem("minecraft:lava_bucket", "熔岩桶", "misc");
        addItem("minecraft:powder_snow_bucket", "细雪桶", "misc");
        addItem("minecraft:cod_bucket", "鳕鱼桶", "misc");
        addItem("minecraft:salmon_bucket", "鲑鱼桶", "misc");
        addItem("minecraft:tropical_fish_bucket", "热带鱼桶", "misc");
        addItem("minecraft:pufferfish_bucket", "河豚桶", "misc");
        addItem("minecraft:axolotl_bucket", "美西螈桶", "misc");
        addItem("minecraft:tadpole_bucket", "蝌蚪桶", "misc");
        addItem("minecraft:egg", "鸡蛋", "misc");
        addItem("minecraft:snowball", "雪球", "misc");
        addItem("minecraft:armor_stand", "盔甲架", "misc");
        addItem("minecraft:item_frame", "物品展示框", "misc");
        addItem("minecraft:glow_item_frame", "荧光物品展示框", "misc");
        addItem("minecraft:painting", "画", "misc");
        addItem("minecraft:oak_boat", "橡木船", "misc");
        addItem("minecraft:oak_chest_boat", "橡木运输船", "misc");
        addItem("minecraft:minecart", "矿车", "misc");
        addItem("minecraft:chest_minecart", "运输矿车", "misc");
        addItem("minecraft:hopper_minecart", "漏斗矿车", "misc");
        addItem("minecraft:tnt_minecart", "TNT矿车", "misc");
        addItem("minecraft:end_crystal", "末影水晶", "misc");
    }

    private static void addItem(String id, String zhName, String category, String pinyin, MinecraftVersion addedIn, MinecraftVersion removedIn) {
        ItemData data = new ItemData(id, zhName, category, pinyin, addedIn, removedIn);
        ITEMS.add(data);
        ITEM_MAP.put(id, data);
    }

    private static void addItem(String id, String zhName, String category, String pinyin) {
        addItem(id, zhName, category, pinyin, MinecraftVersion.V1_21_0, null);
    }

    private static void addItem(String id, String zhName, String category) {
        addItem(id, zhName, category, PinyinMatcher.getPinyinInitials(zhName), MinecraftVersion.V1_21_0, null);
    }

    public static List<ItemData> getAllItems() {
        return Collections.unmodifiableList(ITEMS);
    }

    public static ItemData getItem(String id) {
        return ITEM_MAP.get(id);
    }

    public static List<ItemData> searchItems(String query, String categoryFilter) {
        List<ItemData> results = new ArrayList<>();

        for (ItemData item : ITEMS) {
            // 分类过滤
            if (categoryFilter != null && !categoryFilter.equals("all") && !item.category.equals(categoryFilter)) {
                continue;
            }

            // 搜索匹配
            if (query == null || query.isEmpty()) {
                results.add(item);
                continue;
            }

            String q = query.toLowerCase();
            // 匹配ID
            if (item.id.toLowerCase().contains(q)) {
                results.add(item);
                continue;
            }
            // 匹配中文名
            if (item.zhName.contains(query)) {
                results.add(item);
                continue;
            }
            // 匹配拼音首字母
            if (item.pinyin.contains(q)) {
                results.add(item);
            }
        }

        return results;
    }

    public static String[] getCategories() {
        return new String[]{"all", "weapon", "tool", "armor", "block", "food", "material", "potion", "spawn_egg", "misc"};
    }

    /**
     * 获取指定Minecraft版本中可用的所有物品
     *
     * @param version 目标Minecraft版本
     * @return 该版本中可用的物品列表
     */
    public static List<ItemData> getItemsForVersion(MinecraftVersion version) {
        List<ItemData> result = new ArrayList<>();
        for (ItemData item : ITEMS) {
            if (isAvailableInVersion(item, version)) {
                result.add(item);
            }
        }
        return result;
    }

    /**
     * 检查物品是否在指定版本中可用
     *
     * @param item 物品数据
     * @param version 目标版本
     * @return 物品在该版本中是否可用
     */
    private static boolean isAvailableInVersion(ItemData item, MinecraftVersion version) {
        // 如果物品添加版本晚于目标版本，则不可用
        if (item.addedIn != null && version.getOrder() < item.addedIn.getOrder()) {
            return false;
        }
        // 如果物品移除版本早于或等于目标版本，则不可用
        if (item.removedIn != null && version.getOrder() >= item.removedIn.getOrder()) {
            return false;
        }
        return true;
    }

    /**
     * 搜索物品并按版本过滤
     *
     * @param query 搜索查询
     * @param categoryFilter 分类过滤
     * @param versionFilter 版本过滤（可为null则不过滤）
     * @return 符合条件的物品列表
     */
    public static List<ItemData> searchItems(String query, String categoryFilter, MinecraftVersion versionFilter) {
        List<ItemData> results = searchItems(query, categoryFilter);

        if (versionFilter != null) {
            results.removeIf(item -> !isAvailableInVersion(item, versionFilter));
        }

        return results;
    }
}
