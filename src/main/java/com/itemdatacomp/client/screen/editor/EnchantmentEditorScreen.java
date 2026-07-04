package com.itemdatacomp.client.screen.editor;

import com.itemdatacomp.client.screen.ItemDataScreen;
import com.itemdatacomp.client.widget.AutoCompleteTextFieldWidget;
import com.itemdatacomp.client.widget.suggestion.EnchantmentSuggestionProvider;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Enchantment editor with vanilla suggestions and unrestricted custom IDs.
 */
public class EnchantmentEditorScreen extends ItemDataScreen {
    private static final int ROW_HEIGHT = 26;

    private static final String[] COMMON_ENCHANTMENTS = {
        "minecraft:sharpness",
        "minecraft:smite",
        "minecraft:bane_of_arthropods",
        "minecraft:knockback",
        "minecraft:fire_aspect",
        "minecraft:sweeping_edge",
        "minecraft:looting",
        "minecraft:unbreaking",
        "minecraft:mending",
        "minecraft:efficiency",
        "minecraft:silk_touch",
        "minecraft:fortune",
        "minecraft:power",
        "minecraft:punch",
        "minecraft:flame",
        "minecraft:infinity",
        "minecraft:protection",
        "minecraft:fire_protection",
        "minecraft:blast_protection",
        "minecraft:projectile_protection",
        "minecraft:feather_falling",
        "minecraft:respiration",
        "minecraft:aqua_affinity",
        "minecraft:depth_strider",
        "minecraft:frost_walker",
        "minecraft:soul_speed",
        "minecraft:swift_sneak",
        "minecraft:thorns",
        "minecraft:loyalty",
        "minecraft:channeling",
        "minecraft:riptide",
        "minecraft:impaling",
        "minecraft:multishot",
        "minecraft:quick_charge",
        "minecraft:piercing",
        "minecraft:luck_of_the_sea",
        "minecraft:lure",
        "minecraft:density",
        "minecraft:breach",
        "minecraft:wind_burst",
        "minecraft:vanishing_curse",
        "minecraft:binding_curse"
    };

    private final Screen parent;
    private final Map<String, Integer> enchantments;
    private final Map<String, String> aliases;
    private final Consumer<EnchantmentEditResult> onSave;
    private final Map<String, TextFieldWidget> levelFields = new LinkedHashMap<>();

    private AutoCompleteTextFieldWidget enchantmentIdField;
    private TextFieldWidget aliasField;
    private TextFieldWidget newLevelField;
    private int scrollOffset = 0;

    public EnchantmentEditorScreen(Screen parent, Map<String, Integer> currentEnchantments, Consumer<Map<String, Integer>> onSave) {
        this(parent, currentEnchantments, Collections.emptyMap(), result -> onSave.accept(result.levels()));
    }

    public EnchantmentEditorScreen(Screen parent, Map<String, Integer> currentEnchantments, Map<String, String> currentAliases, Consumer<EnchantmentEditResult> onSave) {
        super(Text.translatable("editor.minecraft.enchantments"));
        this.parent = parent;
        this.enchantments = currentEnchantments != null ? new LinkedHashMap<>(currentEnchantments) : new LinkedHashMap<>();
        this.aliases = currentAliases != null ? new LinkedHashMap<>(currentAliases) : new LinkedHashMap<>();
        this.onSave = onSave;
    }

    @Override
    protected void init() {
        super.init();
        levelFields.clear();

        int centerX = this.width / 2;
        int formY = getFormY();
        int idX = centerX - 520;
        int aliasX = centerX - 145;
        int levelX = centerX + 130;
        int addX = centerX + 225;

        this.enchantmentIdField = new AutoCompleteTextFieldWidget(
            this.client,
            idX,
            formY,
            360,
            20,
            Text.translatable("gui.itemdatacomp.enchantment.id"),
            EnchantmentSuggestionProvider::getSuggestions
        );
        this.enchantmentIdField.setMaxLength(128);
        this.addSelectableChild(this.enchantmentIdField);

        this.aliasField = new TextFieldWidget(
            this.textRenderer,
            aliasX,
            formY,
            260,
            20,
            Text.literal("")
        );
        this.aliasField.setMaxLength(128);
        this.aliasField.setPlaceholder(Text.literal("可选"));
        this.addSelectableChild(this.aliasField);

        this.newLevelField = new TextFieldWidget(
            this.textRenderer,
            levelX,
            formY,
            80,
            20,
            Text.translatable("gui.itemdatacomp.enchantment.level")
        );
        this.newLevelField.setText("1");
        this.addSelectableChild(this.newLevelField);

        this.addDrawableChild(ButtonWidget.builder(
            Text.translatable("gui.itemdatacomp.enchantment.add"),
            button -> addTypedEnchantment()
        ).dimensions(addX, formY, 100, 20).build());

        int listY = getListY();
        int visibleRows = getVisibleRows();
        List<String> displayEnchantments = getDisplayEnchantments();

        for (int i = scrollOffset; i < Math.min(scrollOffset + visibleRows, displayEnchantments.size()); i++) {
            String id = displayEnchantments.get(i);
            int y = listY + (i - scrollOffset) * ROW_HEIGHT;
            TextFieldWidget field = new TextFieldWidget(
                this.textRenderer,
                getLevelFieldX(),
                y + 2,
                90,
                20,
                Text.literal("")
            );
            field.setText(String.valueOf(enchantments.getOrDefault(id, 0)));
            levelFields.put(id, field);
            this.addSelectableChild(field);

            if (enchantments.containsKey(id)) {
                this.addDrawableChild(ButtonWidget.builder(
                    Text.literal("X"),
                    button -> removeEnchantment(id)
                ).dimensions(getRemoveButtonX(), y + 2, 22, 20).build());
            }
        }

        this.addDrawableChild(ButtonWidget.builder(
            Text.translatable("gui.itemdatacomp.enchantment.clear"),
            button -> {
                enchantments.clear();
                aliases.clear();
                scrollOffset = 0;
                this.clearAndInit();
            }
        ).dimensions(centerX - 100, this.height - 70, 200, 20).build());

        this.addDrawableChild(ButtonWidget.builder(
            Text.translatable("gui.done"),
            button -> save()
        ).dimensions(centerX - 155, this.height - 40, 150, 20).build());

        this.addDrawableChild(ButtonWidget.builder(
            Text.translatable("gui.cancel"),
            button -> this.client.setScreen(parent)
        ).dimensions(centerX + 5, this.height - 40, 150, 20).build());
    }

    private int getFormY() {
        return 72;
    }

    private int getListY() {
        return 150;
    }

    private int getListLeft() {
        return this.width / 2 - 520;
    }

    private int getListRight() {
        return this.width / 2 + 430;
    }

    private int getLevelLabelX() {
        return this.width / 2 + 120;
    }

    private int getLevelFieldX() {
        return this.width / 2 + 190;
    }

    private int getRemoveButtonX() {
        return this.width / 2 + 295;
    }

    private int getVisibleRows() {
        return Math.max(1, (this.height - getListY() - 95) / ROW_HEIGHT);
    }

    private List<String> getDisplayEnchantments() {
        List<String> result = new ArrayList<>(enchantments.keySet());
        for (String id : COMMON_ENCHANTMENTS) {
            if (!result.contains(id)) {
                result.add(id);
            }
        }
        return result;
    }

    private void addTypedEnchantment() {
        syncVisibleLevelFields();
        String id = resolveTypedEnchantmentId();
        if (id.isEmpty()) {
            return;
        }

        Integer level = parsePositiveLevel(newLevelField.getText());
        if (level != null) {
            enchantments.put(id, level);
            String alias = aliasField.getText().trim();
            if (!alias.isEmpty()) {
                aliases.put(id, alias);
            } else {
                aliases.remove(id);
            }
            enchantmentIdField.setText("");
            aliasField.setText("");
            newLevelField.setText("1");
            scrollOffset = 0;
            this.clearAndInit();
        }
    }

    private void removeEnchantment(String id) {
        syncVisibleLevelFields();
        enchantments.remove(id);
        aliases.remove(id);
        scrollOffset = Math.min(scrollOffset, Math.max(0, getDisplayEnchantments().size() - getVisibleRows()));
        this.clearAndInit();
    }

    private void save() {
        syncVisibleLevelFields();
        addTypedEnchantmentIfPresent();
        aliases.keySet().removeIf(id -> !enchantments.containsKey(id));
        onSave.accept(new EnchantmentEditResult(new LinkedHashMap<>(enchantments), new LinkedHashMap<>(aliases)));
        this.client.setScreen(parent);
    }

    private void addTypedEnchantmentIfPresent() {
        String id = resolveTypedEnchantmentId();
        if (id.isEmpty()) {
            return;
        }

        Integer level = parsePositiveLevel(newLevelField.getText());
        if (level != null) {
            enchantments.put(id, level);
            String alias = aliasField.getText().trim();
            if (!alias.isEmpty()) {
                aliases.put(id, alias);
            }
        }
    }

    private String resolveTypedEnchantmentId() {
        String text = enchantmentIdField.getText();
        String firstSuggestion = enchantmentIdField.getFirstSuggestionOrNull();
        String normalized = text == null ? "" : text.trim().toLowerCase();
        if (firstSuggestion != null && !normalized.equals(firstSuggestion.toLowerCase())) {
            return firstSuggestion;
        }
        return EnchantmentSuggestionProvider.resolveToId(text);
    }

    private void syncVisibleLevelFields() {
        for (Map.Entry<String, TextFieldWidget> entry : levelFields.entrySet()) {
            String id = entry.getKey();
            Integer level = parseLevel(entry.getValue().getText());
            if (level == null) {
                continue;
            }
            if (level > 0) {
                enchantments.put(id, level);
            } else {
                enchantments.remove(id);
            }
        }
    }

    private static Integer parsePositiveLevel(String raw) {
        Integer level = parseLevel(raw);
        return level != null && level > 0 ? level : null;
    }

    private static Integer parseLevel(String raw) {
        try {
            return Integer.parseInt(raw.trim());
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);

        int centerX = this.width / 2;
        int formY = getFormY();
        int idX = centerX - 520;
        int aliasX = centerX - 145;
        int levelX = centerX + 130;

        context.drawCenteredTextWithShadow(this.textRenderer, this.title, centerX, 20, 0xFFFFFF);
        context.drawCenteredTextWithShadow(this.textRenderer,
            Text.translatable("gui.itemdatacomp.enchantment.hint"),
            centerX, 40, 0x888888);

        context.drawTextWithShadow(this.textRenderer,
            Text.translatable("gui.itemdatacomp.enchantment.id"),
            idX, formY - 13, 0xA78BFA);
        context.drawTextWithShadow(this.textRenderer,
            Text.translatable("gui.itemdatacomp.enchantment.level"),
            levelX, formY - 13, 0xA78BFA);
        context.drawTextWithShadow(this.textRenderer,
            Text.literal("显示名"),
            aliasX, formY - 13, 0xA78BFA);

        enchantmentIdField.render(context, mouseX, mouseY, delta);
        aliasField.render(context, mouseX, mouseY, delta);
        newLevelField.render(context, mouseX, mouseY, delta);

        int listY = getListY();
        context.drawTextWithShadow(this.textRenderer,
            Text.translatable("gui.itemdatacomp.enchantment.list"),
            getListLeft(), listY - 15, 0xA78BFA);

        List<String> displayEnchantments = getDisplayEnchantments();
        int visibleRows = getVisibleRows();
        for (int i = scrollOffset; i < Math.min(scrollOffset + visibleRows, displayEnchantments.size()); i++) {
            String id = displayEnchantments.get(i);
            int y = listY + (i - scrollOffset) * ROW_HEIGHT;
            boolean active = enchantments.containsKey(id);
            int bg = active ? 0x402A8A4A : 0x30000000;
            context.fill(getListLeft(), y, getListRight(), y + ROW_HEIGHT - 2, bg);

            String subtitle = getLocalizedEnchantmentName(id);
            String alias = aliases.get(id);
            if (alias != null && !alias.isBlank()) {
                subtitle = alias + " (" + subtitle + ")";
            }
            int titleColor = active ? 0xA7F3D0 : 0xA78BFA;
            context.drawTextWithShadow(this.textRenderer,
                Text.literal(this.textRenderer.trimToWidth(id, getLevelLabelX() - getListLeft() - 16)),
                getListLeft() + 10, y + 2, titleColor);
            context.drawText(this.textRenderer,
                Text.literal(this.textRenderer.trimToWidth(subtitle, getLevelLabelX() - getListLeft() - 16)),
                getListLeft() + 10, y + 14, 0x888888, false);

            context.drawTextWithShadow(this.textRenderer,
                Text.translatable("gui.itemdatacomp.enchantment.level.short"),
                getLevelLabelX(), y + 7, 0x888888);
        }

        for (TextFieldWidget field : levelFields.values()) {
            field.render(context, mouseX, mouseY, delta);
        }

        super.render(context, mouseX, mouseY, delta);
        enchantmentIdField.renderCustom(context, mouseX, mouseY, delta);
    }

    private String getLocalizedEnchantmentName(String id) {
        String key = "enchantment." + id.replace(":", ".");
        String translated = Text.translatable(key).getString();
        if (!translated.equals(key)) {
            return translated;
        }
        return Text.translatable("gui.itemdatacomp.enchantment.custom").getString();
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (enchantmentIdField != null && enchantmentIdField.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount)) {
            return true;
        }
        syncVisibleLevelFields();
        int maxScroll = Math.max(0, getDisplayEnchantments().size() - getVisibleRows());
        scrollOffset = Math.max(0, Math.min(maxScroll, scrollOffset - (int) verticalAmount));
        this.clearAndInit();
        return true;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (enchantmentIdField != null && enchantmentIdField.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    public record EnchantmentEditResult(Map<String, Integer> levels, Map<String, String> aliases) {
    }
}
