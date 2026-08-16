package me.myogoo.extendedmolecularassembler.client.screen;

import appeng.client.gui.style.ScreenStyle;
import de.mari_023.ae2wtlib.api.gui.ScrollingUpgradesPanel;
import de.mari_023.ae2wtlib.api.terminal.IUniversalTerminalCapable;
import de.mari_023.ae2wtlib.api.terminal.ItemWUT;
import de.mari_023.ae2wtlib.api.terminal.WTMenuHost;
import me.myogoo.extendedmolecularassembler.menu.pattern.ExtendedPatternEncodingTermMenu;
import me.myogoo.myotus.api.annotation.mods.AE2WTLib;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

@AE2WTLib
public class WirelessExtendedPatternEncodingTermScreen extends ExtendedPatternEncodingTermScreen
        implements IUniversalTerminalCapable {
    private final ScrollingUpgradesPanel upgradesPanel;

    public WirelessExtendedPatternEncodingTermScreen(
            ExtendedPatternEncodingTermMenu menu,
            Inventory playerInventory,
            Component title,
            ScreenStyle style) {
        super(menu, playerInventory, title, style);

        if (getHost().getItemStack().getItem() instanceof ItemWUT) {
            addToLeftToolbar(cycleTerminalButton());
        }
        this.upgradesPanel = addUpgradePanel(widgets, menu);
    }

    @Override
    public void init() {
        super.init();
        upgradesPanel.setMaxRows(Math.max(2, getVisibleRows()));
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        boolean handled = super.keyPressed(keyCode, scanCode, modifiers);
        return handled || checkForTerminalKeys(keyCode, scanCode);
    }

    @Override
    public WTMenuHost getHost() {
        return (WTMenuHost) getMenu().getHost();
    }
}
