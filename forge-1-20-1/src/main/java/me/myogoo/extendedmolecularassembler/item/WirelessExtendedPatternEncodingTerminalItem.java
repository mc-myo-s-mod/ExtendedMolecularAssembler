package me.myogoo.extendedmolecularassembler.item;

import de.mari_023.ae2wtlib.terminal.ItemWT;
import me.myogoo.extendedmolecularassembler.menu.pattern.ExtendedPatternEncodingTermMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;

/** Wireless counterpart of EMA's extended pattern encoding terminal. */
public class WirelessExtendedPatternEncodingTerminalItem extends ItemWT {
    @Override
    public MenuType<?> getMenuType(ItemStack itemStack) {
        return ExtendedPatternEncodingTermMenu.TYPE;
    }
}
