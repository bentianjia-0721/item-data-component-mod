package com.itemdatacomp.client.screen;

import com.itemdatacomp.client.config.ConfigManager;
import com.itemdatacomp.client.data.*;
import com.itemdatacomp.client.util.CommandParser;
import com.itemdatacomp.client.util.SNBTSerializer;
import com.itemdatacomp.client.widget.*;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import java.util.*;

public class ComponentEditorScreen extends ItemDataScreen {
    private static final int PANEL_MARGIN = 10;
    private static final int LEFT_PANEL_WIDTH = 250;
    private static final int RIGHT_PANEL_WIDTH_MIN = 400;

    // UI组件
    private TextFieldWidget searchField;
    private TextFieldWidget commandOutput;
    private ItemListWidget itemList;
    private ComponentListWidget componentList;
    private ButtonWidget groupModeButton;
    private ButtonWidget azModeButton;

    // 状态
    private ItemRegistry.ItemData selectedItem = null;
    private ItemStack previewStack = ItemStack.EMPTY;
    private MinecraftVersion selectedVersion;
    private boolean componentViewMode = true; // true=分组, false=A-Z
    private String currentCategory = "all";

    // 组件数据
    private final Map<String, Object> componentData = new HashMap<>();
    private ItemPreviewWidget previewWidget;

    // 版本选择UI
    private ButtonWidget versionLeftButton;
    private ButtonWidget versionRightButton;

    public ComponentEditorScreen() {
        super(Text.translatable("screen.itemdatacomp.title"));
        // 从配置加载保存的版本
        this.selectedVersion = ConfigManager.getSelectedVersion();
    }

    public void updatePreviewInstant(String componentId, Object newValue) {
        if (newValue != null) {
            componentData.put(componentId, newValue);
        } else {
            componentData.remove(componentId);
        }
        if (previewWidget != null) {
            if (newValue != null) {
                previewWidget.updateComponent(componentId, newValue);
            } else {
                previewWidget.removeComponent(componentId);
            }
        }
        updateCommandOutput();
    }

    @Override
    protected void init() {
        super.init();

        int leftPanelX = PANEL_MARGIN;
        int leftPanelY = 55;
        int leftPanelHeight = this.height - 160;

        // 右侧面板尺寸计算
        int rightPanelX = leftPanelX + LEFT_PANEL_WIDTH + PANEL_MARGIN;
        int rightPanelWidth = this.width - rightPanelX - PANEL_MARGIN;

        // 初始化预览Widget
        int previewY = leftPanelY + 10;
        int previewHeight = 100;
        this.previewWidget = new ItemPreviewWidget(
            rightPanelX + 10, previewY,
            rightPanelWidth - 20, previewHeight
        );
        if (selectedItem != null && !previewStack.isEmpty()) {
            this.previewWidget.setItem(previewStack);
            this.previewWidget.setComponentData(componentData);
        }

        // 搜索框
        this.searchField = new TextFieldWidget(
            this.textRenderer,
            leftPanelX,
            30,
            LEFT_PANEL_WIDTH - 10,
            20,
            Text.translatable("gui.itemdatacomp.search")
        );
        this.searchField.setPlaceholder(Text.translatable("gui.itemdatacomp.search.placeholder"));
        this.searchField.setChangedListener(this::onSearchChanged);
        this.addSelectableChild(this.searchField);

        // 物品列表
        this.itemList = new ItemListWidget(
            this.client,
            LEFT_PANEL_WIDTH - 10,
            leftPanelHeight,
            leftPanelY,
            26
        );
        this.itemList.setX(leftPanelX);
        this.itemList.setSelectionCallback(this::onItemSelected);
        this.addSelectableChild(this.itemList);

        // 右侧组件列表（在预览区域下方）
        int componentListY = previewY + previewHeight + 10;
        int componentListHeight = leftPanelY + leftPanelHeight - componentListY;

        this.componentList = new ComponentListWidget(
            this.client,
            rightPanelWidth - 20,
            componentListHeight,
            componentListY,
            26
        );
        this.componentList.setX(rightPanelX + 10);
        this.componentList.setVersion(selectedVersion);
        this.componentList.setGroupMode(componentViewMode);
        this.componentList.setSelectionCallback(this::onComponentSelected);
        this.addSelectableChild(this.componentList);

        // 命令输出框（底部）
        int commandY = this.height - 80;
        this.commandOutput = new TextFieldWidget(
            this.textRenderer,
            PANEL_MARGIN,
            commandY,
            this.width - PANEL_MARGIN * 2,
            40,
            Text.literal("")
        );
        this.commandOutput.setMaxLength(32767);
        this.commandOutput.setEditable(false);
        updateCommandOutput();
        this.addSelectableChild(this.commandOutput);

        // 底部按钮
        int buttonY = commandY + 45;
        this.addDrawableChild(ButtonWidget.builder(
            Text.translatable("gui.itemdatacomp.copy"),
            button -> copyCommand()
        ).dimensions(PANEL_MARGIN, buttonY, 80, 20).build());

        this.addDrawableChild(ButtonWidget.builder(
            Text.translatable("gui.itemdatacomp.export"),
            button -> exportJSON()
        ).dimensions(PANEL_MARGIN + 85, buttonY, 100, 20).build());

        this.addDrawableChild(ButtonWidget.builder(
            Text.translatable("gui.itemdatacomp.import"),
            button -> importCommand()
        ).dimensions(PANEL_MARGIN + 190, buttonY, 80, 20).build());

        this.addDrawableChild(ButtonWidget.builder(
            Text.translatable("gui.itemdatacomp.clear"),
            button -> clearAll()
        ).dimensions(PANEL_MARGIN + 275, buttonY, 80, 20).build());

        // 组件视图切换按钮（调整位置以防止重叠）
        this.groupModeButton = this.addDrawableChild(ButtonWidget.builder(
            Text.translatable("gui.itemdatacomp.view.group"),
            button -> {
                componentViewMode = true;
                componentList.setGroupMode(true);
                updateViewModeButtons();
            }
        ).dimensions(rightPanelX + rightPanelWidth - 115, 35, 50, 20).build());

        this.azModeButton = this.addDrawableChild(ButtonWidget.builder(
            Text.translatable("gui.itemdatacomp.view.az"),
            button -> {
                componentViewMode = false;
                componentList.setGroupMode(false);
                updateViewModeButtons();
            }
        ).dimensions(rightPanelX + rightPanelWidth - 60, 35, 55, 20).build());

        // 版本选择按钮（左右箭头）
        this.versionLeftButton = this.addDrawableChild(ButtonWidget.builder(
            Text.literal("<"),
            button -> changeVersion(-1)
        ).dimensions(this.width - 150, 10, 30, 20).build());

        this.versionRightButton = this.addDrawableChild(ButtonWidget.builder(
            Text.literal(">"),
            button -> changeVersion(1)
        ).dimensions(this.width - 50, 10, 30, 20).build());

        updateViewModeButtons();
    }

    /**
     * 切换版本
     */
    private void changeVersion(int direction) {
        MinecraftVersion[] versions = MinecraftVersion.values();
        int currentIndex = selectedVersion.ordinal();
        int newIndex = currentIndex + direction;

        if (newIndex >= 0 && newIndex < versions.length) {
            selectedVersion = versions[newIndex];
            ConfigManager.setSelectedVersion(selectedVersion);
            if (componentList != null) {
                componentList.setVersion(selectedVersion);
            }
        }
    }

    private void updateViewModeButtons() {
        if (groupModeButton != null) {
            groupModeButton.active = !componentViewMode;
        }
        if (azModeButton != null) {
            azModeButton.active = componentViewMode;
        }
    }

    private void onSearchChanged(String query) {
        if (itemList != null) {
            itemList.setSearchQuery(query);
        }
    }

    private void onItemSelected(ItemRegistry.ItemData item) {
        this.selectedItem = item;

        // 创建预览ItemStack
        try {
            Identifier id = Identifier.tryParse(item.id());
            if (id != null) {
                Item minecraftItem = Registries.ITEM.get(id);
                this.previewStack = new ItemStack(minecraftItem);

                // 更新预览Widget
                if (previewWidget != null) {
                    previewWidget.setItem(previewStack);
                    previewWidget.setComponentData(componentData);
                }
            }
        } catch (Exception e) {
            this.previewStack = ItemStack.EMPTY;
        }

        updateCommandOutput();
    }

    private void onComponentSelected(ComponentRegistry.ComponentDef component) {
        // 打开对应的组件编辑器
        switch (component.type()) {
            case UNIT -> toggleUnitComponent(component);
            case BOOL -> toggleBooleanComponent(component);
            case STRING, INT -> openTextInputEditor(component);
            case JSON_TEXT -> openTextInputEditor(component); // TODO: 富文本编辑器
            case LORE_ARRAY -> openLoreEditor();
            case ENCHANTMENTS -> openEnchantmentEditor(component);
            case ATTRIBUTES -> openAttributeEditor();
            case TRIM -> openArmorTrimEditor();
            case DYED_COLOR -> openColorPicker();
            case POTION -> openPotionEditor();
            case FOOD -> openFoodEditor();
            case CONSUMABLE -> openConsumableEditor();
            case ENTITY_DATA -> openEntityDataEditor();
            case EQUIPPABLE -> openEquippableEditor();
            case FIREWORKS -> openFireworksEditor();
            case JSON, ENUM, FIREWORK_STAR -> openTextInputEditor(component);
        }
    }

    private void toggleUnitComponent(ComponentRegistry.ComponentDef component) {
        boolean enabled = !componentData.containsKey(component.id());
        updatePreviewInstant(component.id(), enabled ? Collections.emptyMap() : null);
        componentList.markComponentModified(component.id(), enabled);
    }

    private void toggleBooleanComponent(ComponentRegistry.ComponentDef component) {
        boolean enabled = !Boolean.TRUE.equals(componentData.get(component.id()));
        updatePreviewInstant(component.id(), enabled ? Boolean.TRUE : null);
        componentList.markComponentModified(component.id(), enabled);
    }

    private void openTextInputEditor(ComponentRegistry.ComponentDef component) {
        // 检查是否需要物品选择器
        if (needsItemSelector(component.id())) {
            openItemSelectorEditor(component);
            return;
        }

        String currentValue = (String) componentData.get(component.id());
        this.client.setScreen(new com.itemdatacomp.client.screen.editor.TextInputEditorScreen(
            this,
            component.id(),
            currentValue,
            value -> {
                if (value != null && !value.isEmpty()) {
                    updatePreviewInstant(component.id(), value);
                    componentList.markComponentModified(component.id(), true);
                } else {
                    updatePreviewInstant(component.id(), null);
                    componentList.markComponentModified(component.id(), false);
                }
            }
        ));
    }

    private boolean needsItemSelector(String componentId) {
        return componentId.equals("minecraft:repairable") ||
               componentId.equals("minecraft:use_remainder") ||
               componentId.equals("minecraft:jukebox_playable") ||
               componentId.equals("minecraft:instrument");
    }

    private void openItemSelectorEditor(ComponentRegistry.ComponentDef component) {
        String currentValue = (String) componentData.get(component.id());
        String hintText = getItemSelectorHint(component.id());

        this.client.setScreen(new com.itemdatacomp.client.screen.editor.ItemSelectorEditorScreen(
            this,
            component.id(),
            currentValue,
            value -> {
                if (value != null && !value.isEmpty()) {
                    updatePreviewInstant(component.id(), value);
                    componentList.markComponentModified(component.id(), true);
                } else {
                    updatePreviewInstant(component.id(), null);
                    componentList.markComponentModified(component.id(), false);
                }
            },
            hintText
        ));
    }

    private String getItemSelectorHint(String componentId) {
        return switch (componentId) {
            case "minecraft:repairable" -> "选择可以用来修复此物品的材料";
            case "minecraft:use_remainder" -> "选择使用后剩余的物品";
            case "minecraft:jukebox_playable" -> "选择唱片机可以播放的音乐唱片";
            case "minecraft:instrument" -> "选择山羊角的乐器音色";
            default -> "选择一个物品ID";
        };
    }

    private void openLoreEditor() {
        @SuppressWarnings("unchecked")
        List<String> currentLore = (List<String>) componentData.get("minecraft:lore");
        this.client.setScreen(new com.itemdatacomp.client.screen.editor.LoreEditorScreen(
            this,
            currentLore,
            lore -> {
                if (lore != null && !lore.isEmpty()) {
                    updatePreviewInstant("minecraft:lore", lore);
                    componentList.markComponentModified("minecraft:lore", true);
                } else {
                    updatePreviewInstant("minecraft:lore", null);
                    componentList.markComponentModified("minecraft:lore", false);
                }
            }
        ));
    }

    private void openEnchantmentEditor(ComponentRegistry.ComponentDef component) {
        Map<String, Integer> currentEnch = getEnchantmentLevels(component.id());
        this.client.setScreen(new com.itemdatacomp.client.screen.editor.EnchantmentEditorScreen(
            this,
            currentEnch,
            getEnchantmentAliases(component.id()),
            result -> {
                if (result != null && !result.levels().isEmpty()) {
                    updatePreviewInstant(component.id(), buildEnchantmentComponentValue(result.levels(), result.aliases()));
                    componentList.markComponentModified(component.id(), true);
                } else {
                    updatePreviewInstant(component.id(), null);
                    componentList.markComponentModified(component.id(), false);
                }
            }
        ));
    }

    private Object buildEnchantmentComponentValue(Map<String, Integer> levels, Map<String, String> aliases) {
        if (aliases == null || aliases.isEmpty()) {
            return levels;
        }

        Map<String, Object> value = new LinkedHashMap<>();
        value.put("levels", new LinkedHashMap<>(levels));
        value.put("__aliases", new LinkedHashMap<>(aliases));
        return value;
    }

    private Map<String, Integer> getEnchantmentLevels(String componentId) {
        Map<String, Integer> result = new LinkedHashMap<>();
        Object current = componentData.get(componentId);
        if (!(current instanceof Map<?, ?> map)) {
            return result;
        }

        Object levels = map.containsKey("levels") ? map.get("levels") : map;
        if (!(levels instanceof Map<?, ?> levelMap)) {
            return result;
        }

        for (Map.Entry<?, ?> entry : levelMap.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof Number number) {
                result.put(String.valueOf(entry.getKey()), number.intValue());
            } else {
                try {
                    result.put(String.valueOf(entry.getKey()), Integer.parseInt(String.valueOf(value)));
                } catch (NumberFormatException ignored) {
                }
            }
        }
        return result;
    }

    private Map<String, String> getEnchantmentAliases(String componentId) {
        Map<String, String> result = new LinkedHashMap<>();
        Object current = componentData.get(componentId);
        if (!(current instanceof Map<?, ?> map)) {
            return result;
        }

        Object aliases = map.get("__aliases");
        if (!(aliases instanceof Map<?, ?> aliasMap)) {
            return result;
        }

        for (Map.Entry<?, ?> entry : aliasMap.entrySet()) {
            result.put(String.valueOf(entry.getKey()), String.valueOf(entry.getValue()));
        }
        return result;
    }

    private void openColorPicker() {
        Integer currentColor = (Integer) componentData.get("minecraft:dyed_color");
        this.client.setScreen(new com.itemdatacomp.client.screen.editor.ColorPickerScreen(
            this,
            currentColor,
            color -> {
                updatePreviewInstant("minecraft:dyed_color", color);
                componentList.markComponentModified("minecraft:dyed_color", true);
            }
        ));
    }

private void updateCommandOutput() {
        if (selectedItem == null) {
            this.commandOutput.setText("");
        } else {
            String command = SNBTSerializer.generateCommand(selectedItem.id(), componentData, selectedVersion.getId());
            this.commandOutput.setText(command);
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // 背景
        this.renderBackground(context, mouseX, mouseY, delta);

        // 标题
        context.drawTextWithShadow(this.textRenderer, this.title, 10, 10, 0xFFFFFF);

        // 版本选择器（右上角）
        String versionText = "Version: " + selectedVersion.getId();
        int versionTextWidth = this.textRenderer.getWidth(versionText);
        int versionX = this.width - versionTextWidth - 110;
        int versionY = 15;

        // 绘制版本文字（居中对齐）
        context.drawTextWithShadow(this.textRenderer, Text.literal(versionText),
            versionX + 50, versionY, 0xA78BFA);

        // 左侧面板背景
        int leftPanelX = PANEL_MARGIN;
        int leftPanelY = 55;
        int leftPanelHeight = this.height - 160;
        context.fill(leftPanelX, leftPanelY,
            leftPanelX + LEFT_PANEL_WIDTH, leftPanelY + leftPanelHeight,
            0xFF1A1A1A);

        // 右侧面板背景
        int rightPanelX = leftPanelX + LEFT_PANEL_WIDTH + PANEL_MARGIN;
        int rightPanelWidth = this.width - rightPanelX - PANEL_MARGIN;
        context.fill(rightPanelX, leftPanelY,
            rightPanelX + rightPanelWidth, leftPanelY + leftPanelHeight,
            0xFF1A1A1A);

        // 预览区域（右上）
        int previewY = leftPanelY + 10;
        int previewHeight = 100;
        int componentListY = previewY + previewHeight + 10;
        context.fill(rightPanelX + 10, previewY,
            rightPanelX + rightPanelWidth - 10, previewY + previewHeight,
            0xFF2A2A2A);

        // 渲染增强预览Widget
        if (previewWidget != null && selectedItem != null && !previewStack.isEmpty()) {
            previewWidget.render(context, mouseX, mouseY, delta);
        } else if (selectedItem == null || previewStack.isEmpty()) {
            context.drawText(this.textRenderer,
                Text.translatable("gui.itemdatacomp.no_item_selected"),
                rightPanelX + 20, previewY + 40, 0x888888, false);
        }

        // 组件统计
        String componentStats = Text.translatable("gui.itemdatacomp.components_set", componentData.size()).getString();
        int componentStatsWidth = this.textRenderer.getWidth(componentStats);
        context.drawText(this.textRenderer, Text.literal(componentStats),
            rightPanelX + rightPanelWidth - componentStatsWidth - 15, componentListY - 12, 0x888888, false);

        // 渲染搜索框和命令输出
        this.searchField.render(context, mouseX, mouseY, delta);
        this.commandOutput.render(context, mouseX, mouseY, delta);

        // 渲染物品列表和组件列表
        if (itemList != null) {
            itemList.render(context, mouseX, mouseY, delta);
        }
        if (componentList != null) {
            componentList.render(context, mouseX, mouseY, delta);
        }

        // 渲染所有按钮
        super.render(context, mouseX, mouseY, delta);
    }

    private void renderItemPreview(DrawContext context, int x, int y, int mouseX, int mouseY) {
        // 渲染物品图标
        context.drawItem(previewStack, x, y);

        // 渲染物品名称
        if (selectedItem != null) {
            context.drawText(this.textRenderer,
                Text.literal(selectedItem.zhName()),
                x + 20, y + 2, 0xFFFFFF, false);

            context.drawText(this.textRenderer,
                Text.literal(selectedItem.id()),
                x + 20, y + 13, 0x888888, false);
        }

        // 渲染提示框（鼠标悬停）
        boolean isHovering = mouseX >= x && mouseX <= x + 16 && mouseY >= y && mouseY <= y + 16;
        if (isHovering && !previewStack.isEmpty()) {
            context.drawItemTooltip(this.textRenderer, previewStack, mouseX, mouseY);
        }
    }

    private void copyCommand() {
        String command = this.commandOutput.getText();
        this.client.keyboard.setClipboard(command);
        // Show copy success message to user
        this.client.player.sendMessage(Text.translatable("chat.copy.success"), false);
    }

    private void exportJSON() {
        if (selectedItem == null) {
            return;
        }
        // Export format: { "item": "...", "version": "...", "components": {...} }
        StringBuilder json = new StringBuilder();
        json.append("{\n");
        json.append("  \"item\": \"").append(selectedItem.id()).append("\",\n");
        json.append("  \"version\": \"").append(selectedVersion.getDisplayName()).append("\",\n");
        json.append("  \"components\": {\n");

        boolean first = true;
        for (Map.Entry<String, Object> entry : componentData.entrySet()) {
            if (!first) json.append(",\n");
            json.append("    \"").append(entry.getKey()).append("\": ");
            json.append(serializeValueToJSON(entry.getValue()));
            first = false;
        }

        json.append("\n  }\n}");
        this.client.keyboard.setClipboard(json.toString());
        this.client.player.sendMessage(Text.translatable("chat.copy.success"), false);
    }

    private void importCommand() {
        // 从剪贴板读取 /give 命令并解析
        String clipboard = this.client.keyboard.getClipboard();
        if (clipboard != null && !clipboard.isEmpty()) {
            try {
                CommandParser.ParseResult result = CommandParser.parseGiveCommand(clipboard);
                if (result.isSuccess()) {
                    // 设置物品
                    ItemRegistry.ItemData item = ItemRegistry.getItem(result.itemId);
                    if (item != null) {
                        onItemSelected(item);
                    }
                    // 设置组件数据
                    componentData.clear();
                    componentData.putAll(result.components);
                    if (previewWidget != null) {
                        previewWidget.setComponentData(componentData);
                    }
                    updateCommandOutput();
                } else {
                    // 显示解析错误对话框
                    ParseErrorDialog.show(this, result.error);
                }
            } catch (Exception e) {
                ParseErrorDialog.show(this, e.getMessage());
            }
        }
    }

    private void clearAll() {
        this.selectedItem = null;
        this.previewStack = ItemStack.EMPTY;
        this.componentData.clear();
        if (previewWidget != null) {
            previewWidget.setItem(ItemStack.EMPTY);
            previewWidget.setComponentData(componentData);
        }
        updateCommandOutput();
    }

    private String serializeValueToJSON(Object value) {
        if (value == null) {
            return "null";
        } else if (value instanceof String) {
            return "\"" + ((String) value).replace("\"", "\\\"") + "\"";
        } else if (value instanceof Number) {
            return value.toString();
        } else if (value instanceof Boolean) {
            return value.toString();
        } else if (value instanceof List) {
            StringBuilder sb = new StringBuilder("[");
            boolean first = true;
            for (Object item : (List<?>) value) {
                if (!first) sb.append(",");
                sb.append(serializeValueToJSON(item));
                first = false;
            }
            sb.append("]");
            return sb.toString();
        } else if (value instanceof Map) {
            StringBuilder sb = new StringBuilder("{");
            boolean first = true;
            for (Map.Entry<?, ?> entry : ((Map<?, ?>) value).entrySet()) {
                if (!first) sb.append(",");
                sb.append("\"").append(entry.getKey()).append("\":");
                sb.append(serializeValueToJSON(entry.getValue()));
                first = false;
            }
            sb.append("}");
            return sb.toString();
        } else {
            return "\"" + value.toString() + "\"";
        }
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.searchField.isFocused() && this.searchField.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (itemList != null && isInsideWidget(itemList, mouseX, mouseY) && itemList.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        if (componentList != null && isInsideWidget(componentList, mouseX, mouseY) && componentList.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (componentList != null && isInsideWidget(componentList, mouseX, mouseY)) {
            componentList.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
            return true;
        }
        if (itemList != null && isInsideWidget(itemList, mouseX, mouseY)) {
            itemList.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (componentList != null && componentList.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)) {
            return true;
        }
        if (itemList != null && itemList.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)) {
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (componentList != null && componentList.mouseReleased(mouseX, mouseY, button)) {
            return true;
        }
        if (itemList != null && itemList.mouseReleased(mouseX, mouseY, button)) {
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    private boolean isInsideWidget(net.minecraft.client.gui.widget.ClickableWidget widget, double mouseX, double mouseY) {
        return mouseX >= widget.getX()
            && mouseX < widget.getX() + widget.getWidth()
            && mouseY >= widget.getY()
            && mouseY < widget.getY() + widget.getHeight();
    }

    private void openAttributeEditor() {
        @SuppressWarnings("unchecked")
        List<com.itemdatacomp.client.screen.editor.AttributeEditorScreen.AttributeModifier> currentAttrs =
            (List<com.itemdatacomp.client.screen.editor.AttributeEditorScreen.AttributeModifier>)
            componentData.get("minecraft:attribute_modifiers");

        this.client.setScreen(new com.itemdatacomp.client.screen.editor.AttributeEditorScreen(
            this,
            currentAttrs,
            attrs -> {
                if (attrs != null && !attrs.isEmpty()) {
                    updatePreviewInstant("minecraft:attribute_modifiers", attrs);
                    componentList.markComponentModified("minecraft:attribute_modifiers", true);
                } else {
                    updatePreviewInstant("minecraft:attribute_modifiers", null);
                    componentList.markComponentModified("minecraft:attribute_modifiers", false);
                }
            }
        ));
    }

    private void openArmorTrimEditor() {
        // Extract existing trim data from componentData
        String currentMaterial = null;
        String currentPattern = null;

        Object trimObj = componentData.get("minecraft:trim");
        if (trimObj instanceof Map) {
            Map<?, ?> trim = (Map<?, ?>) trimObj;
            Object matObj = trim.get("material");
            Object patObj = trim.get("pattern");

            if (matObj instanceof String) {
                String mat = (String) matObj;
                currentMaterial = mat.replace("minecraft:", "");
            }
            if (patObj instanceof String) {
                String pat = (String) patObj;
                currentPattern = pat.replace("minecraft:", "");
            }
        }

        this.client.setScreen(new com.itemdatacomp.client.screen.editor.ArmorTrimEditorScreen(
            this,
            currentMaterial,
            currentPattern,
            (material, pattern) -> {
                Map<String, String> trimData = new HashMap<>();
                trimData.put("material", "minecraft:" + material);
                trimData.put("pattern", "minecraft:" + pattern);
                updatePreviewInstant("minecraft:trim", trimData);
                componentList.markComponentModified("minecraft:trim", true);
            }
        ));
    }

    private void openPotionEditor() {
        @SuppressWarnings("unchecked")
        com.itemdatacomp.client.screen.editor.PotionEditorScreen.PotionData currentPotion =
            (com.itemdatacomp.client.screen.editor.PotionEditorScreen.PotionData)
            componentData.get("minecraft:potion_contents");

        this.client.setScreen(new com.itemdatacomp.client.screen.editor.PotionEditorScreen(
            this,
            currentPotion,
            potion -> {
                if (potion != null && !potion.customEffects.isEmpty()) {
                    updatePreviewInstant("minecraft:potion_contents", potion);
                    componentList.markComponentModified("minecraft:potion_contents", true);
                } else {
                    updatePreviewInstant("minecraft:potion_contents", null);
                    componentList.markComponentModified("minecraft:potion_contents", false);
                }
            }
        ));
    }

    private void openFoodEditor() {
        com.itemdatacomp.client.screen.editor.FoodEditorScreen.FoodData currentFood =
            (com.itemdatacomp.client.screen.editor.FoodEditorScreen.FoodData) componentData.get("minecraft:food");
        if (currentFood == null) {
            currentFood = new com.itemdatacomp.client.screen.editor.FoodEditorScreen.FoodData();
        }

        this.client.setScreen(new com.itemdatacomp.client.screen.editor.FoodEditorScreen(
            this,
            currentFood,
            food -> {
                if (food != null) {
                    updatePreviewInstant("minecraft:food", food);
                    componentList.markComponentModified("minecraft:food", true);
                } else {
                    updatePreviewInstant("minecraft:food", null);
                    componentList.markComponentModified("minecraft:food", false);
                }
            }
        ));
    }

    private void openConsumableEditor() {
        com.itemdatacomp.client.screen.editor.ConsumableEditorScreen.ConsumableData currentConsumable =
            (com.itemdatacomp.client.screen.editor.ConsumableEditorScreen.ConsumableData) componentData.get("minecraft:consumable");
        if (currentConsumable == null) {
            currentConsumable = new com.itemdatacomp.client.screen.editor.ConsumableEditorScreen.ConsumableData();
        }

        this.client.setScreen(new com.itemdatacomp.client.screen.editor.ConsumableEditorScreen(
            this,
            currentConsumable,
            consumable -> {
                if (consumable != null) {
                    updatePreviewInstant("minecraft:consumable", consumable);
                    componentList.markComponentModified("minecraft:consumable", true);
                } else {
                    updatePreviewInstant("minecraft:consumable", null);
                    componentList.markComponentModified("minecraft:consumable", false);
                }
            }
        ));
    }

    private void openEntityDataEditor() {
        com.itemdatacomp.client.screen.editor.EntityDataEditorScreen.EntityData currentEntityData =
            (com.itemdatacomp.client.screen.editor.EntityDataEditorScreen.EntityData) componentData.get("minecraft:entity_data");
        if (currentEntityData == null) {
            currentEntityData = new com.itemdatacomp.client.screen.editor.EntityDataEditorScreen.EntityData();
        }

        this.client.setScreen(new com.itemdatacomp.client.screen.editor.EntityDataEditorScreen(
            this,
            currentEntityData,
            entityData -> {
                if (entityData != null) {
                    updatePreviewInstant("minecraft:entity_data", entityData);
                    componentList.markComponentModified("minecraft:entity_data", true);
                } else {
                    updatePreviewInstant("minecraft:entity_data", null);
                    componentList.markComponentModified("minecraft:entity_data", false);
                }
            }
        ));
    }

    private void openEquippableEditor() {
        com.itemdatacomp.client.screen.editor.EquippableEditorScreen.EquippableData currentEquippable =
            (com.itemdatacomp.client.screen.editor.EquippableEditorScreen.EquippableData) componentData.get("minecraft:equippable");
        if (currentEquippable == null) {
            currentEquippable = new com.itemdatacomp.client.screen.editor.EquippableEditorScreen.EquippableData();
        }

        this.client.setScreen(new com.itemdatacomp.client.screen.editor.EquippableEditorScreen(
            this,
            currentEquippable,
            equippable -> {
                if (equippable != null) {
                    updatePreviewInstant("minecraft:equippable", equippable);
                    componentList.markComponentModified("minecraft:equippable", true);
                } else {
                    updatePreviewInstant("minecraft:equippable", null);
                    componentList.markComponentModified("minecraft:equippable", false);
                }
            }
        ));
    }

    private void openFireworksEditor() {
        com.itemdatacomp.client.screen.editor.FireworksEditorScreen.FireworksData currentFireworks =
            (com.itemdatacomp.client.screen.editor.FireworksEditorScreen.FireworksData) componentData.get("minecraft:fireworks");
        if (currentFireworks == null) {
            currentFireworks = new com.itemdatacomp.client.screen.editor.FireworksEditorScreen.FireworksData();
        }

        this.client.setScreen(new com.itemdatacomp.client.screen.editor.FireworksEditorScreen(
            this,
            currentFireworks,
            fireworks -> {
                if (fireworks != null) {
                    updatePreviewInstant("minecraft:fireworks", fireworks);
                    componentList.markComponentModified("minecraft:fireworks", true);
                } else {
                    updatePreviewInstant("minecraft:fireworks", null);
                    componentList.markComponentModified("minecraft:fireworks", false);
                }
            }
        ));
    }
}
