package com.itemdatacomp.client.screen.editor;

import com.itemdatacomp.client.screen.ItemDataScreen;
import com.itemdatacomp.client.widget.AutoCompleteTextFieldWidget;
import com.itemdatacomp.client.widget.suggestion.EntitySuggestionProvider;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import java.util.function.Consumer;

/**
 * 实体数据编辑器 - 增强版
 * 用于 entity_data 组件，支持全面的NBT编辑
 */
public class EntityDataEditorScreen extends ItemDataScreen {
    private final Screen parent;
    private final EntityData entityData;
    private final Consumer<EntityData> onSave;

    private TextFieldWidget entityIdField;
    private TextFieldWidget customNameField;
    private TextFieldWidget healthField;
    private TextFieldWidget rotationField;
    private TextFieldWidget velocityField;

    private boolean noAI;
    private boolean silent;
    private boolean noGravity;
    private boolean invulnerable;
    private boolean glowing;
    private boolean onFire;
    private boolean showArms;

    private int scrollOffset = 0;

    public EntityDataEditorScreen(Screen parent, EntityData currentData, Consumer<EntityData> onSave) {
        super(Text.translatable("editor.minecraft.entity_data"));
        this.parent = parent;
        this.entityData = currentData != null ? currentData : new EntityData();
        this.onSave = onSave;

        // 初始化标志位
        this.noAI = this.entityData.noAI;
        this.silent = this.entityData.silent;
        this.noGravity = this.entityData.noGravity;
        this.invulnerable = this.entityData.invulnerable;
        this.glowing = this.entityData.glowing;
        this.onFire = this.entityData.onFire;
        this.showArms = this.entityData.showArms;
    }

    @Override
    protected void init() {
        super.init();

        int centerX = this.width / 2;
        int startY = 60;

        // 实体类型输入框 - 带自动完成
        this.entityIdField = new AutoCompleteTextFieldWidget(
            this.client,
            centerX - 150,
            startY,
            300,
            20,
            Text.literal("Entity Type"),
            EntitySuggestionProvider::getSuggestions
        );
        this.entityIdField.setText(this.entityData.id);
        this.entityIdField.setMaxLength(50);
        this.addSelectableChild(this.entityIdField);

        // 自定义名称输入框
        this.customNameField = new TextFieldWidget(
            this.textRenderer,
            centerX - 150,
            startY + 40,
            300,
            20,
            Text.literal("Custom Name")
        );
        this.customNameField.setText(this.entityData.customName);
        this.customNameField.setMaxLength(100);
        this.addSelectableChild(this.customNameField);

        // 生命值输入框
        this.healthField = new TextFieldWidget(
            this.textRenderer,
            centerX - 150,
            startY + 80,
            140,
            20,
            Text.literal("Health")
        );
        this.healthField.setText(String.valueOf(this.entityData.health));
        this.healthField.setMaxLength(6);
        this.addSelectableChild(this.healthField);

        // 旋转输入框 (Rotation: [yaw, pitch])
        this.rotationField = new TextFieldWidget(
            this.textRenderer,
            centerX + 10,
            startY + 80,
            140,
            20,
            Text.literal("Rotation")
        );
        this.rotationField.setText(this.entityData.rotation);
        this.rotationField.setMaxLength(30);
        this.addSelectableChild(this.rotationField);

        // 速度输入框 (Motion: [x, y, z])
        this.velocityField = new TextFieldWidget(
            this.textRenderer,
            centerX - 150,
            startY + 120,
            300,
            20,
            Text.literal("Motion (x,y,z)")
        );
        this.velocityField.setText(this.entityData.motion);
        this.velocityField.setMaxLength(50);
        this.addSelectableChild(this.velocityField);

        // 标志位复选框 - 第一行
        this.addDrawableChild(ButtonWidget.builder(
            Text.literal(noAI ? "✓ NoAI" : "☐ NoAI"),
            button -> noAI = !noAI
        ).dimensions(centerX - 150, startY + 170, 60, 20).build());

        this.addDrawableChild(ButtonWidget.builder(
            Text.literal(silent ? "✓ Silent" : "☐ Silent"),
            button -> silent = !silent
        ).dimensions(centerX - 80, startY + 170, 60, 20).build());

        this.addDrawableChild(ButtonWidget.builder(
            Text.literal(noGravity ? "✓ NoGrav" : "☐ NoGrav"),
            button -> noGravity = !noGravity
        ).dimensions(centerX - 10, startY + 170, 60, 20).build());

        this.addDrawableChild(ButtonWidget.builder(
            Text.literal(invulnerable ? "✓ Invul" : "☐ Invul"),
            button -> invulnerable = !invulnerable
        ).dimensions(centerX + 60, startY + 170, 60, 20).build());

        // 标志位复选框 - 第二行
        this.addDrawableChild(ButtonWidget.builder(
            Text.literal(glowing ? "✓ Glow" : "☐ Glow"),
            button -> glowing = !glowing
        ).dimensions(centerX - 150, startY + 200, 60, 20).build());

        this.addDrawableChild(ButtonWidget.builder(
            Text.literal(onFire ? "✓ Fire" : "☐ Fire"),
            button -> onFire = !onFire
        ).dimensions(centerX - 80, startY + 200, 60, 20).build());

        this.addDrawableChild(ButtonWidget.builder(
            Text.literal(showArms ? "✓ Arms" : "☐ Arms"),
            button -> showArms = !showArms
        ).dimensions(centerX - 10, startY + 200, 60, 20).build());

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
        entityData.id = entityIdField.getText();
        entityData.customName = customNameField.getText();
        entityData.rotation = rotationField.getText();
        entityData.motion = velocityField.getText();

        try {
            float health = Float.parseFloat(healthField.getText());
            // Validate health is within valid range (0.1 - 1024)
            if (health < 0.1f) {
                health = 0.1f;
            } else if (health > 1024f) {
                health = 1024f;
            }
            entityData.health = health;
        } catch (NumberFormatException ignored) {
            entityData.health = 20.0f;
        }

        entityData.noAI = noAI;
        entityData.silent = silent;
        entityData.noGravity = noGravity;
        entityData.invulnerable = invulnerable;
        entityData.glowing = glowing;
        entityData.onFire = onFire;
        entityData.showArms = showArms;

        onSave.accept(entityData);
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

        // 标签
        context.drawText(this.textRenderer, "Entity Type:",
            centerX - 150, startY - 15, 0xA78BFA, false);
        context.drawText(this.textRenderer, "Custom Name:",
            centerX - 150, startY + 25, 0xA78BFA, false);
        context.drawText(this.textRenderer, "Health:",
            centerX - 150, startY + 65, 0xA78BFA, false);
        context.drawText(this.textRenderer, "Rotation:",
            centerX + 10, startY + 65, 0xA78BFA, false);
        context.drawText(this.textRenderer, "Motion:",
            centerX - 150, startY + 105, 0xA78BFA, false);

        // 渲染文本框
        this.entityIdField.render(context, mouseX, mouseY, delta);
        this.customNameField.render(context, mouseX, mouseY, delta);
        this.healthField.render(context, mouseX, mouseY, delta);
        this.rotationField.render(context, mouseX, mouseY, delta);
        this.velocityField.render(context, mouseX, mouseY, delta);

        this.renderRegisteredDrawables(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    // 实体数据类
    public static class EntityData {
        public String id = "minecraft:armor_stand";
        public String customName = "";
        public float health = 20.0f;
        public String rotation = "[0.0f, 0.0f]";
        public String motion = "[0.0d, 0.0d, 0.0d]";
        public boolean noAI = false;
        public boolean silent = false;
        public boolean noGravity = false;
        public boolean invulnerable = false;
        public boolean glowing = false;
        public boolean onFire = false;
        public boolean showArms = false;
    }
}
