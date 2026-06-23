package me.myogoo.extendedmolecularassembler.integration;

import net.minecraft.world.item.ItemStack;

public interface AssemblerMatrixJobContext {
    int speedCore();

    default ItemStack insertOutput(ItemStack stack) {
        return stack;
    }

    void release();
}
