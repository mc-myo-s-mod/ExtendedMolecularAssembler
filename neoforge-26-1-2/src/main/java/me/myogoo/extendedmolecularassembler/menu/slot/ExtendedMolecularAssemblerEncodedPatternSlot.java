package me.myogoo.extendedmolecularassembler.menu.slot;

import appeng.api.inventories.InternalInventory;
import appeng.client.Point;
import appeng.menu.slot.IOptionalSlot;
import appeng.menu.slot.RestrictedInputSlot;

public class ExtendedMolecularAssemblerEncodedPatternSlot extends RestrictedInputSlot implements IOptionalSlot {
    public ExtendedMolecularAssemblerEncodedPatternSlot(InternalInventory inv, int invSlot) {
        super(PlacableItemType.ENCODED_PATTERN, inv, invSlot);
        setStackLimit(1);
    }

    @Override
    public boolean isRenderDisabled() {
        return true;
    }

    @Override
    public Point getBackgroundPos() {
        return new Point(x - 1, y - 1);
    }
}
