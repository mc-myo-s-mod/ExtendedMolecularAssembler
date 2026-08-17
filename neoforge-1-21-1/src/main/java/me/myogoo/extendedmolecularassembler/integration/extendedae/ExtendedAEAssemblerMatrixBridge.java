package me.myogoo.extendedmolecularassembler.integration.extendedae;

import appeng.api.config.Actionable;
import appeng.api.crafting.IPatternDetails;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.KeyCounter;
import com.glodblock.github.extendedae.common.me.matrix.ClusterAssemblerMatrix;
import com.glodblock.github.extendedae.common.tileentities.matrix.TileAssemblerMatrixBase;
import me.myogoo.extendedmolecularassembler.pattern.ExtendedTableCraftingPattern;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public final class ExtendedAEAssemblerMatrixBridge {
    private ExtendedAEAssemblerMatrixBridge() {
    }

    public static int usedJobCount(ClusterAssemblerMatrix cluster) {
        if (cluster == null || cluster.isDestroyed()) {
            return 0;
        }
        var usedThreads = 0;
        var iterator = cluster.getBlockEntities();
        while (iterator.hasNext()) {
            if (iterator.next() instanceof ExtendedAssemblerMatrixCraftingCoreBlockEntity core) {
                usedThreads += core.usedThreadCount();
            }
        }
        return usedThreads;
    }

    public static void cancelJobs(ClusterAssemblerMatrix cluster) {
        if (cluster == null || cluster.isDestroyed()) {
            return;
        }
        var iterator = cluster.getBlockEntities();
        while (iterator.hasNext()) {
            var matrixBlock = iterator.next();
            if (matrixBlock instanceof ExtendedAssemblerMatrixCraftingCoreBlockEntity core) {
                core.cancelJobs();
            }
        }
    }

    public static boolean pushJob(ClusterAssemblerMatrix cluster, IPatternDetails patternDetails,
            KeyCounter[] inputHolder) {
        if (!(patternDetails instanceof ExtendedTableCraftingPattern)
                || cluster == null
                || cluster.isDestroyed()) {
            return false;
        }

        ExtendedAssemblerMatrixCraftingCoreBlockEntity availableCore = null;
        var hasPatternCore = false;
        var iterator = cluster.getBlockEntities();
        while (iterator.hasNext()) {
            var matrixBlock = iterator.next();
            if (matrixBlock instanceof ExtendedAssemblerMatrixPatternCoreBlockEntity) {
                hasPatternCore = true;
                continue;
            }
            if (matrixBlock instanceof ExtendedAssemblerMatrixCraftingCoreBlockEntity core) {
                var coreFreeThreads = core.freeThreadCount();
                if (coreFreeThreads <= 0) {
                    continue;
                }
                if (availableCore == null) {
                    availableCore = core;
                }
            }
        }

        if (!hasPatternCore || availableCore == null) {
            return false;
        }
        return availableCore.pushJob(patternDetails, inputHolder);
    }

    static ItemStack insertOutput(Level level, BlockPos pos, ItemStack stack) {
        if (stack.isEmpty() || !(level.getBlockEntity(pos) instanceof TileAssemblerMatrixBase matrixBlock)) {
            return stack;
        }

        return insertOutput(matrixBlock.getCluster(), stack);
    }

    static ItemStack insertOutput(ClusterAssemblerMatrix cluster, ItemStack stack) {
        if (stack.isEmpty() || cluster == null || cluster.isDestroyed()) {
            return stack;
        }

        var node = cluster.getNode();
        var key = AEItemKey.of(stack);
        if (node == null || !node.isActive() || key == null) {
            return stack;
        }

        var inserted = node.getGrid().getStorageService().getInventory()
                .insert(key, stack.getCount(), Actionable.MODULATE, cluster.getSrc());
        if (inserted <= 0) {
            return stack;
        }

        var remaining = stack.copy();
        remaining.shrink((int) inserted);
        return remaining;
    }
}
