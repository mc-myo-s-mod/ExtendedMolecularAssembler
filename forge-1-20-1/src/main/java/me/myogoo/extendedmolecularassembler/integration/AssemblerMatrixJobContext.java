package me.myogoo.extendedmolecularassembler.integration;

import net.minecraft.world.item.ItemStack;

/**
 * Optional ExtendedAE matrix job context. The Forge 1.20.1 base port keeps this
 * tiny bridge so the core EMA lane runtime can compile before the optional
 * ExtendedAE integration itself is ported.
 */
public interface AssemblerMatrixJobContext {
    int speedCore();

    ItemStack insertOutput(ItemStack stack);

    void release();
}
