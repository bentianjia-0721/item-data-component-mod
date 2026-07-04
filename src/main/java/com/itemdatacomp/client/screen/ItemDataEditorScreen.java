package com.itemdatacomp.client.screen;

import com.itemdatacomp.client.data.ItemRegistry;
import com.itemdatacomp.client.data.ComponentRegistry;
import com.itemdatacomp.client.widget.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

/**
 * 物品数据编辑主屏幕
 * 三列布局：左侧物品列表，中间组件编辑，右侧预览面板
 */
public class ItemDataEditorScreen extends ItemDataScreen {

    private ItemListWidget itemList;
    private ComponentListWidget componentList;
    private PreviewPanelWidget previewPanel;
    private TextInputWidget itemSearchInput;
    private ColorPickerWidget colorPicker;
    private DropdownWidget<String> versionSelector;

    private ItemRegistry.ItemData currentItem;
    private ComponentRegistry.ComponentDef currentComponent;

    // 布局参数
    private static final int SIDEBAR_WIDTH = 200;
    private static final int EDITOR_WIDTH = 280;
    private static final int PREVIEW_WIDTH = 250;
    private static final int HEADER_HEIGHT = 30;
    private static final int SEARCH_BOX_HEIGHT = 20;

    public ItemDataEditorScreen() {
        super(Text.literal("物品数据编辑"));
    }

    @Override
    protected void init() {
        super.init();

        // 计算布局
        int sidebarX = 5;
        int editorX = sidebarX + SIDEBAR_WIDTH + 5;
        int previewX = editorX + EDITOR_WIDTH + 5;

        // 左侧：物品搜索框
        itemSearchInput = new TextInputWidget(sidebarX, 5, SIDEBAR_WIDTH - 2, SEARCH_BOX_HEIGHT);
        itemSearchInput.setPlaceholder(Text.literal("搜索物品..."));
        // TextInputWidget has been deprecated and simplified - use addDrawable instead
        this.addDrawable(itemSearchInput);

        // 左侧：物品列表
        itemList = new ItemListWidget(
            this.client,
            SIDEBAR_WIDTH,
            this.height - HEADER_HEIGHT - SEARCH_BOX_HEIGHT - 10,
            HEADER_HEIGHT + SEARCH_BOX_HEIGHT + 5,
            20
        );
        itemList.setSelectionCallback(item -> {
            currentItem = item;
            previewPanel.setItem(item);
            updateComponentList();
        });
        this.addDrawable(itemList);

        // 中间：组件列表
        componentList = new ComponentListWidget(
            this.client,
            EDITOR_WIDTH,
            this.height - HEADER_HEIGHT - 10,
            HEADER_HEIGHT + 5,
            18
        );
        componentList.setSelectionCallback(comp -> {
            currentComponent = comp;
            // 这里可以弹出组件编辑对话框
        });
        this.addDrawable(componentList);

        // 右侧：预览面板
        previewPanel = new PreviewPanelWidget(
            previewX,
            HEADER_HEIGHT + 5,
            PREVIEW_WIDTH,
            this.height - HEADER_HEIGHT - 10
        );
        this.addDrawable(previewPanel);

        // 版本选择器（顶部）
        versionSelector = new DropdownWidget<>(5, 5, 100, 20);
        versionSelector.addOption("1.20.1", "Minecraft 1.20.1");
        versionSelector.addOption("1.21", "Minecraft 1.21");
        versionSelector.addOption("1.21.4", "Minecraft 1.21.4");
        versionSelector.setSelectedOption("1.21.4");
        // DropdownWidget has been deprecated - callback removed and use addDrawable instead
        this.addDrawable(versionSelector);

        // 颜色选择器（底部测试）
        colorPicker = new ColorPickerWidget(editorX, this.height - 40, 100, 30);
        // ColorPickerWidget has been deprecated and simplified - use addDrawable instead
        this.addDrawable(colorPicker);
    }

    private void updateComponentList() {
        if (componentList != null) {
            // componentList.clearEntries() is protected, cannot be called directly
            // Instead, recreate the component list or refresh via public methods
            componentList.setSelectionCallback(currentComponent != null ? comp -> {
                currentComponent = comp;
            } : null);
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // 绘制深色背景
        context.fill(0, 0, this.width, this.height, 0xFF0A0A0A);

        // 绘制顶部栏
        context.fill(0, 0, this.width, HEADER_HEIGHT, 0xFF1A1A1A);
        context.drawTextWithShadow(
            this.client.textRenderer,
            "物品数据组件编辑器",
            10,
            10,
            0xFFFFFF
        );

        // 绘制分割线
        context.fill(SIDEBAR_WIDTH + 5, HEADER_HEIGHT, SIDEBAR_WIDTH + 5 + 1, this.height, 0xFF333333);
        context.fill(SIDEBAR_WIDTH + EDITOR_WIDTH + 10, HEADER_HEIGHT, SIDEBAR_WIDTH + EDITOR_WIDTH + 11, this.height, 0xFF333333);

        // 绘制注册的drawables
        this.renderRegisteredDrawables(context, mouseX, mouseY, delta);

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
        if (versionSelector != null) {
            versionSelector.render(context, mouseX, mouseY, delta);
        }
        if (colorPicker != null) {
            colorPicker.render(context, mouseX, mouseY, delta);
        }
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
    public boolean charTyped(char chr, int modifiers) {
        // TextInputWidget no longer supports charTyped - use native EditBox instead
        return super.charTyped(chr, modifiers);
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
}
