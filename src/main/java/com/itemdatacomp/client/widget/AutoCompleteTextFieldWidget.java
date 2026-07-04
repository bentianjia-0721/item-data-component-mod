package com.itemdatacomp.client.widget;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * 自动完成文本输入框
 * 支持下拉建议、键盘导航和前缀搜索
 */
public class AutoCompleteTextFieldWidget extends TextFieldWidget {
    private List<String> allSuggestions = new ArrayList<>();
    private List<String> filteredSuggestions = new ArrayList<>();
    private int selectedSuggestionIndex = -1;
    private boolean showSuggestions = false;
    private static final int MAX_SUGGESTIONS = 8;
    private static final int SUGGESTION_HEIGHT = 20;

    public AutoCompleteTextFieldWidget(
            net.minecraft.client.font.TextRenderer textRenderer,
            int x, int y, int width, int height,
            Text placeholder) {
        super(textRenderer, x, y, width, height, placeholder);
        this.allSuggestions = new ArrayList<>();
    }

    public void setSuggestions(List<String> suggestions) {
        this.allSuggestions = suggestions != null ? new ArrayList<>(suggestions) : new ArrayList<>();
        updateSuggestions();
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        boolean result = super.charTyped(chr, modifiers);
        updateSuggestions();
        return result;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (showSuggestions && !filteredSuggestions.isEmpty()) {
            switch (keyCode) {
                case 265: // Up arrow
                    selectedSuggestionIndex = (selectedSuggestionIndex - 1 + filteredSuggestions.size()) % filteredSuggestions.size();
                    return true;
                case 264: // Down arrow
                    selectedSuggestionIndex = (selectedSuggestionIndex + 1) % filteredSuggestions.size();
                    return true;
                case 257: // Enter
                    if (selectedSuggestionIndex >= 0 && selectedSuggestionIndex < filteredSuggestions.size()) {
                        this.setText(filteredSuggestions.get(selectedSuggestionIndex));
                        showSuggestions = false;
                        filteredSuggestions.clear();
                        return true;
                    }
                    break;
                case 256: // ESC
                    showSuggestions = false;
                    filteredSuggestions.clear();
                    selectedSuggestionIndex = -1;
                    return true;
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private void updateSuggestions() {
        String text = this.getText().toLowerCase();
        if (text.isEmpty()) {
            filteredSuggestions.clear();
            showSuggestions = false;
            selectedSuggestionIndex = -1;
            return;
        }

        filteredSuggestions = allSuggestions.stream()
            .filter(s -> s.toLowerCase().contains(text))
            .limit(MAX_SUGGESTIONS)
            .toList();
        showSuggestions = !filteredSuggestions.isEmpty();
        selectedSuggestionIndex = -1;
    }

    public void renderCustom(DrawContext context, int mouseX, int mouseY, float delta) {
        if (showSuggestions && !filteredSuggestions.isEmpty()) {
            renderSuggestions(context, mouseX, mouseY);
        }
    }

    public String getFirstSuggestionOrNull() {
        return filteredSuggestions.isEmpty() ? null : filteredSuggestions.get(0);
    }

    private void renderSuggestions(DrawContext context, int mouseX, int mouseY) {
        int x = this.getX();
        int y = this.getY() + this.getHeight();
        int width = this.getWidth();
        int displayCount = Math.min(filteredSuggestions.size(), MAX_SUGGESTIONS);

        // 背景
        context.fill(x - 1, y - 1, x + width + 1, y + displayCount * SUGGESTION_HEIGHT + 1, 0xFF555555);
        context.fill(x, y, x + width, y + displayCount * SUGGESTION_HEIGHT, 0xFF2A2A2A);

        // 渲染建议
        for (int i = 0; i < displayCount; i++) {
            String suggestion = filteredSuggestions.get(i);
            int suggestionY = y + i * SUGGESTION_HEIGHT;
            boolean isHovered = mouseX >= x && mouseX < x + width && mouseY >= suggestionY && mouseY < suggestionY + SUGGESTION_HEIGHT;
            boolean isSelected = i == selectedSuggestionIndex;

            // 背景颜色
            if (isSelected) {
                context.fill(x, suggestionY, x + width, suggestionY + SUGGESTION_HEIGHT, 0xFF4A90E2);
            } else if (isHovered) {
                context.fill(x, suggestionY, x + width, suggestionY + SUGGESTION_HEIGHT, 0xFF3A3A3A);
            }

            // 文本
            int textColor = isSelected ? 0xFFFFFF : 0xAAAAA0;
            context.drawText(MinecraftClient.getInstance().textRenderer, suggestion,
                    x + 4, suggestionY + (SUGGESTION_HEIGHT - 8) / 2, textColor, false);
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (showSuggestions && !filteredSuggestions.isEmpty()) {
            selectedSuggestionIndex = (int) ((selectedSuggestionIndex - (int)verticalAmount + filteredSuggestions.size()) % filteredSuggestions.size());
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (showSuggestions && !filteredSuggestions.isEmpty()) {
            int x = this.getX();
            int y = this.getY() + this.getHeight();
            int width = this.getWidth();
            int displayCount = Math.min(filteredSuggestions.size(), MAX_SUGGESTIONS);

            if (mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + displayCount * SUGGESTION_HEIGHT) {
                int index = (int) ((mouseY - y) / SUGGESTION_HEIGHT);
                if (index >= 0 && index < filteredSuggestions.size()) {
                    this.setText(filteredSuggestions.get(index));
                    showSuggestions = false;
                    filteredSuggestions.clear();
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private MinecraftClient getMinecraftClient() {
        return MinecraftClient.getInstance();
    }
}
