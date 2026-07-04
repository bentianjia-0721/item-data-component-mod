package com.itemdatacomp.client.screen;

import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Keeps vanilla widgets clickable while letting screens draw them manually.
 * Screen.render() draws renderBackground() first in Minecraft 1.21.4, so
 * calling it after custom UI text blurs/darkens that text.
 */
public abstract class ItemDataScreen extends Screen {
    private final List<Drawable> registeredDrawables = new ArrayList<>();

    protected ItemDataScreen(Text title) {
        super(title);
    }

    @Override
    protected <T extends Element & Drawable & Selectable> T addDrawableChild(T drawableElement) {
        this.registeredDrawables.add(drawableElement);
        return this.addSelectableChild(drawableElement);
    }

    @Override
    protected <T extends Drawable> T addDrawable(T drawable) {
        this.registeredDrawables.add(drawable);
        return drawable;
    }

    @Override
    protected void clearChildren() {
        super.clearChildren();
        this.registeredDrawables.clear();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderRegisteredDrawables(context, mouseX, mouseY, delta);
    }

    protected void renderRegisteredDrawables(DrawContext context, int mouseX, int mouseY, float delta) {
        for (Drawable drawable : this.registeredDrawables) {
            drawable.render(context, mouseX, mouseY, delta);
        }
    }
}
