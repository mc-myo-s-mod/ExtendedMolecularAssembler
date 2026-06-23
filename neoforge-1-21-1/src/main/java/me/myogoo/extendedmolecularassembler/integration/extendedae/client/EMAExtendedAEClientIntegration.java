package me.myogoo.extendedmolecularassembler.integration.extendedae.client;

import appeng.init.client.InitScreens;
import me.myogoo.extendedmolecularassembler.integration.extendedae.EMAExtendedAEIntegration;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

public final class EMAExtendedAEClientIntegration {
    private EMAExtendedAEClientIntegration() {
    }

    public static void initScreens(RegisterMenuScreensEvent event) {
        InitScreens.register(event,
                EMAExtendedAEIntegration.EXTENDED_ASSEMBLER_MATRIX_PATTERN_CORE_MENU.get(),
                ExtendedAssemblerMatrixPatternCoreScreen::new,
                "/screens/extended_molecular_assembler/extended_assembler_matrix_pattern_core.json");
    }
}
