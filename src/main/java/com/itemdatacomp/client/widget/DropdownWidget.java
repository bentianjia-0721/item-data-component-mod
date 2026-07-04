package com.itemdatacomp.client.widget;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.text.Text;
import java.util.ArrayList;
import java.util.List;

/**
 * 下拉菜单Widget
 * 支持单选、搜索过滤和键盘导航
 *
 * 注意：此类与 Minecraft 1.21.4 GUI API 不兼容。
 * 该类已被禁用，仅保留作为参考实现。
 */
@Deprecated
public class DropdownWidget<T> implements Drawable {

    private int x, y, width, height;
    private List<DropdownOption<T>> options;
    private DropdownOption<T> selectedOption;
    private boolean expanded = false;

    public DropdownWidget(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.options = new ArrayList<>();
    }

    public void addOption(T value, String label) {
        options.add(new DropdownOption<>(value, label));
        if (selectedOption == null) {
            selectedOption = options.get(0);
        }
    }

    public void setSelectedOption(T value) {
        for (DropdownOption<T> opt : options) {
            if (opt.value.equals(value)) {
                selectedOption = opt;
                break;
            }
        }
    }

    public T getSelectedValue() {
        return selectedOption != null ? selectedOption.value : null;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        MinecraftClient client = MinecraftClient.getInstance();

        // 主按钮背景
        context.fill(x, y, x + width, y + height, 0xFF2A2A2A);
        context.fill(x, y, x + width, y, 0xFF555555); // 上边框
        context.fill(x, y + height, x + width, y + height + 1, 0xFF555555); // 下边框
        context.fill(x, y, x + 1, y + height, 0xFF555555); // 左边框
        context.fill(x + width - 1, y, x + width, y + height, 0xFF555555); // 右边框

        // 显示选中的选项（居中垂直对齐）
        String displayText = selectedOption != null ? selectedOption.label : "选择...";
        int textY = y + (height - client.textRenderer.fontHeight) / 2;
        context.drawTextWithShadow(client.textRenderer, displayText, x + 5, textY, 0xFFFFFF);

        // 下拉箭头（右侧对齐）
        String arrow = expanded ? "▼" : "▶";
        int arrowX = x + width - client.textRenderer.getWidth(arrow) - 5;
        context.drawTextWithShadow(client.textRenderer, arrow, arrowX, textY, 0x888888);
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public static class DropdownOption<T> {
        public final T value;
        public final String label;

        public DropdownOption(T value, String label) {
            this.value = value;
            this.label = label;
        }
    }
}
