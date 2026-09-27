package me.myogoo.extendedmolecularassembler.item;

import de.mari_023.ae2wtlib.terminal.ItemWT;
import me.myogoo.extendedmolecularassembler.menu.pattern.ExtendedPatternEncodingTermMenu;
import me.myogoo.myotus.api.annotation.mods.AE2WTLib;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;

@AE2WTLib
public class WirelessExtendedPatternEncodingTerminalItem extends ItemWT {
    private final int gridSide;

    public WirelessExtendedPatternEncodingTerminalItem(int gridSide) {
        this.gridSide = gridSide;
    }

    public int getGridSide() {
        return gridSide;
    }

    @Override
    public MenuType<?> getMenuType(ItemStack itemStack) {
        return ExtendedPatternEncodingTermMenu.TYPE;
    }

}
