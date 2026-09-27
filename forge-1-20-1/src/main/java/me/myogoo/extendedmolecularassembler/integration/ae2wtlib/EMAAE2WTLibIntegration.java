package me.myogoo.extendedmolecularassembler.integration.ae2wtlib;

import appeng.api.features.GridLinkables;
import appeng.items.tools.powered.WirelessTerminalItem;
import de.mari_023.ae2wtlib.AE2wtlibSlotSemantics;
import me.myogoo.myotus.api.wt.AddTerminalEvent;
import de.mari_023.ae2wtlib.terminal.WTMenuHost;
import me.myogoo.extendedmolecularassembler.ExtendedMolecularAssembler;
import me.myogoo.extendedmolecularassembler.init.EMAItems;
import me.myogoo.extendedmolecularassembler.item.WirelessExtendedPatternEncodingTerminalItem;
import me.myogoo.extendedmolecularassembler.menu.pattern.ExtendedPatternEncodingTermMenu;
import me.myogoo.myotus.api.annotation.mods.AE2WTLib;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@AE2WTLib
public final class EMAAE2WTLibIntegration {
    public static final String TERMINAL_NAME = "extended_pattern_encoding";
    public static final String HOTKEY_NAME = "key.ae2wtlib.extended_pattern_encoding";
    public static final String EPIC_TERMINAL_NAME = "epic_pattern_encoding";
    public static final String EPIC_HOTKEY_NAME = "key.ae2wtlib.epic_pattern_encoding";
    public static final String LEGENDARY_TERMINAL_NAME = "legendary_pattern_encoding";
    public static final String LEGENDARY_HOTKEY_NAME = "key.ae2wtlib.legendary_pattern_encoding";
    private static WirelessExtendedPatternEncodingTerminalItem terminalItem;
    private static WirelessExtendedPatternEncodingTerminalItem epicTerminalItem;
    private static WirelessExtendedPatternEncodingTerminalItem legendaryTerminalItem;

    private EMAAE2WTLibIntegration() {
    }

    public static void registerTerminal() {
        if (terminalItem != null) {
            return;
        }
        terminalItem = new WirelessExtendedPatternEncodingTerminalItem(9);
        epicTerminalItem = new WirelessExtendedPatternEncodingTerminalItem(11);
        legendaryTerminalItem = new WirelessExtendedPatternEncodingTerminalItem(13);
        EMAItems.ITEMS.register("wireless_extended_pattern_encoding_terminal", () -> terminalItem);
        EMAItems.ITEMS.register("epic_wireless_extended_pattern_encoding_terminal", () -> epicTerminalItem);
        EMAItems.ITEMS.register("legendary_wireless_extended_pattern_encoding_terminal", () -> legendaryTerminalItem);
        AddTerminalEvent.register(event -> {
            event.addTerminal(
                    TERMINAL_NAME,
                    (player, slot, itemStack, returnToMainMenu) -> new WirelessExtendedPatternEncodingTerminalMenuHost(
                            player, slot, itemStack, returnToMainMenu, terminalItem.getGridSide()),
                    ExtendedPatternEncodingTermMenu.TYPE,
                    terminalItem,
                    HOTKEY_NAME,
                    "item.extendedmolecularassembler.wireless_extended_pattern_encoding_terminal");
            event.addTerminal(
                    EPIC_TERMINAL_NAME,
                    (player, slot, itemStack, returnToMainMenu) -> new WirelessExtendedPatternEncodingTerminalMenuHost(
                            player, slot, itemStack, returnToMainMenu, epicTerminalItem.getGridSide()),
                    ExtendedPatternEncodingTermMenu.TYPE,
                    epicTerminalItem,
                    EPIC_HOTKEY_NAME,
                    "item.extendedmolecularassembler.epic_wireless_extended_pattern_encoding_terminal");
            event.addTerminal(
                    LEGENDARY_TERMINAL_NAME,
                    (player, slot, itemStack, returnToMainMenu) -> new WirelessExtendedPatternEncodingTerminalMenuHost(
                            player, slot, itemStack, returnToMainMenu, legendaryTerminalItem.getGridSide()),
                    ExtendedPatternEncodingTermMenu.TYPE,
                    legendaryTerminalItem,
                    LEGENDARY_HOTKEY_NAME,
                    "item.extendedmolecularassembler.legendary_wireless_extended_pattern_encoding_terminal");
        });
    }

    public static void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            if (terminalItem != null) {
                GridLinkables.register(terminalItem, WirelessTerminalItem.LINKABLE_HANDLER);
                GridLinkables.register(epicTerminalItem, WirelessTerminalItem.LINKABLE_HANDLER);
                GridLinkables.register(legendaryTerminalItem, WirelessTerminalItem.LINKABLE_HANDLER);
            }
        });
    }

    public static void addCreativeTabItems(CreativeModeTab.Output output) {
        if (terminalItem != null) {
            output.accept(terminalItem);
            output.accept(epicTerminalItem);
            output.accept(legendaryTerminalItem);
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

}
