package me.myogoo.extendedmolecularassembler.integration.extendedae;

import appeng.api.config.Actionable;
import appeng.api.crafting.IPatternDetails;
import appeng.api.networking.IGrid;
import appeng.api.networking.crafting.ICraftingPlan;
import appeng.api.networking.crafting.ICraftingProvider;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.KeyCounter;
import appeng.me.service.CraftingService;
import com.glodblock.github.extendedae.common.me.matrix.ClusterAssemblerMatrix;
import com.glodblock.github.extendedae.common.tileentities.matrix.TileAssemblerMatrixBase;
import me.myogoo.extendedmolecularassembler.pattern.ExtendedTableCraftingPattern;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public final class ExtendedAEAssemblerMatrixBridge {
    private ExtendedAEAssemblerMatrixBridge() {
    }

    @Nullable
    public static Component getPlanBlockReason(@Nullable IGrid grid, @Nullable ICraftingPlan plan) {
        if (grid == null || plan == null || plan.simulation()
                || !(grid.getCraftingService() instanceof CraftingService service)) {
            return null;
        }
        for (var entry : plan.patternTimes().entrySet()) {
            if (entry.getValue() > 0 && entry.getKey() instanceof ExtendedTableCraftingPattern pattern
                    && !hasExecutableProvider(service.getProviders(pattern), pattern)) {
                return Component.translatable("gui.extendedmolecularassembler.matrix_missing_crafting_core",
                        pattern.tableSideLength(), pattern.tableSideLength());
            }
        }
        return null;
    }

    static boolean hasExecutableProvider(Iterable<ICraftingProvider> providers,
            ExtendedTableCraftingPattern pattern) {
        var executable = false;
        // AE2 shares this round-robin cursor with crafting dispatch; consume a full cycle.
        for (var provider : providers) {
            if (!(provider instanceof ExtendedAssemblerMatrixPatternCoreBlockEntity core)) {
                // Ordinary Pattern Providers keep their existing adjacent-machine behavior.
                executable = true;
            } else if (!executable && !core.isRemoved() && core.getMainNode().isActive()
                    && core.getAvailablePatterns().contains(pattern)
                    && hasCraftingCore(core.getCluster(), pattern)) {
                executable = true;
            }
        }
        return executable;
    }

    static boolean hasCraftingCore(@Nullable ClusterAssemblerMatrix cluster, ExtendedTableCraftingPattern pattern) {
        if (cluster == null || cluster.isDestroyed()) {
            return false;
        }
        var iterator = cluster.getBlockEntities();
        while (iterator.hasNext()) {
            if (iterator.next() instanceof ExtendedAssemblerMatrixCraftingCoreBlockEntity core
                    && !core.isRemoved() && core.supportsPattern(pattern)) {
                return true;
            }
        }
        return false;
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

        var pattern = (ExtendedTableCraftingPattern) patternDetails;
        var hasPatternCore = false;
        var iterator = cluster.getBlockEntities();
        while (iterator.hasNext()) {
            if (iterator.next() instanceof ExtendedAssemblerMatrixPatternCoreBlockEntity patternCore
                    && !patternCore.isRemoved()) {
                hasPatternCore |= patternCore.getAvailablePatterns().contains(pattern);
            }
        }
        if (!hasPatternCore) {
            return false;
        }

        iterator = cluster.getBlockEntities();
        while (iterator.hasNext()) {
            if (iterator.next() instanceof ExtendedAssemblerMatrixCraftingCoreBlockEntity core
                    && !core.isRemoved() && core.freeThreadCount() > 0 && core.supportsPattern(pattern)
                    && core.pushJob(pattern, inputHolder)) {
                return true;
            }
        }
        return false;
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
