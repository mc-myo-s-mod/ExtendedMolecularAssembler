package me.myogoo.extendedmolecularassembler.integration.extendedae;

import appeng.api.config.Actionable;
import appeng.api.crafting.IPatternDetails;
import appeng.api.implementations.blockentities.ICraftingMachine;
import appeng.api.stacks.AEItemKey;
import com.glodblock.github.extendedae.common.me.matrix.ClusterAssemblerMatrix;
import com.glodblock.github.extendedae.common.tileentities.matrix.TileAssemblerMatrixBase;
import com.glodblock.github.extendedae.common.tileentities.matrix.TileAssemblerMatrixCrafter;
import com.glodblock.github.extendedae.common.tileentities.matrix.TileAssemblerMatrixPattern;
import me.myogoo.extendedmolecularassembler.block.blockentity.ExtendedMolecularAssemblerBlockEntity;
import me.myogoo.extendedmolecularassembler.integration.AssemblerMatrixJobContext;
import me.myogoo.extendedmolecularassembler.pattern.ExtendedTableCraftingPattern;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

public final class ExtendedAEAssemblerMatrixBridge {
    private static final Direction[] DIRECTIONS = Direction.values();
    private static final String EXTENDEDAE_PLUS_NAMESPACE = "extendedae_plus";
    private static final String MATRIX_CRAFTER_PLUS_ID = "assembler_matrix_crafter_plus";
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

    public static MatrixStatusSummary describeMatrixStatus(TileAssemblerMatrixBase matrixBlock) {
        if (matrixBlock == null || !matrixBlock.isFormed()) {
            return MatrixStatusSummary.EMPTY;
        }
        return describeMatrixStatus(matrixBlock.getCluster());
    }

    public static MatrixStatusSummary describeMatrixStatus(ClusterAssemblerMatrix cluster) {
        if (cluster == null || cluster.isDestroyed()) {
            return MatrixStatusSummary.EMPTY;
        }

        var matrixTotalParallelCrafters = 0;
        var matrixUsedParallelCrafters = 0;
        var matrixPatternSlots = 0;
        var emaTotalParallelCrafters = 0;
        var emaUsedParallelCrafters = 0;
        var emaPatternSlots = 0;

        var iterator = cluster.getBlockEntities();
        while (iterator.hasNext()) {
            var matrixBlock = iterator.next();
            if (matrixBlock instanceof TileAssemblerMatrixCrafter crafter) {
                var capacity = matrixCrafterCapacity(crafter);
                matrixTotalParallelCrafters += capacity;
                matrixUsedParallelCrafters += Math.min(capacity, Math.max(0, crafter.usedThread()));
            }
            if (matrixBlock instanceof TileAssemblerMatrixPattern patternCore) {
                matrixPatternSlots += patternCore.getPatternInventory().size();
            }
            if (matrixBlock instanceof ExtendedAssemblerMatrixCraftingCoreBlockEntity emaCrafter) {
                var capacity = emaCrafter.extendedmolecularassembler$getExtendedThreadCapacity();
                emaTotalParallelCrafters += capacity;
                emaUsedParallelCrafters += Math.min(capacity,
                        Math.max(0, emaCrafter.extendedmolecularassembler$getExtendedUsedThreadCount()));
            }
            if (matrixBlock instanceof ExtendedAssemblerMatrixPatternCoreBlockEntity emaPatternCore) {
                emaPatternSlots += emaPatternCore.getPatternInventory().size();
            }
        }

        var speed = cluster.getSpeedCore();
        return new MatrixStatusSummary(
                new MatrixCraftingStatus(
                        Math.max(0, matrixTotalParallelCrafters - matrixUsedParallelCrafters),
                        matrixTotalParallelCrafters,
                        matrixPatternSlots,
                        speed,
                        MatrixCraftingStatus.MAX_SPEED),
                new MatrixCraftingStatus(
                        Math.max(0, emaTotalParallelCrafters - emaUsedParallelCrafters),
                        emaTotalParallelCrafters,
                        emaPatternSlots,
                        speed,
                        MatrixCraftingStatus.MAX_SPEED));
    }

    private static int matrixCrafterCapacity(TileAssemblerMatrixCrafter crafter) {
        return isExtendedAEPlusBlock(crafter, MATRIX_CRAFTER_PLUS_ID)
                ? ExtendedAssemblerMatrixCraftingCoreBlockEntity.PLUS_THREAD_COUNT
                : TileAssemblerMatrixCrafter.MAX_THREAD;
    }

    private static boolean isExtendedAEPlusBlock(TileAssemblerMatrixBase matrixBlock, String path) {
        var key = BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(matrixBlock.getType());
        return EXTENDEDAE_PLUS_NAMESPACE.equals(key.getNamespace()) && path.equals(key.getPath());
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

    public record MatrixStatusSummary(MatrixCraftingStatus matrix, MatrixCraftingStatus ema) {
        public static final MatrixStatusSummary EMPTY = new MatrixStatusSummary(
                MatrixCraftingStatus.EMPTY,
                MatrixCraftingStatus.EMPTY);
    }

    public static boolean hasExtendedPatternCore(ClusterAssemblerMatrix cluster) {
        if (cluster == null || cluster.isDestroyed()) {
            return false;
        }
        var iterator = cluster.getBlockEntities();
        while (iterator.hasNext()) {
            if (iterator.next() instanceof ExtendedAssemblerMatrixPatternCoreBlockEntity) {
                return true;
            }
        }
        return false;
    }

    public static int getAvailableExtendedCraftingSlots(ClusterAssemblerMatrix cluster) {
        if (cluster == null || cluster.isDestroyed()) {
            return 0;
        }
        return Math.max(0, getExtendedCraftingSlotCapacity(cluster) - getUsedExtendedCraftingSlots(cluster));
    }

    public static int getUsedExtendedCraftingSlots(ClusterAssemblerMatrix cluster) {
        if (cluster == null || cluster.isDestroyed()) {
            return 0;
        }
        var used = 0;
        var iterator = cluster.getBlockEntities();
        while (iterator.hasNext()) {
            if (iterator.next() instanceof ExtendedAssemblerMatrixCraftingCoreBlockEntity core) {
                used += core.extendedmolecularassembler$getExtendedUsedThreadCount();
            }
        }
        return used;
    }

    private static int getExtendedCraftingSlotCapacity(ClusterAssemblerMatrix cluster) {
        if (cluster == null || cluster.isDestroyed()) {
            return 0;
        }
        var capacity = 0;
        var iterator = cluster.getBlockEntities();
        while (iterator.hasNext()) {
            if (iterator.next() instanceof ExtendedAssemblerMatrixCraftingCoreBlockEntity core) {
                capacity += core.extendedmolecularassembler$getExtendedThreadCapacity();
            }
        }
        return capacity;
    }

    public static void cancelExtendedAssemblerJobs(ClusterAssemblerMatrix cluster) {
        if (cluster == null || cluster.isDestroyed()) {
            return;
        }
        var iterator = cluster.getBlockEntities();
        while (iterator.hasNext()) {
            var matrixBlock = iterator.next();
            if (matrixBlock instanceof ExtendedAssemblerMatrixCraftingCoreBlockEntity core) {
                core.extendedmolecularassembler$cancelExtendedJobs();
            }
        }
    }

    @Nullable
    public static ReservedMatrixJob reserveCraftingSlot(ClusterAssemblerMatrix cluster) {
        if (getAvailableExtendedCraftingSlots(cluster) <= 0) {
            return null;
        }

        var state = JOB_STATES.computeIfAbsent(cluster, ignored -> new ClusterJobState());
        state.reserve();
        return new ReservedMatrixJob(cluster, state, cluster.getSpeedCore());
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
}
