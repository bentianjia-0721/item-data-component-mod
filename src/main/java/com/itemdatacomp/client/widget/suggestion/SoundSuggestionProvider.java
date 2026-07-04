package com.itemdatacomp.client.widget.suggestion;

import com.itemdatacomp.client.util.PinyinMatcher;
import java.util.ArrayList;
import java.util.List;

/**
 * 音效ID自动完成提供器
 * 包含所有 minecraft:entity.*, minecraft:block.* 和 minecraft:item.* 音效
 */
public class SoundSuggestionProvider {
    private static final List<SoundEntry> SOUNDS = new ArrayList<>();

    static {
        // 常用音效 - 优先显示
        addSound("minecraft:block.note_block.harp", "竖琴");
        addSound("minecraft:entity.player.levelup", "升级");
        addSound("minecraft:entity.player.hurt", "伤害");
        addSound("minecraft:block.portal.travel", "传送");
        addSound("minecraft:entity.endereye.launch", "末影眼");
        addSound("minecraft:entity.experience_orb.pickup", "经验球");
        addSound("minecraft:item.shield.block", "盾牌格挡");
        addSound("minecraft:entity.armor_stand.place", "盔甲架放置");

        // 实体音效
        addSound("minecraft:entity.bat.ambient", "蝙蝠");
        addSound("minecraft:entity.bee.sting", "蜜蜂");
        addSound("minecraft:entity.blaze.ambient", "烈焰人");
        addSound("minecraft:entity.cat.ambient", "猫");
        addSound("minecraft:entity.chicken.ambient", "鸡");
        addSound("minecraft:entity.cow.ambient", "牛");
        addSound("minecraft:entity.creeper.primed", "苦力怕点火");
        addSound("minecraft:entity.dolphin.ambient", "海豚");
        addSound("minecraft:entity.enderman.ambient", "末影人");
        addSound("minecraft:entity.enderman.teleport", "末影人传送");
        addSound("minecraft:entity.evoker.ambient", "唤魔者");
        addSound("minecraft:entity.ghast.ambient", "恶魂");
        addSound("minecraft:entity.goat.ambient", "山羊");
        addSound("minecraft:entity.guardian.ambient", "守卫者");
        addSound("minecraft:entity.horse.ambient", "马");
        addSound("minecraft:entity.iron_golem.step", "铁傀儡");
        addSound("minecraft:entity.llama.ambient", "羊驼");
        addSound("minecraft:entity.parrot.ambient", "鹦鹉");
        addSound("minecraft:entity.pig.ambient", "猪");
        addSound("minecraft:entity.rabbit.ambient", "兔子");
        addSound("minecraft:entity.sheep.ambient", "羊");
        addSound("minecraft:entity.skeleton.step", "骷髅");
        addSound("minecraft:entity.slime.jump", "史莱姆");
        addSound("minecraft:entity.spider.ambient", "蜘蛛");
        addSound("minecraft:entity.squid.ambient", "鱿鱼");
        addSound("minecraft:entity.villager.ambient", "村民");
        addSound("minecraft:entity.warden.ambient", "守卫者");
        addSound("minecraft:entity.witch.ambient", "女巫");
        addSound("minecraft:entity.wither.ambient", "凋零");
        addSound("minecraft:entity.wolf.ambient", "狼");
        addSound("minecraft:entity.zombie.ambient", "僵尸");
        addSound("minecraft:entity.zombie_villager.ambient", "僵尸村民");

        // 方块音效
        addSound("minecraft:block.anvil.use", "铁砧");
        addSound("minecraft:block.bell.use", "钟");
        addSound("minecraft:block.blast_furnace.fire_crackle", "高炉");
        addSound("minecraft:block.brewing_stand.brew", "酿造台");
        addSound("minecraft:block.button.click_off", "按钮");
        addSound("minecraft:block.chest.close", "箱子");
        addSound("minecraft:block.comparator.click", "比较器");
        addSound("minecraft:block.dispenser.dispense", "发射器");
        addSound("minecraft:block.door.close", "门");
        addSound("minecraft:block.enchantment_table.use", "附魔台");
        addSound("minecraft:block.fence_gate.close", "栅栏门");
        addSound("minecraft:block.fire.extinguish", "火焰熄灭");
        addSound("minecraft:block.furnace.fire_crackle", "熔炉");
        addSound("minecraft:block.glass.break", "玻璃破碎");
        addSound("minecraft:block.grindstone.use", "砂轮");
        addSound("minecraft:block.hopper.transfer", "漏斗");
        addSound("minecraft:block.iron_trapdoor.close", "铁活板门");
        addSound("minecraft:block.lantern.fall", "灯笼");
        addSound("minecraft:block.lava.pop", "岩浆");
        addSound("minecraft:block.lever.click", "拉杆");
        addSound("minecraft:block.lodestone.break", "磁石");
        addSound("minecraft:block.note_block.basedrum", "音符盒");
        addSound("minecraft:block.piston.extend", "活塞");
        addSound("minecraft:block.portal.trigger", "传送门");
        addSound("minecraft:block.redstone_torch.burn", "红石火焰");
        addSound("minecraft:block.repeater.click", "中继器");
        addSound("minecraft:block.stone.break", "石头破碎");
        addSound("minecraft:block.tripwire.attach", "绊线");
        addSound("minecraft:block.water.ambient", "水");
        addSound("minecraft:block.wooden_button.click_off", "木按钮");
        addSound("minecraft:block.wooden_door.close", "木门");
        addSound("minecraft:block.wooden_trapdoor.close", "木活板门");

        // 物品音效
        addSound("minecraft:item.armor.equip_chain", "盔甲穿戴");
        addSound("minecraft:item.armor.equip_diamond", "钻石盔甲");
        addSound("minecraft:item.armor.equip_elytra", "鞘翅");
        addSound("minecraft:item.armor.equip_generic", "通用盔甲");
        addSound("minecraft:item.armor.equip_gold", "金盔甲");
        addSound("minecraft:item.armor.equip_iron", "铁盔甲");
        addSound("minecraft:item.armor.equip_leather", "皮革盔甲");
        addSound("minecraft:item.armor.equip_netherite", "下界合金盔甲");
        addSound("minecraft:item.axe.scrape", "斧头刮削");
        addSound("minecraft:item.book.page_turn", "书页翻动");
        addSound("minecraft:item.book.put", "书放置");
        addSound("minecraft:item.bucket.empty", "桶倒空");
        addSound("minecraft:item.bucket.fill", "桶填满");
        addSound("minecraft:item.crossbow.charge", "弩充能");
        addSound("minecraft:item.crossbow.hit", "弩命中");
        addSound("minecraft:item.crossbow.loading_end", "弩加载完成");
        addSound("minecraft:item.crossbow.loading_middle", "弩加载中");
        addSound("minecraft:item.crossbow.loading_start", "弩加载开始");
        addSound("minecraft:item.crossbow.quick_charge_1", "弩快速充能1");
        addSound("minecraft:item.crossbow.quick_charge_2", "弩快速充能2");
        addSound("minecraft:item.crossbow.quick_charge_3", "弩快速充能3");
        addSound("minecraft:item.crossbow.shoot", "弩射击");
        addSound("minecraft:item.flintandsteel.use", "打火石");
        addSound("minecraft:item.goat_horn.resonates_1", "山羊角1");
        addSound("minecraft:item.goat_horn.resonates_2", "山羊角2");
        addSound("minecraft:item.nether_wart.plant", "地狱疣");
        addSound("minecraft:item.shield.break", "盾牌破碎");
        addSound("minecraft:item.spyglass.stop_using", "望远镜放下");
        addSound("minecraft:item.spyglass.use", "望远镜使用");
        addSound("minecraft:item.sweetberries.pick_from_bush", "甜浆果采摘");
        addSound("minecraft:item.trident.return", "三叉戟回收");
        addSound("minecraft:item.trident.riptide_1", "三叉戟激流1");
        addSound("minecraft:item.trident.riptide_2", "三叉戟激流2");
        addSound("minecraft:item.trident.riptide_3", "三叉戟激流3");
        addSound("minecraft:item.trident.throw", "三叉戟投掷");
    }

    private static void addSound(String id, String desc) {
        SOUNDS.add(new SoundEntry(id, desc));
    }

    public static List<String> getSuggestions(String query) {
        List<String> results = new ArrayList<>();
        if (query == null || query.isEmpty()) {
            return results;
        }

        String lowerQuery = query.toLowerCase();
        for (SoundEntry sound : SOUNDS) {
            if (sound.id.toLowerCase().contains(lowerQuery) ||
                sound.description.toLowerCase().contains(lowerQuery) ||
                PinyinMatcher.getPinyinInitials(sound.description).contains(lowerQuery)) {
                results.add(sound.id);
            }
            if (results.size() >= 10) break;
        }

        return results;
    }

    private static class SoundEntry {
        final String id;
        final String description;

        SoundEntry(String id, String description) {
            this.id = id;
            this.description = description;
        }
    }
}
