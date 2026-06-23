package me.myogoo.extendedmolecularassembler.integration.ae2wtlib;

import appeng.menu.ISubMenu;
import appeng.menu.locator.ItemMenuHostLocator;
import de.mari_023.ae2wtlib.api.terminal.ItemWT;
import de.mari_023.ae2wtlib.api.terminal.WTMenuHost;
import me.myogoo.extendedmolecularassembler.menu.pattern.ExtendedPatternEncodingLogic;
import me.myogoo.extendedmolecularassembler.menu.pattern.IExtendedPatternEncodingTerminalHost;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;

import java.util.function.BiConsumer;

public class WirelessExtendedPatternEncodingTerminalMenuHost extends WTMenuHost
        implements IExtendedPatternEncodingTerminalHost {
    private static final String LOGIC_TAG = "extendedmolecularassembler:extendedPatternEncoding";

    private final ExtendedPatternEncodingLogic logic = new ExtendedPatternEncodingLogic(this);

    public WirelessExtendedPatternEncodingTerminalMenuHost(ItemWT item, Player player, ItemMenuHostLocator locator,
            BiConsumer<Player, ISubMenu> returnToMainMenu) {
        super(item, player, locator, returnToMainMenu);
        readExtendedPatternData();
    }

    @Override
    public ExtendedPatternEncodingLogic getExtendedPatternEncodingLogic() {
        return logic;
    }

    @Override
    public Level getLevel() {
        return getPlayer().level();
    }

    @Override
    public void markForSave() {
        if (!getLevel().isClientSide()) {
            writeExtendedPatternData();
        }
    }

    private void readExtendedPatternData() {
        var stack = getItemStack();
        var customData = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        var root = customData.copyTag();
        if (root.contains(LOGIC_TAG)) {
            logic.readFromNBT(root.getCompound(LOGIC_TAG), getPlayer().registryAccess());
        }
    }

    private void writeExtendedPatternData() {
        var stack = getItemStack();
        var root = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        var logicTag = new CompoundTag();
        logic.writeToNBT(logicTag, getPlayer().registryAccess());
        root.put(LOGIC_TAG, logicTag);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(root));
    }
}
