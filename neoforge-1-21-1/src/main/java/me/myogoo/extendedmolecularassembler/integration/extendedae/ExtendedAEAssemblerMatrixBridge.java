package me.myogoo.extendedmolecularassembler.integration.extendedae;

import appeng.api.config.Actionable;
import appeng.api.crafting.IPatternDetails;
import appeng.api.implementations.blockentities.ICraftingMachine;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.KeyCounter;
import com.glodblock.github.extendedae.common.me.matrix.ClusterAssemblerMatrix;
import com.glodblock.github.extendedae.common.tileentities.matrix.TileAssemblerMatrixBase;
import me.myogoo.extendedmolecularassembler.block.blockentity.ExtendedMolecularAssemblerBlockEntity;
import me.myogoo.extendedmolecularassembler.integration.AssemblerMatrixJobContext;
import me.myogoo.extendedmolecularassembler.pattern.ExtendedTableCraftingPattern;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

public final class ExtendedAEAssemblerMatrixBridge {
    private static final Direction[] DIRECTIONS = Direction.values();
    private static final Map<ClusterAssemblerMatrix, ClusterJobState> JOB_STATES =
            Collections.synchronizedMap(new WeakHashMap<>());
    private static final ThreadLocal<ReservedMatrixJob> CURRENT_JOB = new ThreadLocal<>();

    private ExtendedAEAssemblerMatrixBridge() {
    }

    public static boolean hasExtendedPattern(List<IPatternDetails> patterns) {
        for (var pattern : patterns) {
            if (pattern instanceof ExtendedTableCraftingPattern) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasAvailableExtendedAssembler(ClusterAssemblerMatrix cluster) {
        return getAvailableExtendedCraftingSlots(cluster) > 0;
    }

    public static boolean hasAvailableExtendedAssembler(Level level, BlockPos matrixPos) {
        for (var direction : DIRECTIONS) {
            if (getUsableExtendedAssemblerMachine(level, matrixPos.relative(direction), direction.getOpposite()) != null) {
                return true;
            }
        }
        return false;
    }

    @Nullable
    public static ICraftingMachine getUsableExtendedAssemblerMachine(Level level, BlockPos machinePos, Direction side) {
        if (!(level.getBlockEntity(machinePos) instanceof ExtendedMolecularAssemblerBlockEntity assembler)) {
            return null;
        }

        if (!assembler.isActive() || !assembler.acceptsPlans()) {
            return null;
        }
        return assembler;
    }

    public static TargetCheck describeExtendedAssemblerTarget(Level level, BlockPos machinePos, Direction side) {
        if (!(level.getBlockEntity(machinePos) instanceof ExtendedMolecularAssemblerBlockEntity assembler)) {
            return TargetCheck.NO_ASSEMBLER;
        }
        if (!assembler.isActive()) {
            return TargetCheck.INACTIVE;
        }
        if (!assembler.acceptsPlans()) {
            return TargetCheck.BUSY;
        }
        return TargetCheck.USABLE;
    }

    public enum TargetCheck {
        NO_ASSEMBLER,
        INACTIVE,
        BUSY,
        USABLE
    }

    public static boolean hasExtendedPatternCore(ClusterAssemblerMatrix cluster) {
        return scanExtendedCraftingState(cluster).hasPatternCore();
    }

    public static int getAvailableExtendedCraftingSlots(ClusterAssemblerMatrix cluster) {
        var state = scanExtendedCraftingState(cluster);
        return Math.max(0, state.freeThreads() - getReservedJobCount(cluster));
    }

    public static int getUsedExtendedCraftingSlots(ClusterAssemblerMatrix cluster) {
        return scanExtendedCraftingState(cluster).usedThreads();
    }

    public static void cancelExtendedAssemblerJobs(ClusterAssemblerMatrix cluster) {
        if (cluster == null || cluster.isDestroyed()) {
            return;
        }
        var iterator = cluster.getBlockEntities();
        while (iterator.hasNext()) {
            var matrixBlock = iterator.next();
            if (matrixBlock instanceof ExtendedAssemblerMatrixCraftingCoreBlockEntity core) {
                core.ema$cancelExtendedJobs();
            }
        }
    }

    @Nullable
    public static ReservedMatrixJob reserveCraftingSlot(ClusterAssemblerMatrix cluster) {
        var craftingState = scanExtendedCraftingState(cluster);
        if (craftingState.freeThreads() <= getReservedJobCount(cluster)) {
            return null;
        }

        var jobState = JOB_STATES.computeIfAbsent(cluster, ignored -> new ClusterJobState());
        jobState.reserve();
        return new ReservedMatrixJob(cluster, jobState, cluster.getSpeedCore());
    }

    public static boolean pushExtendedCraftingJob(ClusterAssemblerMatrix cluster, IPatternDetails patternDetails,
            KeyCounter[] inputHolder) {
        if (!(patternDetails instanceof ExtendedTableCraftingPattern)
                || cluster == null
                || cluster.isDestroyed()) {
            return false;
        }

        ExtendedAssemblerMatrixCraftingCoreBlockEntity availableCore = null;
        var hasPatternCore = false;
        var freeThreads = 0;
        var iterator = cluster.getBlockEntities();
        while (iterator.hasNext()) {
            var matrixBlock = iterator.next();
            if (matrixBlock instanceof ExtendedAssemblerMatrixPatternCoreBlockEntity) {
                hasPatternCore = true;
                continue;
            }
            if (matrixBlock instanceof ExtendedAssemblerMatrixCraftingCoreBlockEntity core) {
                var coreFreeThreads = core.ema$getExtendedFreeThreadCount();
                if (coreFreeThreads <= 0) {
                    continue;
                }
                freeThreads += coreFreeThreads;
                if (availableCore == null) {
                    availableCore = core;
                }
            }
        }

        if (!hasPatternCore || availableCore == null || freeThreads <= getReservedJobCount(cluster)) {
            return false;
        }
        return availableCore.ema$pushExtendedJob(patternDetails, inputHolder);
    }

    public static JobScope activateJob(ReservedMatrixJob job) {
        var previous = CURRENT_JOB.get();
        CURRENT_JOB.set(job);
        return new JobScope(previous);
    }

    @Nullable
    public static AssemblerMatrixJobContext claimCurrentJobContext() {
        var job = CURRENT_JOB.get();
        if (job == null || job.isReleased()) {
            return null;
        }
        return job;
    }

    public static ItemStack insertIntoMatrixNetwork(Level level, BlockPos pos, ItemStack stack) {
        if (stack.isEmpty() || !(level.getBlockEntity(pos) instanceof TileAssemblerMatrixBase matrixBlock)) {
            return stack;
        }

        return insertIntoMatrixNetwork(matrixBlock.getCluster(), stack);
    }

    public static ItemStack insertIntoMatrixNetwork(ClusterAssemblerMatrix cluster, ItemStack stack) {
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

    public static final class ReservedMatrixJob implements AssemblerMatrixJobContext {
        private final ClusterAssemblerMatrix cluster;
        private final ClusterJobState state;
        private final int speedCore;
        private boolean released = false;

        private ReservedMatrixJob(ClusterAssemblerMatrix cluster, ClusterJobState state, int speedCore) {
            this.cluster = cluster;
            this.state = state;
            this.speedCore = speedCore;
        }

        @Override
        public int speedCore() {
            return this.speedCore;
        }

        @Override
        public ItemStack insertOutput(ItemStack stack) {
            return ExtendedAEAssemblerMatrixBridge.insertIntoMatrixNetwork(this.cluster, stack);
        }

        @Override
        public void release() {
            if (this.released) {
                return;
            }
            this.released = true;
            if (this.state.release() <= 0) {
                JOB_STATES.remove(this.cluster);
            }
        }

        public boolean isReleased() {
            return this.released;
        }
    }

    public static final class JobScope implements AutoCloseable {
        @Nullable
        private final ReservedMatrixJob previous;
        private boolean closed = false;

        private JobScope(@Nullable ReservedMatrixJob previous) {
            this.previous = previous;
        }

        @Override
        public void close() {
            if (this.closed) {
                return;
            }
            this.closed = true;
            if (this.previous == null) {
                CURRENT_JOB.remove();
            } else {
                CURRENT_JOB.set(this.previous);
            }
        }
    }

    private static final class ClusterJobState {
        private int reservedJobs = 0;

        private void reserve() {
            this.reservedJobs++;
        }

        private int release() {
            if (this.reservedJobs > 0) {
                this.reservedJobs--;
            }
            return this.reservedJobs;
        }
    }

    private static int getReservedJobCount(ClusterAssemblerMatrix cluster) {
        var state = JOB_STATES.get(cluster);
        return state == null ? 0 : state.reservedJobs;
    }

    private static ExtendedCraftingState scanExtendedCraftingState(ClusterAssemblerMatrix cluster) {
        if (cluster == null || cluster.isDestroyed()) {
            return ExtendedCraftingState.EMPTY;
        }

        var hasPatternCore = false;
        var usedThreads = 0;
        var capacity = 0;
        var iterator = cluster.getBlockEntities();
        while (iterator.hasNext()) {
            var matrixBlock = iterator.next();
            if (matrixBlock instanceof ExtendedAssemblerMatrixPatternCoreBlockEntity) {
                hasPatternCore = true;
            } else if (matrixBlock instanceof ExtendedAssemblerMatrixCraftingCoreBlockEntity core) {
                usedThreads += core.ema$getExtendedUsedThreadCount();
                capacity += core.ema$getExtendedThreadCapacity();
            }
        }
        return new ExtendedCraftingState(hasPatternCore, usedThreads, capacity);
    }

    private record ExtendedCraftingState(boolean hasPatternCore, int usedThreads, int capacity) {
        private static final ExtendedCraftingState EMPTY = new ExtendedCraftingState(false, 0, 0);

        private int freeThreads() {
            return Math.max(0, this.capacity - this.usedThreads);
        }
    }
}
