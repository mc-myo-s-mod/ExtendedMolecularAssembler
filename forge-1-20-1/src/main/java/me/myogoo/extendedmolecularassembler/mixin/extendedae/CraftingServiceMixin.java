package me.myogoo.extendedmolecularassembler.mixin.extendedae;

import appeng.api.networking.IGrid;
import appeng.api.networking.crafting.ICraftingCPU;
import appeng.api.networking.crafting.ICraftingPlan;
import appeng.api.networking.crafting.ICraftingRequester;
import appeng.api.networking.crafting.ICraftingSubmitResult;
import appeng.api.networking.security.IActionSource;
import appeng.crafting.execution.CraftingSubmitResult;
import appeng.me.service.CraftingService;
import me.myogoo.extendedmolecularassembler.integration.extendedae.ExtendedAEAssemblerMatrixBridge;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = CraftingService.class, remap = false)
public abstract class CraftingServiceMixin {
    @Shadow @Final
    private IGrid grid;

    @Inject(method = "submitJob", at = @At("HEAD"), cancellable = true, require = 1)
    private void ema$requireMatrixCraftingCore(ICraftingPlan plan, ICraftingRequester requester,
            ICraftingCPU target, boolean prioritizePower, IActionSource source,
            CallbackInfoReturnable<ICraftingSubmitResult> cir) {
        if (ExtendedAEAssemblerMatrixBridge.getPlanBlockReason(this.grid, plan) != null) {
            cir.setReturnValue(CraftingSubmitResult.INCOMPLETE_PLAN);
        }
    }
}
