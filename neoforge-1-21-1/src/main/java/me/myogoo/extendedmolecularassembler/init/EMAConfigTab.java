package me.myogoo.extendedmolecularassembler.init;

import me.myogoo.extendedmolecularassembler.ExtendedMolecularAssembler;
import me.myogoo.extendedmolecularassembler.client.screen.config.ExtendedPatternEncodingTerminalConfigScreen;
import me.myogoo.extendedmolecularassembler.integration.ae2wtlib.WirelessExtendedPatternEncodingTerminalMenuHost;
import me.myogoo.extendedmolecularassembler.lang.EMATranslationKey;
import me.myogoo.extendedmolecularassembler.part.ExtendedPatternEncodingTerminalPart;
import me.myogoo.myotus.api.MyotusAPI;
import me.myogoo.myotus.api.annotation.mods.AE2WTLib;
import me.myogoo.myotus.api.config.MyoConfigTab;
import net.minecraft.network.chat.Component;

public final class EMAConfigTab {
    private EMAConfigTab() {
    }

    public static void initialize() {
        var ae2wtlibLoaded = MyotusAPI.integrations().isLoaded(AE2WTLib.class);
        MyotusAPI.configTabs()
                .terminalConfigTab(new MyoConfigTab(
                        ExtendedMolecularAssembler.makeId("extended_pattern_encoding_terminal"),
                        Component.translatable(EMATranslationKey.GUI.EXTENDED_PATTERN_ENCODING_TERMINAL_CONFIG_TITLE.key()),
                        EMAParts.EXTENDED_PATTERN_ENCODING_TERMINAL.stack(),
                        "extended_pattern_encoding_terminal_config.json",
                        new ExtendedPatternEncodingTerminalConfigScreen()
                ).visibleWhen(context -> context.host() instanceof ExtendedPatternEncodingTerminalPart
                        || (ae2wtlibLoaded && context.host() instanceof WirelessExtendedPatternEncodingTerminalMenuHost)));
    }
}
