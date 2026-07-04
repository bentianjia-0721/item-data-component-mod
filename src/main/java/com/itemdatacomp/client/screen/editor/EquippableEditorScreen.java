package com.itemdatacomp.client.screen.editor;

import com.itemdatacomp.client.screen.ItemDataScreen;
import com.itemdatacomp.client.widget.AutoCompleteTextFieldWidget;
import com.itemdatacomp.client.widget.suggestion.SoundSuggestionProvider;
import com.itemdatacomp.client.widget.suggestion.ModelSuggestionProvider;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import java.util.function.Consumer;

/**
 * 装备编辑器
 * 用于 equippable 组件
 * 配置：槽位、装备音效、模型覆盖
 */
public class EquippableEditorScreen extends ItemDataScreen {
    private final Screen parent;
    private final EquippableData equippableData;
    private final Consumer<EquippableData> onSave;

    private static final String[] SLOTS = {
        "head", "chest", "legs", "feet", "body"
    };

    private int slotIndex = 0;
    private TextFieldWidget soundField;
    private TextFieldWidget modelField;
    private boolean swapAnimationFlag = false;

    public EquippableEditorScreen(Screen parent, EquippableData currentData, Consumer<EquippableData> onSave) {
        super(Text.translatable("editor.minecraft.equippable"));
        this.parent = parent;
        this.equippableData = currentData != null ? currentData : new EquippableData();
        this.onSave = onSave;

        // 设置当前槽位
        for (int i = 0; i < SLOTS.length; i++) {
            if (SLOTS[i].equals(this.equippableData.slot)) {
                this.slotIndex = i;
                break;
            }
        }
        this.swapAnimationFlag = this.equippableData.swappable;
    }

    @Override
    protected void init() {
        super.init();

        int centerX = this.width / 2;
        int startY = 60;

        // 装备音效输入框 - 带自动完成
        this.soundField = new AutoCompleteTextFieldWidget(
            this.client,
            centerX - 75,
            startY + 40,
            150,
            20,
            Text.translatable("label.equip_sound"),
            SoundSuggestionProvider::getSuggestions
        );
        this.soundField.setText(equippableData.equip_sound);
        this.soundField.setMaxLength(60);
        this.addSelectableChild(this.soundField);

        // 模型覆盖输入框 - 带自动完成
        this.modelField = new AutoCompleteTextFieldWidget(
            this.client,
            centerX - 75,
            startY + 80,
            150,
            20,
            Text.translatable("label.model"),
            ModelSuggestionProvider::getSuggestions
        );
        this.modelField.setText(equippableData.model);
        this.modelField.setMaxLength(80);
        this.addSelectableChild(this.modelField);

        // 槽位选择按钮（左箭头）
        this.addDrawableChild(ButtonWidget.builder(
            Text.translatable("symbol.arrow_left"),
            button -> {
                slotIndex = (slotIndex - 1 + SLOTS.length) % SLOTS.length;
            }
        ).dimensions(centerX - 150, startY, 30, 20).build());

        // 槽位选择按钮（右箭头）
        this.addDrawableChild(ButtonWidget.builder(
            Text.translatable("symbol.arrow_right"),
            button -> {
                slotIndex = (slotIndex + 1) % SLOTS.length;
            }
        ).dimensions(centerX + 120, startY, 30, 20).build());

        // 可交换复选框
        this.addDrawableChild(ButtonWidget.builder(
            Text.literal(swapAnimationFlag ? "✓ " : "☐ ").append(Text.translatable("label.swappable")),
            button -> swapAnimationFlag = !swapAnimationFlag
        ).dimensions(centerX - 100, startY + 120, 200, 20).build());

        // 保存按钮
        this.addDrawableChild(ButtonWidget.builder(
            Text.translatable("gui.done"),
            button -> save()
        ).dimensions(centerX - 155, this.height - 40, 150, 20).build());

        // 取消按钮
        this.addDrawableChild(ButtonWidget.builder(
            Text.translatable("gui.cancel"),
            button -> this.client.setScreen(parent)
        ).dimensions(centerX + 5, this.height - 40, 150, 20).build());
    }

    private void save() {
        equippableData.slot = SLOTS[slotIndex];
        equippableData.equip_sound = soundField.getText();
        equippableData.model = modelField.getText();
        equippableData.swappable = swapAnimationFlag;

        onSave.accept(equippableData);
        this.client.setScreen(parent);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);

        int centerX = this.width / 2;
        int startY = 60;

        // 标题
        context.drawCenteredTextWithShadow(this.textRenderer, this.title,
            centerX, 20, 0xFFFFFF);

        // 背景盒
        context.fill(centerX - 200, startY - 10, centerX + 200, startY + 140, 0x40000000);

        // 槽位标签和显示
        context.drawText(this.textRenderer, Text.translatable("label.slot").getString(),
            centerX - 90, startY + 5, 0xA78BFA, false);
        context.drawCenteredTextWithShadow(this.textRenderer,
            SLOTS[slotIndex],
            centerX, startY + 25, 0xFFFFFF);

        // 音效标签
        context.drawText(this.textRenderer, Text.translatable("label.equip_sound").getString(),
            centerX - 150, startY + 28, 0x888888, false);

        // 模型标签
        context.drawText(this.textRenderer, Text.translatable("label.model").getString(),
            centerX - 150, startY + 68, 0x888888, false);

        // 渲染文本框
        this.soundField.render(context, mouseX, mouseY, delta);
        this.modelField.render(context, mouseX, mouseY, delta);

        this.renderRegisteredDrawables(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    // 装备数据类
    public static class EquippableData {
        public String slot = "head";
        public String equip_sound = "";
        public String model = "";
        public boolean swappable = true;

        /**
         * 将EquippableData转换为Map用于序列化
         */
        public java.util.Map<String, Object> toMap() {
            java.util.Map<String, Object> map = new java.util.HashMap<>();
            map.put("slot", this.slot);
            if (!this.equip_sound.isEmpty()) {
                map.put("equip_sound", this.equip_sound);
            }
            if (!this.model.isEmpty()) {
                map.put("model", this.model);
            }
            map.put("swappable", this.swappable);
            return map;
        }

        /**
         * 从Map创建EquippableData
         */
        public static EquippableData fromMap(java.util.Map<String, Object> map) {
            EquippableData data = new EquippableData();
            if (map.containsKey("slot")) {
                Object slotObj = map.get("slot");
                if (slotObj instanceof String) {
                    data.slot = (String) slotObj;
                }
            }
            if (map.containsKey("equip_sound")) {
                Object soundObj = map.get("equip_sound");
                if (soundObj instanceof String) {
                    data.equip_sound = (String) soundObj;
                }
            }
            if (map.containsKey("model")) {
                Object modelObj = map.get("model");
                if (modelObj instanceof String) {
                    data.model = (String) modelObj;
                }
            }
            if (map.containsKey("swappable")) {
                Object swappableObj = map.get("swappable");
                if (swappableObj instanceof Boolean) {
                    data.swappable = (Boolean) swappableObj;
                }
            }
            return data;
        }
    }
}
