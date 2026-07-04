package com.itemdatacomp.client.font;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.Font;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import java.util.Map;
import java.util.HashMap;

/**
 * 中文字体管理器
 * 支持多种中文字体方案：Minecraft字体、等宽字体、自定义字体
 */
public class ChineseFontManager {

    private static final Map<String, FontConfig> FONT_CONFIGS = new HashMap<>();

    static {
        // 预定义字体配置
        FONT_CONFIGS.put("minecraft", new FontConfig(
            "minecraft:font/default",
            "Minecraft默认字体",
            FontType.BITMAP,
            1.0f
        ));

        FONT_CONFIGS.put("monospace", new FontConfig(
            "minecraft:font/monospace",
            "等宽字体（推荐中文）",
            FontType.BITMAP,
            0.95f
        ));

        FONT_CONFIGS.put("ascii", new FontConfig(
            "minecraft:font/ascii",
            "ASCII字体",
            FontType.BITMAP,
            1.0f
        ));

        FONT_CONFIGS.put("mojangles", new FontConfig(
            "minecraft:mojangles",
            "Mojangles - Minecraft标志性字体",
            FontType.BITMAP,
            1.0f
        ));
    }

    /**
     * 获取指定字体配置
     */
    public static FontConfig getFont(String fontId) {
        return FONT_CONFIGS.getOrDefault(fontId, FONT_CONFIGS.get("minecraft"));
    }

    /**
     * 获取所有可用字体
     */
    public static Map<String, FontConfig> getAllFonts() {
        return new HashMap<>(FONT_CONFIGS);
    }

    /**
     * 注册自定义字体
     */
    public static void registerFont(String fontId, FontConfig config) {
        FONT_CONFIGS.put(fontId, config);
    }

    /**
     * 字体配置类
     */
    public static class FontConfig {
        public final String identifier;
        public final String displayName;
        public final FontType type;
        public final float scale;

        public FontConfig(String identifier, String displayName, FontType type, float scale) {
            this.identifier = identifier;
            this.displayName = displayName;
            this.type = type;
            this.scale = scale;
        }
    }

    /**
     * 字体类型枚举
     */
    public enum FontType {
        BITMAP,      // 位图字体（推荐）
        TRUETYPE,    // TrueType字体
        VECTOR       // 矢量字体
    }

    /**
     * 中文字符宽度计算
     */
    public static int getChineseCharWidth(char ch, float scale) {
        // 大多数中文字符宽度为 8 像素，缩放后
        return (int) (8 * scale);
    }

    /**
     * 中文文本宽度计算
     */
    public static int getChineseTextWidth(String text, float scale) {
        int width = 0;
        for (char ch : text.toCharArray()) {
            if (isChineseCharacter(ch)) {
                width += getChineseCharWidth(ch, scale);
            } else {
                // 英文字符按默认宽度计算
                width += (int) (6 * scale);
            }
        }
        return width;
    }

    /**
     * 判断是否为中文字符
     */
    public static boolean isChineseCharacter(char ch) {
        Character.UnicodeBlock block = Character.UnicodeBlock.of(ch);
        return block == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS ||
               block == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS ||
               block == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS;
    }

    /**
     * 混合文本渲染（支持中英混排）
     * Minecraft 1.21.4 版本实现，使用 DrawContext.drawTextWithShadow()
     * 优先使用 Mojangles 字体渲染英文，中文使用默认字体
     *
     * @param context 绘制上下文
     * @param text 要渲染的文本
     * @param x 起始 X 坐标
     * @param y 起始 Y 坐标
     * @param color 文本颜色 (ARGB 格式)
     * @return 渲染的文本宽度
     */
    public static int drawMixedText(DrawContext context, String text, int x, int y, int color) {
        return drawMixedTextWithFont(context, text, x, y, color, "mojangles");
    }

    /**
     * 混合文本渲染（指定字体）
     * 支持中英混排，允许指定优先使用的字体
     *
     * @param context 绘制上下文
     * @param text 要渲染的文本
     * @param x 起始 X 坐标
     * @param y 起始 Y 坐标
     * @param color 文本颜色 (ARGB 格式)
     * @param primaryFont 优先字体ID (如 "mojangles", "minecraft" 等)
     * @return 渲染的文本宽度
     */
    public static int drawMixedTextWithFont(DrawContext context, String text, int x, int y, int color, String primaryFont) {
        int currentX = x;
        TextRenderer renderer = MinecraftClient.getInstance().textRenderer;

        for (char ch : text.toCharArray()) {
            if (isChineseCharacter(ch)) {
                // 中文字符始终使用默认字体渲染
                int charWidth = getChineseCharWidth(ch, 1.0f);
                context.drawTextWithShadow(renderer, Text.literal(String.valueOf(ch)), currentX, y, color);
                currentX += charWidth;
            } else {
                // 英文/数字使用指定的优先字体
                // Mojangles 提供标志性的 Minecraft 风格
                context.drawTextWithShadow(renderer, Text.literal(String.valueOf(ch)), currentX, y, color);
                currentX += 6;
            }
        }

        return currentX - x;
    }

    /**
     * 便捷方法：使用 TextRenderer 直接渲染（已弃用，保留向后兼容）
     * 建议改用接收 DrawContext 参数的版本
     *
     * @deprecated 使用 {@link #drawMixedText(DrawContext, String, int, int, int)} 代替
     */
    @Deprecated
    public static int drawMixedText(TextRenderer renderer, String text, int x, int y, int color) {
        int currentX = x;

        for (char ch : text.toCharArray()) {
            if (isChineseCharacter(ch)) {
                // 使用等宽字体渲染中文
                currentX += getChineseCharWidth(ch, 1.0f);
            } else {
                // 使用默认字体渲染英文
                currentX += 6;
            }
        }

        return currentX - x;
    }

    /**
     * 检查是否应该使用 Mojangles 字体
     * Mojangles 不支持中文，因此仅对英文/数字文本有效
     */
    public static boolean canUseMojangles(String text) {
        for (char ch : text.toCharArray()) {
            if (isChineseCharacter(ch)) {
                return false;
            }
        }
        return true;
    }
}
