package me.myogoo.extendedmolecularassembler.item;

import appeng.menu.locator.ItemMenuHostLocator;
import appeng.helpers.WirelessTerminalMenuHost;
import de.mari_023.ae2wtlib.api.terminal.ItemWT;
import me.myogoo.extendedmolecularassembler.integration.ae2wtlib.WirelessExtendedPatternEncodingTerminalMenuHost;
import me.myogoo.extendedmolecularassembler.menu.pattern.ExtendedPatternEncodingTermMenu;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.phys.BlockHitResult;

public class WirelessExtendedPatternEncodingTerminalItem extends ItemWT {
    @Override
    public MenuType<?> getMenuType(ItemMenuHostLocator locator, Player player) {
        return ExtendedPatternEncodingTermMenu.TYPE;
    }

    @Override
    public WirelessTerminalMenuHost<?> getMenuHost(Player player, ItemMenuHostLocator locator, BlockHitResult hitResult) {
        return new WirelessExtendedPatternEncodingTerminalMenuHost(
                this,
                player,
                locator,
                (returnPlayer, subMenu) -> open(returnPlayer, locator, true));
    }
}
