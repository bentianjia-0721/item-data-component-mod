package com.itemdatacomp.client.screen.editor;

import com.itemdatacomp.client.screen.ItemDataScreen;
import com.itemdatacomp.client.widget.AutoCompleteTextFieldWidget;
import com.itemdatacomp.client.widget.suggestion.PotionEffectSuggestionProvider;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import java.util.*;
import java.util.function.Consumer;

/**
 * 药水效果编辑器
 * 用于 potion_contents 组件
 */
public class PotionEditorScreen extends ItemDataScreen {
    private final Screen parent;
    private final PotionData potionData;
    private final Consumer<PotionData> onSave;


    private final List<EffectEntry> effects = new ArrayList<>();
    private int scrollOffset = 0;

    private TextFieldWidget colorField;

    public PotionEditorScreen(Screen parent, PotionData currentData, Consumer<PotionData> onSave) {
        super(Text.translatable("editor.minecraft.potion_contents"));
        this.parent = parent;
        this.potionData = currentData != null ? currentData : new PotionData();
        this.onSave = onSave;

        // 初始化效果列表
        if (potionData.customEffects.isEmpty()) {
            potionData.customEffects.add(new PotionEffect("regeneration", 1, 30));
        }
    }

    @Override
    protected void init() {
        super.init();

        int centerX = this.width / 2;

        // 颜色输入框
        this.colorField = new TextFieldWidget(
            this.textRenderer,
            centerX - 75,
            60,
            150,
            20,
            Text.literal("")
        );
        this.colorField.setText(Integer.toHexString(potionData.customColor));
        this.colorField.setMaxLength(8);
        this.addSelectableChild(this.colorField);

        // 效果列表
        effects.clear();
        int startY = 100;
        int rowHeight = 70;
        int visibleRows = Math.min(potionData.customEffects.size(), (this.height - 220) / rowHeight);

        for (int i = scrollOffset; i < Math.min(scrollOffset + visibleRows, potionData.customEffects.size()); i++) {
            PotionEffect effect = potionData.customEffects.get(i);
            int y = startY + (i - scrollOffset) * rowHeight;
            effects.add(new EffectEntry(i, effect, y));
        }

        // 添加效果按钮
        this.addDrawableChild(ButtonWidget.builder(
            Text.translatable("gui.itemdatacomp.potion.add_effect"),
            button -> addEffect()
        ).dimensions(centerX - 100, this.height - 90, 200, 20).build());

        // 保存按钮
        this.addDrawableChild(ButtonWidget.builder(
            Text.translatable("gui.done"),
            button -> save()
        ).dimensions(centerX - 155, this.height - 60, 150, 20).build());

        // 取消按钮
        this.addDrawableChild(ButtonWidget.builder(
            Text.translatable("gui.cancel"),
            button -> this.client.setScreen(parent)
        ).dimensions(centerX + 5, this.height - 60, 150, 20).build());
    }

    private void addEffect() {
        potionData.customEffects.add(new PotionEffect("regeneration", 1, 30));
        this.clearAndInit();
    }

    private void removeEffect(int index) {
        if (potionData.customEffects.size() > 1) {
            potionData.customEffects.remove(index);
            this.clearAndInit();
        }
    }

    private void save() {
        // 更新颜色
        try {
            potionData.customColor = Integer.parseUnsignedInt(colorField.getText(), 16);
        } catch (NumberFormatException ignored) {
        }

        // 更新效果
        for (EffectEntry entry : effects) {
            entry.updateEffect();
        }

        onSave.accept(potionData);
        this.client.setScreen(parent);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);

        int centerX = this.width / 2;

        // 标题
        context.drawCenteredTextWithShadow(this.textRenderer, this.title,
            centerX, 20, 0xFFFFFF);

        // 颜色设置
        context.drawText(this.textRenderer, "Custom Color (Hex):",
            centerX - 110, 45, 0xA78BFA, false);
        this.colorField.render(context, mouseX, mouseY, delta);

        // 颜色预览
        int color = 0xFF000000 | potionData.customColor;
        context.fill(centerX + 85, 60, centerX + 105, 80, color);
        context.drawBorder(centerX + 84, 59, 22, 22, 0xFFFFFFFF);

        // 效果列表标题
        context.drawText(this.textRenderer, "Custom Effects:",
            centerX - 200, 88, 0xA78BFA, false);

        // 渲染效果
        for (EffectEntry entry : effects) {
            entry.render(context, mouseX, mouseY, delta);
        }

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        int maxScroll = Math.max(0, potionData.customEffects.size() - (this.height - 220) / 70);
        scrollOffset = Math.max(0, Math.min(maxScroll, scrollOffset - (int)verticalAmount));
        this.clearAndInit();
        return true;
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    // 药水数据类
    public static class PotionData {
        public String potion = null; // 基础药水类型（可选）
        public int customColor = 0xFF385DC6; // 默认水蓝色
        public List<PotionEffect> customEffects = new ArrayList<>();
    }

    // 药水效果类
    public static class PotionEffect {
        public String id;
        public int amplifier;
        public int duration;
        public boolean ambient = false;
        public boolean showParticles = true;
        public boolean showIcon = true;

        public PotionEffect(String id, int amplifier, int duration) {
            this.id = id;
            this.amplifier = amplifier;
            this.duration = duration;
        }
    }

    // 效果条目UI
    private class EffectEntry {
        final int index;
        final int y;
        final AutoCompleteTextFieldWidget effectIdField;
        final TextFieldWidget amplifierField;
        final TextFieldWidget durationField;

        EffectEntry(int index, PotionEffect effect, int y) {
            this.index = index;
            this.y = y;

            int centerX = width / 2;

            // 效果ID输入框 - 带自动完成
            this.effectIdField = new AutoCompleteTextFieldWidget(
                client,
                centerX - 200,
                y,
                120,
                20,
                Text.literal("Effect"),
                PotionEffectSuggestionProvider::getSuggestions
            );
            this.effectIdField.setText(effect.id);
            this.effectIdField.setMaxLength(60);
            addSelectableChild(this.effectIdField);

            // 等级输入
            this.amplifierField = new TextFieldWidget(
                textRenderer,
                centerX - 70,
                y,
                60,
                20,
                Text.literal("")
            );
            this.amplifierField.setText(String.valueOf(effect.amplifier));
            this.amplifierField.setMaxLength(3);
            addSelectableChild(this.amplifierField);

            // 持续时间输入
            this.durationField = new TextFieldWidget(
                textRenderer,
                centerX + 10,
                y,
                80,
                20,
                Text.literal("")
            );
            this.durationField.setText(String.valueOf(effect.duration));
            this.durationField.setMaxLength(6);
            addSelectableChild(this.durationField);

            // 删除按钮
            addDrawableChild(ButtonWidget.builder(
                Text.literal("✕"),
                button -> removeEffect(index)
            ).dimensions(centerX + 180, y, 20, 20).build());
        }

        void render(DrawContext context, int mouseX, int mouseY, float delta) {
            int centerX = width / 2;

            // 背景
            context.fill(centerX - 220, y - 5, centerX + 220, y + 25, 0x40000000);

            // 标签
            context.drawText(textRenderer, "Effect:",
                centerX - 220, y - 20, 0xA78BFA, false);
            context.drawText(textRenderer, "Level:",
                centerX - 70, y - 20, 0x888888, false);
            context.drawText(textRenderer, "Duration (ticks):",
                centerX + 10, y - 20, 0x888888, false);

            effectIdField.render(context, mouseX, mouseY, delta);
            amplifierField.render(context, mouseX, mouseY, delta);
            durationField.render(context, mouseX, mouseY, delta);
        }

        void updateEffect() {
            try {
                PotionEffect effect = potionData.customEffects.get(index);
                String effectId = effectIdField.getText().trim();

                // Validate potion effect ID format
                if (!effectId.isEmpty() && !effectId.contains(":")) {
                    effectId = "minecraft:" + effectId;
                }

                effect.id = effectId;
                effect.amplifier = Integer.parseInt(amplifierField.getText());
                effect.duration = Integer.parseInt(durationField.getText());
            } catch (NumberFormatException | IndexOutOfBoundsException ignored) {
            }
        }
    }
}
