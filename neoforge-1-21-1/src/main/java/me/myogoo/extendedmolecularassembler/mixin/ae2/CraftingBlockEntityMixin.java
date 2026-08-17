package me.myogoo.extendedmolecularassembler.mixin.ae2;

import appeng.blockentity.crafting.CraftingBlockEntity;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(CraftingBlockEntity.class)
public abstract class CraftingBlockEntityMixin {
    @WrapWithCondition(
            method = "updateSubType",
            at = @At(
                    value = "INVOKE",
                    target = "Lappeng/blockentity/crafting/CraftingBlockEntity;onGridConnectableSidesChanged()V"),
            require = 1)
    private boolean ema$allowGridSideUpdate(CraftingBlockEntity craftingBlock) {
        // Before the first onReady call, ManagedGridNode still accepts initialization data.
        // Afterwards, a non-ready node has been destroyed and must not be reconfigured.
        return craftingBlock.getReadyInvoked() == 0 || craftingBlock.getMainNode().isReady();
    }
}
