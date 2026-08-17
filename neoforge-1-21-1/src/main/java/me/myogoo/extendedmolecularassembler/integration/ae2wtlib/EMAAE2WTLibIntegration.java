package me.myogoo.extendedmolecularassembler.integration.ae2wtlib;

import appeng.api.features.GridLinkables;
import appeng.items.tools.powered.WirelessTerminalItem;
import de.mari_023.ae2wtlib.api.gui.Icon;
import de.mari_023.ae2wtlib.api.gui.AE2wtlibSlotSemantics;
import de.mari_023.ae2wtlib.api.registration.AddTerminalEvent;
import de.mari_023.ae2wtlib.api.terminal.WTMenuHost;
import me.myogoo.extendedmolecularassembler.ExtendedMolecularAssembler;
import me.myogoo.extendedmolecularassembler.item.WirelessExtendedPatternEncodingTerminalItem;
import me.myogoo.extendedmolecularassembler.menu.pattern.ExtendedPatternEncodingTermMenu;
import me.myogoo.myotus.api.annotation.mods.AE2WTLib;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

@AE2WTLib
public final class EMAAE2WTLibIntegration {
    public static final String TERMINAL_NAME = "extended_pattern_encoding";
    public static final String HOTKEY_NAME = "key.ae2wtlib.extended_pattern_encoding";
    private static final Icon.Texture TERMINAL_ICON_TEXTURE = new Icon.Texture(
            ExtendedMolecularAssembler.makeId(
                    "textures/item/wireless_extended_pattern_encoding_terminal.png"),
            16,
            16);
    private static final Icon TERMINAL_ICON = new Icon(0, 0, 16, 16, TERMINAL_ICON_TEXTURE);
    private static WirelessExtendedPatternEncodingTerminalItem terminalItem;

    private EMAAE2WTLibIntegration() {
    }

    public static void registerTerminal() {
        AddTerminalEvent.register(event -> event.builder(
                TERMINAL_NAME,
                WirelessExtendedPatternEncodingTerminalMenuHost::new,
                ExtendedPatternEncodingTermMenu.TYPE,
                registerTerminalItem(),
                TERMINAL_ICON)
                .hotkeyName(HOTKEY_NAME)
                .addTerminal());
    }

    public static void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            if (terminalItem != null) {
                GridLinkables.register(
                        terminalItem,
                        WirelessTerminalItem.LINKABLE_HANDLER);
            }
        });
    }

    public static void addCreativeTabItems(CreativeModeTab.Output output) {
        if (terminalItem != null) {
            output.accept(terminalItem);
        }
    }

    public static void addSingularitySlot(ExtendedPatternEncodingTermMenu menu, Object host) {
        if (host instanceof WTMenuHost wirelessHost) {
            var singularityInventory = wirelessHost.getSubInventory(WTMenuHost.INV_SINGULARITY);
            if (singularityInventory != null) {
                menu.addSingularitySlot(singularityInventory, AE2wtlibSlotSemantics.SINGULARITY);
            }
        }
    }

    public static boolean isWirelessExtendedPatternEncodingTerminalHost(Object host) {
        return host instanceof WirelessExtendedPatternEncodingTerminalMenuHost;
    }

    private static WirelessExtendedPatternEncodingTerminalItem registerTerminalItem() {
        if (terminalItem == null) {
            terminalItem = Registry.register(
                    BuiltInRegistries.ITEM,
                    ExtendedMolecularAssembler.makeId("wireless_extended_pattern_encoding_terminal"),
                    new WirelessExtendedPatternEncodingTerminalItem());
        }
        return terminalItem;
    }
}
