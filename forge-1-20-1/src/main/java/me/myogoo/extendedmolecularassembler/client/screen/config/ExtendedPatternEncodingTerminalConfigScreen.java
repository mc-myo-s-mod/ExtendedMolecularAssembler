package me.myogoo.extendedmolecularassembler.client.screen.config;

import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.WidgetContainer;
import appeng.client.gui.widgets.AECheckbox;
import me.myogoo.extendedmolecularassembler.lang.EMATranslationKey;
import me.myogoo.extendedmolecularassembler.menu.pattern.ExtendedPatternEncodingTermMenu;
import me.myogoo.myotus.api.config.MyoConfigTabScreen;
import net.minecraft.network.chat.Component;

public class ExtendedPatternEncodingTerminalConfigScreen implements MyoConfigTabScreen {
    private ExtendedPatternEncodingTermMenu menu;
    private AECheckbox rememberRecipeType;

    @Override
    public void buildTab(WidgetContainer widget, AEBaseScreen<?> screen) {
        if (!(screen.getMenu() instanceof ExtendedPatternEncodingTermMenu menu)) {
            return;
        }

        this.menu = menu;
        this.rememberRecipeType = widget.addCheckbox(
                "rememberRecipeType",
                Component.translatable(EMATranslationKey.GUI.EXTENDED_PATTERN_ENCODING_TERMINAL_REMEMBER_RECIPE_TYPE.key()),
                this::save);
        updateState();
    }

    private void updateState() {
        if (rememberRecipeType != null && menu != null) {
            rememberRecipeType.setSelected(menu.rememberRecipeType());
        }
    }

    private void save() {
        if (menu == null || rememberRecipeType == null) {
            return;
        }

        menu.setRememberRecipeType(rememberRecipeType.isSelected());
    }
}
