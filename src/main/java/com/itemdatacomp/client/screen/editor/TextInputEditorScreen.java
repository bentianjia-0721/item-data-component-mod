package com.itemdatacomp.client.screen.editor;

import com.itemdatacomp.client.screen.ItemDataScreen;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import java.util.function.Consumer;

/**
 * 简单文本输入编辑器
 * 用于 custom_name, item_name 等简单字符串组件
 */
public class TextInputEditorScreen extends ItemDataScreen {
    private final Screen parent;
    private final String componentId;
    private final String currentValue;
    private final Consumer<String> onSave;

    private TextFieldWidget textField;

    public TextInputEditorScreen(Screen parent, String componentId, String currentValue, Consumer<String> onSave) {
        super(editorTitle(componentId));
        this.parent = parent;
        this.componentId = componentId;
        this.currentValue = currentValue != null ? currentValue : "";
        this.onSave = onSave;
    }

    private static String editorKey(String componentId) {
        return "editor." + componentId.replace(":", ".");
    }

    private static String componentNameKey(String componentId) {
        return "component." + componentId.replace(":", ".");
    }

    private static String componentDescKey(String componentId) {
        return componentNameKey(componentId) + ".desc";
    }

    private static Text editorTitle(String componentId) {
        String key = editorKey(componentId);
        String translated = Text.translatable(key).getString();
        if (!translated.equals(key)) {
            return Text.literal(translated);
        }
        return Text.translatable("gui.itemdatacomp.edit_component", Text.translatable(componentNameKey(componentId)));
    }

    @Override
    protected void init() {
        super.init();

        // 文本输入框
        this.textField = new TextFieldWidget(
            this.textRenderer,
            this.width / 2 - 150,
            this.height / 2 - 10,
            300,
            20,
            Text.literal("")
        );
        this.textField.setText(currentValue);
        this.textField.setMaxLength(256);
        this.addSelectableChild(this.textField);
        this.setInitialFocus(this.textField);

        // 保存按钮
        this.addDrawableChild(ButtonWidget.builder(
            Text.translatable("gui.done"),
            button -> {
                onSave.accept(textField.getText());
                this.client.setScreen(parent);
            }
        ).dimensions(this.width / 2 - 155, this.height / 2 + 30, 150, 20).build());

        // 取消按钮
        this.addDrawableChild(ButtonWidget.builder(
            Text.translatable("gui.cancel"),
            button -> this.client.setScreen(parent)
        ).dimensions(this.width / 2 + 5, this.height / 2 + 30, 150, 20).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);

        // 标题
        context.drawCenteredTextWithShadow(this.textRenderer, this.title,
            this.width / 2, this.height / 2 - 40, 0xFFFFFF);

        // 提示文本
        String hintKey = editorKey(componentId) + ".hint";
        String hint = Text.translatable(hintKey).getString();
        if (hint.equals(hintKey)) {
            hint = Text.translatable(componentDescKey(componentId)).getString();
            if (hint.equals(componentDescKey(componentId))) {
                hint = Text.translatable("gui.itemdatacomp.generic_input_hint").getString();
            }
        }
        context.drawCenteredTextWithShadow(this.textRenderer, Text.literal(hint),
            this.width / 2, this.height / 2 - 25, 0x888888);

        this.textField.render(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
