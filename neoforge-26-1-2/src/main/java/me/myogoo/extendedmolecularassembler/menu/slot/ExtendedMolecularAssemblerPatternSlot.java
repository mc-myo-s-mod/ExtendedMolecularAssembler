package me.myogoo.extendedmolecularassembler.menu.slot;

import appeng.api.inventories.InternalInventory;
import appeng.client.Point;
import appeng.menu.slot.AppEngSlot;
import appeng.menu.slot.IOptionalSlot;
import me.myogoo.extendedmolecularassembler.block.blockentity.ExtendedMolecularAssemblerBlockEntity;
import me.myogoo.extendedmolecularassembler.menu.ExtendedMolecularAssemblerMenu;
import net.minecraft.world.item.ItemStack;

public class ExtendedMolecularAssemblerPatternSlot extends AppEngSlot implements IOptionalSlot {
    private final ExtendedMolecularAssemblerMenu menu;
    private final int laneIndex;

    public ExtendedMolecularAssemblerPatternSlot(ExtendedMolecularAssemblerMenu menu, InternalInventory inv,
            int invSlot) {
        this(menu, inv, invSlot, 0);
    }

    public ExtendedMolecularAssemblerPatternSlot(ExtendedMolecularAssemblerMenu menu, InternalInventory inv,
            int invSlot, int laneIndex) {
        super(inv, invSlot);
        this.menu = menu;
        this.laneIndex = laneIndex;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return super.mayPlace(stack) && this.menu.isValidItemForSlot(this.laneIndex, this.getSlotIndex(), stack);
    }

    @Override
    protected boolean getCurrentValidationState() {
        ItemStack stack = getItem();
        return stack.isEmpty() || mayPlace(stack);
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
        if (!super.isSlotEnabled()) {
            return false;
        }

        int slotIndex = getSlotIndex();
        if (!getInventory().getStackInSlot(slotIndex).isEmpty()) {
            return true;
        }

        var pattern = menu.getCurrentPattern(this.laneIndex);
        return slotIndex >= 0
                && slotIndex < ExtendedMolecularAssemblerBlockEntity.GRID_SIZE
                && pattern != null
                && pattern.isSlotEnabled(slotIndex);
    }

    @Override
    public Point getBackgroundPos() {
        return new Point(x - 1, y - 1);
    }
}
