package com.itemdatacomp.client.screen.editor;

import com.itemdatacomp.client.screen.ItemDataScreen;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import java.util.function.Consumer;

/**
 * 颜色选择器 - 增强版
 * 用于 dyed_color 组件，支持 RGB 滑块、输入框、十六进制
 */
public class ColorPickerScreen extends ItemDataScreen {
    private final Screen parent;
    private final Consumer<Integer> onSave;

    private int red = 255;
    private int green = 255;
    private int blue = 255;

    private TextFieldWidget redField;
    private TextFieldWidget greenField;
    private TextFieldWidget blueField;
    private TextFieldWidget hexField;

    // 滑块状态
    private boolean draggingRed = false;
    private boolean draggingGreen = false;
    private boolean draggingBlue = false;
    private int sliderY;
    private int sliderHeight = 150;
    private int sliderWidth = 20;

    public ColorPickerScreen(Screen parent, Integer currentColor, Consumer<Integer> onSave) {
        super(Text.translatable("editor.minecraft.dyed_color"));
        this.parent = parent;
        this.onSave = onSave;

        if (currentColor != null) {
            this.red = (currentColor >> 16) & 0xFF;
            this.green = (currentColor >> 8) & 0xFF;
            this.blue = currentColor & 0xFF;
        }
    }

    @Override
    protected void init() {
        super.init();

        int centerX = this.width / 2;
        int startY = this.height / 2 - 80;
        this.sliderY = startY + 40;

        // RGB输入框
        this.redField = createColorField(centerX - 120, startY, String.valueOf(red));
        this.greenField = createColorField(centerX, startY, String.valueOf(green));
        this.blueField = createColorField(centerX + 120, startY, String.valueOf(blue));

        // 十六进制输入框
        this.hexField = new TextFieldWidget(
            this.textRenderer,
            centerX - 75,
            startY + 90,
            150,
            20,
            Text.literal("")
        );
        this.hexField.setText(String.format("%02X%02X%02X", red, green, blue));
        this.hexField.setMaxLength(6);
        this.hexField.setChangedListener(this::onHexChanged);
        this.addSelectableChild(this.hexField);

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

    private TextFieldWidget createColorField(int x, int y, String value) {
        TextFieldWidget field = new TextFieldWidget(
            this.textRenderer,
            x,
            y,
            60,
            20,
            Text.literal("")
        );
        field.setText(value);
        field.setMaxLength(3);
        field.setChangedListener(this::onRGBChanged);
        this.addSelectableChild(field);
        return field;
    }

    private void onRGBChanged(String text) {
        try {
            red = Math.max(0, Math.min(255, Integer.parseInt(redField.getText())));
            green = Math.max(0, Math.min(255, Integer.parseInt(greenField.getText())));
            blue = Math.max(0, Math.min(255, Integer.parseInt(blueField.getText())));
            hexField.setText(String.format("%02X%02X%02X", red, green, blue));
        } catch (NumberFormatException ignored) {
        }
    }

    private void onHexChanged(String hex) {
        if (hex.length() == 6) {
            try {
                red = Integer.parseInt(hex.substring(0, 2), 16);
                green = Integer.parseInt(hex.substring(2, 4), 16);
                blue = Integer.parseInt(hex.substring(4, 6), 16);
                redField.setText(String.valueOf(red));
                greenField.setText(String.valueOf(green));
                blueField.setText(String.valueOf(blue));
            } catch (NumberFormatException ignored) {
            }
        }
    }

    private void save() {
        int color = (red << 16) | (green << 8) | blue;
        onSave.accept(color);
        this.client.setScreen(parent);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int centerX = this.width / 2;

        // 检查红色滑块
        int redSliderX = centerX - 150;
        if (isInSliderRange(mouseX, mouseY, redSliderX)) {
            draggingRed = true;
            updateColorFromSlider((int) mouseY, redSliderX, 'R');
            return true;
        }

        // 检查绿色滑块
        int greenSliderX = centerX - 40;
        if (isInSliderRange(mouseX, mouseY, greenSliderX)) {
            draggingGreen = true;
            updateColorFromSlider((int) mouseY, greenSliderX, 'G');
            return true;
        }

        // 检查蓝色滑块
        int blueSliderX = centerX + 70;
        if (isInSliderRange(mouseX, mouseY, blueSliderX)) {
            draggingBlue = true;
            updateColorFromSlider((int) mouseY, blueSliderX, 'B');
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        draggingRed = false;
        draggingGreen = false;
        draggingBlue = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        int centerX = this.width / 2;

        if (draggingRed) {
            updateColorFromSlider((int) mouseY, centerX - 150, 'R');
            return true;
        }
        if (draggingGreen) {
            updateColorFromSlider((int) mouseY, centerX - 40, 'G');
            return true;
        }
        if (draggingBlue) {
            updateColorFromSlider((int) mouseY, centerX + 70, 'B');
            return true;
        }

        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    private boolean isInSliderRange(double mouseX, double mouseY, int sliderX) {
        return mouseX >= sliderX && mouseX <= sliderX + sliderWidth &&
               mouseY >= sliderY && mouseY <= sliderY + sliderHeight;
    }

    private void updateColorFromSlider(int mouseY, int sliderX, char channel) {
        int relY = Math.max(0, Math.min(sliderHeight, mouseY - sliderY));
        int value = 255 - (relY * 255 / sliderHeight);

        switch (channel) {
            case 'R' -> {
                red = value;
                redField.setText(String.valueOf(red));
            }
            case 'G' -> {
                green = value;
                greenField.setText(String.valueOf(green));
            }
            case 'B' -> {
                blue = value;
                blueField.setText(String.valueOf(blue));
            }
        }
        hexField.setText(String.format("%02X%02X%02X", red, green, blue));
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);

        int centerX = this.width / 2;
        int startY = this.height / 2 - 80;

        // 标题
        context.drawCenteredTextWithShadow(this.textRenderer, this.title,
            centerX, startY - 30, 0xFFFFFF);

        // RGB标签和滑块
        drawColorSlider(context, centerX - 150, sliderY, "R", red, 0xFF5555);
        drawColorSlider(context, centerX - 40, sliderY, "G", green, 0x55FF55);
        drawColorSlider(context, centerX + 70, sliderY, "B", blue, 0x5555FF);

        // RGB值标签
        context.drawText(this.textRenderer, "R:", centerX - 180, startY + 5, 0xFF5555, false);
        context.drawText(this.textRenderer, "G:", centerX - 70, startY + 5, 0x55FF55, false);
        context.drawText(this.textRenderer, "B:", centerX + 40, startY + 5, 0x5555FF, false);

        // 十六进制标签
        context.drawText(this.textRenderer, "Hex:", centerX - 110, startY + 95, 0xFFFFFF, false);

        // 颜色预览
        int previewSize = 120;
        int previewX = centerX - previewSize / 2;
        int previewY = startY + 140;
        int color = 0xFF000000 | (red << 16) | (green << 8) | blue;
        context.fill(previewX, previewY, previewX + previewSize, previewY + previewSize, color);
        context.drawBorder(previewX - 1, previewY - 1, previewSize + 2, previewSize + 2, 0xFFFFFFFF);

        // RGB值显示
        String rgbText = String.format("RGB(%d, %d, %d)", red, green, blue);
        context.drawCenteredTextWithShadow(this.textRenderer, Text.literal(rgbText),
            centerX, previewY + previewSize + 10, 0xAAAAAA);

        // 渲染文本框
        this.redField.render(context, mouseX, mouseY, delta);
        this.greenField.render(context, mouseX, mouseY, delta);
        this.blueField.render(context, mouseX, mouseY, delta);
        this.hexField.render(context, mouseX, mouseY, delta);

        super.render(context, mouseX, mouseY, delta);
    }

    private void drawColorSlider(DrawContext context, int x, int y, String label, int value, int color) {
        // 背景条
        for (int i = 0; i < sliderHeight; i++) {
            int blend = 255 - (i * 255 / sliderHeight);
            int sliderColor = 0xFF000000 | (((color >> 16) & 0xFF) * blend / 255) << 16 |
                              (((color >> 8) & 0xFF) * blend / 255) << 8 |
                              ((color & 0xFF) * blend / 255);
            context.fill(x, y + i, x + sliderWidth, y + i + 1, sliderColor);
        }

        // 滑块指示器
        int sliderPos = y + (255 - value) * sliderHeight / 255;
        context.fill(x - 5, sliderPos - 3, x + sliderWidth + 5, sliderPos + 3, 0xFFFFFFFF);
        context.drawBorder(x - 6, sliderPos - 4, sliderWidth + 12, 8, 0xFF000000);

        // 标签
        context.drawText(this.textRenderer, label, x - 15, y - 10, color, false);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
