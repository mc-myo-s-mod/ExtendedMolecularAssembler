package me.myogoo.extendedmolecularassembler.item;

import de.mari_023.ae2wtlib.terminal.ItemWT;
import de.mari_023.ae2wtlib.wet.WETMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;

/**
 * AE2WTLib-powered wireless counterpart of EMA's extended pattern encoding terminal.
 *
 * <p>The Forge 1.20.1 EMA terminal currently reuses AE2's pattern encoding menu,
 * so this wireless item opens AE2WTLib's wireless pattern-encoding menu/host stack,
 * which is the wireless equivalent of that wired terminal behavior.</p>
 */
public class WirelessExtendedPatternEncodingTerminalItem extends ItemWT {
    @Override
    public MenuType<?> getMenuType(ItemStack itemStack) {
        return WETMenu.TYPE;
    }
}
