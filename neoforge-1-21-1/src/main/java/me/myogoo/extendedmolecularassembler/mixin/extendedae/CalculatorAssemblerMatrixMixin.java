package me.myogoo.extendedmolecularassembler.mixin.extendedae;

import appeng.me.cluster.IAEMultiBlock;
import com.glodblock.github.extendedae.common.me.matrix.CalculatorAssemblerMatrix;
import com.glodblock.github.extendedae.common.tileentities.matrix.TileAssemblerMatrixCrafter;
import com.glodblock.github.extendedae.common.tileentities.matrix.TileAssemblerMatrixFrame;
import com.glodblock.github.extendedae.common.tileentities.matrix.TileAssemblerMatrixFunction;
import com.glodblock.github.extendedae.common.tileentities.matrix.TileAssemblerMatrixPattern;
import com.glodblock.github.extendedae.common.tileentities.matrix.TileAssemblerMatrixWall;
import me.myogoo.extendedmolecularassembler.integration.extendedae.ExtendedAssemblerMatrixCraftingCoreBlockEntity;
import me.myogoo.extendedmolecularassembler.integration.extendedae.ExtendedAssemblerMatrixPatternCoreBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = CalculatorAssemblerMatrix.class, remap = false)
public abstract class CalculatorAssemblerMatrixMixin {
    @Inject(method = "verifyInternalStructure", at = @At("HEAD"), cancellable = true)
    private void extendedmolecularassembler$verifyExtendedPatternCore(ServerLevel level, BlockPos min, BlockPos max,
            CallbackInfoReturnable<Boolean> cir) {
        var anyPattern = false;
        var anyCrafter = false;
        for (var pos : BlockPos.betweenClosed(min, max)) {
            var blockEntity = level.getBlockEntity(pos);
            if (!(blockEntity instanceof IAEMultiBlock<?> multiblock) || !multiblock.isValid()) {
                cir.setReturnValue(false);
                return;
            }

            anyPattern = anyPattern
                    || blockEntity instanceof TileAssemblerMatrixPattern
                    || blockEntity instanceof ExtendedAssemblerMatrixPatternCoreBlockEntity;
            anyCrafter = anyCrafter
                    || blockEntity instanceof TileAssemblerMatrixCrafter
                    || blockEntity instanceof ExtendedAssemblerMatrixCraftingCoreBlockEntity;

            if (extendedmolecularassembler$isInternal(pos, min, max)) {
                if (!(blockEntity instanceof TileAssemblerMatrixFunction)) {
                    cir.setReturnValue(false);
                    return;
                }
            } else if (extendedmolecularassembler$isEdge(pos, min, max)) {
                if (!(blockEntity instanceof TileAssemblerMatrixFrame)) {
                    cir.setReturnValue(false);
                    return;
                }
            } else if (!(blockEntity instanceof TileAssemblerMatrixWall)) {
                cir.setReturnValue(false);
                return;
            }
        }
        cir.setReturnValue(anyCrafter && anyPattern);
    }

    @Unique
    private static boolean extendedmolecularassembler$isInternal(BlockPos pos, BlockPos min, BlockPos max) {
        return pos.getX() < max.getX() && pos.getX() > min.getX()
                && pos.getY() < max.getY() && pos.getY() > min.getY()
                && pos.getZ() < max.getZ() && pos.getZ() > min.getZ();
    }

    @Unique
    private static boolean extendedmolecularassembler$isEdge(BlockPos pos, BlockPos min, BlockPos max) {
        var boundaryAxes = 0;
        if (pos.getX() == min.getX() || pos.getX() == max.getX()) {
            boundaryAxes++;
        }
        if (pos.getY() == min.getY() || pos.getY() == max.getY()) {
            boundaryAxes++;
        }
        if (pos.getZ() == min.getZ() || pos.getZ() == max.getZ()) {
            boundaryAxes++;
        }
        return boundaryAxes >= 2;
    }
}
