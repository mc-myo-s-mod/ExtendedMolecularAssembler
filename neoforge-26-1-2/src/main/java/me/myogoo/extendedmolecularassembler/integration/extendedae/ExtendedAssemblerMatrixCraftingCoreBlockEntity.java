package me.myogoo.extendedmolecularassembler.integration.extendedae;

import appeng.api.config.Actionable;
import appeng.api.config.PowerMultiplier;
import appeng.api.crafting.IPatternDetails;
import appeng.api.crafting.PatternDetailsHelper;
import appeng.api.networking.IGridNode;
import appeng.api.networking.energy.IEnergyService;
import appeng.api.networking.ticking.IGridTickable;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.networking.ticking.TickingRequest;
import appeng.api.stacks.KeyCounter;
import com.glodblock.github.extendedae.common.me.matrix.ClusterAssemblerMatrix;
import com.glodblock.github.extendedae.common.tileentities.matrix.TileAssemblerMatrixFunction;
import me.myogoo.extendedmolecularassembler.config.EMAConfig;
import me.myogoo.extendedmolecularassembler.pattern.ExtendedTableCraftingPattern;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.List;

public class ExtendedAssemblerMatrixCraftingCoreBlockEntity extends TileAssemblerMatrixFunction
        implements IGridTickable {
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

    boolean pushJob(IPatternDetails patternDetails, KeyCounter[] inputHolder) {
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

    int usedThreadCount() {
        return this.usedThreadCount;
    }

    int freeThreadCount() {
        return this.extendedThreads.length - this.usedThreadCount;
    }

    void cancelJobs() {
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
        return new TickingRequest(1, 1, this.usedThreadCount == 0);
    }

    @Override
    public TickRateModulation tickingRequest(IGridNode node, int ticksSinceLastCall) {
        if (this.usedThreadCount == 0) {
            return TickRateModulation.SLEEP;
        }

        var changed = false;
        var speedCore = this.cluster == null ? 0 : this.cluster.getSpeedCore();
        var speed = SPEED_PROFILES[Math.max(0, Math.min(speedCore, SPEED_PROFILES.length - 1))];
        var energyService = node.getGrid().getEnergyService();
        var powerMultiplier = EMAConfig.extendedAssemblerMatrixCraftingCoreCraftingPowerMultiplier(this.isPlusCore());
        for (var thread : this.extendedThreads) {
            if (thread.isUsed()) {
                changed |= thread.tick(speed, ticksSinceLastCall, energyService, powerMultiplier);
            }
        }
        if (changed) {
            this.saveChanges();
        }
        return this.usedThreadCount > 0 ? TickRateModulation.URGENT : TickRateModulation.SLEEP;
    }

    @Override
    public void saveAdditional(ValueOutput data) {
        super.saveAdditional(data);
        for (int i = 0; i < this.extendedThreads.length; i++) {
            if (this.extendedThreads[i].isUsed()) {
                this.extendedThreads[i].save(data.child("ema_extended_thread_" + i));
            }
        }
    }

    @Override
    public void loadTag(ValueInput data) {
        super.loadTag(data);
        for (int i = 0; i < this.extendedThreads.length; i++) {
            this.extendedThreads[i].load(data.childOrEmpty("ema_extended_thread_" + i));
        }
        this.wakeCore();
    }

    @Override
    public void onReady() {
        super.onReady();
        if (this.usedThreadCount > 0) {
            this.wakeCore();
        }
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

    private int usePower(IEnergyService energyService, int ticksPassed, int bonusValue, double acceleratorTax,
            double powerMultiplier) {
        var progress = ticksPassed * bonusValue;
        if (powerMultiplier <= 0) {
            return progress;
        }

        var requestedPower = Math.min(progress * acceleratorTax, 5000) * powerMultiplier;
        return (int) (energyService.extractAEPower(requestedPower,
                Actionable.MODULATE, PowerMultiplier.CONFIG) / acceleratorTax / powerMultiplier);
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

        private boolean tick(SpeedProfile speed, int ticksSinceLastCall, IEnergyService energyService,
                double powerMultiplier) {
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

            this.progress += usePower(energyService, ticksSinceLastCall, speed.speed(), speed.acceleratorTax(),
                    powerMultiplier);
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
            var remaining = ExtendedAEAssemblerMatrixBridge.insertOutput(
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
                this.setGridItem(i, ExtendedAEAssemblerMatrixBridge.insertOutput(
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

        private void save(ValueOutput tag) {
            if (!this.patternStack.isEmpty()) {
                tag.store("pattern", ItemStack.CODEC, this.patternStack);
                tag.putDouble("progress", this.progress);
            }
            if (this.outputRetryCooldown > 0) {
                tag.putInt("outputRetryCooldown", this.outputRetryCooldown);
            }
            for (int i = 0; i < this.grid.length; i++) {
                if (!this.grid[i].isEmpty()) {
                    tag.store("grid" + i, ItemStack.CODEC, this.grid[i]);
                }
            }
        }

        private void load(ValueInput tag) {
            this.clearGrid();
            this.pattern = null;
            this.patternStack = ItemStack.EMPTY;
            this.progress = 0;
            this.outputRetryCooldown = tag.getIntOr("outputRetryCooldown", 0);
            this.updateUsedState();
            this.patternStack = tag.read("pattern", ItemStack.CODEC).orElse(ItemStack.EMPTY);
            if (!this.patternStack.isEmpty()) {
                if (!this.patternStack.isEmpty()
                        && PatternDetailsHelper.decodePattern(this.patternStack,
                                ExtendedAssemblerMatrixCraftingCoreBlockEntity.this.getLevel()) instanceof ExtendedTableCraftingPattern decoded) {
                    this.pattern = decoded;
                }
                this.progress = tag.getDoubleOr("progress", 0);
                this.updateUsedState();
            }
            for (int i = 0; i < this.grid.length; i++) {
                this.setGridItemDirect(i, tag.read("grid" + i, ItemStack.CODEC).orElse(ItemStack.EMPTY));
            }
        }
    }

    private record SpeedProfile(int speed, double acceleratorTax) {
    }
}
