package com.itemdatacomp.client.widget;

import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

/**
 * 文本输入框Widget - 包装原生 TextFieldWidget
 * 支持单行文本输入、搜索回调
 * 使用 Minecraft 1.21.4 原生 TextFieldWidget API
 */
public class TextInputWidget extends TextFieldWidget {

    private SearchCallback searchCallback;

    public TextInputWidget(int x, int y, int width, int height) {
        super(MinecraftClient.getInstance().textRenderer, x, y, width, height, Text.literal("Search"));
        this.setMaxLength(100);
        // Set up auto-callback on text change
        this.setChangedListener(query -> {
            if (searchCallback != null) {
                searchCallback.onSearchChanged(query);
            }
        });
    }

    public void setSearchCallback(SearchCallback callback) {
        this.searchCallback = callback;
    }

    @FunctionalInterface
    public interface SearchCallback {
        void onSearchChanged(String query);
    }
}
