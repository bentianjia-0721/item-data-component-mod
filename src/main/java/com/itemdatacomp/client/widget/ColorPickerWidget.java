package com.itemdatacomp.client.widget;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.text.Text;

/**
 * 颜色选择器Widget
 * 支持RGB颜色选择和16进制输入
 *
 * 注意：此类与 Minecraft 1.21.4 GUI API 不兼容。
 * 该类已被禁用，仅保留作为参考实现。
 */
@Deprecated
public class ColorPickerWidget implements Drawable {

    private int x, y, width, height;
    private int currentColor = 0xFFFFFFFF;

    public ColorPickerWidget(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void setColor(int color) {
        this.currentColor = color;
    }

    public int getColor() {
        return currentColor;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        MinecraftClient client = MinecraftClient.getInstance();

        // 背景
        context.fill(x, y, x + width, y + height, 0xFF1A1A1A);

        // 标题
        context.drawTextWithShadow(client.textRenderer, "颜色", x + 8, y + 6, 0xFFFFFF);

        // 颜色预览
        int previewX = x + width - 30;
        int previewY = y + 4;
        int rgb = currentColor & 0xFFFFFF;
        context.fill(previewX, previewY, previewX + 24, previewY + 12, 0xFF000000 | rgb);
        context.fill(previewX - 1, previewY - 1, previewX + 25, previewY + 13, 0xFF888888);

        // 16进制输入框背景
        int inputY = y + 20;
        context.fill(x + 8, inputY, x + width - 8, inputY + 16, 0xFF2A2A2A);

        // 16进制值显示
        String hexValue = String.format("#%06X", rgb);
        context.drawTextWithShadow(client.textRenderer, hexValue, x + 12, inputY + 4, 0xA78BFA);
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
