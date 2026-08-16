package me.myogoo.extendedmolecularassembler.integration.extendedae;

import appeng.api.config.Actionable;
import appeng.api.config.PowerMultiplier;
import appeng.api.crafting.IPatternDetails;
import appeng.api.crafting.PatternDetailsHelper;
import appeng.api.networking.IGridNode;
import appeng.api.networking.ticking.IGridTickable;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.networking.ticking.TickingRequest;
import appeng.api.stacks.KeyCounter;
import com.glodblock.github.extendedae.common.me.matrix.ClusterAssemblerMatrix;
import com.glodblock.github.extendedae.common.tileentities.matrix.TileAssemblerMatrixFunction;
import me.myogoo.extendedmolecularassembler.config.EMAConfig;
import me.myogoo.extendedmolecularassembler.pattern.ExtendedTableCraftingPattern;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class ExtendedAssemblerMatrixCraftingCoreBlockEntity extends TileAssemblerMatrixFunction
        implements IGridTickable, ExtendedAEAssemblerMatrixCrafterAccess {
    public static final int DEFAULT_THREAD_COUNT = 8;
    public static final int PLUS_THREAD_COUNT = 32;
    private static final int OUTPUT_SLOT = ExtendedTableCraftingPattern.MACHINE_GRID_SIZE;
    private static final SpeedProfile[] SPEED_PROFILES = {
            new SpeedProfile(20, 1.0),
            new SpeedProfile(26, 1.3),
            new SpeedProfile(34, 1.7),
            new SpeedProfile(40, 2.0),
            new SpeedProfile(50, 2.5),
            new SpeedProfile(100, 5.0)
    };

    private final ExtendedMatrixThread[] extendedThreads;
    private int usedThreadCount = 0;

    public ExtendedAssemblerMatrixCraftingCoreBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        this(type, pos, blockState, DEFAULT_THREAD_COUNT);
    }

    public ExtendedAssemblerMatrixCraftingCoreBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState,
            int threadCount) {
        super(type, pos, blockState);
        this.extendedThreads = new ExtendedMatrixThread[threadCount];
        for (int i = 0; i < this.extendedThreads.length; i++) {
            this.extendedThreads[i] = new ExtendedMatrixThread(i);
        }
        this.getMainNode()
                .setIdlePowerUsage(EMAConfig.extendedAssemblerMatrixCraftingCoreIdlePowerUsage(this.isPlusCore()))
                .addService(IGridTickable.class, this);
    }

    @Override
    public void add(ClusterAssemblerMatrix cluster) {
        // Execution core only. It contributes EMA extended threads; pattern exposure stays in Pattern Core.
    }

    @Override
    public boolean extendedmolecularassembler$pushExtendedJob(IPatternDetails patternDetails, KeyCounter[] inputHolder) {
        if (!(patternDetails instanceof ExtendedTableCraftingPattern pattern)) {
            return false;
        }
        if (this.usedThreadCount >= this.extendedThreads.length) {
            return false;
        }
        for (var thread : this.extendedThreads) {
            if (thread.acceptJob(pattern, inputHolder)) {
                this.wakeCore();
                return true;
            }
        }
        return false;
    }

    @Override
    public int extendedmolecularassembler$getExtendedUsedThreadCount() {
        return this.usedThreadCount;
    }

    @Override
    public int extendedmolecularassembler$getExtendedThreadCapacity() {
        return this.extendedThreads.length;
    }

    public int extendedmolecularassembler$getExtendedFreeThreadCount() {
        return this.extendedThreads.length - this.usedThreadCount;
    }

    @Override
    public void extendedmolecularassembler$cancelExtendedJobs() {
        var changed = false;
        for (var thread : this.extendedThreads) {
            changed |= thread.stopProcessing();
        }
        if (changed) {
            this.saveChanges();
            this.wakeCore();
        }
    }

    @Override
    public TickingRequest getTickingRequest(IGridNode node) {
        // Keep the EMA execution core independently tickable. ExtendedAE's vanilla Matrix crafter
        // manages wake/sleep through its own state bits; EMA jobs are stored separately and can be
        // running while the upstream Matrix crafter state changes. Allowing AE2 to sleep this device
        // based on the initial/request-time used count can leave EMA jobs stalled until another EMA
        // event explicitly wakes the node.
        return new TickingRequest(1, 1, false);
    }

    @Override
    public TickRateModulation tickingRequest(IGridNode node, int ticksSinceLastCall) {
        var changed = false;
        var active = false;
        var speedCore = this.cluster == null ? 0 : this.cluster.getSpeedCore();
        for (var thread : this.extendedThreads) {
            active |= thread.isUsed();
            changed |= thread.tick(speedCore, ticksSinceLastCall);
            active |= thread.isUsed();
        }
        if (changed) {
            this.saveChanges();
        }
        return active ? TickRateModulation.URGENT : TickRateModulation.SLEEP;
    }

    @Override
    public void saveAdditional(CompoundTag data, HolderLookup.Provider registries) {
        super.saveAdditional(data, registries);
        for (int i = 0; i < this.extendedThreads.length; i++) {
            var tag = this.extendedThreads[i].save(registries);
            if (!tag.isEmpty()) {
                data.put("ema_extended_thread_" + i, tag);
            }
        }
    }

    @Override
    public void loadTag(CompoundTag data, HolderLookup.Provider registries) {
        super.loadTag(data, registries);
        for (int i = 0; i < this.extendedThreads.length; i++) {
            this.extendedThreads[i].load(data.getCompound("ema_extended_thread_" + i), registries);
        }
        this.wakeCore();
    }

    @Override
    public void addAdditionalDrops(Level level, BlockPos pos, List<ItemStack> drops) {
        super.addAdditionalDrops(level, pos, drops);
        for (var thread : this.extendedThreads) {
            thread.addAdditionalDrops(drops);
        }
    }

    @Override
    public void clearContent() {
        super.clearContent();
        for (var thread : this.extendedThreads) {
            thread.clear();
        }
    }

    private void wakeCore() {
        this.getMainNode().ifPresent((grid, node) -> grid.getTickManager().wakeDevice(node));
    }

    private boolean isPlusCore() {
        return this.extendedThreads.length > DEFAULT_THREAD_COUNT;
    }

    private int usePower(int ticksPassed, int bonusValue, double acceleratorTax) {
        var grid = this.getMainNode().getGrid();
        if (grid == null) {
            return 0;
        }
        var powerMultiplier = EMAConfig.extendedAssemblerMatrixCraftingCoreCraftingPowerMultiplier(this.isPlusCore());
        var progress = ticksPassed * bonusValue;
        if (powerMultiplier <= 0) {
            return progress;
        }

        var requestedPower = Math.min(progress * acceleratorTax, 5000) * powerMultiplier;
        return (int) (grid.getEnergyService().extractAEPower(requestedPower,
                Actionable.MODULATE, PowerMultiplier.CONFIG) / acceleratorTax / powerMultiplier);
    }

    private SpeedProfile speedProfile(int speedCore) {
        return SPEED_PROFILES[Math.max(0, Math.min(speedCore, SPEED_PROFILES.length - 1))];
    }

    private final class ExtendedMatrixThread {
        private final int index;
        private final ItemStack[] grid = new ItemStack[ExtendedTableCraftingPattern.MACHINE_GRID_SIZE + 1];
        private ExtendedTableCraftingPattern pattern;
        private ItemStack patternStack = ItemStack.EMPTY;
        private double progress = 0;
        private int outputRetryCooldown = 0;
        private int occupiedGridSlots = 0;
        private boolean used = false;

        private ExtendedMatrixThread(int index) {
            this.index = index;
            this.clearGrid();
        }

        private boolean acceptJob(ExtendedTableCraftingPattern pattern, KeyCounter[] table) {
            if (this.isUsed()) {
                return false;
            }
            this.pattern = pattern;
            this.patternStack = pattern.getDefinition().toStack();
            this.updateUsedState();
            try {
                pattern.fillCraftingGrid(table, this::setGridItem);
                for (var list : table) {
                    list.removeZeros();
                    if (!list.isEmpty()) {
                        throw new RuntimeException("Could not fill extended matrix crafting core grid with some items, including "
                                + list.iterator().next());
                    }
                }
            } catch (RuntimeException e) {
                this.clear();
                throw e;
            }
            ExtendedAssemblerMatrixCraftingCoreBlockEntity.this.saveChanges();
            return true;
        }

        private boolean tick(int speedCore, int ticksSinceLastCall) {
            if (this.outputRetryCooldown > 0) {
                this.outputRetryCooldown = Math.max(0, this.outputRetryCooldown - ticksSinceLastCall);
                return false;
            }

            if (!this.grid[OUTPUT_SLOT].isEmpty()) {
                var before = this.grid[OUTPUT_SLOT].copy();
                this.pushOutputToNetwork();
                this.ejectHeldItems();
                if (this.pattern == null && this.isGridEmpty()) {
                    this.clear();
                }
                return !ItemStack.isSameItemSameComponents(before, this.grid[OUTPUT_SLOT])
                        || before.getCount() != this.grid[OUTPUT_SLOT].getCount();
            }

            if (this.pattern == null) {
                if (this.isGridEmpty()) {
                    return false;
                }
                var changed = this.ejectHeldItems();
                if (this.isGridEmpty()) {
                    this.clear();
                }
                return changed;
            }

            var speed = speedProfile(speedCore);
            this.progress += usePower(ticksSinceLastCall, speed.speed(), speed.acceleratorTax());
            if (this.progress < 100) {
                return false;
            }

            this.progress = 0;
            var output = this.pattern.assembleFromMachineGrid(this::getGridItem,
                    ExtendedAssemblerMatrixCraftingCoreBlockEntity.this.getLevel());
            if (output.isEmpty()) {
                this.stopProcessing();
                return true;
            }
            if (ExtendedAssemblerMatrixCraftingCoreBlockEntity.this.getLevel() != null) {
                output.onCraftedBySystem(ExtendedAssemblerMatrixCraftingCoreBlockEntity.this.getLevel());
            }

            var remainders = this.pattern.getRemainingItemsFromMachineGrid(this::getGridItem);
            this.pattern = null;
            this.patternStack = ItemStack.EMPTY;
            this.clearGrid();
            for (int i = 0; i < Math.min(remainders.size(), ExtendedTableCraftingPattern.MACHINE_GRID_SIZE); i++) {
                this.setGridItem(i, remainders.get(i));
            }
            this.setGridItem(OUTPUT_SLOT, output);
            this.updateUsedState();
            this.pushOutputToNetwork();
            this.ejectHeldItems();
            if (this.isGridEmpty()) {
                this.clear();
            }
            return true;
        }

        private void pushOutputToNetwork() {
            if (this.grid[OUTPUT_SLOT].isEmpty()) {
                return;
            }
            var remaining = ExtendedAEAssemblerMatrixBridge.insertIntoMatrixNetwork(
                    ExtendedAssemblerMatrixCraftingCoreBlockEntity.this.cluster, this.grid[OUTPUT_SLOT].copy());
            this.setGridItem(OUTPUT_SLOT, remaining);
            if (!remaining.isEmpty()) {
                this.outputRetryCooldown = 100;
            }
        }

        private boolean ejectHeldItems() {
            if (!this.grid[OUTPUT_SLOT].isEmpty()) {
                return false;
            }
            for (int i = 0; i < ExtendedTableCraftingPattern.MACHINE_GRID_SIZE; i++) {
                var stack = this.grid[i];
                if (!stack.isEmpty()) {
                    this.setGridItem(OUTPUT_SLOT, stack);
                    this.setGridItem(i, ItemStack.EMPTY);
                    return true;
                }
            }
            return false;
        }

        private void moveFirstInputToOutput() {
            if (!this.grid[OUTPUT_SLOT].isEmpty()) {
                return;
            }
            for (int i = 0; i < ExtendedTableCraftingPattern.MACHINE_GRID_SIZE; i++) {
                if (!this.grid[i].isEmpty()) {
                    this.setGridItem(OUTPUT_SLOT, this.grid[i]);
                    this.setGridItem(i, ItemStack.EMPTY);
                    break;
                }
            }
            if (this.isGridEmpty()) {
                this.clear();
            }
        }

        private boolean isUsed() {
            return this.used;
        }

        private boolean isGridEmpty() {
            return this.occupiedGridSlots == 0;
        }

        private ItemStack getGridItem(int slot) {
            if (slot < 0 || slot >= this.grid.length) {
                return ItemStack.EMPTY;
            }
            return this.grid[slot];
        }

        private void setGridItem(int slot, ItemStack stack) {
            if (slot >= 0 && slot < this.grid.length) {
                this.setGridItemDirect(slot, stack.isEmpty() ? ItemStack.EMPTY : stack.copy());
            }
        }

        private void setGridItemDirect(int slot, ItemStack stack) {
            var oldEmpty = this.grid[slot].isEmpty();
            var newStack = stack.isEmpty() ? ItemStack.EMPTY : stack;
            var newEmpty = newStack.isEmpty();
            this.grid[slot] = newStack;
            if (oldEmpty && !newEmpty) {
                this.occupiedGridSlots++;
            } else if (!oldEmpty && newEmpty) {
                this.occupiedGridSlots--;
            }
            this.updateUsedState();
        }

        private boolean stopProcessing() {
            var wasUsed = this.isUsed();
            this.pattern = null;
            this.patternStack = ItemStack.EMPTY;
            this.progress = 0;
            this.outputRetryCooldown = 0;
            this.updateUsedState();
            this.returnHeldItemsToNetwork();
            this.dropHeldItems();
            return wasUsed;
        }

        private void returnHeldItemsToNetwork() {
            for (int i = 0; i < this.grid.length; i++) {
                var stack = this.grid[i];
                if (stack.isEmpty()) {
                    continue;
                }
                this.setGridItem(i, ExtendedAEAssemblerMatrixBridge.insertIntoMatrixNetwork(
                        ExtendedAssemblerMatrixCraftingCoreBlockEntity.this.cluster, stack.copy()));
            }
        }

        private void dropHeldItems() {
            var level = ExtendedAssemblerMatrixCraftingCoreBlockEntity.this.getLevel();
            for (int i = 0; i < this.grid.length; i++) {
                var stack = this.grid[i];
                if (stack.isEmpty()) {
                    continue;
                }
                if (level != null) {
                    var pos = ExtendedAssemblerMatrixCraftingCoreBlockEntity.this.getBlockPos();
                    Containers.dropItemStack(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                            stack.copy());
                }
                this.setGridItem(i, ItemStack.EMPTY);
            }
        }

        private void addAdditionalDrops(List<ItemStack> drops) {
            for (var stack : this.grid) {
                if (!stack.isEmpty()) {
                    drops.add(stack.copy());
                }
            }
        }

        private void clear() {
            this.pattern = null;
            this.patternStack = ItemStack.EMPTY;
            this.progress = 0;
            this.outputRetryCooldown = 0;
            this.updateUsedState();
            this.clearGrid();
            ExtendedAssemblerMatrixCraftingCoreBlockEntity.this.saveChanges();
        }

        private void clearGrid() {
            for (int i = 0; i < this.grid.length; i++) {
                this.grid[i] = ItemStack.EMPTY;
            }
            this.occupiedGridSlots = 0;
            this.updateUsedState();
        }

        private void updateUsedState() {
            var newUsed = this.pattern != null || this.occupiedGridSlots > 0;
            if (this.used == newUsed) {
                return;
            }
            this.used = newUsed;
            ExtendedAssemblerMatrixCraftingCoreBlockEntity.this.usedThreadCount += newUsed ? 1 : -1;
        }

        private CompoundTag save(HolderLookup.Provider registries) {
            var tag = new CompoundTag();
            if (!this.patternStack.isEmpty()) {
                tag.put("pattern", this.patternStack.save(registries));
                tag.putDouble("progress", this.progress);
            }
            if (this.outputRetryCooldown > 0) {
                tag.putInt("outputRetryCooldown", this.outputRetryCooldown);
            }
            for (int i = 0; i < this.grid.length; i++) {
                if (!this.grid[i].isEmpty()) {
                    tag.put("grid" + i, this.grid[i].save(registries));
                }
            }
            return tag;
        }

        private void load(CompoundTag tag, HolderLookup.Provider registries) {
            this.clearGrid();
            this.pattern = null;
            this.patternStack = ItemStack.EMPTY;
            this.progress = 0;
            this.outputRetryCooldown = tag.getInt("outputRetryCooldown");
            this.updateUsedState();
            if (tag.isEmpty()) {
                return;
            }
            if (tag.contains("pattern")) {
                this.patternStack = ItemStack.parseOptional(registries, tag.getCompound("pattern"));
                if (!this.patternStack.isEmpty()
                        && PatternDetailsHelper.decodePattern(this.patternStack,
                                ExtendedAssemblerMatrixCraftingCoreBlockEntity.this.getLevel()) instanceof ExtendedTableCraftingPattern decoded) {
                    this.pattern = decoded;
                }
                this.progress = tag.getDouble("progress");
                this.updateUsedState();
            }
            for (int i = 0; i < this.grid.length; i++) {
                if (tag.contains("grid" + i)) {
                    this.setGridItemDirect(i, ItemStack.parseOptional(registries, tag.getCompound("grid" + i)));
                }
            }
        }
    }

    private record SpeedProfile(int speed, double acceleratorTax) {
    }
}
