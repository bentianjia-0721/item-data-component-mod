package com.itemdatacomp.client.widget;

import com.itemdatacomp.client.data.ComponentRegistry;
import com.itemdatacomp.client.data.MinecraftVersion;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.text.Text;
import java.util.*;

/**
 * 组件列表Widget
 * 右侧可滚动的组件选择列表（支持分组/A-Z模式）
 */
public class ComponentListWidget extends AlwaysSelectedEntryListWidget<ComponentListWidget.ComponentEntry> {

    private MinecraftVersion currentVersion;
    private boolean groupMode = true; // true=分组, false=A-Z
    private ComponentSelectionCallback callback;
    private final Map<String, Boolean> expandedGroups = new HashMap<>();
    private final Set<String> modifiedComponents = new HashSet<>();

    public ComponentListWidget(MinecraftClient client, int width, int height, int top, int itemHeight) {
        super(client, width, height, top, itemHeight);
        this.currentVersion = MinecraftVersion.V1_21_4;
        refreshEntries();
    }

    public void setVersion(MinecraftVersion version) {
        this.currentVersion = version;
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
        Map<String, List<ComponentRegistry.ComponentDef>> grouped =
            ComponentRegistry.getComponentsByGroup(currentVersion);

        for (Map.Entry<String, List<ComponentRegistry.ComponentDef>> entry : grouped.entrySet()) {
            String group = entry.getKey();
            List<ComponentRegistry.ComponentDef> components = entry.getValue();

            // 添加组头
            boolean expanded = expandedGroups.getOrDefault(group, false);
            int modifiedCount = (int) components.stream()
                .filter(c -> modifiedComponents.contains(c.id()))
                .count();

            this.addEntry(new GroupHeaderEntry(group, expanded, modifiedCount));

            // 如果展开，添加组件
            if (expanded) {
                for (ComponentRegistry.ComponentDef comp : components) {
                    boolean modified = modifiedComponents.contains(comp.id());
                    this.addEntry(new ComponentItemEntry(comp, modified));
                }
            }
        }
    }

    private void renderAZMode() {
        Map<String, List<ComponentRegistry.ComponentDef>> grouped =
            ComponentRegistry.getComponentsByLetter(currentVersion);

        for (Map.Entry<String, List<ComponentRegistry.ComponentDef>> entry : grouped.entrySet()) {
            String letter = entry.getKey();
            List<ComponentRegistry.ComponentDef> components = entry.getValue();

            // 添加字母组头
            boolean expanded = expandedGroups.getOrDefault(letter, true);
            this.addEntry(new LetterHeaderEntry(letter, expanded));

            // 如果展开，添加组件
            if (expanded) {
                for (ComponentRegistry.ComponentDef comp : components) {
                    boolean modified = modifiedComponents.contains(comp.id());
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
    public int getRowLeft() {
        return this.getX() + 5;
    }

    @Override
    protected int getScrollbarX() {
        return this.getX() + this.width - 6;
    }

    // 组头条目
    public class GroupHeaderEntry extends ComponentEntry {
        private final String group;
        private final boolean expanded;
        private final int modifiedCount;

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
                context.fill(x, y, x + entryWidth, y + entryHeight, 0x20FFFFFF);
            }

            // 箭头
            String arrow = expanded ? "▼" : "▶";
            context.drawText(client.textRenderer, arrow, x + 5, y + 5, 0x888888, false);

            // 组名（翻译键）
            String groupName = Text.translatable("component.group." + group).getString();
            int groupTextX = x + 20;
            context.drawText(client.textRenderer, groupName, groupTextX, y + 5, 0xFFFFFF, false);

            // 已设置数量徽章（右对齐，在滚动条左侧）
            if (modifiedCount > 0) {
                String badge = String.valueOf(modifiedCount);
                int badgeWidth = client.textRenderer.getWidth(badge) + 8;

                // 计算组名的实际宽度和结束位置
                int groupNameWidth = client.textRenderer.getWidth(groupName);
                int groupNameEndX = groupTextX + groupNameWidth;

                // 徽章位置：组名后方至少 20 像素，或从右边 10 像素处（取较左的位置）
                int badgeXFromGroupName = groupNameEndX + 20;
                int badgeXFromRight = x + entryWidth - badgeWidth - 10;
                int badgeX = Math.min(badgeXFromGroupName, badgeXFromRight);

                // 确保徽章不会超出右边界
                badgeX = Math.min(badgeX, x + entryWidth - badgeWidth - 10);

                // 徽章背景（圆角矩形效果）
                context.fill(badgeX, y + 4, badgeX + badgeWidth, y + 14, 0x80A78BFA);

                // 徽章文字（居中）
                int textX = badgeX + (badgeWidth - client.textRenderer.getWidth(badge)) / 2;
                context.drawText(client.textRenderer, badge, textX, y + 6, 0xFFFFFF, false);
            }
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (button == 0) {
                expandedGroups.put(group, !expanded);
                refreshEntries();
                return true;
            }
            return false;
        }

        @Override
        public Text getNarration() {
            return Text.literal(group);
        }
    }

    // 字母组头条目
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
                context.fill(x, y, x + entryWidth, y + entryHeight, 0x20FFFFFF);
            }

            String arrow = expanded ? "▼" : "▶";
            context.drawText(client.textRenderer, arrow, x + 5, y + 5, 0x888888, false);
            context.drawText(client.textRenderer, letter, x + 20, y + 5, 0xFFFFFF, false);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (button == 0) {
                expandedGroups.put(letter, !expanded);
                refreshEntries();
                return true;
            }
            return false;
        }

        @Override
        public Text getNarration() {
            return Text.literal(letter);
        }
    }

    // 组件条目
    public class ComponentItemEntry extends ComponentEntry {
        private final ComponentRegistry.ComponentDef component;
        private final boolean modified;

        public ComponentItemEntry(ComponentRegistry.ComponentDef component, boolean modified) {
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
                context.fill(x, y, x + 3, y + entryHeight, 0xFFA78BFA);
            }

            String displayId = component.id();
            String path = displayId.replace("minecraft:", "");
            String nameKey = "component.minecraft." + path;
            String descKey = nameKey + ".desc";
            String localizedDesc = Text.translatable(descKey).getString();
            if (localizedDesc.equals(descKey)) {
                localizedDesc = Text.translatable(nameKey).getString();
            }
            if (localizedDesc.equals(nameKey)) {
                localizedDesc = path.replace("_", " ");
            }

            int textWidth = entryWidth - 16;
            context.drawTextWithShadow(client.textRenderer,
                Text.literal(client.textRenderer.trimToWidth(displayId, textWidth)),
                x + 8, y + 2, 0xA78BFA);
            context.drawText(client.textRenderer,
                Text.literal(client.textRenderer.trimToWidth(localizedDesc, textWidth)),
                x + 8, y + 13, 0x888888, false);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (button == 0) {
                ComponentListWidget.this.setSelected(this);
                if (callback != null) {
                    callback.onComponentSelected(component);
                }
                return true;
            }
            return false;
        }

        @Override
        public Text getNarration() {
            return Text.literal(component.id());
        }

        public ComponentRegistry.ComponentDef getComponent() {
            return component;
        }
    }

    public static abstract class ComponentEntry extends Entry<ComponentEntry> {
    }

    @FunctionalInterface
    public interface ComponentSelectionCallback {
        void onComponentSelected(ComponentRegistry.ComponentDef component);
    }
}
