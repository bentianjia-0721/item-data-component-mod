package com.itemdatacomp.modmenu;

import com.itemdatacomp.client.screen.ComponentEditorScreen;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

/**
 * ModMenu集成
 * 允许从ModMenu打开组件编辑器
 */
public class ModMenuIntegration implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> new ComponentEditorScreen();
    }
}
