package com.itemdatacomp.client;

import com.itemdatacomp.client.screen.ComponentEditorScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class ItemDataComponentClient implements ClientModInitializer {
    public static final String MOD_ID = "itemdatacomp";

    private static KeyBinding openEditorKey;

    @Override
    public void onInitializeClient() {
        // 注册快捷键 U+O (组合键，无默认按键绑定，使用自定义检测)
        openEditorKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.itemdatacomp.open_editor",
            InputUtil.Type.KEYSYM,
            InputUtil.UNKNOWN_KEY.getCode(),
            "category.itemdatacomp"
        ));

        // 注册客户端tick事件 - 检测U+O组合键
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.getWindow() != null) {
                long windowHandle = client.getWindow().getHandle();
                // 检测U键和O键同时按下
                if (GLFW.glfwGetKey(windowHandle, GLFW.GLFW_KEY_U) == GLFW.GLFW_PRESS &&
                    GLFW.glfwGetKey(windowHandle, GLFW.GLFW_KEY_O) == GLFW.GLFW_PRESS) {
                    if (client.currentScreen == null) {
                        client.setScreen(new ComponentEditorScreen());
                    }
                }
            }
        });
    }
}
