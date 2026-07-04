package com.itemdatacomp.client.screen.editor;

import com.itemdatacomp.client.data.ItemRegistry;
import com.itemdatacomp.client.screen.ItemDataScreen;
import com.itemdatacomp.client.widget.AutoCompleteTextFieldWidget;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import java.util.List;
import java.util.function.Consumer;

/**
 * 物品选择编辑器
 * 用于需要选择物品ID的组件，如 repairable、use_remainder 等
 */
public class ItemSelectorEditorScreen extends ItemDataScreen {
    private final Screen parent;
    private final String componentId;
    private final String currentValue;
    private final Consumer<String> onSave;
    private final String hintText;

    private AutoCompleteTextFieldWidget itemField;
    private TextFieldWidget categoryField;
    private String currentCategory = "all";

    public ItemSelectorEditorScreen(Screen parent, String componentId, String currentValue, Consumer<String> onSave) {
        this(parent, componentId, currentValue, onSave, null);
    }

    public ItemSelectorEditorScreen(Screen parent, String componentId, String currentValue, Consumer<String> onSave, String hintText) {
        super(editorTitle(componentId));
        this.parent = parent;
        this.componentId = componentId;
        this.currentValue = currentValue != null ? currentValue : "";
        this.onSave = onSave;
        this.hintText = hintText;
    }

    private static String editorKey(String componentId) {
        return "editor." + componentId.replace(":", ".");
    }

    private static String componentNameKey(String componentId) {
        return "component." + componentId.replace(":", ".");
    }

    private static Text editorTitle(String componentId) {
        String key = editorKey(componentId);
        String translated = Text.translatable(key).getString();
        if (!translated.equals(key)) {
            return Text.literal(translated);
        }
        return Text.translatable("gui.itemdatacomp.select_item", Text.translatable(componentNameKey(componentId)));
    }

    @Override
    protected void init() {
        super.init();

        int centerX = this.width / 2;
        int centerY = this.height / 2;

        // 分类选择按钮组
        int categoryY = centerY - 80;
        String[] categories = ItemRegistry.getCategories();
        int buttonWidth = 60;
        int totalWidth = categories.length * (buttonWidth + 2);
        int startX = centerX - totalWidth / 2;

        for (int i = 0; i < categories.length; i++) {
            final String category = categories[i];
            int x = startX + i * (buttonWidth + 2);

            this.addDrawableChild(ButtonWidget.builder(
                Text.translatable("category." + category),
                button -> {
                    currentCategory = category;
                    updateSuggestions();
                }
            ).dimensions(x, categoryY, buttonWidth, 20).build());
        }

        // 物品搜索框（带自动补全）
        this.itemField = new AutoCompleteTextFieldWidget(
            this.textRenderer,
            centerX - 150,
            centerY - 40,
            300,
            20,
            Text.translatable("gui.itemdatacomp.search_item")
        );
        this.itemField.setText(currentValue);
        this.itemField.setMaxLength(256);

        // 设置自动补全
        updateSuggestions();

        this.addSelectableChild(this.itemField);
        this.setInitialFocus(this.itemField);

        // 提示文本
        int hintY = centerY - 15;

        // 预览区域（显示选中的物品）
        int previewY = centerY + 10;

        // 保存按钮
        this.addDrawableChild(ButtonWidget.builder(
            Text.translatable("gui.done"),
            button -> {
                String value = itemField.getText().trim();
                // 验证是否是有效的物品ID
                if (value.isEmpty()) {
                    onSave.accept(null);
                } else {
                    // 确保格式为 minecraft:xxx
                    if (!value.contains(":")) {
                        value = "minecraft:" + value;
                    }
                    onSave.accept(value);
                }
                this.client.setScreen(parent);
            }
        ).dimensions(centerX - 155, centerY + 60, 150, 20).build());

        // 取消按钮
        this.addDrawableChild(ButtonWidget.builder(
            Text.translatable("gui.cancel"),
            button -> this.client.setScreen(parent)
        ).dimensions(centerX + 5, centerY + 60, 150, 20).build());

        // 清除按钮
        this.addDrawableChild(ButtonWidget.builder(
            Text.literal("×"),
            button -> {
                itemField.setText("");
                updateSuggestions();
            }
        ).dimensions(centerX + 155, centerY - 40, 20, 20).build());
    }

    private void updateSuggestions() {
        List<ItemRegistry.ItemData> items = ItemRegistry.searchItems("", currentCategory);
        List<String> suggestions = items.stream()
            .map(ItemRegistry.ItemData::id)
            .toList();

        if (itemField != null) {
            itemField.setSuggestions(suggestions);
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);

        int centerX = this.width / 2;
        int centerY = this.height / 2;

        // 标题
        context.drawCenteredTextWithShadow(this.textRenderer, this.title,
            centerX, 20, 0xFFFFFF);

        // 分类标签
        context.drawCenteredTextWithShadow(this.textRenderer,
            Text.translatable("gui.itemdatacomp.category"),
            centerX, centerY - 95, 0xAAAAAA);

        // 当前选中的分类高亮显示
        context.drawCenteredTextWithShadow(this.textRenderer,
            Text.translatable("gui.itemdatacomp.current_category",
                Text.translatable("category." + currentCategory)),
            centerX, centerY - 55, 0x55FF55);

        // 提示文本
        String hint = hintText != null ? hintText :
            Text.translatable(editorKey(componentId) + ".hint").getString();
        if (hint.equals(editorKey(componentId) + ".hint")) {
            hint = Text.translatable("gui.itemdatacomp.select_item_hint").getString();
        }
        context.drawCenteredTextWithShadow(this.textRenderer, Text.literal(hint),
            centerX, centerY - 15, 0x888888);

        // 显示当前输入的物品预览
        String currentInput = itemField.getText().trim();
        if (!currentInput.isEmpty()) {
            ItemRegistry.ItemData itemData = ItemRegistry.getItem(
                currentInput.contains(":") ? currentInput : "minecraft:" + currentInput
            );
            if (itemData != null) {
                context.drawCenteredTextWithShadow(this.textRenderer,
                    Text.literal("✓ " + itemData.zhName() + " (" + itemData.id() + ")"),
                    centerX, centerY + 10, 0x55FF55);
            } else {
                context.drawCenteredTextWithShadow(this.textRenderer,
                    Text.translatable("gui.itemdatacomp.invalid_item"),
                    centerX, centerY + 10, 0xFF5555);
            }
        }

        this.itemField.render(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
