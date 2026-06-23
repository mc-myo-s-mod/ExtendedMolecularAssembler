package me.myogoo.extendedmolecularassembler.mixin.extendedae;

import appeng.api.crafting.IPatternDetails;
import appeng.api.stacks.KeyCounter;
import com.glodblock.github.extendedae.common.me.matrix.ClusterAssemblerMatrix;
import com.glodblock.github.extendedae.common.tileentities.matrix.TileAssemblerMatrixBase;
import me.myogoo.extendedmolecularassembler.integration.extendedae.ExtendedAEAssemblerMatrixBridge;
import me.myogoo.extendedmolecularassembler.integration.extendedae.ExtendedAssemblerMatrixCraftingCoreBlockEntity;
import me.myogoo.extendedmolecularassembler.pattern.ExtendedTableCraftingPattern;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Iterator;

@Mixin(value = ClusterAssemblerMatrix.class, remap = false)
public abstract class ClusterAssemblerMatrixMixin {
    @Shadow
    public abstract Iterator<TileAssemblerMatrixBase> getBlockEntities();

    @Inject(method = "pushCraftingJob", at = @At("HEAD"), cancellable = true)
    private void extendedmolecularassembler$pushExtendedPattern(IPatternDetails patternDetails,
            KeyCounter[] inputHolder, CallbackInfoReturnable<Boolean> cir) {
        if (patternDetails instanceof ExtendedTableCraftingPattern) {
            if (!ExtendedAEAssemblerMatrixBridge.hasExtendedPatternCore(this.extendedmolecularassembler$self())) {
                cir.setReturnValue(false);
                return;
            }
            cir.setReturnValue(this.extendedmolecularassembler$pushToMatrixCrafter(patternDetails, inputHolder));
        }
    }

    @Unique
    private boolean extendedmolecularassembler$pushToMatrixCrafter(IPatternDetails patternDetails, KeyCounter[] inputHolder) {
        if (ExtendedAEAssemblerMatrixBridge.getAvailableExtendedCraftingSlots(this.extendedmolecularassembler$self()) <= 0) {
            return false;
        }

        var iterator = this.getBlockEntities();
        while (iterator.hasNext()) {
            if (!(iterator.next() instanceof ExtendedAssemblerMatrixCraftingCoreBlockEntity core)) {
                continue;
            }
            if (core.extendedmolecularassembler$getExtendedUsedThreadCount()
                    >= core.extendedmolecularassembler$getExtendedThreadCapacity()) {
                continue;
            }
            if (core.extendedmolecularassembler$pushExtendedJob(patternDetails, inputHolder)) {
                return true;
            }
        }
        return false;
    }

    @Unique
    private ClusterAssemblerMatrix extendedmolecularassembler$self() {
        return (ClusterAssemblerMatrix) (Object) this;
    }
}
