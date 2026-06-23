package me.myogoo.extendedmolecularassembler.integration.ae2wtlib;

import appeng.api.features.GridLinkables;
import appeng.items.tools.powered.WirelessTerminalItem;
import de.mari_023.ae2wtlib.api.gui.Icon;
import de.mari_023.ae2wtlib.api.registration.AddTerminalEvent;
import me.myogoo.extendedmolecularassembler.init.EMAItems;
import me.myogoo.extendedmolecularassembler.menu.pattern.ExtendedPatternEncodingTermMenu;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

public final class EMAAE2WTLibIntegration {
    public static final String TERMINAL_NAME = "extended_pattern_encoding";
    public static final String HOTKEY_NAME = "key.ae2wtlib.extended_pattern_encoding";

    private EMAAE2WTLibIntegration() {
    }

    public static void registerTerminal() {
        AddTerminalEvent.register(event -> event.builder(
                TERMINAL_NAME,
                WirelessExtendedPatternEncodingTerminalMenuHost::new,
                ExtendedPatternEncodingTermMenu.TYPE,
                EMAItems.registerWirelessExtendedPatternEncodingTerminal(),
                Icon.PATTERN_ENCODING)
                .hotkeyName(HOTKEY_NAME)
                .addTerminal());
    }

    public static void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> GridLinkables.register(
                EMAItems.wirelessExtendedPatternEncodingTerminal(),
                WirelessTerminalItem.LINKABLE_HANDLER));
    }
}
