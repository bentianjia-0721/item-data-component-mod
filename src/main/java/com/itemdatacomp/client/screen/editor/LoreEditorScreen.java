package com.itemdatacomp.client.screen.editor;

import com.itemdatacomp.client.screen.ItemDataScreen;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Lore行编辑器
 * 用于编辑物品描述（多行文本）
 */
public class LoreEditorScreen extends ItemDataScreen {
    private final Screen parent;
    private final List<String> loreLines;
    private final Consumer<List<String>> onSave;

    private final List<TextFieldWidget> textFields = new ArrayList<>();
    private final List<Integer> textFieldLineIndexes = new ArrayList<>();
    private int scrollOffset = 0;

    public LoreEditorScreen(Screen parent, List<String> currentLore, Consumer<List<String>> onSave) {
        super(Text.translatable("editor.minecraft.lore"));
        this.parent = parent;
        this.loreLines = currentLore != null ? new ArrayList<>(currentLore) : new ArrayList<>();
        this.onSave = onSave;

        // 确保至少有一行
        if (this.loreLines.isEmpty()) {
            this.loreLines.add("");
        }
    }

    @Override
    protected void init() {
        super.init();

        int startY = 60;
        int fieldWidth = 400;
        int fieldHeight = 20;
        int spacing = 25;

        // 为每行Lore创建文本框
        textFields.clear();
        textFieldLineIndexes.clear();
        for (int i = 0; i < loreLines.size(); i++) {
            int y = startY + i * spacing - scrollOffset;

            if (y < 50 || y > this.height - 100) {
                continue; // 不在可见区域
            }

            TextFieldWidget field = new TextFieldWidget(
                this.textRenderer,
                this.width / 2 - fieldWidth / 2,
                y,
                fieldWidth - 30,
                fieldHeight,
                Text.literal("")
            );
            field.setText(loreLines.get(i));
            field.setMaxLength(256);
            textFields.add(field);
            textFieldLineIndexes.add(i);
            this.addSelectableChild(field);

            // 删除按钮
            final int index = i;
            this.addDrawableChild(ButtonWidget.builder(
                Text.literal("X"),
                button -> removeLine(index)
            ).dimensions(this.width / 2 + fieldWidth / 2 - 25, y, 20, 20).build());
        }

        // 添加行按钮
        this.addDrawableChild(ButtonWidget.builder(
            Text.translatable("gui.itemdatacomp.lore.add"),
            button -> addLine()
        ).dimensions(this.width / 2 - 100, this.height - 70, 200, 20).build());

        // 保存按钮
        this.addDrawableChild(ButtonWidget.builder(
            Text.translatable("gui.done"),
            button -> save()
        ).dimensions(this.width / 2 - 155, this.height - 40, 150, 20).build());

        // 取消按钮
        this.addDrawableChild(ButtonWidget.builder(
            Text.translatable("gui.cancel"),
            button -> this.client.setScreen(parent)
        ).dimensions(this.width / 2 + 5, this.height - 40, 150, 20).build());
    }

    private void addLine() {
        syncTextFieldsToLoreLines();
        loreLines.add("");
        this.clearAndInit();
    }

    private void removeLine(int index) {
        syncTextFieldsToLoreLines();
        if (loreLines.size() > 1) {
            loreLines.remove(index);
            this.clearAndInit();
        }
    }

    private void save() {
        syncTextFieldsToLoreLines();
        List<String> result = new ArrayList<>();
        for (String line : loreLines) {
            if (!line.isEmpty()) {
                result.add(line);
            }
        }

        onSave.accept(result);
        this.client.setScreen(parent);
    }

    private void syncTextFieldsToLoreLines() {
        for (int i = 0; i < textFields.size(); i++) {
            int lineIndex = textFieldLineIndexes.get(i);
            if (lineIndex >= 0 && lineIndex < loreLines.size()) {
                loreLines.set(lineIndex, textFields.get(i).getText());
            }
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);

        // 标题
        context.drawCenteredTextWithShadow(this.textRenderer, this.title,
            this.width / 2, 20, 0xFFFFFF);

        // 提示
        context.drawCenteredTextWithShadow(this.textRenderer,
            Text.translatable("gui.itemdatacomp.lore.hint"),
            this.width / 2, 40, 0x888888);

        // 渲染文本框
        for (TextFieldWidget field : textFields) {
            field.render(context, mouseX, mouseY, delta);
        }

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        syncTextFieldsToLoreLines();
        scrollOffset += (int)(verticalAmount * 10);
        scrollOffset = Math.max(0, scrollOffset);
        this.clearAndInit();
        return true;
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
