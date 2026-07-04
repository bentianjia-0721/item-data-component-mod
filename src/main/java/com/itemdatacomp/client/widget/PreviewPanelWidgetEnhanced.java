package com.itemdatacomp.client.widget;

import com.itemdatacomp.client.font.ChineseFontManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.text.Text;

/**
 * 增强的预览面板Widget - 已优化版本
 * 右侧实时预览面板，展示物品效果和组件预览
 * 改进：平滑动画、详细信息展示、颜色编码
 */
public class PreviewPanelWidgetEnhanced implements Drawable {

    private int x, y, width, height;
    private ItemData currentItem;
    private java.util.Map<String, ComponentData> appliedComponents;
    private float scrollOffset = 0;
    private long lastUpdateTime = 0;

    // 可复用的数据类
    public static class ItemData {
        public final String id;
        public final String zhName;
        public final String rarity;
        public final String category;

        public ItemData(String id, String zhName, String rarity, String category) {
            this.id = id;
            this.zhName = zhName;
            this.rarity = rarity;
            this.category = category;
        }
    }

    public static class ComponentData {
        public final String id;
        public final String type;
        public final String description;
        public final java.util.Map<String, Object> properties;

        public ComponentData(String id, String type, String description, java.util.Map<String, Object> properties) {
            this.id = id;
            this.type = type;
            this.description = description;
            this.properties = properties;
        }
    }

    public PreviewPanelWidgetEnhanced(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.appliedComponents = new java.util.HashMap<>();
    }

    public void setItem(ItemData item) {
        this.currentItem = item;
        this.scrollOffset = 0;
        this.lastUpdateTime = System.currentTimeMillis();
    }

    public void applyComponent(String componentId, ComponentData component) {
        this.appliedComponents.put(componentId, component);
        this.lastUpdateTime = System.currentTimeMillis();
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

        // 顶部栏
        context.fill(x, y, x + width, y + 25, 0xFF1A1A1A);
        context.fill(x, y + 24, x + width, y + 25, 0xFF333333);

        MinecraftClient client = MinecraftClient.getInstance();

        // 标题
        context.drawTextWithShadow(client.textRenderer, Text.literal("预览面板"), x + 8, y + 8, 0xFFFFFF);

        if (currentItem == null) {
            context.drawTextWithShadow(client.textRenderer, Text.literal("选择物品以预览效果"),
                x + 8, y + 60, 0x888888);
            return;
        }

        int contentY = y + 30;
        int maxContentHeight = height - 35;

        // 物品信息区
        renderItemInfo(context, contentY);
        contentY += 50;

        // 已应用组件列表
        if (appliedComponents.isEmpty()) {
            context.drawTextWithShadow(client.textRenderer, Text.literal("暂无应用组件"),
                x + 8, contentY + 20, 0x666666);
        } else {
            renderComponentsList(context, contentY, maxContentHeight - 60);
        }
    }

    private void renderItemInfo(DrawContext context, int startY) {
        MinecraftClient client = MinecraftClient.getInstance();

        // 物品ID（紫色）
        context.drawTextWithShadow(client.textRenderer, Text.literal("物品: " + currentItem.id),
            x + 8, startY, 0xA78BFA);

        // 物品中文名（亮白）
        context.drawTextWithShadow(client.textRenderer, Text.literal(currentItem.zhName),
            x + 8, startY + 12, 0xFFFFFF);

        // 稀有度标签
        int rarityColor = getRarityColor(currentItem.rarity);
        String rarityTag = "[" + currentItem.rarity + "]";
        context.drawTextWithShadow(client.textRenderer, Text.literal(rarityTag),
            x + 8, startY + 24, rarityColor);

        // 分类标签
        int categoryColor = getCategoryColor(currentItem.category);
        String categoryTag = "[" + currentItem.category + "]";
        int categoryX = x + 8 + client.textRenderer.getWidth(rarityTag) + 8;
        context.drawTextWithShadow(client.textRenderer, Text.literal(categoryTag),
            categoryX, startY + 24, categoryColor);
    }

    private void renderComponentsList(DrawContext context, int startY, int maxHeight) {
        MinecraftClient client = MinecraftClient.getInstance();

        // 组件列表标题
        int componentCount = appliedComponents.size();
        String header = "已应用组件 (" + componentCount + ")";
        context.drawTextWithShadow(client.textRenderer, Text.literal(header),
            x + 8, startY, 0xAAAA88);

        int componentY = startY + 15;
        int maxItems = Math.min(appliedComponents.size(), (maxHeight - 15) / 20);

        java.util.List<ComponentData> components = new java.util.ArrayList<>(appliedComponents.values());
        for (int i = 0; i < maxItems; i++) {
            if (componentY > y + height - 30) break;

            ComponentData comp = components.get(i);
            renderComponentItem(context, comp, componentY);
            componentY += 20;
        }

        // 如果有更多组件，显示省略号
        if (componentCount > maxItems) {
            String more = "... 还有 " + (componentCount - maxItems) + " 个组件";
            context.drawTextWithShadow(client.textRenderer, Text.literal(more),
                x + 8, componentY, 0x666666);
        }
    }

    private void renderComponentItem(DrawContext context, ComponentData comp, int y) {
        MinecraftClient client = MinecraftClient.getInstance();

        // 背景
        context.fill(x + 4, y - 1, x + width - 4, y + 16, 0x20A78BFA);

        // 类型颜色编码
        int typeColor = getTypeColor(comp.type);

        // 类型标签
        String typeTag = "[" + comp.type + "]";
        context.drawTextWithShadow(client.textRenderer, Text.literal(typeTag),
            x + 8, y, typeColor);

        // 组件ID
        String displayId = comp.id.replace("minecraft:", "");
        int idStartX = x + 8 + client.textRenderer.getWidth(typeTag) + 6;
        context.drawTextWithShadow(client.textRenderer, Text.literal(displayId),
            idStartX, y, 0xC0A8FF);
    }

    private int getRarityColor(String rarity) {
        return switch (rarity.toLowerCase()) {
            case "common" -> 0xFF888888;
            case "uncommon" -> 0xFF66FF66;
            case "rare" -> 0xFF6BA3FF;
            case "epic" -> 0xFFB366FF;
            case "legendary" -> 0xFFFFB366;
            default -> 0xFFFFFFFF;
        };
    }

    private int getCategoryColor(String category) {
        return switch (category.toLowerCase()) {
            case "tool" -> 0xFFB366FF;
            case "weapon" -> 0xFFFF6B6B;
            case "armor" -> 0xFF6BA3FF;
            case "food" -> 0xFFFFB366;
            case "block" -> 0xFF66FF66;
            default -> 0xFF888888;
        };
    }

    private int getTypeColor(String type) {
        return switch (type.toLowerCase()) {
            case "attribute" -> 0xFF6BA3FF;
            case "enchantment" -> 0xFFFFB366;
            case "effect" -> 0xFF66FF66;
            case "damage" -> 0xFFFF6B6B;
            case "custom_model_data" -> 0xFFB366FF;
            default -> 0xFF888888;
        };
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
