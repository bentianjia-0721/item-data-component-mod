package com.itemdatacomp.client.screen.editor;

import com.itemdatacomp.client.screen.ItemDataScreen;
import com.itemdatacomp.client.widget.AutoCompleteTextFieldWidget;
import com.itemdatacomp.client.widget.suggestion.AttributeSuggestionProvider;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import java.util.*;
import java.util.function.Consumer;

/**
 * 属性修饰符编辑器
 * 用于 attribute_modifiers 组件
 */
public class AttributeEditorScreen extends ItemDataScreen {
    private final Screen parent;
    private final List<AttributeModifier> modifiers;
    private final Consumer<List<AttributeModifier>> onSave;

    // 常用属性类型
    private static final String[] ATTRIBUTES = {
        "minecraft:generic.max_health",
        "minecraft:generic.knockback_resistance",
        "minecraft:generic.movement_speed",
        "minecraft:generic.flying_speed",
        "minecraft:generic.attack_damage",
        "minecraft:generic.attack_knockback",
        "minecraft:generic.attack_speed",
        "minecraft:generic.armor",
        "minecraft:generic.armor_toughness",
        "minecraft:generic.luck",
        "minecraft:generic.scale",
        "minecraft:player.block_break_speed",
        "minecraft:player.block_interaction_range",
        "minecraft:player.entity_interaction_range",
        "minecraft:zombie.spawn_reinforcements"
    };

    private static final String[] SLOTS = {
        "mainhand", "offhand", "head", "chest", "legs", "feet", "any"
    };

    private static final String[] OPERATIONS = {
        "add_value", "add_multiplied_base", "add_multiplied_total"
    };

    private int scrollOffset = 0;
    private final List<ModifierRow> modifierRows = new ArrayList<>();

    public AttributeEditorScreen(Screen parent, List<AttributeModifier> currentModifiers, Consumer<List<AttributeModifier>> onSave) {
        super(Text.translatable("editor.minecraft.attribute_modifiers"));
        this.parent = parent;
        this.modifiers = currentModifiers != null ? new ArrayList<>(currentModifiers) : new ArrayList<>();
        this.onSave = onSave;

        if (this.modifiers.isEmpty()) {
            this.modifiers.add(new AttributeModifier("minecraft:generic.attack_damage", 1.0, "mainhand", "add_value"));
        }
    }

    @Override
    protected void init() {
        super.init();

        modifierRows.clear();

        int startY = 60;
        int rowHeight = 90;
        int visibleRows = Math.min(modifiers.size(), (this.height - 180) / rowHeight);

        for (int i = scrollOffset; i < Math.min(scrollOffset + visibleRows, modifiers.size()); i++) {
            AttributeModifier mod = modifiers.get(i);
            int y = startY + (i - scrollOffset) * rowHeight;

            ModifierRow row = new ModifierRow(i, mod, y);
            modifierRows.add(row);
        }

        // 添加按钮
        this.addDrawableChild(ButtonWidget.builder(
            Text.translatable("gui.itemdatacomp.attribute.add"),
            button -> addModifier()
        ).dimensions(this.width / 2 - 100, this.height - 90, 200, 20).build());

        // 保存按钮
        this.addDrawableChild(ButtonWidget.builder(
            Text.translatable("gui.done"),
            button -> save()
        ).dimensions(this.width / 2 - 155, this.height - 60, 150, 20).build());

        // 取消按钮
        this.addDrawableChild(ButtonWidget.builder(
            Text.translatable("gui.cancel"),
            button -> this.client.setScreen(parent)
        ).dimensions(this.width / 2 + 5, this.height - 60, 150, 20).build());
    }

    private void addModifier() {
        modifiers.add(new AttributeModifier("minecraft:generic.attack_damage", 1.0, "mainhand", "add_value"));
        this.clearAndInit();
    }

    private void removeModifier(int index) {
        if (modifiers.size() > 1) {
            modifiers.remove(index);
            this.clearAndInit();
        }
    }

    private void save() {
        List<AttributeModifier> result = new ArrayList<>();
        for (ModifierRow row : modifierRows) {
            row.updateModifier();
            result.add(modifiers.get(row.index));
        }
        onSave.accept(result);
        this.client.setScreen(parent);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);

        // 标题
        context.drawCenteredTextWithShadow(this.textRenderer, this.title,
            this.width / 2, 20, 0xFFFFFF);

        // 提示
        context.drawCenteredTextWithShadow(this.textRenderer,
            Text.translatable("gui.itemdatacomp.attribute.hint"),
            this.width / 2, 40, 0x888888);

        // 渲染每个修饰符行
        for (ModifierRow row : modifierRows) {
            row.render(context, mouseX, mouseY, delta);
        }

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        int maxScroll = Math.max(0, modifiers.size() - (this.height - 180) / 90);
        scrollOffset = Math.max(0, Math.min(maxScroll, scrollOffset - (int)verticalAmount));
        this.clearAndInit();
        return true;
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    // 属性修饰符数据类
    public static class AttributeModifier {
        public String attribute;
        public double amount;
        public String slot;
        public String operation;

        public AttributeModifier(String attribute, double amount, String slot, String operation) {
            this.attribute = attribute;
            this.amount = amount;
            this.slot = slot;
            this.operation = operation;
        }
    }

    // 修饰符行UI
    private class ModifierRow {
        final int index;
        final int y;
        final AutoCompleteTextFieldWidget attributeField;
        final TextFieldWidget amountField;
        int slotIndex;
        int operationIndex;

        ModifierRow(int index, AttributeModifier mod, int y) {
            this.index = index;
            this.y = y;

            this.slotIndex = Arrays.asList(SLOTS).indexOf(mod.slot);
            if (this.slotIndex == -1) this.slotIndex = 0;

            this.operationIndex = Arrays.asList(OPERATIONS).indexOf(mod.operation);
            if (this.operationIndex == -1) this.operationIndex = 0;

            // 属性ID输入框 - 带自动完成
            this.attributeField = new AutoCompleteTextFieldWidget(
                AttributeEditorScreen.this.client,
                width / 2 - 200,
                y,
                160,
                20,
                Text.literal("Attribute"),
                AttributeSuggestionProvider::getSuggestions
            );
            this.attributeField.setText(mod.attribute);
            this.attributeField.setMaxLength(60);
            addSelectableChild(this.attributeField);

            // 数值输入框
            this.amountField = new TextFieldWidget(
                textRenderer,
                width / 2 + 80,
                y + 35,
                100,
                20,
                Text.literal("")
            );
            this.amountField.setText(String.valueOf(mod.amount));
            this.amountField.setMaxLength(10);
            addSelectableChild(this.amountField);

            // 槽位选择
            addDrawableChild(ButtonWidget.builder(
                Text.literal("<"),
                button -> {
                    slotIndex = (slotIndex - 1 + SLOTS.length) % SLOTS.length;
                    clearAndInit();
                }
            ).dimensions(width / 2 - 200, y + 25, 20, 20).build());

            addDrawableChild(ButtonWidget.builder(
                Text.literal(">"),
                button -> {
                    slotIndex = (slotIndex + 1) % SLOTS.length;
                    clearAndInit();
                }
            ).dimensions(width / 2 - 50, y + 25, 20, 20).build());

            // 操作类型选择
            addDrawableChild(ButtonWidget.builder(
                Text.literal("<"),
                button -> {
                    operationIndex = (operationIndex - 1 + OPERATIONS.length) % OPERATIONS.length;
                    clearAndInit();
                }
            ).dimensions(width / 2 - 200, y + 50, 20, 20).build());

            addDrawableChild(ButtonWidget.builder(
                Text.literal(">"),
                button -> {
                    operationIndex = (operationIndex + 1) % OPERATIONS.length;
                    clearAndInit();
                }
            ).dimensions(width / 2 + 180, y + 50, 20, 20).build());

            // 删除按钮
            addDrawableChild(ButtonWidget.builder(
                Text.literal("✕"),
                button -> removeModifier(index)
            ).dimensions(width / 2 + 190, y + 35, 20, 20).build());
        }

        void render(DrawContext context, int mouseX, int mouseY, float delta) {
            // 背景
            context.fill(width / 2 - 220, y - 5, width / 2 + 220, y + 80, 0x40000000);

            // 属性标签
            context.drawText(textRenderer, "Attribute:",
                width / 2 - 200, y - 15, 0xA78BFA, false);

            // 槽位
            context.drawText(textRenderer, "Slot:",
                width / 2 - 180, y + 30, 0x888888, false);
            context.drawCenteredTextWithShadow(textRenderer, SLOTS[slotIndex],
                width / 2 - 120, y + 30, 0xFFFFFF);

            // 操作类型
            context.drawText(textRenderer, "Operation:",
                width / 2 - 180, y + 55, 0x888888, false);
            context.drawCenteredTextWithShadow(textRenderer, OPERATIONS[operationIndex],
                width / 2, y + 55, 0xFFFFFF);

            // 数值标签
            context.drawText(textRenderer, "Amount:",
                width / 2 + 20, y + 40, 0x888888, false);

            attributeField.render(context, mouseX, mouseY, delta);
            amountField.render(context, mouseX, mouseY, delta);
        }

        void updateModifier() {
            try {
                String amountText = amountField.getText();
                double amount = Double.parseDouble(amountText);

                // Validate amount is not NaN
                if (Double.isNaN(amount)) {
                    amount = 1.0;
                }

                modifiers.get(index).attribute = attributeField.getText();
                modifiers.get(index).amount = amount;
                modifiers.get(index).slot = SLOTS[slotIndex];
                modifiers.get(index).operation = OPERATIONS[operationIndex];
            } catch (NumberFormatException ignored) {
                // Keep previous value
            }
        }
    }
}
