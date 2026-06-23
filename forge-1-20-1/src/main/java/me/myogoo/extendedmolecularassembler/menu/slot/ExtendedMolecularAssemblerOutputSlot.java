package me.myogoo.extendedmolecularassembler.menu.slot;

import appeng.api.inventories.InternalInventory;
import appeng.client.Point;
import appeng.menu.slot.IOptionalSlot;
import appeng.menu.slot.OutputSlot;
import me.myogoo.extendedmolecularassembler.menu.ExtendedMolecularAssemblerMenu;

public class ExtendedMolecularAssemblerOutputSlot extends OutputSlot implements IOptionalSlot {
    private final ExtendedMolecularAssemblerMenu menu;
    private final int laneIndex;

    public ExtendedMolecularAssemblerOutputSlot(InternalInventory inv, int invSlot) {
        this(null, inv, invSlot, 0);
    }

    public ExtendedMolecularAssemblerOutputSlot(ExtendedMolecularAssemblerMenu menu, InternalInventory inv,
            int invSlot, int laneIndex) {
        super(inv, invSlot, null);
        this.menu = menu;
        this.laneIndex = laneIndex;
    }

    public int getLaneIndex() {
        return this.laneIndex;
    }

    @Override
    public boolean isRenderDisabled() {
        return true;
    }

    @Override
    public boolean isSlotEnabled() {
        return this.menu == null || super.isSlotEnabled();
    }

    @Override
    public Point getBackgroundPos() {
        return new Point(x - 1, y - 1);
    }
}
