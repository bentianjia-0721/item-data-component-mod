package com.itemdatacomp.client.screen.editor;

import com.itemdatacomp.client.screen.ItemDataScreen;
import com.itemdatacomp.client.widget.AutoCompleteTextFieldWidget;
import com.itemdatacomp.client.widget.suggestion.SoundSuggestionProvider;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import java.util.function.Consumer;

/**
 * 消耗品编辑器
 * 用于 consumable 组件
 */
public class ConsumableEditorScreen extends ItemDataScreen {
    private final Screen parent;
    private final ConsumableData consumableData;
    private final Consumer<ConsumableData> onSave;

    private static final String[] ANIMATIONS = {
        "eat", "drink", "block", "bow", "spyglass", "crossbow", "spear"
    };

    private TextFieldWidget secondsField;
    private TextFieldWidget soundField;
    private int animationIndex = 0;
    private boolean hasConsumeParticles = true;

    public ConsumableEditorScreen(Screen parent, ConsumableData currentData, Consumer<ConsumableData> onSave) {
        super(Text.translatable("editor.minecraft.consumable"));
        this.parent = parent;
        this.consumableData = currentData != null ? currentData : new ConsumableData();
        this.onSave = onSave;

        // 设置当前动画
        for (int i = 0; i < ANIMATIONS.length; i++) {
            if (ANIMATIONS[i].equals(this.consumableData.animation)) {
                this.animationIndex = i;
                break;
            }
        }
        this.hasConsumeParticles = this.consumableData.has_consume_particles;
    }

    @Override
    protected void init() {
        super.init();

        int centerX = this.width / 2;
        int startY = 60;

        // 消耗秒数输入框
        this.secondsField = new TextFieldWidget(
            this.textRenderer,
            centerX - 75,
            startY + 40,
            150,
            20,
            Text.translatable("label.duration")
        );
        this.secondsField.setText(String.format("%.1f", consumableData.consume_seconds));
        this.secondsField.setMaxLength(4);
        this.addSelectableChild(this.secondsField);

        // 音效输入框 - 带自动完成
        this.soundField = new AutoCompleteTextFieldWidget(
            this.client,
            centerX - 75,
            startY + 80,
            150,
            20,
            Text.translatable("label.sound"),
            SoundSuggestionProvider::getSuggestions
        );
        this.soundField.setText(consumableData.sound);
        this.soundField.setMaxLength(50);
        this.addSelectableChild(this.soundField);

        // 动画类型选择按钮
        this.addDrawableChild(ButtonWidget.builder(
            Text.translatable("symbol.arrow_left"),
            button -> {
                animationIndex = (animationIndex - 1 + ANIMATIONS.length) % ANIMATIONS.length;
            }
        ).dimensions(centerX - 150, startY, 30, 20).build());

        this.addDrawableChild(ButtonWidget.builder(
            Text.translatable("symbol.arrow_right"),
            button -> {
                animationIndex = (animationIndex + 1) % ANIMATIONS.length;
            }
        ).dimensions(centerX + 120, startY, 30, 20).build());

        // 有粒子复选框
        this.addDrawableChild(ButtonWidget.builder(
            Text.literal(hasConsumeParticles ? "✓ " : "☐ ").append(Text.translatable("label.particles")),
            button -> hasConsumeParticles = !hasConsumeParticles
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
        consumableData.animation = ANIMATIONS[animationIndex];

        try {
            consumableData.consume_seconds = Float.parseFloat(secondsField.getText());
        } catch (NumberFormatException ignored) {
            consumableData.consume_seconds = 1.6f;
        }

        String soundId = soundField.getText().trim();
        // Validate sound ID format (should be namespace:id)
        if (!soundId.isEmpty() && !soundId.contains(":")) {
            soundId = "minecraft:" + soundId;
        }
        consumableData.sound = soundId;
        consumableData.has_consume_particles = hasConsumeParticles;

        onSave.accept(consumableData);
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

        // 动画类型标签和显示
        context.drawText(this.textRenderer, Text.translatable("label.animation").getString(),
            centerX - 90, startY + 5, 0xA78BFA, false);
        context.drawCenteredTextWithShadow(this.textRenderer,
            ANIMATIONS[animationIndex],
            centerX, startY + 25, 0xFFFFFF);

        // 消耗秒数标签
        context.drawText(this.textRenderer, Text.translatable("label.consume_seconds").getString(),
            centerX - 150, startY + 28, 0x888888, false);

        // 音效标签
        context.drawText(this.textRenderer, Text.translatable("label.sound").getString(),
            centerX - 150, startY + 68, 0x888888, false);

        // 渲染文本框
        this.secondsField.render(context, mouseX, mouseY, delta);
        this.soundField.render(context, mouseX, mouseY, delta);

        this.renderRegisteredDrawables(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    // 消耗品数据类
    public static class ConsumableData {
        public String animation = "eat";
        public float consume_seconds = 1.6f;
        public String sound = "";
        public boolean has_consume_particles = true;
    }
}
