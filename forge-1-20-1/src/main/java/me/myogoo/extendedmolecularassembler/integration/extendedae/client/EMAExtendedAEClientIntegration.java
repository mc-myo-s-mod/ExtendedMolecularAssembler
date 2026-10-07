package me.myogoo.extendedmolecularassembler.integration.extendedae.client;

import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.style.StyleManager;
import appeng.init.client.InitScreens;
import me.myogoo.extendedmolecularassembler.integration.extendedae.EMAExtendedAEIntegration;
import me.myogoo.extendedmolecularassembler.integration.extendedae.menu.ExtendedCraftingPatternViewMenu;
import net.minecraft.client.gui.screens.MenuScreens;

public final class EMAExtendedAEClientIntegration {
    private static final String EXTENDED_CRAFTING_PATTERN_VIEW_STYLE =
            "/screens/extended_molecular_assembler/extended_crafting_pattern_view.json";
    private static final String EPIC_CRAFTING_PATTERN_VIEW_STYLE =
            "/screens/extended_molecular_assembler/epic_crafting_pattern_view.json";
    private static final String LEGENDARY_CRAFTING_PATTERN_VIEW_STYLE =
            "/screens/extended_molecular_assembler/legendary_crafting_pattern_view.json";

    private EMAExtendedAEClientIntegration() {
    }

    public static void initScreens() {
        InitScreens.register(
                EMAExtendedAEIntegration.EXTENDED_ASSEMBLER_MATRIX_PATTERN_CORE_MENU.get(),
                ExtendedAssemblerMatrixPatternCoreScreen::new,
                "/screens/extended_molecular_assembler/extended_assembler_matrix_pattern_core.json");
        ExtendedCraftingPatternViewMenu.setClientLayoutProvider(gridSide ->
                ExtendedCraftingPatternViewScreen.createMenuLayout(loadPatternViewStyle(gridSide), gridSide));
        MenuScreens.<ExtendedCraftingPatternViewMenu, ExtendedCraftingPatternViewScreen>register(
                EMAExtendedAEIntegration.EXTENDED_CRAFTING_PATTERN_VIEW_MENU.get(),
                (menu, inventory, title) -> new ExtendedCraftingPatternViewScreen(
                        menu,
                        inventory,
                        title,
                        loadPatternViewStyle(menu.getGridSide())));
    }

    private static ScreenStyle loadPatternViewStyle(int gridSide) {
        String stylePath = switch (gridSide) {
            case 9 -> EXTENDED_CRAFTING_PATTERN_VIEW_STYLE;
            case 11 -> EPIC_CRAFTING_PATTERN_VIEW_STYLE;
            case 13 -> LEGENDARY_CRAFTING_PATTERN_VIEW_STYLE;
            default -> throw new IllegalArgumentException("Unsupported pattern view grid side: " + gridSide);
        };
        return StyleManager.loadStyleDoc(stylePath);
    }
}
