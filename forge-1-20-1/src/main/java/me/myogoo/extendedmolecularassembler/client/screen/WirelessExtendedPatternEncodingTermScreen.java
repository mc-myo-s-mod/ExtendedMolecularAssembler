package me.myogoo.extendedmolecularassembler.client.screen;

import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.BackgroundPanel;
import de.mari_023.ae2wtlib.wut.CycleTerminalButton;
import de.mari_023.ae2wtlib.wut.IUniversalTerminalCapable;
import de.mari_023.ae2wtlib.wut.ItemWUT;
import de.mari_023.ae2wtlib.terminal.WTMenuHost;
import me.myogoo.extendedmolecularassembler.menu.pattern.ExtendedPatternEncodingTermMenu;
import me.myogoo.myotus.api.annotation.mods.AE2WTLib;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

@AE2WTLib
public class WirelessExtendedPatternEncodingTermScreen extends ExtendedPatternEncodingTermScreen
        implements IUniversalTerminalCapable {

    public WirelessExtendedPatternEncodingTermScreen(
            ExtendedPatternEncodingTermMenu menu,
            Inventory playerInventory,
            Component title,
            ScreenStyle style) {
        super(menu, playerInventory, title, style, false);

        if (getHost().getItemStack().getItem() instanceof ItemWUT) {
            addToLeftToolbar(new CycleTerminalButton(button -> cycleTerminal()));
        }
        widgets.add("singularityBackground", new BackgroundPanel(style.getImage("singularityBackground")));
    }

    public WTMenuHost getHost() {
        return (WTMenuHost) getMenu().getHost();
    }
}
