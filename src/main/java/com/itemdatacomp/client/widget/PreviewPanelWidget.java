package com.itemdatacomp.client.widget;

import com.itemdatacomp.client.data.ItemRegistry;
import com.itemdatacomp.client.data.ComponentRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import java.util.*;

/**
 * 右侧预览面板Widget
 * 实时显示物品效果和组件预览
 */
public class PreviewPanelWidget implements Drawable {

    private int x, y, width, height;
    private ItemRegistry.ItemData currentItem;
    private Map<String, ComponentRegistry.ComponentDef> appliedComponents;
    private float renderScale = 1.0f;

    public PreviewPanelWidget(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.appliedComponents = new HashMap<>();
    }

    public void setItem(ItemRegistry.ItemData item) {
        this.currentItem = item;
    }

    public void applyComponent(String componentId, ComponentRegistry.ComponentDef component) {
        this.appliedComponents.put(componentId, component);
    }

    public void removeComponent(String componentId) {
        this.appliedComponents.remove(componentId);
    }

    public void clearComponents() {
        this.appliedComponents.clear();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // 背景
        context.fill(x, y, x + width, y + height, 0xFF0A0A0A);
        context.fill(x, y, x + width, y + 20, 0xFF1A1A1A);

        MinecraftClient client = MinecraftClient.getInstance();

        // 标题栏
        context.drawTextWithShadow(client.textRenderer, "预览", x + 8, y + 6, 0xFFFFFF);

        if (currentItem == null) {
            context.drawTextWithShadow(client.textRenderer, "选择物品以预览效果", x + 8, y + 40, 0x888888);
            return;
        }

        // 物品信息区
        int infoY = y + 30;
        context.drawTextWithShadow(client.textRenderer, "物品: " + currentItem.id(), x + 8, infoY, 0xA78BFA);
        context.drawTextWithShadow(client.textRenderer, currentItem.zhName(), x + 8, infoY + 12, 0xFFFFFF);

        // 已应用的组件列表
        int componentY = infoY + 30;
        context.drawTextWithShadow(client.textRenderer, "已应用组件 (" + appliedComponents.size() + ")", x + 8, componentY, 0xAAAA88);

        int i = 0;
        for (Map.Entry<String, ComponentRegistry.ComponentDef> entry : appliedComponents.entrySet()) {
            int entryY = componentY + 15 + (i * 12);
            if (entryY > y + height - 20) break;

            String componentId = entry.getKey();
            context.fill(x + 12, entryY - 1, x + width - 12, entryY + 10, 0x20A78BFA);
            context.drawTextWithShadow(client.textRenderer, "• " + componentId, x + 16, entryY, 0xA78BFA);
            i++;
        }

        // 效果预览区（如果需要）
        int effectY = componentY + 15 + (Math.min(i, 3) * 12) + 15;
        if (effectY < y + height - 30) {
            renderEffectPreview(context, effectY);
        }
    }

    private void renderEffectPreview(DrawContext context, int startY) {
        MinecraftClient client = MinecraftClient.getInstance();

        context.drawTextWithShadow(client.textRenderer, "效果预览", x + 8, startY, 0xAAAA88);

        // 这里可以添加更多的效果预览
        // 例如属性修改、特殊效果等的实时显示
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }
}
