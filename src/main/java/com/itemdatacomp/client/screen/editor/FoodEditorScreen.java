package com.itemdatacomp.client.screen.editor;

import com.itemdatacomp.client.screen.ItemDataScreen;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import java.util.function.Consumer;

/**
 * 食物编辑器
 * 用于 food 组件
 */
public class FoodEditorScreen extends ItemDataScreen {
    private final Screen parent;
    private final FoodData foodData;
    private final Consumer<FoodData> onSave;

    private TextFieldWidget nutritionField;
    private TextFieldWidget saturationField;
    private boolean canAlwaysEat;

    public FoodEditorScreen(Screen parent, FoodData currentData, Consumer<FoodData> onSave) {
        super(Text.translatable("editor.minecraft.food"));
        this.parent = parent;
        this.foodData = currentData != null ? currentData : new FoodData();
        this.onSave = onSave;
        this.canAlwaysEat = this.foodData.can_always_eat;
    }

    @Override
    protected void init() {
        super.init();

        int centerX = this.width / 2;
        int centerY = this.height / 2;

        // 营养值输入框
        this.nutritionField = new TextFieldWidget(
            this.textRenderer,
            centerX - 150,
            centerY - 40,
            100,
            20,
            Text.translatable("label.nutrition")
        );
        this.nutritionField.setText(String.valueOf(foodData.nutrition));
        this.nutritionField.setMaxLength(3);
        this.addSelectableChild(this.nutritionField);

        // 饱和度输入框
        this.saturationField = new TextFieldWidget(
            this.textRenderer,
            centerX + 50,
            centerY - 40,
            100,
            20,
            Text.translatable("label.saturation")
        );
        this.saturationField.setText(String.format("%.1f", foodData.saturation));
        this.saturationField.setMaxLength(5);
        this.addSelectableChild(this.saturationField);

        // 总是可食用复选框
        this.addDrawableChild(ButtonWidget.builder(
            Text.literal(canAlwaysEat ? "✓ " : "☐ ").append(Text.translatable("label.always_eat")),
            button -> canAlwaysEat = !canAlwaysEat
        ).dimensions(centerX - 100, centerY + 10, 200, 20).build());

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
        try {
            foodData.nutrition = Integer.parseInt(nutritionField.getText());
        } catch (NumberFormatException ignored) {
            foodData.nutrition = 0;
        }

        try {
            foodData.saturation = Float.parseFloat(saturationField.getText());
        } catch (NumberFormatException ignored) {
            foodData.saturation = 0.0f;
        }

        foodData.can_always_eat = canAlwaysEat;

        onSave.accept(foodData);
        this.client.setScreen(parent);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);

        int centerX = this.width / 2;
        int centerY = this.height / 2;

        // 标题
        context.drawCenteredTextWithShadow(this.textRenderer, this.title,
            centerX, 20, 0xFFFFFF);

        // 背景盒
        context.fill(centerX - 200, centerY - 60, centerX + 200, centerY + 40, 0x40000000);

        // 营养值标签
        context.drawText(this.textRenderer, Text.translatable("label.nutrition").getString(),
            centerX - 150, centerY - 55, 0xA78BFA, false);

        // 饱和度标签
        context.drawText(this.textRenderer, Text.translatable("label.saturation").getString(),
            centerX + 50, centerY - 55, 0xA78BFA, false);

        // 渲染文本框
        this.nutritionField.render(context, mouseX, mouseY, delta);
        this.saturationField.render(context, mouseX, mouseY, delta);

        // 提示文字
        context.drawCenteredTextWithShadow(this.textRenderer,
            Text.translatable("format.food_info", foodData.nutrition, String.format("%.1f", foodData.saturation)),
            centerX, centerY - 15, 0x888888);

        this.renderRegisteredDrawables(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    // 食物数据类
    public static class FoodData {
        public int nutrition = 4;
        public float saturation = 2.4f;
        public boolean can_always_eat = false;

        /**
         * 将FoodData转换为Map用于序列化
         */
        public java.util.Map<String, Object> toMap() {
            java.util.Map<String, Object> map = new java.util.HashMap<>();
            map.put("nutrition", this.nutrition);
            map.put("saturation", this.saturation);
            map.put("can_always_eat", this.can_always_eat);
            return map;
        }

        /**
         * 从Map创建FoodData
         */
        public static FoodData fromMap(java.util.Map<String, Object> map) {
            FoodData data = new FoodData();
            if (map.containsKey("nutrition")) {
                Object nutritionObj = map.get("nutrition");
                if (nutritionObj instanceof Number) {
                    data.nutrition = ((Number) nutritionObj).intValue();
                }
            }
            if (map.containsKey("saturation")) {
                Object saturationObj = map.get("saturation");
                if (saturationObj instanceof Number) {
                    data.saturation = ((Number) saturationObj).floatValue();
                }
            }
            if (map.containsKey("can_always_eat")) {
                Object eatObj = map.get("can_always_eat");
                if (eatObj instanceof Boolean) {
                    data.can_always_eat = (Boolean) eatObj;
                }
            }
            return data;
        }
    }
}
