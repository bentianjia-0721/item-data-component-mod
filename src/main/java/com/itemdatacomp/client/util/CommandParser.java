package com.itemdatacomp.client.util;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 命令解析器
 * 解析 /give 命令并提取物品ID、数量和组件数据
 */
public class CommandParser {

    /**
     * 解析 /give 命令
     * @param command 完整的 /give 命令，例如: /give @p minecraft:diamond_sword[enchantments={levels:{minecraft:sharpness:5}},custom_name='{"text":"Test"}'] 1
     * @return 解析结果，包含物品ID、组件数据和数量
     */
    public static ParseResult parseGiveCommand(String command) {
        if (command == null || command.isEmpty()) {
            return new ParseResult(null, new HashMap<>(), 1, "命令为空");
        }

        command = command.trim();

        try {
            // 移除 /give 前缀
            if (command.startsWith("/give")) {
                command = command.substring(5).trim();
            }

            // 移除目标选择器 (@p, @s, 玩家名等)
            int spaceIndex = command.indexOf(' ');
            if (spaceIndex <= 0) {
                return new ParseResult(null, new HashMap<>(), 1, "无效的目标选择器格式");
            }
            command = command.substring(spaceIndex + 1).trim();

            // 解析物品ID和组件
            String itemId = null;
            Map<String, Object> components = new HashMap<>();
            int count = 1;

            int bracketStart = command.indexOf('[');
            if (bracketStart > 0) {
                // 有组件
                itemId = command.substring(0, bracketStart).trim();
                if (itemId.isEmpty()) {
                    return new ParseResult(null, new HashMap<>(), 1, "物品ID为空");
                }

                int bracketEnd = findMatchingBracket(command, bracketStart);
                if (bracketEnd <= bracketStart) {
                    return new ParseResult(itemId, new HashMap<>(), 1, "组件括号不匹配 (位置: " + bracketStart + ")");
                }

                String componentStr = command.substring(bracketStart + 1, bracketEnd);
                components = parseComponents(componentStr);

                // 解析数量
                String remaining = command.substring(bracketEnd + 1).trim();
                if (!remaining.isEmpty()) {
                    try {
                        count = Integer.parseInt(remaining);
                        if (count < 1) {
                            count = 1;
                        }
                    } catch (NumberFormatException e) {
                        return new ParseResult(itemId, components, 1, "物品数量格式无效: " + remaining);
                    }
                }
            } else {
                // 没有组件
                String[] parts = command.split("\\s+");
                itemId = parts[0];
                if (parts.length > 1) {
                    try {
                        count = Integer.parseInt(parts[1]);
                        if (count < 1) {
                            count = 1;
                        }
                    } catch (NumberFormatException ignored) {
                    }
                }
            }

            if (itemId == null || itemId.isEmpty()) {
                return new ParseResult(null, new HashMap<>(), 1, "无法解析物品ID");
            }

            return new ParseResult(itemId, components, count, null);
        } catch (Exception e) {
            return new ParseResult(null, new HashMap<>(), 1, "解析失败: " + e.getMessage());
        }
    }

    /**
     * 解析组件字符串
     * 例如: enchantments={levels:{minecraft:sharpness:5}},custom_name='{"text":"Test"}'
     * 改进: 更好的错误处理
     */
    private static Map<String, Object> parseComponents(String componentStr) {
        Map<String, Object> components = new HashMap<>();

        if (componentStr == null || componentStr.isEmpty()) {
            return components;
        }

        List<String> parts = splitTopLevel(componentStr, ',');

        for (String part : parts) {
            part = part.trim();
            if (part.isEmpty()) {
                continue;
            }

            int eqIndex = part.indexOf('=');
            if (eqIndex > 0) {
                String key = part.substring(0, eqIndex).trim();
                String value = part.substring(eqIndex + 1).trim();

                if (key.isEmpty() || value.isEmpty()) {
                    continue;
                }

                try {
                    Object parsed = parseSNBT(value);

                    // 特殊处理：规范化附魔格式
                    if ("minecraft:enchantments".equals(key) || "minecraft:stored_enchantments".equals(key)) {
                        parsed = normalizeEnchantments(parsed);
                    }

                    // 特殊处理：规范化Lore格式为HTML兼容的格式
                    if ("minecraft:lore".equals(key)) {
                        parsed = normalizeLore(parsed);
                    }

                    components.put(key, parsed);
                } catch (Exception e) {
                    // 保存原始字符串以便手动修正
                    components.put(key, value);
                }
            }
        }

        return components;
    }

    /**
     * 规范化附魔数据 - 将unquoted附魔ID转换为带命名空间的格式
     * 输入: {levels:{sharpness:5}}
     * 输出: {levels:{"minecraft:sharpness":5}}
     */
    @SuppressWarnings("unchecked")
    private static Object normalizeEnchantments(Object enchData) {
        if (!(enchData instanceof Map)) {
            return enchData;
        }

        Map<String, Object> enchMap = (Map<String, Object>) enchData;
        Map<String, Object> result = new HashMap<>();

        for (Map.Entry<String, Object> entry : enchMap.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if ("levels".equals(key) && value instanceof Map) {
                Map<String, Object> levels = (Map<String, Object>) value;
                Map<String, Object> normalizedLevels = new HashMap<>();
                for (Map.Entry<String, Object> level : levels.entrySet()) {
                    String enchId = normalizeEnchantmentId(level.getKey());
                    normalizedLevels.put(enchId, level.getValue());
                }
                result.put(key, normalizedLevels);
            } else {
                result.put(key, value);
            }
        }

        return result;
    }

    /**
     * 规范化Lore格式 - 转换为HTML兼容的JSON数组格式
     * 输入可能是: ["line1", "line2"] 或 [{"text":"line1"}, {"text":"line2"}]
     * 输出: [{"text":"line1"}, {"text":"line2"}]
     */
    @SuppressWarnings("unchecked")
    private static Object normalizeLore(Object loreData) {
        if (!(loreData instanceof List)) {
            return loreData;
        }

        List<Object> loreList = (List<Object>) loreData;
        List<Object> result = new ArrayList<>();

        for (Object item : loreList) {
            if (item instanceof String) {
                // 简单字符串转换为JSON格式
                Map<String, Object> textObj = new HashMap<>();
                textObj.put("text", item);
                result.add(textObj);
            } else if (item instanceof Map) {
                // 已经是JSON对象，保持原样
                result.add(item);
            } else {
                result.add(item);
            }
        }

        return result;
    }

    /**
     * 解析SNBT格式数据
     * 支持: {}, [], 字符串, 数字, 布尔值, unquoted keys, JSON文本
     * 改进: 更好的错误处理和边界情况支持
     */
    public static Object parseSNBT(String snbt) {
        if (snbt == null || snbt.isEmpty()) {
            return null;
        }

        snbt = snbt.trim();

        // 布尔值
        if ("true".equals(snbt) || "false".equals(snbt)) {
            return Boolean.parseBoolean(snbt);
        }

        // 数字（包括后缀）
        if (snbt.matches("^-?\\d+(\\.\\d+)?[fFdDlL]?$")) {
            try {
                if (snbt.endsWith("f") || snbt.endsWith("F")) {
                    return Float.parseFloat(snbt.substring(0, snbt.length() - 1));
                } else if (snbt.endsWith("d") || snbt.endsWith("D")) {
                    return Double.parseDouble(snbt.substring(0, snbt.length() - 1));
                } else if (snbt.endsWith("l") || snbt.endsWith("L")) {
                    return Long.parseLong(snbt.substring(0, snbt.length() - 1));
                } else if (snbt.contains(".")) {
                    return Double.parseDouble(snbt);
                } else {
                    return Integer.parseInt(snbt);
                }
            } catch (NumberFormatException ignored) {
            }
        }

        // 字符串（去掉引号）
        if ((snbt.startsWith("'") && snbt.endsWith("'")) ||
            (snbt.startsWith("\"") && snbt.endsWith("\""))) {
            String unquoted = snbt.substring(1, snbt.length() - 1)
                .replace("\\'", "'")
                .replace("\\\"", "\"");
            // 如果是JSON，尝试解析为Map或Array
            if (unquoted.startsWith("{") || unquoted.startsWith("[")) {
                return parseJsonString(unquoted);
            }
            return unquoted;
        }

        // JSON对象 - 先规范化unquoted keys
        if (snbt.startsWith("{") && snbt.endsWith("}")) {
            String normalized = normalizeUnquotedKeys(snbt);
            return parseJsonObject(normalized);
        }

        // JSON数组
        if (snbt.startsWith("[") && snbt.endsWith("]")) {
            return parseJsonArray(snbt);
        }

        return snbt;
    }

    /**
     * 规范化unquoted keys为带引号的格式
     * 例如: {levels:{sharpness:5}} -> {"levels":{"sharpness":5}}
     * 使用循环递归处理嵌套结构
     */
    private static String normalizeUnquotedKeys(String snbt) {
        String result = snbt;
        String prev;
        int iterations = 0;
        final int MAX_ITERATIONS = 10; // 防止无限循环

        // 多次迭代以处理嵌套的unquoted keys
        do {
            prev = result;
            // 替换 {key: 或 ,key: 为 {"key":
            result = result.replaceAll("([\\{,])\\s*([a-zA-Z_][a-zA-Z0-9_.]*)\\s*:", "$1\"$2\":");
            // 替换 {key= 或 ,key= 为 {"key":
            result = result.replaceAll("([\\{,])\\s*([a-zA-Z_][a-zA-Z0-9_.]*)\\s*=", "$1\"$2\":");
            iterations++;
        } while (!result.equals(prev) && iterations < MAX_ITERATIONS);

        return result;
    }

    /**
     * 解析JSON字符串
     * 递归解析嵌套的JSON文本
     */
    private static Object parseJsonString(String json) {
        if (json == null || json.isEmpty()) {
            return json;
        }

        json = json.trim();

        if (json.startsWith("{")) {
            return parseJsonObject(json);
        } else if (json.startsWith("[")) {
            return parseJsonArray(json);
        }
        return json;
    }

    /**
     * 解析JSON对象 {key:value, ...}
     * 支持SNBT格式（unquoted keys, 冒号或等号）、JSON文本和HTML兼容格式
     */
    private static Map<String, Object> parseJsonObject(String json) {
        Map<String, Object> result = new HashMap<>();
        json = json.trim();

        if (!json.startsWith("{") || !json.endsWith("}")) {
            return result;
        }

        String inner = json.substring(1, json.length() - 1).trim();
        if (inner.isEmpty()) {
            return result;
        }

        List<String> pairs = splitTopLevel(inner, ',');

        for (String pair : pairs) {
            int colonIdx = findKeyValueSeparator(pair);
            if (colonIdx > 0) {
                String key = pair.substring(0, colonIdx).trim();
                String value = pair.substring(colonIdx + 1).trim();

                // 清理key的引号
                if ((key.startsWith("\"") && key.endsWith("\"")) ||
                    (key.startsWith("'") && key.endsWith("'"))) {
                    key = key.substring(1, key.length() - 1);
                }

                try {
                    Object parsed = parseSNBT(value);
                    result.put(key, parsed);
                } catch (Exception e) {
                    result.put(key, value);
                }
            }
        }

        return result;
    }

    /**
     * 解析JSON数组 [value1, value2, ...]
     */
    private static List<Object> parseJsonArray(String json) {
        List<Object> result = new ArrayList<>();
        json = json.trim();

        if (!json.startsWith("[") || !json.endsWith("]")) {
            return result;
        }

        String inner = json.substring(1, json.length() - 1).trim();
        if (inner.isEmpty()) {
            return result;
        }

        List<String> elements = splitTopLevel(inner, ',');

        for (String element : elements) {
            try {
                result.add(parseSNBT(element.trim()));
            } catch (Exception e) {
                result.add(element.trim());
            }
        }

        return result;
    }

    /**
     * 找到键值对中的分隔符（冒号或等号），忽略嵌套括号内的分隔符
     */
    private static int findKeyValueSeparator(String pair) {
        int depth = 0;
        boolean inQuote = false;
        char quoteChar = 0;

        for (int i = 0; i < pair.length(); i++) {
            char c = pair.charAt(i);

            if (inQuote) {
                if (c == quoteChar && (i == 0 || pair.charAt(i - 1) != '\\')) {
                    inQuote = false;
                }
            } else {
                if (c == '"' || c == '\'') {
                    inQuote = true;
                    quoteChar = c;
                } else if (c == '{' || c == '[') {
                    depth++;
                } else if (c == '}' || c == ']') {
                    depth--;
                } else if ((c == ':' || c == '=') && depth == 0) {
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * 规范化附魔ID - 补充命名空间前缀
     * 例如: sharpness -> minecraft:sharpness
     */
    private static String normalizeEnchantmentId(String id) {
        if (id == null || id.isEmpty()) {
            return id;
        }
        if (!id.contains(":")) {
            return "minecraft:" + id;
        }
        return id;
    }

    /**
     * 找到与给定位置的开括号相匹配的闭括号
     */
    private static int findMatchingBracket(String str, int openIndex) {
        if (str.charAt(openIndex) != '[') {
            return -1;
        }

        int depth = 1;
        boolean inQuote = false;
        char quoteChar = 0;

        for (int i = openIndex + 1; i < str.length(); i++) {
            char c = str.charAt(i);

            if (inQuote) {
                if (c == quoteChar && (i == 0 || str.charAt(i - 1) != '\\')) {
                    inQuote = false;
                }
            } else {
                if (c == '"' || c == '\'') {
                    inQuote = true;
                    quoteChar = c;
                } else if (c == '[') {
                    depth++;
                } else if (c == ']') {
                    depth--;
                    if (depth == 0) {
                        return i;
                    }
                }
            }
        }

        return -1; // 括号不匹配
    }

    /**
     * 按顶层分隔符分割字符串（忽略嵌套的括号和引号内的分隔符）
     */
    private static List<String> splitTopLevel(String str, char separator) {
        List<String> result = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        int depth = 0;
        boolean inQuote = false;
        char quoteChar = 0;

        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);

            if (inQuote) {
                current.append(c);
                if (c == quoteChar && (i == 0 || str.charAt(i - 1) != '\\')) {
                    inQuote = false;
                }
            } else {
                if (c == '"' || c == '\'') {
                    inQuote = true;
                    quoteChar = c;
                    current.append(c);
                } else if (c == '{' || c == '[') {
                    depth++;
                    current.append(c);
                } else if (c == '}' || c == ']') {
                    depth--;
                    current.append(c);
                } else if (c == separator && depth == 0) {
                    if (current.length() > 0) {
                        result.add(current.toString().trim());
                        current = new StringBuilder();
                    }
                } else {
                    current.append(c);
                }
            }
        }

        if (current.length() > 0) {
            result.add(current.toString().trim());
        }

        return result;
    }

    /**
     * 解析结果
     */
    public static class ParseResult {
        public final String itemId;
        public final Map<String, Object> components;
        public final int count;
        public final String error; // null表示成功

        public ParseResult(String itemId, Map<String, Object> components, int count, String error) {
            this.itemId = itemId;
            this.components = components != null ? components : new HashMap<>();
            this.count = count;
            this.error = error;
        }

        public boolean isSuccess() {
            return error == null;
        }
    }
}
