package me.myogoo.extendedmolecularassembler.integration.ae2wtlib;

import appeng.api.features.GridLinkables;
import appeng.items.tools.powered.WirelessTerminalItem;
import me.myogoo.extendedmolecularassembler.init.EMAItems;
import me.myogoo.extendedmolecularassembler.menu.pattern.ExtendedPatternEncodingTermMenu;
import me.myogoo.myotus.api.wt.AddTerminalEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

/** Registers EMA's wireless terminal with AE2 and Myotus' AE2WTLib hook once registries are ready. */
public final class EMAAE2WTLibIntegration {
    public static final String TERMINAL_NAME = "extended_pattern_encoding";
    public static final String HOTKEY_NAME = "key.ae2wtlib.extended_pattern_encoding";
    public static final String TRANSLATION_KEY =
            "item.extendedmolecularassembler.wireless_extended_pattern_encoding_terminal";

    private EMAAE2WTLibIntegration() {
    }

    public static void registerTerminal() {
        AddTerminalEvent.register(event -> {
            var item = EMAItems.WIRELESS_EXTENDED_PATTERN_ENCODING_TERMINAL_ITEM;
            event.addTerminal(
                    TERMINAL_NAME,
                    WirelessExtendedPatternEncodingTerminalMenuHost::new,
                    ExtendedPatternEncodingTermMenu.TYPE,
                    item,
                    HOTKEY_NAME,
                    TRANSLATION_KEY);
        });
    }

    public static void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> GridLinkables.register(
                EMAItems.WIRELESS_EXTENDED_PATTERN_ENCODING_TERMINAL_ITEM,
                WirelessTerminalItem.LINKABLE_HANDLER));
    }
}
