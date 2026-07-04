package com.itemdatacomp.client.widget;

import com.itemdatacomp.client.util.PinyinMatcher;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.text.Text;
import java.util.*;

/**
 * 增强的组件列表Widget - 已优化版本
 * 支持分组/A-Z两种模式，改进视觉效果和交互
 */
public class ComponentListWidgetEnhanced extends AlwaysSelectedEntryListWidget<ComponentListWidgetEnhanced.ComponentEntry> {

    private boolean groupMode = true; // true=分组, false=A-Z
    private ComponentSelectionCallback callback;
    private final Map<String, Boolean> expandedGroups = new HashMap<>();
    private final Set<String> modifiedComponents = new HashSet<>();
    private final List<ComponentData> allComponents;

    public ComponentListWidgetEnhanced(MinecraftClient client, int width, int height, int top, int itemHeight,
                                       List<ComponentData> components) {
        super(client, width, height, top, itemHeight);
        this.allComponents = new ArrayList<>(components);
        // 默认所有组展开
        this.allComponents.stream()
            .map(c -> c.group)
            .distinct()
            .forEach(g -> expandedGroups.put(g, true));
        refreshEntries();
    }

    public void setGroupMode(boolean groupMode) {
        this.groupMode = groupMode;
        refreshEntries();
    }

    public void setSelectionCallback(ComponentSelectionCallback callback) {
        this.callback = callback;
    }

    public void markComponentModified(String componentId, boolean modified) {
        if (modified) {
            modifiedComponents.add(componentId);
        } else {
            modifiedComponents.remove(componentId);
        }
    }

    private void refreshEntries() {
        this.clearEntries();

        if (groupMode) {
            renderGroupMode();
        } else {
            renderAZMode();
        }
    }

    private void renderGroupMode() {
        // 按组分类
        Map<String, List<ComponentData>> grouped = new TreeMap<>();
        for (ComponentData comp : allComponents) {
            grouped.computeIfAbsent(comp.group, k -> new ArrayList<>()).add(comp);
        }

        for (Map.Entry<String, List<ComponentData>> entry : grouped.entrySet()) {
            String group = entry.getKey();
            List<ComponentData> components = entry.getValue();

            boolean expanded = expandedGroups.getOrDefault(group, true);
            long modifiedCount = components.stream()
                .filter(c -> modifiedComponents.contains(c.id))
                .count();

            this.addEntry(new GroupHeaderEntry(group, expanded, (int) modifiedCount));

            if (expanded) {
                for (ComponentData comp : components) {
                    boolean modified = modifiedComponents.contains(comp.id);
                    this.addEntry(new ComponentItemEntry(comp, modified));
                }
            }
        }
    }

    private void renderAZMode() {
        // 按首字母分类
        Map<String, List<ComponentData>> grouped = new TreeMap<>();
        for (ComponentData comp : allComponents) {
            String letter = comp.id.substring(0, 1).toUpperCase();
            grouped.computeIfAbsent(letter, k -> new ArrayList<>()).add(comp);
        }

        for (Map.Entry<String, List<ComponentData>> entry : grouped.entrySet()) {
            String letter = entry.getKey();
            List<ComponentData> components = entry.getValue();

            boolean expanded = expandedGroups.getOrDefault(letter, true);
            this.addEntry(new LetterHeaderEntry(letter, expanded));

            if (expanded) {
                for (ComponentData comp : components) {
                    boolean modified = modifiedComponents.contains(comp.id);
                    this.addEntry(new ComponentItemEntry(comp, modified));
                }
            }
        }
    }

    @Override
    public int getRowWidth() {
        return this.width - 10;
    }

    @Override
    protected int getScrollbarX() {
        return this.getX() + this.width - 6;
    }

    public class GroupHeaderEntry extends ComponentEntry {
        private final String group;
        private final boolean expanded;
        private final int modifiedCount;
        private long hoverStartTime = 0;

        public GroupHeaderEntry(String group, boolean expanded, int modifiedCount) {
            this.group = group;
            this.expanded = expanded;
            this.modifiedCount = modifiedCount;
        }

        @Override
        public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight,
                          int mouseX, int mouseY, boolean hovered, float tickDelta) {
            MinecraftClient client = MinecraftClient.getInstance();

            // 背景
            context.fill(x, y, x + entryWidth, y + entryHeight, 0xFF1A1A1A);

            if (hovered) {
                context.fill(x, y, x + entryWidth, y + entryHeight, 0x30A78BFA);
            }

            // 左侧装饰线
            context.fill(x, y, x + 2, y + entryHeight, 0xFFA78BFA);

            // 展开/收起箭头
            String arrow = expanded ? "▼" : "▶";
            int arrowColor = hovered ? 0xC0A8FF : 0xA78BFA;
            context.drawTextWithShadow(client.textRenderer, Text.literal(arrow), x + 8, y + 5, arrowColor);

            // 组名
            String groupText = group;
            int groupTextX = x + 20;
            context.drawTextWithShadow(client.textRenderer, Text.literal(groupText), groupTextX, y + 5, 0xFFFFFF);

            // 修改数量徽章（紧贴组名右侧，留出间距）
            if (modifiedCount > 0) {
                String badge = String.valueOf(modifiedCount);
                int groupNameWidth = client.textRenderer.getWidth(groupText);
                int badgeWidth = client.textRenderer.getWidth(badge) + 10;
                int badgeX = groupTextX + groupNameWidth + 6;  // 组名结束位置 + 6像素间距

                // 徽章背景（圆角矩形效果）
                context.fill(badgeX, y + 4, badgeX + badgeWidth, y + 14, 0x80A78BFA);

                // 徽章文字（居中）
                int textX = badgeX + (badgeWidth - client.textRenderer.getWidth(badge)) / 2;
                context.drawTextWithShadow(client.textRenderer, Text.literal(badge), textX, y + 5, 0xFFFFFF);
            }
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (button == 0) {
                expandedGroups.put(group, !expanded);
                ComponentListWidgetEnhanced.this.refreshEntries();
                return true;
            }
            return false;
        }

        @Override
        public Text getNarration() {
            return Text.literal(group);
        }
    }

    public class LetterHeaderEntry extends ComponentEntry {
        private final String letter;
        private final boolean expanded;

        public LetterHeaderEntry(String letter, boolean expanded) {
            this.letter = letter;
            this.expanded = expanded;
        }

        @Override
        public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight,
                          int mouseX, int mouseY, boolean hovered, float tickDelta) {
            MinecraftClient client = MinecraftClient.getInstance();

            context.fill(x, y, x + entryWidth, y + entryHeight, 0xFF1A1A1A);

            if (hovered) {
                context.fill(x, y, x + entryWidth, y + entryHeight, 0x30A78BFA);
            }

            context.fill(x, y, x + 2, y + entryHeight, 0xFF6BA3FF);

            String arrow = expanded ? "▼" : "▶";
            context.drawTextWithShadow(client.textRenderer, Text.literal(arrow), x + 8, y + 5, 0x6BA3FF);
            context.drawTextWithShadow(client.textRenderer, Text.literal(letter), x + 20, y + 5, 0xFFFFFF);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (button == 0) {
                expandedGroups.put(letter, !expanded);
                ComponentListWidgetEnhanced.this.refreshEntries();
                return true;
            }
            return false;
        }

        @Override
        public Text getNarration() {
            return Text.literal(letter);
        }
    }

    public class ComponentItemEntry extends ComponentEntry {
        private final ComponentData component;
        private final boolean modified;
        private long hoverStartTime = 0;

        public ComponentItemEntry(ComponentData component, boolean modified) {
            this.component = component;
            this.modified = modified;
        }

        @Override
        public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight,
                          int mouseX, int mouseY, boolean hovered, float tickDelta) {
            MinecraftClient client = MinecraftClient.getInstance();

            // 背景
            if (hovered || this == getSelectedOrNull()) {
                context.fill(x, y, x + entryWidth, y + entryHeight, 0x40A78BFA);
            }

            // 修改标记（左侧竖线）
            if (modified) {
                context.fill(x, y, x + 3, y + entryHeight, 0xFFFF6B6B);
            }

            // 选中指示线
            if (this == getSelectedOrNull()) {
                context.fill(x, y, x + 2, y + entryHeight, 0xFFA78BFA);
            }

            // 组件ID（带冒号前缀处理）
            String displayId = component.id.replace("minecraft:", "");
            int textColor = hovered ? 0xC0A8FF : 0xA78BFA;
            context.drawTextWithShadow(client.textRenderer, Text.literal(displayId), x + 12, y + 2, textColor);

            // 组件描述（灰色）
            String desc = component.description;
            if (desc.length() > 30) {
                desc = desc.substring(0, 27) + "...";
            }
            context.drawTextWithShadow(client.textRenderer, Text.literal(desc), x + 12, y + 12, 0x888888);

            // 类型标签
            String typeTag = "[" + component.type + "]";
            int typeColor = getTypeColor(component.type);
            context.drawTextWithShadow(client.textRenderer, Text.literal(typeTag), x + entryWidth - 60, y + 5, typeColor);
        }

        private int getTypeColor(String type) {
            return switch (type) {
                case "attribute" -> 0xFF6BA3FF;   // 蓝
                case "enchantment" -> 0xFFFFB366; // 橙
                case "effect" -> 0xFF66FF66;      // 绿
                case "damage" -> 0xFFFF6B6B;      // 红
                default -> 0xFF888888;
            };
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (button == 0) {
                ComponentListWidgetEnhanced.this.setSelected(this);
                if (callback != null) {
                    callback.onComponentSelected(component);
                }
                return true;
            }
            return false;
        }

        @Override
        public Text getNarration() {
            return Text.literal(component.id);
        }

        public ComponentData getComponent() {
            return component;
        }
    }

    public static abstract class ComponentEntry extends Entry<ComponentEntry> {
    }

    public static class ComponentData {
        public final String id;
        public final String group;
        public final String description;
        public final String type;

        public ComponentData(String id, String group, String description, String type) {
            this.id = id;
            this.group = group;
            this.description = description;
            this.type = type;
        }
    }

    @FunctionalInterface
    public interface ComponentSelectionCallback {
        void onComponentSelected(ComponentData component);
    }
}
