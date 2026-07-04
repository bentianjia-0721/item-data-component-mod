package com.itemdatacomp.client.config;

import com.itemdatacomp.client.data.MinecraftVersion;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * 配置管理器 - 管理mod配置持久化
 * 配置文件位置: .minecraft/config/itemdatacomp.json
 */
public class ConfigManager {
    private static final String CONFIG_DIR = "config";
    private static final String CONFIG_FILE = "itemdatacomp.json";

    private static ConfigData configData = null;

    /**
     * 获取配置目录
     */
    private static Path getConfigPath() {
        String minecraftDir = System.getProperty("user.dir");
        return Paths.get(minecraftDir, CONFIG_DIR);
    }

    /**
     * 获取配置文件路径
     */
    private static Path getConfigFilePath() {
        return getConfigPath().resolve(CONFIG_FILE);
    }

    /**
     * 简单的JSON写入器（不依赖Gson）
     */
    private static String toJson(Map<String, String> data) {
        StringBuilder sb = new StringBuilder("{\n");
        boolean first = true;
        for (Map.Entry<String, String> entry : data.entrySet()) {
            if (!first) sb.append(",\n");
            sb.append("  \"").append(entry.getKey()).append("\": \"")
              .append(escapeJson(entry.getValue())).append("\"");
            first = false;
        }
        sb.append("\n}");
        return sb.toString();
    }

    /**
     * 简单的JSON字符串转义
     */
    private static String escapeJson(String str) {
        return str.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    /**
     * 简单的JSON解析器（不依赖Gson）
     */
    private static Map<String, String> parseJson(String json) {
        Map<String, String> result = new HashMap<>();
        // 移除首尾的大括号和空白
        json = json.trim();
        if (json.startsWith("{")) json = json.substring(1);
        if (json.endsWith("}")) json = json.substring(0, json.length() - 1);

        // 按逗号分割（简化版）
        String[] pairs = json.split(",");
        for (String pair : pairs) {
            pair = pair.trim();
            int colonIdx = pair.indexOf(":");
            if (colonIdx > 0) {
                String key = pair.substring(0, colonIdx).trim();
                String value = pair.substring(colonIdx + 1).trim();

                // 移除引号
                key = key.replaceAll("^\"|\"$", "");
                value = value.replaceAll("^\"|\"$", "");

                result.put(key, value);
            }
        }
        return result;
    }

    /**
     * 加载配置
     */
    public static void loadConfig() {
        try {
            Path configFile = getConfigFilePath();
            if (Files.exists(configFile)) {
                String json = new String(Files.readAllBytes(configFile));
                Map<String, String> data = parseJson(json);

                configData = new ConfigData();
                if (data.containsKey("selectedVersion")) {
                    String versionId = data.get("selectedVersion");
                    configData.selectedVersion = MinecraftVersion.fromId(versionId);
                } else {
                    configData.selectedVersion = getDefaultVersion();
                }

                if (data.containsKey("lastSelectedItem")) {
                    configData.lastSelectedItem = data.get("lastSelectedItem");
                }
            } else {
                // 创建默认配置
                configData = new ConfigData();
                configData.selectedVersion = getDefaultVersion();
                saveConfig();
            }
        } catch (Exception e) {
            System.err.println("Failed to load config: " + e.getMessage());
            configData = new ConfigData();
            configData.selectedVersion = getDefaultVersion();
        }
    }

    /**
     * 获取默认版本 - 从游戏版本检测
     */
    private static MinecraftVersion getDefaultVersion() {
        // 使用 1.21.4 作为默认，如果需要自动检测游戏版本，可以在这里添加逻辑
        return MinecraftVersion.V1_21_4;
    }

    /**
     * 保存配置
     */
    public static void saveConfig() {
        try {
            Path configPath = getConfigPath();
            if (!Files.exists(configPath)) {
                Files.createDirectories(configPath);
            }

            Map<String, String> data = new HashMap<>();
            if (configData != null && configData.selectedVersion != null) {
                data.put("selectedVersion", configData.selectedVersion.getId());
            }
            if (configData != null && configData.lastSelectedItem != null) {
                data.put("lastSelectedItem", configData.lastSelectedItem);
            }

            String json = toJson(data);
            Path configFile = getConfigFilePath();
            Files.write(configFile, json.getBytes());
        } catch (IOException e) {
            System.err.println("Failed to save config: " + e.getMessage());
        }
    }

    /**
     * 获取选中的版本
     */
    public static MinecraftVersion getSelectedVersion() {
        if (configData == null) {
            loadConfig();
        }
        return configData != null ? configData.selectedVersion : MinecraftVersion.V1_21_4;
    }

    /**
     * 设置选中的版本并自动保存
     */
    public static void setSelectedVersion(MinecraftVersion version) {
        if (configData == null) {
            configData = new ConfigData();
        }
        configData.selectedVersion = version;
        saveConfig();
    }

    /**
     * 获取最后选中的物品
     */
    public static String getLastSelectedItem() {
        if (configData == null) {
            loadConfig();
        }
        return configData != null ? configData.lastSelectedItem : null;
    }

    /**
     * 设置最后选中的物品并自动保存
     */
    public static void setLastSelectedItem(String itemId) {
        if (configData == null) {
            configData = new ConfigData();
        }
        configData.lastSelectedItem = itemId;
        saveConfig();
    }

    /**
     * 配置数据类
     */
    private static class ConfigData {
        MinecraftVersion selectedVersion;
        String lastSelectedItem;
    }
}

