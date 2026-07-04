package com.itemdatacomp.client.screen;

import com.itemdatacomp.client.widget.*;
import com.itemdatacomp.client.font.ChineseFontManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import java.util.ArrayList;
import java.util.List;

/**
 * 物品数据编辑主屏幕 - 增强版
 * 三列布局：左侧物品列表，中间组件编辑，右侧预览面板
 * 改进：拼音搜索、中文字体支持、实时预览、视觉优化
 */
public class ItemDataEditorScreenEnhanced extends ItemDataScreen {

    private ItemListWidgetEnhanced itemList;
    private ComponentListWidgetEnhanced componentList;
    private PreviewPanelWidgetEnhanced previewPanel;
    private TextInputWidget itemSearchInput;
    private DropdownWidget<String> versionSelector;
    private DropdownWidget<String> fontSelector;

    private ItemListWidgetEnhanced.ItemData currentItem;
    private ComponentListWidgetEnhanced.ComponentData currentComponent;

    // 布局参数
    private static final int SIDEBAR_WIDTH = 220;
    private static final int EDITOR_WIDTH = 300;
    private static final int PREVIEW_WIDTH = 280;
    private static final int HEADER_HEIGHT = 35;
    private static final int SEARCH_BOX_HEIGHT = 28;
    private static final int PADDING = 5;

    public ItemDataEditorScreenEnhanced() {
        super(Text.literal("物品数据组件编辑器"));
    }

    @Override
    protected void init() {
        super.init();

        MinecraftClient client = MinecraftClient.getInstance();

        // 计算布局
        int sidebarX = PADDING;
        int editorX = sidebarX + SIDEBAR_WIDTH + PADDING;
        int previewX = editorX + EDITOR_WIDTH + PADDING;

        // 创建示例数据
        List<ItemListWidgetEnhanced.ItemData> items = createSampleItems();
        List<ComponentListWidgetEnhanced.ComponentData> components = createSampleComponents();

        // 左侧：物品搜索框
        itemSearchInput = new TextInputWidget(sidebarX, HEADER_HEIGHT + 5, SIDEBAR_WIDTH - 2, SEARCH_BOX_HEIGHT);
        itemSearchInput.setSearchCallback(query -> {
            if (itemList != null) {
                itemList.setSearchQuery(query);
            }
        });
        this.addSelectableChild(itemSearchInput);

        // 左侧：物品列表
        itemList = new ItemListWidgetEnhanced(
            this.client,
            SIDEBAR_WIDTH,
            this.height - HEADER_HEIGHT - SEARCH_BOX_HEIGHT - PADDING * 3,
            HEADER_HEIGHT + SEARCH_BOX_HEIGHT + 5,
            25,
            items
        );
        itemList.setSelectionCallback(item -> {
            currentItem = item;
            PreviewPanelWidgetEnhanced.ItemData previewItem = new PreviewPanelWidgetEnhanced.ItemData(
                item.id, item.zhName, "common", item.category
            );
            previewPanel.setItem(previewItem);
        });

        // 中间：组件列表
        componentList = new ComponentListWidgetEnhanced(
            this.client,
            EDITOR_WIDTH,
            this.height - HEADER_HEIGHT - PADDING * 2,
            HEADER_HEIGHT + PADDING,
            22,
            components
        );
        componentList.setSelectionCallback(comp -> {
            currentComponent = comp;
        });

        // 右侧：预览面板
        previewPanel = new PreviewPanelWidgetEnhanced(
            previewX,
            HEADER_HEIGHT + PADDING,
            PREVIEW_WIDTH,
            this.height - HEADER_HEIGHT - PADDING * 2
        );

        // 版本选择器（顶部，标题右侧）
        int titleWidth = client.textRenderer.getWidth("物品数据组件编辑器");
        int versionX = 12 + titleWidth + 20;  // 标题位置 + 标题宽度 + 间距
        versionSelector = new DropdownWidget<>(versionX, 8, 100, 20);
        versionSelector.addOption("1.20.1", "Minecraft 1.20.1");
        versionSelector.addOption("1.21", "Minecraft 1.21");
        versionSelector.addOption("1.21.4", "Minecraft 1.21.4");
        versionSelector.setSelectedOption("1.21.4");

        // 字体选择器（版本选择器右侧）
        fontSelector = new DropdownWidget<>(versionX + 110, 8, 100, 20);
        fontSelector.addOption("minecraft", "Minecraft默认");
        fontSelector.addOption("monospace", "等宽字体");
        fontSelector.addOption("ascii", "ASCII");
        fontSelector.setSelectedOption("monospace");
    }

    private List<ItemListWidgetEnhanced.ItemData> createSampleItems() {
        List<ItemListWidgetEnhanced.ItemData> items = new ArrayList<>();
        items.add(new ItemListWidgetEnhanced.ItemData("minecraft:diamond_sword", "钻石剑", "weapon"));
        items.add(new ItemListWidgetEnhanced.ItemData("minecraft:diamond_pickaxe", "钻石镐", "tool"));
        items.add(new ItemListWidgetEnhanced.ItemData("minecraft:apple", "苹果", "food"));
        items.add(new ItemListWidgetEnhanced.ItemData("minecraft:diamond_armor", "钻石盔甲", "armor"));
        items.add(new ItemListWidgetEnhanced.ItemData("minecraft:oak_log", "橡木原木", "block"));
        items.add(new ItemListWidgetEnhanced.ItemData("minecraft:iron_ingot", "铁锭", "other"));
        items.add(new ItemListWidgetEnhanced.ItemData("minecraft:golden_apple", "金苹果", "food"));
        items.add(new ItemListWidgetEnhanced.ItemData("minecraft:enchanted_book", "附魔书", "other"));
        return items;
    }

    private List<ComponentListWidgetEnhanced.ComponentData> createSampleComponents() {
        List<ComponentListWidgetEnhanced.ComponentData> components = new ArrayList<>();
        components.add(new ComponentListWidgetEnhanced.ComponentData(
            "minecraft:damage", "属性", "设置物品伤害值", "attribute"
        ));
        components.add(new ComponentListWidgetEnhanced.ComponentData(
            "minecraft:enchantment_glint_override", "特效", "覆盖附魔闪光", "effect"
        ));
        components.add(new ComponentListWidgetEnhanced.ComponentData(
            "minecraft:custom_model_data", "模型", "自定义模型数据", "custom_model_data"
        ));
        components.add(new ComponentListWidgetEnhanced.ComponentData(
            "minecraft:attribute_modifiers", "属性", "属性修饰符", "attribute"
        ));
        components.add(new ComponentListWidgetEnhanced.ComponentData(
            "minecraft:enchantments", "附魔", "附魔列表", "enchantment"
        ));
        components.add(new ComponentListWidgetEnhanced.ComponentData(
            "minecraft:potion_contents", "药水", "药水内容", "effect"
        ));
        return components;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // 绘制深色背景
        context.fill(0, 0, this.width, this.height, 0xFF0A0A0A);

        // 绘制顶部栏
        context.fill(0, 0, this.width, HEADER_HEIGHT, 0xFF1A1A1A);
        context.fill(0, HEADER_HEIGHT - 1, this.width, HEADER_HEIGHT, 0xFF333333);

        MinecraftClient client = MinecraftClient.getInstance();

        // 标题
        context.drawTextWithShadow(client.textRenderer, Text.literal("物品数据组件编辑器"),
            12, 10, 0xFFFFFF);

        // 版本和字体选择器
        if (versionSelector != null) {
            versionSelector.render(context, mouseX, mouseY, delta);
        }
        if (fontSelector != null) {
            fontSelector.render(context, mouseX, mouseY, delta);
        }

        // 绘制分割线
        int sidebarX = PADDING;
        int editorX = sidebarX + SIDEBAR_WIDTH + PADDING;
        int previewX = editorX + EDITOR_WIDTH + PADDING;

        context.fill(sidebarX + SIDEBAR_WIDTH + PADDING / 2, HEADER_HEIGHT,
            sidebarX + SIDEBAR_WIDTH + PADDING / 2 + 1, this.height, 0xFF333333);
        context.fill(editorX + EDITOR_WIDTH + PADDING / 2, HEADER_HEIGHT,
            editorX + EDITOR_WIDTH + PADDING / 2 + 1, this.height, 0xFF333333);

        // 绘制UI组件
        if (itemSearchInput != null) {
            itemSearchInput.render(context, mouseX, mouseY, delta);
        }
        if (itemList != null) {
            itemList.render(context, mouseX, mouseY, delta);
        }
        if (componentList != null) {
            componentList.render(context, mouseX, mouseY, delta);
        }
        if (previewPanel != null) {
            previewPanel.render(context, mouseX, mouseY, delta);
        }

        // 绘制热键提示
        context.drawTextWithShadow(client.textRenderer, Text.literal("[ESC] 关闭"),
            this.width - 70, this.height - 10, 0x666666);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (itemList != null && itemList.isMouseOver(mouseX, mouseY)) {
            return itemList.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
        }
        if (componentList != null && componentList.isMouseOver(mouseX, mouseY)) {
            return componentList.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
        }
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (itemList != null && itemList.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        if (componentList != null && componentList.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) { // ESC
            this.close();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void close() {
        super.close();
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }
}
