package me.myogoo.extendedmolecularassembler.integration.extendedae.client;

import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.Nullable;

public final class AssemblerMatrixNavigationContext {
    private static @Nullable BlockPos lastMatrixPos;

    private AssemblerMatrixNavigationContext() {
    }

    public static void rememberMatrixPos(BlockPos pos) {
        lastMatrixPos = pos.immutable();
    }

    public static BlockPos consumeMatrixPosOr(BlockPos fallback) {
        var matrixPos = lastMatrixPos != null ? lastMatrixPos : fallback;
        clear();
        return matrixPos;
    }

    public static void clear() {
        lastMatrixPos = null;
    }
}
