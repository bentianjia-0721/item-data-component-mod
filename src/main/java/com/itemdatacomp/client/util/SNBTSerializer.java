package com.itemdatacomp.client.util;

import java.util.*;

/**
 * SNBT序列化器
 * 将Java对象转换为Minecraft SNBT格式命令
 */
public class SNBTSerializer {

    /**
     * 生成/give命令
     */
    public static String generateCommand(String itemId, Map<String, Object> components) {
        return generateCommand(itemId, components, null);
    }

    /**
     * 生成/give命令（包含版本信息）
     */
    public static String generateCommand(String itemId, Map<String, Object> components, String version) {
        if (itemId == null || itemId.isEmpty()) {
            return "";
        }

        StringBuilder cmd = new StringBuilder("/give @p ");
        cmd.append(itemId);

        if (components != null && !components.isEmpty()) {
            cmd.append("[");
            List<String> componentStrings = new ArrayList<>();
            Map<String, Object> commandComponents = buildCommandComponents(components);

            for (Map.Entry<String, Object> entry : commandComponents.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();

                if (value != null) {
                    String snbt = toComponentSNBT(key, value);
                    componentStrings.add(key + "=" + snbt);
                }
            }

            cmd.append(String.join(",", componentStrings));
            cmd.append("]");
        }

        cmd.append(" 1");

        return cmd.toString();
    }

    private static Map<String, Object> buildCommandComponents(Map<String, Object> components) {
        Map<String, Object> result = new LinkedHashMap<>(components);
        List<String> generatedLore = new ArrayList<>();

        collectEnchantmentAliasLore(result.get("minecraft:enchantments"), generatedLore);
        collectEnchantmentAliasLore(result.get("minecraft:stored_enchantments"), generatedLore);

        if (!generatedLore.isEmpty()) {
            Object existingLore = result.get("minecraft:lore");
            List<Object> mergedLore = new ArrayList<>();
            mergedLore.addAll(generatedLore);
            if (existingLore instanceof List<?> list) {
                mergedLore.addAll(list);
            }
            result.put("minecraft:lore", mergedLore);
        }

        return result;
    }

    private static void collectEnchantmentAliasLore(Object value, List<String> generatedLore) {
        if (!(value instanceof Map<?, ?> map) || !hasAliases(map)) {
            return;
        }

        Object levels = map.containsKey("levels") ? map.get("levels") : map;
        Object aliases = map.get("__aliases");
        if (!(levels instanceof Map<?, ?> levelMap) || !(aliases instanceof Map<?, ?> aliasMap)) {
            return;
        }

        for (Map.Entry<?, ?> entry : levelMap.entrySet()) {
            String id = String.valueOf(entry.getKey());
            Object alias = aliasMap.get(id);
            String displayName = alias != null ? String.valueOf(alias) : id.replace("minecraft:", "");
            generatedLore.add(displayName + " " + entry.getValue());
        }
    }

    private static String toComponentSNBT(String key, Object value) {
        if (("minecraft:enchantments".equals(key) || "minecraft:stored_enchantments".equals(key))
            && value instanceof Map<?, ?> map) {
            return toEnchantmentsSNBT(map);
        }

        if ("minecraft:lore".equals(key) && value instanceof List<?> list) {
            return toLoreSNBT(list);
        }

        if (("minecraft:custom_name".equals(key) || "minecraft:item_name".equals(key))
            && value instanceof String text) {
            return toTextComponentSNBT(text);
        }

        return toSNBT(value);
    }

    private static String toEnchantmentsSNBT(Map<?, ?> map) {
        Object levels = map.containsKey("levels") ? map.get("levels") : map;
        if (!(levels instanceof Map<?, ?> levelMap)) {
            return "{levels:{}}";
        }

        Map<String, Object> normalized = new LinkedHashMap<>();
        for (Map.Entry<?, ?> entry : levelMap.entrySet()) {
            String id = String.valueOf(entry.getKey());
            if (!id.contains(":")) {
                id = "minecraft:" + id;
            }
            normalized.put(id, entry.getValue());
        }

        StringBuilder result = new StringBuilder("{levels:");
        result.append(toSNBT(normalized));
        if (hasAliases(map)) {
            result.append(",show_in_tooltip:false");
        }
        result.append("}");
        return result.toString();
    }

    private static boolean hasAliases(Map<?, ?> map) {
        Object aliases = map.get("__aliases");
        return aliases instanceof Map<?, ?> aliasMap && !aliasMap.isEmpty();
    }

    private static String toLoreSNBT(List<?> lines) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < lines.size(); i++) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append(toTextComponentSNBT(String.valueOf(lines.get(i))));
        }
        sb.append("]");
        return sb.toString();
    }

    private static String toTextComponentSNBT(String text) {
        String trimmed = text.trim();
        String json = (trimmed.startsWith("{") || trimmed.startsWith("[") || trimmed.startsWith("\""))
            ? text
            : "{\"text\":\"" + escapeJson(text) + "\"}";
        return "'" + json.replace("\\", "\\\\").replace("'", "\\'") + "'";
    }

    private static String escapeJson(String text) {
        return text
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r");
    }

    /**
     * 将Java对象转换为SNBT格式
     */
    public static String toSNBT(Object value) {
        if (value == null) {
            return "{}";
        }

        if (value instanceof Boolean) {
            return value.toString();
        }

        if (value instanceof Number) {
            return value.toString();
        }

        if (value instanceof String) {
            String str = (String) value;
            // 如果是JSON字符串，用单引号包裹
            if (str.startsWith("{") || str.startsWith("[")) {
                return "'" + str.replace("'", "\\'") + "'";
            }
            // 普通字符串
            return "'" + str.replace("'", "\\'") + "'";
        }

        if (value instanceof List) {
            List<?> list = (List<?>) value;
            if (list.isEmpty()) {
                return "[]";
            }
            StringBuilder sb = new StringBuilder("[");
            for (int i = 0; i < list.size(); i++) {
                if (i > 0) sb.append(",");
                sb.append(toSNBT(list.get(i)));
            }
            sb.append("]");
            return sb.toString();
        }

        if (value instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) value;
            if (map.isEmpty()) {
                return "{}";
            }
            StringBuilder sb = new StringBuilder("{");
            boolean first = true;
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                if (!first) sb.append(",");
                first = false;
                sb.append(toSNBTKey(String.valueOf(entry.getKey()))).append(":");
                sb.append(toSNBT(entry.getValue()));
            }
            sb.append("}");
            return sb.toString();
        }

        // 默认转为字符串
        return "'" + value.toString().replace("'", "\\'") + "'";
    }

    private static String toSNBTKey(String key) {
        if (key.matches("[A-Za-z0-9_+.-]+")) {
            return key;
        }
        return "\"" + key.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
    }

    /**
     * 解析SNBT格式（简化版）
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

        // 数字
        try {
            if (snbt.contains(".")) {
                return Double.parseDouble(snbt);
            } else {
                return Integer.parseInt(snbt);
            }
        } catch (NumberFormatException ignored) {
        }

        // 字符串（去掉引号）
        if ((snbt.startsWith("'") && snbt.endsWith("'")) ||
            (snbt.startsWith("\"") && snbt.endsWith("\""))) {
            return snbt.substring(1, snbt.length() - 1).replace("\\'", "'");
        }

        // JSON对象/数组 - 简单解析
        if (snbt.startsWith("{") || snbt.startsWith("[")) {
            try {
                // 简化版本：直接返回字符串，由Minecraft处理
                return snbt;
            } catch (Exception e) {
                return snbt;
            }
        }

        return snbt;
    }

    /**
     * 解析/give命令
     */
    public static ParsedCommand parseCommand(String command) {
        if (command == null || command.isEmpty()) {
            return null;
        }

        command = command.trim();

        // 移除 /give 前缀
        if (command.startsWith("/give")) {
            command = command.substring(5).trim();
        }

        // 移除目标选择器 (@p, @s, 玩家名等)
        int spaceIndex = command.indexOf(' ');
        if (spaceIndex > 0) {
            command = command.substring(spaceIndex + 1).trim();
        }

        // 解析物品ID和组件
        String itemId;
        Map<String, Object> components = new HashMap<>();
        int count = 1;

        int bracketStart = command.indexOf('[');
        if (bracketStart > 0) {
            // 有组件
            itemId = command.substring(0, bracketStart).trim();

            int bracketEnd = command.lastIndexOf(']');
            if (bracketEnd > bracketStart) {
                String componentStr = command.substring(bracketStart + 1, bracketEnd);
                components = parseComponents(componentStr);

                // 解析数量
                String remaining = command.substring(bracketEnd + 1).trim();
                if (!remaining.isEmpty()) {
                    try {
                        count = Integer.parseInt(remaining);
                    } catch (NumberFormatException ignored) {
                    }
                }
            }
        } else {
            // 没有组件
            String[] parts = command.split("\\s+");
            itemId = parts[0];
            if (parts.length > 1) {
                try {
                    count = Integer.parseInt(parts[1]);
                } catch (NumberFormatException ignored) {
                }
            }
        }

        return new ParsedCommand(itemId, components, count);
    }

    /**
     * 解析组件字符串
     */
    private static Map<String, Object> parseComponents(String componentStr) {
        Map<String, Object> components = new HashMap<>();

        // 简化的组件解析（按顶层逗号分割）
        List<String> parts = splitTopLevel(componentStr, ',');

        for (String part : parts) {
            int eqIndex = part.indexOf('=');
            if (eqIndex > 0) {
                String key = part.substring(0, eqIndex).trim();
                String value = part.substring(eqIndex + 1).trim();
                components.put(key, parseSNBT(value));
            }
        }

        return components;
    }

    /**
     * 按顶层分隔符分割字符串（忽略嵌套的括号内的分隔符）
     */
    private static List<String> splitTopLevel(String str, char separator) {
        List<String> result = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        int depth = 0;

        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);

            if (c == '{' || c == '[') {
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

        if (current.length() > 0) {
            result.add(current.toString().trim());
        }

        return result;
    }

    /**
     * 解析结果
     */
    public record ParsedCommand(String itemId, Map<String, Object> components, int count) {}
}
