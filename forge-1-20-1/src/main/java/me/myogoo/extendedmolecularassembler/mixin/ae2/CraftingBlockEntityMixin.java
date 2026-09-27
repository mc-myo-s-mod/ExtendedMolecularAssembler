package me.myogoo.extendedmolecularassembler.mixin.ae2;

import appeng.blockentity.crafting.CraftingBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = CraftingBlockEntity.class, remap = false)
public abstract class CraftingBlockEntityMixin {
    @Inject(
            method = "updateSubType",
            at = @At(
                    value = "INVOKE",
                    target = "Lappeng/blockentity/crafting/CraftingBlockEntity;onGridConnectableSidesChanged()V"),
            cancellable = true,
            require = 1)
    private void ema$allowGridSideUpdate(boolean updateFormed, CallbackInfo ci) {
        // Before the first onReady call, ManagedGridNode still accepts initialization data.
        // Afterwards, a non-ready node has been destroyed and must not be reconfigured.
        var craftingBlock = (CraftingBlockEntity) (Object) this;
        if (craftingBlock.getReadyInvoked() != 0 && !craftingBlock.getMainNode().isReady()) {
            ci.cancel();
        }
    }
}
