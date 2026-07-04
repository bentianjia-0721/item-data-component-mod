package com.itemdatacomp.client.screen.editor;

import com.itemdatacomp.client.screen.ItemDataScreen;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import java.util.*;
import java.util.function.Consumer;

/**
 * 烟火编辑器
 * 用于 fireworks 组件
 */
public class FireworksEditorScreen extends ItemDataScreen {
    private final Screen parent;
    private final FireworksData fireworksData;
    private final Consumer<FireworksData> onSave;

    private static final String[] SHAPES = {
        "small_ball", "large_ball", "star", "burst", "creeper", "palm"
    };

    private final List<ExplosionEntry> explosions = new ArrayList<>();
    private int scrollOffset = 0;

    private TextFieldWidget durationField;

    public FireworksEditorScreen(Screen parent, FireworksData currentData, Consumer<FireworksData> onSave) {
        super(Text.translatable("editor.minecraft.fireworks"));
        this.parent = parent;
        this.fireworksData = currentData != null ? currentData : new FireworksData();
        this.onSave = onSave;

        // 初始化爆炸列表
        if (fireworksData.explosions.isEmpty()) {
            fireworksData.explosions.add(new Explosion("small_ball", new ArrayList<>(), new ArrayList<>(), false, false));
        }
    }

    @Override
    protected void init() {
        super.init();

        int centerX = this.width / 2;

        // 飞行时间输入框
        this.durationField = new TextFieldWidget(
            this.textRenderer,
            centerX - 75,
            60,
            150,
            20,
            Text.translatable("label.duration")
        );
        this.durationField.setText(String.valueOf(fireworksData.flight_duration));
        this.durationField.setMaxLength(3);
        this.addSelectableChild(this.durationField);

        // 爆炸效果列表
        explosions.clear();
        int startY = 100;
        int rowHeight = 120;
        int visibleRows = Math.min(fireworksData.explosions.size(), (this.height - 220) / rowHeight);

        for (int i = scrollOffset; i < Math.min(scrollOffset + visibleRows, fireworksData.explosions.size()); i++) {
            Explosion explosion = fireworksData.explosions.get(i);
            int y = startY + (i - scrollOffset) * rowHeight;
            explosions.add(new ExplosionEntry(i, explosion, y));
        }

        // 添加爆炸按钮
        this.addDrawableChild(ButtonWidget.builder(
            Text.literal("+ ").append(Text.translatable("gui.itemdatacomp.potion.add_effect")),
            button -> addExplosion()
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

    private void addExplosion() {
        fireworksData.explosions.add(new Explosion("small_ball", new ArrayList<>(), new ArrayList<>(), false, false));
        this.clearAndInit();
    }

    private void removeExplosion(int index) {
        if (fireworksData.explosions.size() > 1) {
            fireworksData.explosions.remove(index);
            this.clearAndInit();
        }
    }

    private void save() {
        // 更新飞行时间
        try {
            fireworksData.flight_duration = Integer.parseInt(durationField.getText());
        } catch (NumberFormatException ignored) {
            fireworksData.flight_duration = 1;
        }

        // 更新爆炸效果
        for (ExplosionEntry entry : explosions) {
            entry.updateExplosion();
        }

        onSave.accept(fireworksData);
        this.client.setScreen(parent);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);

        int centerX = this.width / 2;

        // 标题
        context.drawCenteredTextWithShadow(this.textRenderer, this.title,
            centerX, 20, 0xFFFFFF);

        // 飞行时间设置
        context.drawText(this.textRenderer, Text.translatable("label.flight_duration").getString(),
            centerX - 110, 45, 0xA78BFA, false);
        this.durationField.render(context, mouseX, mouseY, delta);

        // 爆炸效果列表标题
        context.drawText(this.textRenderer, Text.translatable("label.effects").getString(),
            centerX - 200, 88, 0xA78BFA, false);

        // 渲染爆炸效果
        for (ExplosionEntry entry : explosions) {
            entry.render(context, mouseX, mouseY, delta);
        }

        this.renderRegisteredDrawables(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        int maxScroll = Math.max(0, fireworksData.explosions.size() - (this.height - 220) / 120);
        scrollOffset = Math.max(0, Math.min(maxScroll, scrollOffset - (int)verticalAmount));
        this.clearAndInit();
        return true;
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    // 烟火数据类
    public static class FireworksData {
        public int flight_duration = 1;
        public List<Explosion> explosions = new ArrayList<>();
    }

    // 爆炸效果类
    public static class Explosion {
        public String shape;
        public List<Integer> colors;
        public List<Integer> fade_colors;
        public boolean has_twinkle;
        public boolean has_trail;

        public Explosion(String shape, List<Integer> colors, List<Integer> fade_colors, boolean twinkle, boolean trail) {
            this.shape = shape;
            this.colors = colors;
            this.fade_colors = fade_colors;
            this.has_twinkle = twinkle;
            this.has_trail = trail;
        }
    }

    // 爆炸条目UI
    private class ExplosionEntry {
        final int index;
        final int y;
        final TextFieldWidget colorsField;
        final TextFieldWidget fadeColorsField;
        int shapeIndex;
        boolean hasTwinkle;
        boolean hasTrail;

        ExplosionEntry(int index, Explosion explosion, int y) {
            this.index = index;
            this.y = y;

            // 找到形状索引
            this.shapeIndex = Arrays.asList(SHAPES).indexOf(explosion.shape);
            if (this.shapeIndex == -1) this.shapeIndex = 0;

            this.hasTwinkle = explosion.has_twinkle;
            this.hasTrail = explosion.has_trail;

            int centerX = width / 2;

            // 形状选择按钮
            addDrawableChild(ButtonWidget.builder(
                Text.translatable("symbol.arrow_left"),
                button -> {
                    shapeIndex = (shapeIndex - 1 + SHAPES.length) % SHAPES.length;
                    clearAndInit();
                }
            ).dimensions(centerX - 200, y, 20, 20).build());

            addDrawableChild(ButtonWidget.builder(
                Text.translatable("symbol.arrow_right"),
                button -> {
                    shapeIndex = (shapeIndex + 1) % SHAPES.length;
                    clearAndInit();
                }
            ).dimensions(centerX + 180, y, 20, 20).build());

            // 颜色输入框
            this.colorsField = new TextFieldWidget(
                textRenderer,
                centerX - 180,
                y + 25,
                160,
                20,
                Text.translatable("label.custom_color")
            );
            String colorsStr = explosion.colors.isEmpty() ? "" :
                explosion.colors.stream()
                    .map(c -> String.format("#%06X", c & 0xFFFFFF))
                    .reduce((a, b) -> a + "," + b)
                    .orElse("");
            this.colorsField.setText(colorsStr);
            this.colorsField.setMaxLength(100);
            addSelectableChild(this.colorsField);

            // 渐变色输入框
            this.fadeColorsField = new TextFieldWidget(
                textRenderer,
                centerX + 20,
                y + 25,
                160,
                20,
                Text.translatable("label.fade_color")
            );
            String fadeStr = explosion.fade_colors.isEmpty() ? "" :
                explosion.fade_colors.stream()
                    .map(c -> String.format("#%06X", c & 0xFFFFFF))
                    .reduce((a, b) -> a + "," + b)
                    .orElse("");
            this.fadeColorsField.setText(fadeStr);
            this.fadeColorsField.setMaxLength(100);
            addSelectableChild(this.fadeColorsField);

            // 删除按钮
            addDrawableChild(ButtonWidget.builder(
                Text.literal("✕"),
                button -> removeExplosion(index)
            ).dimensions(centerX + 190, y + 50, 20, 20).build());

            // 闪烁效果复选框
            addDrawableChild(ButtonWidget.builder(
                Text.literal(hasTwinkle ? "✓ " : "☐ ").append(Text.translatable("label.twinkle")),
                button -> {
                    hasTwinkle = !hasTwinkle;
                    clearAndInit();
                }
            ).dimensions(centerX - 200, y + 50, 80, 20).build());

            // 拖尾效果复选框
            addDrawableChild(ButtonWidget.builder(
                Text.literal(hasTrail ? "✓ " : "☐ ").append(Text.translatable("label.trail")),
                button -> {
                    hasTrail = !hasTrail;
                    clearAndInit();
                }
            ).dimensions(centerX - 110, y + 50, 80, 20).build());
        }

        void render(DrawContext context, int mouseX, int mouseY, float delta) {
            int centerX = width / 2;

            // 背景
            context.fill(centerX - 220, y - 5, centerX + 220, y + 75, 0x40000000);

            // 形状名称
            String shapeName = SHAPES[shapeIndex];
            context.drawCenteredTextWithShadow(textRenderer, shapeName,
                centerX, y + 5, 0xFFD700);

            // 标签
            context.drawText(textRenderer, Text.translatable("label.colors_hex").getString(),
                centerX - 180, y + 13, 0x888888, false);
            context.drawText(textRenderer, Text.translatable("label.fade_hex").getString(),
                centerX + 20, y + 13, 0x888888, false);

            colorsField.render(context, mouseX, mouseY, delta);
            fadeColorsField.render(context, mouseX, mouseY, delta);
        }

        void updateExplosion() {
            try {
                Explosion explosion = fireworksData.explosions.get(index);
                explosion.shape = SHAPES[shapeIndex];

                // 解析颜色
                explosion.colors.clear();
                String colorsStr = colorsField.getText().trim();
                if (!colorsStr.isEmpty()) {
                    for (String color : colorsStr.split(",")) {
                        try {
                            int rgb = Integer.parseUnsignedInt(color.trim().replace("#", ""), 16);
                            explosion.colors.add(rgb);
                        } catch (NumberFormatException ignored) {
                        }
                    }
                }

                // 解析渐变色
                explosion.fade_colors.clear();
                String fadeStr = fadeColorsField.getText().trim();
                if (!fadeStr.isEmpty()) {
                    for (String color : fadeStr.split(",")) {
                        try {
                            int rgb = Integer.parseUnsignedInt(color.trim().replace("#", ""), 16);
                            explosion.fade_colors.add(rgb);
                        } catch (NumberFormatException ignored) {
                        }
                    }
                }

                explosion.has_twinkle = hasTwinkle;
                explosion.has_trail = hasTrail;
            } catch (IndexOutOfBoundsException ignored) {
            }
        }
    }
}
