package com.itemdatacomp.client.screen.editor;

import com.itemdatacomp.client.screen.ItemDataScreen;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import java.util.function.BiConsumer;

/**
 * 盔甲纹饰编辑器
 * 用于 trim 组件
 */
public class ArmorTrimEditorScreen extends ItemDataScreen {
    private final Screen parent;
    private final BiConsumer<String, String> onSave;

    // 纹饰材料
    private static final String[] MATERIALS = {
        "amethyst", "copper", "diamond", "emerald", "gold",
        "iron", "lapis", "netherite", "quartz", "redstone", "resin"
    };

    // 纹饰图案
    private static final String[] PATTERNS = {
        "bolt", "coast", "dune", "eye", "flow", "host",
        "raiser", "rib", "sentry", "shaper", "silence",
        "snout", "spire", "tide", "vex", "ward", "wayfinder", "wild"
    };

    private int materialIndex = 0;
    private int patternIndex = 0;

    public ArmorTrimEditorScreen(Screen parent, String currentMaterial, String currentPattern, BiConsumer<String, String> onSave) {
        super(Text.translatable("editor.minecraft.trim"));
        this.parent = parent;
        this.onSave = onSave;

        // 设置当前选择
        if (currentMaterial != null) {
            for (int i = 0; i < MATERIALS.length; i++) {
                if (MATERIALS[i].equals(currentMaterial)) {
                    materialIndex = i;
                    break;
                }
            }
        }

        if (currentPattern != null) {
            for (int i = 0; i < PATTERNS.length; i++) {
                if (PATTERNS[i].equals(currentPattern)) {
                    patternIndex = i;
                    break;
                }
            }
        }
    }

    @Override
    protected void init() {
        super.init();

        int centerX = this.width / 2;
        int centerY = this.height / 2;

        // 材料选择按钮（左右箭头）
        this.addDrawableChild(ButtonWidget.builder(
            Text.literal("◄"),
            button -> {
                materialIndex = (materialIndex - 1 + MATERIALS.length) % MATERIALS.length;
            }
        ).dimensions(centerX - 200, centerY - 50, 30, 20).build());

        this.addDrawableChild(ButtonWidget.builder(
            Text.literal("►"),
            button -> {
                materialIndex = (materialIndex + 1) % MATERIALS.length;
            }
        ).dimensions(centerX + 170, centerY - 50, 30, 20).build());

        // 图案选择按钮
        this.addDrawableChild(ButtonWidget.builder(
            Text.literal("◄"),
            button -> {
                patternIndex = (patternIndex - 1 + PATTERNS.length) % PATTERNS.length;
            }
        ).dimensions(centerX - 200, centerY + 10, 30, 20).build());

        this.addDrawableChild(ButtonWidget.builder(
            Text.literal("►"),
            button -> {
                patternIndex = (patternIndex + 1) % PATTERNS.length;
            }
        ).dimensions(centerX + 170, centerY + 10, 30, 20).build());

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
        onSave.accept(MATERIALS[materialIndex], PATTERNS[patternIndex]);
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

        // 提示
        context.drawCenteredTextWithShadow(this.textRenderer,
            Text.translatable("gui.itemdatacomp.trim.hint"),
            centerX, 40, 0x888888);

        // 材料选择区域
        context.fill(centerX - 220, centerY - 70, centerX + 220, centerY - 20, 0x40000000);
        context.drawText(this.textRenderer, "Material:",
            centerX - 180, centerY - 60, 0xA78BFA, false);

        // 当前材料名称
        String materialName = MATERIALS[materialIndex];
        int materialColor = getMaterialColor(materialName);
        context.drawCenteredTextWithShadow(this.textRenderer,
            Text.literal(capitalizeFirst(materialName)),
            centerX, centerY - 45, materialColor);

        // 材料序号
        context.drawCenteredTextWithShadow(this.textRenderer,
            Text.literal((materialIndex + 1) + " / " + MATERIALS.length),
            centerX, centerY - 28, 0x888888);

        // 图案选择区域
        context.fill(centerX - 220, centerY - 10, centerX + 220, centerY + 40, 0x40000000);
        context.drawText(this.textRenderer, "Pattern:",
            centerX - 180, centerY, 0xA78BFA, false);

        // 当前图案名称
        String patternName = PATTERNS[patternIndex];
        context.drawCenteredTextWithShadow(this.textRenderer,
            Text.literal(capitalizeFirst(patternName)),
            centerX, centerY + 15, 0xFFFFFF);

        // 图案序号
        context.drawCenteredTextWithShadow(this.textRenderer,
            Text.literal((patternIndex + 1) + " / " + PATTERNS.length),
            centerX, centerY + 28, 0x888888);

        // 预览区域
        context.fill(centerX - 100, centerY + 60, centerX + 100, centerY + 120, 0x40000000);
        context.drawCenteredTextWithShadow(this.textRenderer, "Preview",
            centerX, centerY + 65, 0x888888);

        // 显示完整纹饰ID
        String trimId = "minecraft:" + materialName + " + minecraft:" + patternName;
        context.drawCenteredTextWithShadow(this.textRenderer,
            Text.literal(trimId),
            centerX, centerY + 90, 0xFFFFFF);

        super.render(context, mouseX, mouseY, delta);
    }

    private int getMaterialColor(String material) {
        return switch (material) {
            case "amethyst" -> 0xD946FF;
            case "copper" -> 0xFF6B35;
            case "diamond" -> 0x4FC3F7;
            case "emerald" -> 0x50C878;
            case "gold" -> 0xFFD700;
            case "iron" -> 0xD8D8D8;
            case "lapis" -> 0x3B5DC9;
            case "netherite" -> 0x8B8B8B;
            case "quartz" -> 0xE3D4C5;
            case "redstone" -> 0xFF0000;
            case "resin" -> 0xFFA500;
            default -> 0xFFFFFF;
        };
    }

    private String capitalizeFirst(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
