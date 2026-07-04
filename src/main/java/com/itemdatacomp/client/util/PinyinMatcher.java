package com.itemdatacomp.client.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * 拼音模糊搜索匹配器
 * 从HTML版本移植
 */
public class PinyinMatcher {

    /**
     * 搜索结果包装类
     */
    public static class SearchResult<T> {
        public final T item;
        public final double score;

        public SearchResult(T item, double score) {
            this.item = item;
            this.score = score;
        }
    }
    // 常用汉字拼音首字母映射
    private static final Map<Character, Character> PINYIN_MAP = new HashMap<>();

    static {
        // 从HTML版本移植的拼音映射
        String chars = "钻石剑斧镐锄锹盔甲头胸腿靴皮革锁链铁金下界合木圆沙砾火焰弹弓弩叉箭盾牌钓鱼竿剪刀打指南针时钟望远镜刷拴绳命鞍收纳袋面包牛猪鸡羊排熟肉苹果胡萝卜马铃薯南瓜派蘑菇煲甜菜汤兔曲奇西浆蜜蜂瓶奶桶药水璃紫颂歌光灵海龟壳鞘翅星尘丝线毛火萤幻翼膜鳞犰狳旋风棒沉重核晶粒鹦鹉螺洋心烟花装填不祥喷溅滞留蛋雪球骨粉架框画船矿车末影珍珠眼纸书鲑鳕河豚热带美螈蝌蚪白蓝绿红黑黄灰粉紫青靛橙棕色板台熔炉箱桶告示悬挂字幅旗帜床漏斗投掷发射器潜盒信标潮涌锚共鸣虫蠹恶魂苦怕史莱姆凋骷髅僵尸猪灵疣兽女巫唤魔恼鬼卫道掠夺劫监守沼泽嘎吱怪悦哞豹猫驴骡驼狐狸熊探犰狳蛙鹦鹉海豚鱿蝠狼护腿靴韧性爆炸摔落呼吸速掘深探索冰霜行灵魂疾迅捷荆棘忠诚引雷激流穿刺密度破消失绑定诅咒验修补耐久效率精准采集时运力量击退横扫亡杀抢海眷顾饵密试调棒式知识陶罐纹饰锻造模板回响碎片避腥橡桦杉丛竹樱红绯诡异暗苍白淡湖天品玫瑰";
        String pinyin = "zsfjgcqkjtxtkpgsltigxjhmyslfhydgnhczdjgsdzzndgjmmpnzptngcmgbtqxjgfxysfghghmfhyhmlqyxfbczchkyxtqzcmxxgfshmyxfwymxybsqgfxndzygwjmbxmcxsksmcqkqzxfdsxghhsqmdbgxmcscxglghfypgyxhxzyspzykcpjzldtxqbqfpypjzzsbhgyxzxhsqxsqfxtzhqzgdrtscxsbbvhxhtdyzxgxqzxhhdhjhbgwsjzcfpfqzqczscxtlxmhgzsxggbtgmxydgxzhbswtmsttdzmgbgjwwcsmxwsmgszhdjmzdpljdjszxmsyejgxwdhzwzdjgsxtyylxdxgnxgyhgyyjllxmyxdhmzqhdyzxzgzlhhddhgfzwqhsybjgynglhxdsfshsmyhqghhjfsjzcmbrxxcxdsjwyxslhcxpgsszxywxxcszcxscqlzkjcdbsngdzxyxsmysdhbxsdxjsgxzxdjzsjmcxccsxszjgxldtsycshbzxsxxpgjdyzmppcxlllxgjthtpqczmbthmxcpbclhtgdfhpyjzdyjsggsqygpy";

        for (int i = 0; i < chars.length() && i < pinyin.length(); i++) {
            PINYIN_MAP.put(chars.charAt(i), pinyin.charAt(i));
        }
    }

    /**
     * 获取文本的拼音首字母
     */
    public static String getPinyinInitials(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }

        StringBuilder result = new StringBuilder();
        for (char ch : text.toCharArray()) {
            if (PINYIN_MAP.containsKey(ch)) {
                result.append(PINYIN_MAP.get(ch));
            } else if ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z')) {
                result.append(Character.toLowerCase(ch));
            }
        }
        return result.toString();
    }

    /**
     * 模糊匹配
     * 支持：完全匹配、开头匹配、包含匹配、拼音首字母匹配
     */
    public static boolean fuzzyMatch(String query, String target, String targetChinese, String targetPinyin) {
        if (query == null || query.isEmpty()) {
            return true;
        }

        String q = query.toLowerCase();

        // 精确匹配 ID
        if (target != null && target.toLowerCase().contains(q)) {
            return true;
        }

        // 匹配中文名
        if (targetChinese != null && targetChinese.contains(query)) {
            return true;
        }

        // 匹配拼音首字母
        if (targetPinyin != null && targetPinyin.contains(q)) {
            return true;
        }

        // 匹配单词开头
        if (target != null) {
            String[] words = target.toLowerCase().replace("_", " ").replace(":", " ").split(" ");
            for (String word : words) {
                if (word.startsWith(q)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * 搜索并返回排序的结果列表
     */
    public static <T> List<SearchResult<T>> search(String query, List<T> items,
                                                     Function<T, String> idExtractor,
                                                     Function<T, String> chineseExtractor,
                                                     int maxResults) {
        List<SearchResult<T>> results = new ArrayList<>();

        for (T item : items) {
            String id = idExtractor.apply(item);
            String chinese = chineseExtractor.apply(item);
            String pinyin = getPinyinInitials(chinese);

            if (fuzzyMatch(query, id, chinese, pinyin)) {
                double score = scoreMatch(query, id, chinese, pinyin);
                results.add(new SearchResult<>(item, score));
            }
        }

        // 按评分降序排序
        results.sort((a, b) -> Double.compare(b.score, a.score));

        // 限制结果数量
        if (results.size() > maxResults) {
            results = results.subList(0, maxResults);
        }

        return results;
    }

    /**
     * 计算匹配评分 (0-100)
     */
    public static double scoreMatch(String query, String id, String chinese, String pinyin) {
        if (query == null || query.isEmpty()) {
            return 0;
        }

        String q = query.toLowerCase();
        double score = 0;

        // 精确匹配 ID (100分)
        if (id != null && id.toLowerCase().equals(q)) {
            return 100;
        }

        // ID 包含 (90分)
        if (id != null && id.toLowerCase().contains(q)) {
            score = Math.max(score, 90);
        }

        // 中文名包含 (80分)
        if (chinese != null && chinese.contains(query)) {
            score = Math.max(score, 80);
        }

        // 拼音首字母匹配 (60分)
        if (pinyin != null && pinyin.contains(q)) {
            score = Math.max(score, 60);
        }

        // 单词开头匹配 (70分)
        if (id != null) {
            String[] words = id.toLowerCase().replace("_", " ").replace(":", " ").split(" ");
            for (String word : words) {
                if (word.startsWith(q)) {
                    score = Math.max(score, 70);
                }
            }
        }

        return score;
    }
}
