package me.myogoo.extendedmolecularassembler.integration.ae2wtlib.client;

import appeng.client.gui.style.StyleManager;
import de.mari_023.ae2wtlib.terminal.WTMenuHost;
import me.myogoo.extendedmolecularassembler.client.screen.ExtendedPatternEncodingTermScreen;
import me.myogoo.extendedmolecularassembler.client.screen.WirelessExtendedPatternEncodingTermScreen;
import me.myogoo.extendedmolecularassembler.menu.pattern.ExtendedPatternEncodingTermMenu;
import me.myogoo.myotus.api.annotation.mods.AE2WTLib;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.Nullable;

@AE2WTLib
public final class EMAAE2WTLibClientIntegration {
    private static final String WIRELESS_EXTENDED_PATTERN_ENCODING_TERMINAL_STYLE =
            "/screens/extended_molecular_assembler/wireless_extended_pattern_encoding_terminal.json";
    private static final String EPIC_WIRELESS_EXTENDED_PATTERN_ENCODING_TERMINAL_STYLE =
            "/screens/extended_molecular_assembler/epic_wireless_extended_pattern_encoding_terminal.json";
    private static final String LEGENDARY_WIRELESS_EXTENDED_PATTERN_ENCODING_TERMINAL_STYLE =
            "/screens/extended_molecular_assembler/legendary_wireless_extended_pattern_encoding_terminal.json";

    private EMAAE2WTLibClientIntegration() {
    }

    @Nullable
    public static ExtendedPatternEncodingTermScreen tryCreatePatternEncodingScreen(
            ExtendedPatternEncodingTermMenu menu,
            Inventory playerInventory,
            Component title) {
        if (menu.getHost() instanceof WTMenuHost) {
            var stylePath = switch (menu.getGridSide()) {
                case 11 -> EPIC_WIRELESS_EXTENDED_PATTERN_ENCODING_TERMINAL_STYLE;
                case 13 -> LEGENDARY_WIRELESS_EXTENDED_PATTERN_ENCODING_TERMINAL_STYLE;
                default -> WIRELESS_EXTENDED_PATTERN_ENCODING_TERMINAL_STYLE;
            };
            return new WirelessExtendedPatternEncodingTermScreen(
                    menu,
                    playerInventory,
                    title,
                    StyleManager.loadStyleDoc(stylePath));
        }
        return null;
    }
}
