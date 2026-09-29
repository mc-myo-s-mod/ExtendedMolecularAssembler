package me.myogoo.extendedmolecularassembler.integration.extendedae.client;

import appeng.client.InitScreens;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.style.StyleManager;
import me.myogoo.extendedmolecularassembler.integration.extendedae.EMAExtendedAEIntegration;
import me.myogoo.extendedmolecularassembler.integration.extendedae.menu.ExtendedCraftingPatternViewMenu;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

public final class EMAExtendedAEClientIntegration {
    private static final String EXTENDED_CRAFTING_PATTERN_VIEW_STYLE =
            "/screens/extended_molecular_assembler/extended_crafting_pattern_view.json";

    private EMAExtendedAEClientIntegration() {
    }

    public static void initScreens(RegisterMenuScreensEvent event) {
        InitScreens.register(event,
                EMAExtendedAEIntegration.EXTENDED_ASSEMBLER_MATRIX_PATTERN_CORE_MENU.get(),
                ExtendedAssemblerMatrixPatternCoreScreen::new,
                "/screens/extended_molecular_assembler/extended_assembler_matrix_pattern_core.json");
        ExtendedCraftingPatternViewMenu.setClientLayoutProvider(() ->
                ExtendedCraftingPatternViewScreen.createMenuLayout(loadPatternViewStyle()));
        event.<ExtendedCraftingPatternViewMenu, ExtendedCraftingPatternViewScreen>register(
                EMAExtendedAEIntegration.EXTENDED_CRAFTING_PATTERN_VIEW_MENU.get(),
                (menu, inventory, title) -> new ExtendedCraftingPatternViewScreen(
                        menu,
                        inventory,
                        title,
                        loadPatternViewStyle()));
    }

    private static ScreenStyle loadPatternViewStyle() {
        return StyleManager.loadStyleDoc(EXTENDED_CRAFTING_PATTERN_VIEW_STYLE);
    }
}
