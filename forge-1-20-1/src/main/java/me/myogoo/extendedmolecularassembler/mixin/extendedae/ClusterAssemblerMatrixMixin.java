package me.myogoo.extendedmolecularassembler.mixin.extendedae;

import appeng.api.crafting.IPatternDetails;
import appeng.api.stacks.KeyCounter;
import com.glodblock.github.extendedae.common.me.matrix.ClusterAssemblerMatrix;
import me.myogoo.extendedmolecularassembler.integration.extendedae.ExtendedAEAssemblerMatrixBridge;
import me.myogoo.extendedmolecularassembler.pattern.ExtendedTableCraftingPattern;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ClusterAssemblerMatrix.class, remap = false)
public abstract class ClusterAssemblerMatrixMixin {
    @Inject(method = "pushCraftingJob(Lappeng/api/crafting/IPatternDetails;[Lappeng/api/stacks/KeyCounter;)Z",
            at = @At("HEAD"), cancellable = true, require = 1, expect = 1)
    private void ema$pushExtendedPattern(IPatternDetails patternDetails,
            KeyCounter[] inputHolder, CallbackInfoReturnable<Boolean> cir) {
        if (patternDetails instanceof ExtendedTableCraftingPattern) {
            cir.setReturnValue(ExtendedAEAssemblerMatrixBridge.pushJob(
                    this.ema$self(), patternDetails, inputHolder));
        }
    }

    @Unique
    private ClusterAssemblerMatrix ema$self() {
        return (ClusterAssemblerMatrix) (Object) this;
    }
}
