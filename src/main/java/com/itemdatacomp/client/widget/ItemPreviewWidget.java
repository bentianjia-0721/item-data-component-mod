package com.itemdatacomp.client.widget;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.component.type.EnchantableComponent;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.component.type.UnbreakableComponent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.Unit;
import java.util.*;

/**
 * 增强的物品预览 Widget
 * 支持：附魔罗马数字、颜色代码预览、属性修饰符可视化、动画效果
 */
public class ItemPreviewWidget implements Drawable {

    private int x, y, width, height;
    private ItemStack itemStack = ItemStack.EMPTY;
    private Map<String, Object> componentData = new HashMap<>();
    private long lastUpdateTime = 0;
    private float animationProgress = 0.0f;
    private static final int ANIMATION_DURATION = 300; // ms

    public ItemPreviewWidget(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void setItem(ItemStack stack) {
        this.itemStack = stack;
        this.lastUpdateTime = System.currentTimeMillis();
    }

    public void updateComponent(String componentId, Object value) {
        if (value != null) {
            this.componentData.put(componentId, value);
        } else {
            this.componentData.remove(componentId);
        }
        triggerAnimation();
    }

    public void removeComponent(String componentId) {
        this.componentData.remove(componentId);
        triggerAnimation();
    }

    public void setComponentData(Map<String, Object> data) {
        this.componentData = new HashMap<>(data);
        triggerAnimation();
    }

    private void triggerAnimation() {
        this.lastUpdateTime = System.currentTimeMillis();
        this.animationProgress = 0.0f;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fill(x, y, x + width, y + height, 0xFF1A1A1A);
        context.fill(x, y, x + width, y + 20, 0xFF0A0A0A);

        MinecraftClient client = MinecraftClient.getInstance();
        context.drawTextWithShadow(client.textRenderer, "实时预览", x + 8, y + 6, 0xA78BFA);

        if (itemStack.isEmpty()) {
            context.drawTextWithShadow(client.textRenderer, "未选择物品", x + 8, y + 40, 0x888888);
            return;
        }

        ItemStack previewStack = buildPreviewStack();
        int iconX = x + 10;
        int iconY = y + 32;
        context.drawItem(previewStack, iconX, iconY);
        context.drawStackOverlay(client.textRenderer, previewStack, iconX, iconY);

        int textX = iconX + 24;
        int textY = y + 28;
        List<Text> tooltip = previewStack.getTooltip(
            client.world != null ? Item.TooltipContext.create(client.world) : Item.TooltipContext.DEFAULT,
            client.player,
            TooltipType.BASIC
        );

        if (tooltip.isEmpty()) {
            context.drawTextWithShadow(client.textRenderer, "提示框已隐藏", textX, textY + 5, 0x888888);
        } else {
            int maxRows = Math.max(1, (height - 34) / 11);
            for (int i = 0; i < Math.min(maxRows, tooltip.size()); i++) {
                context.drawTextWithShadow(client.textRenderer,
                    client.textRenderer.trimToWidth(tooltip.get(i).getString(), width - 42),
                    textX, textY + i * 11, 0xFFFFFF);
            }
        }

        boolean hoveringIcon = mouseX >= iconX && mouseX < iconX + 16 && mouseY >= iconY && mouseY < iconY + 16;
        if (hoveringIcon) {
            context.drawItemTooltip(client.textRenderer, previewStack, mouseX, mouseY);
        }
    }

    private ItemStack buildPreviewStack() {
        ItemStack previewStack = itemStack.copy();

        for (Map.Entry<String, Object> entry : componentData.entrySet()) {
            applyPreviewComponent(previewStack, entry.getKey(), entry.getValue());
        }
        applyGeneratedEnchantmentAliasLore(previewStack);

        return previewStack;
    }

    private void applyPreviewComponent(ItemStack stack, String componentId, Object value) {
        if (value == null) {
            return;
        }

        try {
            switch (componentId) {
                case "minecraft:custom_name" -> stack.set(DataComponentTypes.CUSTOM_NAME, toText(value));
                case "minecraft:item_name" -> stack.set(DataComponentTypes.ITEM_NAME, toText(value));
                case "minecraft:lore" -> stack.set(DataComponentTypes.LORE, toLoreComponent(value));
                case "minecraft:enchantments" -> stack.set(DataComponentTypes.ENCHANTMENTS, toEnchantmentsComponent(value));
                case "minecraft:stored_enchantments" -> stack.set(DataComponentTypes.STORED_ENCHANTMENTS, toEnchantmentsComponent(value));
                case "minecraft:enchantment_glint_override" -> stack.set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, toBoolean(value));
                case "minecraft:hide_tooltip" -> stack.set(DataComponentTypes.HIDE_TOOLTIP, Unit.INSTANCE);
                case "minecraft:hide_additional_tooltip" -> stack.set(DataComponentTypes.HIDE_ADDITIONAL_TOOLTIP, Unit.INSTANCE);
                case "minecraft:intangible_projectile" -> stack.set(DataComponentTypes.INTANGIBLE_PROJECTILE, Unit.INSTANCE);
                case "minecraft:glider" -> stack.set(DataComponentTypes.GLIDER, Unit.INSTANCE);
                case "minecraft:unbreakable" -> stack.set(DataComponentTypes.UNBREAKABLE, new UnbreakableComponent(true));
                case "minecraft:dyed_color" -> stack.set(DataComponentTypes.DYED_COLOR, new DyedColorComponent(toInt(value), true));
                case "minecraft:damage" -> stack.set(DataComponentTypes.DAMAGE, Math.max(0, toInt(value)));
                case "minecraft:max_damage" -> stack.set(DataComponentTypes.MAX_DAMAGE, Math.max(1, toInt(value)));
                case "minecraft:max_stack_size" -> stack.set(DataComponentTypes.MAX_STACK_SIZE, Math.max(1, toInt(value)));
                case "minecraft:repair_cost" -> stack.set(DataComponentTypes.REPAIR_COST, Math.max(0, toInt(value)));
                case "minecraft:enchantable" -> stack.set(DataComponentTypes.ENCHANTABLE, new EnchantableComponent(Math.max(1, toInt(value))));
                case "minecraft:item_model" -> stack.set(DataComponentTypes.ITEM_MODEL, Identifier.of(String.valueOf(value).trim()));
                case "minecraft:tooltip_style" -> stack.set(DataComponentTypes.TOOLTIP_STYLE, Identifier.of(String.valueOf(value).trim()));
                case "minecraft:rarity" -> stack.set(DataComponentTypes.RARITY, toRarity(value));
                default -> {
                }
            }
        } catch (Exception ignored) {
            // Preview should never break the whole screen if one component is malformed.
        }
    }

    private Text toText(Object value) {
        String raw = String.valueOf(value).trim();
        if (raw.startsWith("{") || raw.startsWith("[") || raw.startsWith("\"")) {
            try {
                RegistryWrapper.WrapperLookup lookup = getRegistryLookup();
                if (lookup != null) {
                    Text parsed = Text.Serialization.fromJson(raw, lookup);
                    if (parsed != null) {
                        return parsed;
                    }
                }
            } catch (Exception ignored) {
            }
        }

        String plainText = extractTextComponent(value);
        return Text.literal(plainText != null ? plainText : raw);
    }

    private LoreComponent toLoreComponent(Object value) {
        List<Text> lines = new ArrayList<>();
        if (value instanceof List<?> list) {
            for (Object line : list) {
                lines.add(toText(line));
            }
        } else {
            lines.add(toText(value));
        }
        return new LoreComponent(lines);
    }

    private ItemEnchantmentsComponent toEnchantmentsComponent(Object value) {
        ItemEnchantmentsComponent.Builder builder = new ItemEnchantmentsComponent.Builder(ItemEnchantmentsComponent.DEFAULT);
        Map<String, Integer> levels = getEnchantmentLevels(value);
        RegistryWrapper.Impl<Enchantment> enchantmentLookup = getEnchantmentLookup();
        if (enchantmentLookup == null) {
            return builder.build();
        }

        for (Map.Entry<String, Integer> entry : levels.entrySet()) {
            Identifier id = Identifier.tryParse(entry.getKey().contains(":") ? entry.getKey() : "minecraft:" + entry.getKey());
            if (id == null) {
                continue;
            }
            RegistryKey<Enchantment> key = RegistryKey.of(RegistryKeys.ENCHANTMENT, id);
            Optional<RegistryEntry.Reference<Enchantment>> enchantment = enchantmentLookup.getOptional(key);
            enchantment.ifPresent(registryEntry -> builder.set(registryEntry, entry.getValue()));
        }

        ItemEnchantmentsComponent component = builder.build();
        if (hasAliases(value)) {
            return component.withShowInTooltip(false);
        }
        return component;
    }

    private void applyGeneratedEnchantmentAliasLore(ItemStack stack) {
        List<Text> generatedLore = new ArrayList<>();
        collectAliasLore(componentData.get("minecraft:enchantments"), generatedLore);
        collectAliasLore(componentData.get("minecraft:stored_enchantments"), generatedLore);
        if (generatedLore.isEmpty()) {
            return;
        }

        LoreComponent existingLore = stack.get(DataComponentTypes.LORE);
        if (existingLore != null) {
            generatedLore.addAll(existingLore.lines());
        }
        stack.set(DataComponentTypes.LORE, new LoreComponent(generatedLore));
    }

    private void collectAliasLore(Object value, List<Text> generatedLore) {
        if (!(value instanceof Map<?, ?> map) || !hasAliases(value)) {
            return;
        }

        Object levels = map.containsKey("levels") ? map.get("levels") : map;
        Object aliases = map.get("__aliases");
        if (!(levels instanceof Map<?, ?> levelMap) || !(aliases instanceof Map<?, ?> aliasMap)) {
            return;
        }

        for (Map.Entry<?, ?> entry : levelMap.entrySet()) {
            String id = String.valueOf(entry.getKey());
            Object alias = aliasMap.get(id);
            String displayName = alias != null ? String.valueOf(alias) : id.replace("minecraft:", "");
            generatedLore.add(Text.literal(displayName + " " + entry.getValue()));
        }
    }

    private boolean hasAliases(Object value) {
        if (!(value instanceof Map<?, ?> map)) {
            return false;
        }
        Object aliases = map.get("__aliases");
        return aliases instanceof Map<?, ?> aliasMap && !aliasMap.isEmpty();
    }

    private Map<String, Integer> getEnchantmentLevels(Object value) {
        Map<String, Integer> result = new LinkedHashMap<>();
        if (!(value instanceof Map<?, ?> map)) {
            return result;
        }

        Object levels = map.containsKey("levels") ? map.get("levels") : map;
        if (!(levels instanceof Map<?, ?> levelMap)) {
            return result;
        }

        for (Map.Entry<?, ?> entry : levelMap.entrySet()) {
            Integer level = toNullableInt(entry.getValue());
            if (level != null && level > 0) {
                result.put(String.valueOf(entry.getKey()), level);
            }
        }
        return result;
    }

    private RegistryWrapper.WrapperLookup getRegistryLookup() {
        MinecraftClient client = MinecraftClient.getInstance();
        return client.world != null ? client.world.getRegistryManager() : null;
    }

    private RegistryWrapper.Impl<Enchantment> getEnchantmentLookup() {
        RegistryWrapper.WrapperLookup lookup = getRegistryLookup();
        return lookup != null ? lookup.getOrThrow(RegistryKeys.ENCHANTMENT) : null;
    }

    private Boolean toBoolean(Object value) {
        if (value instanceof Boolean bool) {
            return bool;
        }
        return Boolean.parseBoolean(String.valueOf(value));
    }

    private int toInt(Object value) {
        Integer parsed = toNullableInt(value);
        return parsed != null ? parsed : 0;
    }

    private Integer toNullableInt(Object value) {
        if (value instanceof Number number) {
            return number.intValue();
        }
        try {
            return Integer.parseInt(String.valueOf(value).trim());
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    private Rarity toRarity(Object value) {
        String rarity = String.valueOf(value).trim().toUpperCase(Locale.ROOT);
        try {
            return Rarity.valueOf(rarity);
        } catch (IllegalArgumentException ignored) {
            return Rarity.COMMON;
        }
    }

    @SuppressWarnings("unchecked")
    private int renderEnchantments(DrawContext context, MinecraftClient client, String componentId, String label, int contentY) {
        Object raw = componentData.get(componentId);
        Map<String, Integer> enchantments = null;
        if (raw instanceof Map<?, ?> map) {
            Object levels = map.containsKey("levels") ? map.get("levels") : map;
            if (levels instanceof Map<?, ?> levelMap) {
                enchantments = new LinkedHashMap<>();
                for (Map.Entry<?, ?> entry : levelMap.entrySet()) {
                    if (entry.getValue() instanceof Number number) {
                        enchantments.put(String.valueOf(entry.getKey()), number.intValue());
                    }
                }
            }
        }

        if (enchantments == null || enchantments.isEmpty()) {
            return contentY;
        }

        context.drawTextWithShadow(client.textRenderer, label, x + 8, contentY, 0xA78BFA);
        contentY += 12;
        for (Map.Entry<String, Integer> entry : enchantments.entrySet()) {
            String enchName = entry.getKey().replace("minecraft:", "");
            String level = toRomanNumeral(entry.getValue());
            String text = enchName + " " + level;
            context.drawTextWithShadow(client.textRenderer,
                client.textRenderer.trimToWidth(text, width - 24),
                x + 16, contentY, 0xAAAA88);
            contentY += 11;
        }
        return contentY + 4;
    }

    private List<String> getOtherComponentSummaries() {
        List<String> result = new ArrayList<>();
        Set<String> alreadyShown = Set.of(
            "minecraft:custom_name",
            "minecraft:item_name",
            "minecraft:enchantments",
            "minecraft:stored_enchantments",
            "minecraft:dyed_color",
            "minecraft:attribute_modifiers"
        );

        for (String componentId : componentData.keySet()) {
            if (!alreadyShown.contains(componentId)) {
                result.add(componentId.replace("minecraft:", ""));
            }
        }
        Collections.sort(result);
        return result;
    }

    private String extractTextComponent(Object value) {
        if (value == null) {
            return null;
        }
        String text = String.valueOf(value).trim();
        int textIndex = text.indexOf("\"text\"");
        if (textIndex >= 0) {
            int colon = text.indexOf(':', textIndex);
            int firstQuote = colon >= 0 ? text.indexOf('"', colon + 1) : -1;
            int secondQuote = firstQuote >= 0 ? text.indexOf('"', firstQuote + 1) : -1;
            if (firstQuote >= 0 && secondQuote > firstQuote) {
                return text.substring(firstQuote + 1, secondQuote);
            }
        }
        return text;
    }

    private void renderColorPreview(DrawContext context, int x, int y, int color) {
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;

        // 色块 - 确保完整的Alpha通道
        int opaqueColor = (color & 0xFFFFFF) | 0xFF000000;
        context.fill(x, y, x + 12, y + 12, 0xFF000000);
        context.fill(x + 1, y + 1, x + 11, y + 11, opaqueColor);

        // RGB 文本
        MinecraftClient client = MinecraftClient.getInstance();
        String hexStr = String.format("#%06X", color & 0xFFFFFF);
        String rgbStr = String.format("RGB(%d, %d, %d)", r, g, b);
        context.drawTextWithShadow(client.textRenderer, hexStr, x + 16, y, 0xFFFFFF);
        context.drawTextWithShadow(client.textRenderer, rgbStr, x + 16, y + 10, 0xAAAA88);
    }

    private void renderAttributeModifier(DrawContext context, int x, int y, Map<String, Object> attr) {
        MinecraftClient client = MinecraftClient.getInstance();

        String attrType = attr.get("type") != null ? attr.get("type").toString() : "未知";
        Number amount = (Number) attr.get("amount");
        String slot = attr.get("slot") != null ? attr.get("slot").toString() : "通用";

        String prefix = amount != null && amount.doubleValue() > 0 ? "+" : "";
        String slotText = " @" + slot;
        String text = String.format("%s%s %s%s",
            prefix,
            amount != null ? amount : "0",
            attrType.replace("minecraft:", ""),
            slotText);

        int color = amount != null && amount.doubleValue() > 0 ? 0x4CAF50 : 0xF44336;
        context.drawTextWithShadow(client.textRenderer, text, x, y, color);
    }

    /**
     * 将数字转换为罗马数字
     * 1-3999: 标准罗马数字
     * 4000+: 使用 vinculum (上划线) 或数字形式
     */
    private String toRomanNumeral(int num) {
        if (num <= 0) return "0";
        if (num > 3999) return String.valueOf(num); // 超过3999显示为数字

        String[] romanValues = {"M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"};
        int[] intValues = {1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < intValues.length; i++) {
            while (num >= intValues[i]) {
                result.append(romanValues[i]);
                num -= intValues[i];
            }
        }
        return result.toString();
    }

    /**
     * Elastic easing out 动画函数
     * 产生弹簧效果
     */
    private float easeOutElastic(float t) {
        if (t <= 0) return 0;
        if (t >= 1) return 1;

        float c5 = (2 * (float) Math.PI) / 4.5f;
        return (float) (Math.pow(2, -10 * t) * Math.sin((t - 0.075f) * c5) + 1);
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
