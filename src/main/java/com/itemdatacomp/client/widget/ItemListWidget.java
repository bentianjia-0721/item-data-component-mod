package com.itemdatacomp.client.widget;

import com.itemdatacomp.client.data.ItemRegistry;
import com.itemdatacomp.client.util.PinyinMatcher;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.text.Text;
import java.util.ArrayList;
import java.util.List;

/**
 * 物品列表Widget
 * 左侧可滚动的物品选择列表
 */
public class ItemListWidget extends AlwaysSelectedEntryListWidget<ItemListWidget.ItemEntry> {

    private final List<ItemRegistry.ItemData> filteredItems;
    private String searchQuery = "";
    private String categoryFilter = "all";
    private ItemSelectionCallback callback;

    public ItemListWidget(MinecraftClient client, int width, int height, int top, int itemHeight) {
        super(client, width, height, top, itemHeight);
        this.filteredItems = new ArrayList<>(ItemRegistry.getAllItems());
        refreshEntries();
    }

    public void setSearchQuery(String query) {
        this.searchQuery = query;
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

        List<ItemRegistry.ItemData> items = ItemRegistry.searchItems(searchQuery,
            categoryFilter.equals("all") ? null : categoryFilter);

        filteredItems.addAll(items);

        for (ItemRegistry.ItemData item : filteredItems) {
            this.addEntry(new ItemEntry(item));
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

    public class ItemEntry extends Entry<ItemEntry> {
        private final ItemRegistry.ItemData item;

        public ItemEntry(ItemRegistry.ItemData item) {
            this.item = item;
        }

        @Override
        public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight,
                          int mouseX, int mouseY, boolean hovered, float tickDelta) {

            MinecraftClient client = MinecraftClient.getInstance();

            // 背景高亮
            if (hovered || this == getSelectedOrNull()) {
                context.fill(x, y, x + entryWidth, y + entryHeight, 0x40FFFFFF);
            }

            // 物品ID（去除 minecraft: 前缀）
            String displayId = item.id().replace("minecraft:", "");
            context.drawText(client.textRenderer, displayId, x + 5, y + 3, 0xA78BFA, true);

            // 中文名（紧贴ID下方）
            context.drawTextWithShadow(client.textRenderer, "(" + item.zhName() + ")",
                x + 5, y + 13, 0x888888);
        }

        @Override
        public Text getNarration() {
            return Text.literal(item.id() + " " + item.zhName());
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (button == 0) {
                ItemListWidget.this.setSelected(this);
                if (callback != null) {
                    callback.onItemSelected(item);
                }
                return true;
            }
            return false;
        }

        public ItemRegistry.ItemData getItem() {
            return item;
        }
    }

    @FunctionalInterface
    public interface ItemSelectionCallback {
        void onItemSelected(ItemRegistry.ItemData item);
    }
}
