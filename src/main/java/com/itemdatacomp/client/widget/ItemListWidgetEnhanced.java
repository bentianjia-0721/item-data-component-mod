package com.itemdatacomp.client.widget;

import com.itemdatacomp.client.util.PinyinMatcher;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.text.Text;
import java.util.ArrayList;
import java.util.List;

/**
 * 增强的物品列表Widget - 已优化版本
 * 左侧可滚动的物品选择列表，支持拼音模糊搜索和分类过滤
 * 视觉改进：颜色编码、修改指示、流畅动画
 */
public class ItemListWidgetEnhanced extends AlwaysSelectedEntryListWidget<ItemListWidgetEnhanced.ItemEntry> {

    private final List<ItemData> allItems;
    private final List<ItemData> filteredItems;
    private String searchQuery = "";
    private String categoryFilter = "all";
    private ItemSelectionCallback callback;
    private long lastSearchTime = 0;

    // 缓存用于性能优化
    private List<PinyinMatcher.SearchResult<ItemData>> cachedResults;

    public ItemListWidgetEnhanced(MinecraftClient client, int width, int height, int top, int itemHeight,
                         List<ItemData> items) {
        super(client, width, height, top, itemHeight);
        this.allItems = new ArrayList<>(items);
        this.filteredItems = new ArrayList<>(items);
        refreshEntries();
    }

    public void setSearchQuery(String query) {
        this.searchQuery = query;
        this.lastSearchTime = System.currentTimeMillis();
        refreshEntries();
    }

    public void setCategoryFilter(String category) {
        this.categoryFilter = category;
        refreshEntries();
    }

    public void setSelectionCallback(ItemSelectionCallback callback) {
        this.callback = callback;
    }

    private void refreshEntries() {
        this.clearEntries();
        filteredItems.clear();

        // 分类过滤
        List<ItemData> items = new ArrayList<>(allItems);
        if (!categoryFilter.equals("all")) {
            items = items.stream()
                .filter(i -> i.category.equals(categoryFilter))
                .toList();
        }

        // 搜索过滤（支持拼音模糊搜索）
        if (!searchQuery.isEmpty()) {
            items = PinyinMatcher.search(
                searchQuery,
                items,
                ItemData::getId,
                ItemData::getZhName,
                items.size()
            ).stream()
                .map(result -> result.item)
                .toList();
        }

        filteredItems.addAll(items);

        for (ItemData item : filteredItems) {
            this.addEntry(new ItemEntry(item));
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

    public class ItemEntry extends Entry<ItemEntry> {
        private final ItemData item;
        private long hoverStartTime = 0;
        private boolean isHovered = false;

        public ItemEntry(ItemData item) {
            this.item = item;
        }

        @Override
        public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight,
                          int mouseX, int mouseY, boolean hovered, float tickDelta) {

            MinecraftClient client = MinecraftClient.getInstance();
            boolean isSelected = this == getSelectedOrNull();

            // 计算背景色动画（平滑过渡）
            if (hovered != isHovered) {
                isHovered = hovered;
                hoverStartTime = System.currentTimeMillis();
            }

            long hoverDuration = System.currentTimeMillis() - hoverStartTime;
            float hoverAlpha = Math.min(1.0f, hoverDuration / 150.0f);

            // 背景高亮（渐进式）
            if (hovered || isSelected) {
                int bgColor = isSelected ? 0x60A78BFA : (int)(0x40A78BFA * hoverAlpha + 0x20FFFFFF * (1 - hoverAlpha));
                context.fill(x, y, x + entryWidth, y + entryHeight, bgColor);
            }

            // 修改指示线（左侧竖线）
            if (item.isModified) {
                context.fill(x, y, x + 2, y + entryHeight, 0xFFFF6B6B);
            }

            // 选中指示线
            if (isSelected) {
                context.fill(x, y, x + 3, y + entryHeight, 0xFFA78BFA);
            }

            // 物品ID（紫色）
            int idColor = hovered ? 0xC0A8FF : 0xA78BFA;
            String displayId = item.id.replace("minecraft:", "");
            context.drawTextWithShadow(client.textRenderer, Text.literal(displayId), x + 8, y + 4, idColor);

            // 中文名（灰色，紧贴ID下方）
            String displayName = "(" + item.zhName + ")";
            context.drawTextWithShadow(client.textRenderer, Text.literal(displayName), x + 8, y + 14, 0x888888);

            // 搜索匹配度指示（如果有搜索）
            if (!searchQuery.isEmpty()) {
                double relevance = PinyinMatcher.scoreMatch(searchQuery, item.id, item.zhName,
                    PinyinMatcher.getPinyinInitials(item.zhName));
                int relevanceColor = getRelevanceColor(relevance);
                context.fill(x + entryWidth - 8, y + 2, x + entryWidth - 2, y + 8, relevanceColor);
            }
        }

        private String getCategoryTag(String category) {
            return switch (category) {
                case "tool" -> "[工具]";
                case "weapon" -> "[武器]";
                case "armor" -> "[盔甲]";
                case "food" -> "[食物]";
                case "block" -> "[方块]";
                default -> "[其他]";
            };
        }

        private int getCategoryColor(String category) {
            return switch (category) {
                case "tool" -> 0xFFB366FF;  // 蓝紫
                case "weapon" -> 0xFFFF6B6B; // 红
                case "armor" -> 0xFF6BA3FF;  // 蓝
                case "food" -> 0xFFFFB366;   // 橙
                case "block" -> 0xFF66FF66;  // 绿
                default -> 0xFF888888;
            };
        }

        private int getRelevanceColor(double score) {
            if (score >= 80) return 0xFFFF6B6B; // 红 - 极佳
            if (score >= 60) return 0xFFFFB366; // 橙 - 良好
            if (score >= 40) return 0xFF66FF66; // 绿 - 一般
            return 0xFF888888; // 灰 - 弱
        }

        @Override
        public Text getNarration() {
            return Text.literal(item.id + " " + item.zhName);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (button == 0) {
                ItemListWidgetEnhanced.this.setSelected(this);
                if (callback != null) {
                    callback.onItemSelected(item);
                }
                return true;
            }
            return false;
        }

        public ItemData getItem() {
            return item;
        }
    }

    // 简化的物品数据类
    public static class ItemData {
        public final String id;
        public final String zhName;
        public final String category;
        public boolean isModified = false;

        public ItemData(String id, String zhName, String category) {
            this.id = id;
            this.zhName = zhName;
            this.category = category;
        }

        public String getId() { return id; }
        public String getZhName() { return zhName; }
    }

    @FunctionalInterface
    public interface ItemSelectionCallback {
        void onItemSelected(ItemData item);
    }
}
