package me.myogoo.extendedmolecularassembler.integration.ae2wtlib.client;

import appeng.client.gui.style.StyleManager;
import de.mari_023.ae2wtlib.api.terminal.WTMenuHost;
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

    private EMAAE2WTLibClientIntegration() {
    }

    @Nullable
    public static ExtendedPatternEncodingTermScreen tryCreatePatternEncodingScreen(
            ExtendedPatternEncodingTermMenu menu,
            Inventory playerInventory,
            Component title) {
        if (menu.getHost() instanceof WTMenuHost) {
            return new WirelessExtendedPatternEncodingTermScreen(
                    menu,
                    playerInventory,
                    title,
                    StyleManager.loadStyleDoc(WIRELESS_EXTENDED_PATTERN_ENCODING_TERMINAL_STYLE));
        }
        return null;
    }
}
