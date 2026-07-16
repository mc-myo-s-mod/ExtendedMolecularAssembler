package me.myogoo.extendedmolecularassembler.integration.extendedae;

import appeng.api.config.Settings;
import appeng.api.config.YesNo;
import appeng.api.crafting.IPatternDetails;
import appeng.api.crafting.PatternDetailsHelper;
import appeng.api.implementations.blockentities.PatternContainerGroup;
import appeng.api.inventories.InternalInventory;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNodeListener;
import appeng.api.networking.crafting.ICraftingProvider;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.KeyCounter;
import appeng.crafting.pattern.EncodedPatternItem;
import appeng.helpers.patternprovider.PatternContainer;
import appeng.util.inv.AppEngInternalInventory;
import appeng.util.inv.InternalInventoryHost;
import appeng.util.inv.filter.IAEItemFilter;
import com.glodblock.github.extendedae.common.me.matrix.ClusterAssemblerMatrix;
import com.glodblock.github.extendedae.common.tileentities.matrix.TileAssemblerMatrixFunction;
import me.myogoo.extendedmolecularassembler.block.TieredMECraftingProviderTier;
import me.myogoo.extendedmolecularassembler.block.blockentity.TieredMECraftingProviderBlockEntity;
import me.myogoo.extendedmolecularassembler.config.EMAConfig;
import me.myogoo.extendedmolecularassembler.pattern.ExtendedTableCraftingPattern;
import me.myogoo.extendedmolecularassembler.lang.EMATranslationKey;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

public class ExtendedAssemblerMatrixPatternCoreBlockEntity extends TileAssemblerMatrixFunction
        implements InternalInventoryHost, ICraftingProvider, PatternContainer {
    public static final int DEFAULT_INV_SIZE = 36;
    public static final int PLUS_INV_SIZE = 72;

    private final AppEngInternalInventory patternInventory;
    private final List<IPatternDetails> patterns = new ArrayList<>();
    private final Set<IPatternDetails> patternSet = Collections.newSetFromMap(new IdentityHashMap<>());

    public ExtendedAssemblerMatrixPatternCoreBlockEntity(BlockEntityType<?> type, BlockPos pos,
            BlockState blockState) {
        this(type, pos, blockState, DEFAULT_INV_SIZE);
    }

    public ExtendedAssemblerMatrixPatternCoreBlockEntity(BlockEntityType<?> type, BlockPos pos,
            BlockState blockState, int patternSlotCount) {
        super(type, pos, blockState);
        this.patternInventory = new AppEngInternalInventory(this, patternSlotCount, 1);
        this.patternInventory.setFilter(new ExtendedPatternFilter(this::getLevel));
        this.getMainNode()
                .setIdlePowerUsage(EMAConfig.extendedAssemblerMatrixPatternCoreIdlePowerUsage(
                        patternSlotCount > DEFAULT_INV_SIZE))
                .addService(ICraftingProvider.class, this);
    }

    public AppEngInternalInventory getPatternInventory() {
        return this.patternInventory;
    }

    public InternalInventory getExposedInventory() {
        return this.patternInventory;
    }

    @Nullable
    public IItemHandler getPatternInv(Direction ignored) {
        return this.patternInventory.toItemHandler();
    }

    public long getLocateID() {
        return this.worldPosition.asLong();
    }

    @Override
    public void add(ClusterAssemblerMatrix cluster) {
        // This block participates in the matrix as a function block. Its extended patterns are exposed via its own
        // ICraftingProvider because ExtendedAE's cluster pattern list is typed to TileAssemblerMatrixPattern.
    }

    @Override
    public void updateStatus(ClusterAssemblerMatrix c) {
        super.updateStatus(c);
        this.updatePatterns("matrixFormed");
    }

    @Override
    public void onMainNodeStateChanged(IGridNodeListener.State reason) {
        super.onMainNodeStateChanged(reason);
        this.updatePatterns("nodeState:" + reason);
    }

    @Override
    public void saveAdditional(CompoundTag data, HolderLookup.Provider registries) {
        super.saveAdditional(data, registries);
        this.patternInventory.writeToNBT(data, "pattern", registries);
    }

    @Override
    public void loadTag(CompoundTag data, HolderLookup.Provider registries) {
        super.loadTag(data, registries);
        this.patternInventory.readFromNBT(data, "pattern", registries);
    }

    @Override
    public void onReady() {
        super.onReady();
        this.updatePatterns("onReady");
    }

    @Override
    public void saveChangedInventory(AppEngInternalInventory inv) {
        this.saveChanges();
        this.updatePatterns("inventoryChanged");
    }

    public void updatePatterns() {
        this.updatePatterns("manual");
    }

    private void updatePatterns(String reason) {
        this.patterns.clear();
        this.patternSet.clear();
        var level = getLevel();
        for (var stack : this.patternInventory) {
            if (PatternDetailsHelper.decodePattern(stack, level) instanceof ExtendedTableCraftingPattern pattern) {
                this.patterns.add(pattern);
                this.patternSet.add(pattern);
            }
        }
        this.requestCraftingProviderRefresh(reason);
    }

    private void requestCraftingProviderRefresh(String reason) {
        if (this.getMainNode().getNode() == null) {
            return;
        }
        ICraftingProvider.requestUpdate(this.getMainNode());
    }

    @Override
    public void addAdditionalDrops(Level level, BlockPos pos, List<ItemStack> drops) {
        super.addAdditionalDrops(level, pos, drops);
        for (var pattern : this.patternInventory) {
            drops.add(pattern);
        }
    }

    @Override
    public void clearContent() {
        super.clearContent();
        this.patternInventory.clear();
    }

    @Override
    public List<IPatternDetails> getAvailablePatterns() {
        return this.patterns;
    }

    @Override
    public boolean pushPattern(IPatternDetails patternDetails, KeyCounter[] inputHolder) {
        var formed = isFormed();
        var active = this.getMainNode().isActive();
        var knownPattern = this.patternSet.contains(patternDetails);
        if (!formed || !active || !knownPattern) {
            return false;
        }
        if (EMAConfig.tieredMode() && patternDetails instanceof ExtendedTableCraftingPattern pattern
                && !hasMatchingProvider(pattern)) {
            return false;
        }
        return this.cluster != null && this.cluster.pushCraftingJob(patternDetails, inputHolder);
    }

    private boolean hasMatchingProvider(ExtendedTableCraftingPattern pattern) {
        try {
            TieredMECraftingProviderTier.requiredFor(pattern.tableType(), pattern.tableTier());
        } catch (IllegalArgumentException ignored) {
            return false;
        }

        var grid = this.getMainNode().getGrid();
        if (grid == null) {
            return false;
        }

        for (var provider : grid.getActiveMachines(TieredMECraftingProviderBlockEntity.class)) {
            if (provider.getTier().provides(pattern.tableType(), pattern.tableTier()) && provider.isOnline()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isBusy() {
        // Keep the AE2 crafting provider selectable. Actual slot/worker availability is
        // checked in pushPattern()/ClusterAssemblerMatrixMixin. Returning busy here makes
        // AE2 skip pushPattern entirely.
        return this.cluster == null;
    }

    @Override
    public @Nullable IGrid getGrid() {
        return this.getMainNode().getGrid();
    }

    @Override
    public boolean isVisibleInTerminal() {
        return this.manager.getSetting(Settings.PATTERN_ACCESS_TERMINAL) == YesNo.YES;
    }

    @Override
    public InternalInventory getTerminalPatternInventory() {
        return this.patternInventory;
    }

    @Override
    public long getTerminalSortOrder() {
        return this.getLocateID();
    }

    @Override
    public PatternContainerGroup getTerminalGroup() {
        var icon = AEItemKey.of(EMAExtendedAEIntegration.EXTENDED_ASSEMBLER_MATRIX_PATTERN_CORE.get());
        var name = this.hasCustomName()
                ? this.getCustomName()
                : EMAExtendedAEIntegration.EXTENDED_ASSEMBLER_MATRIX_PATTERN_CORE_ITEM.get().getDescription();
        return new PatternContainerGroup(icon, name,
                List.of(Component.translatable(EMATranslationKey.GUI.MATRIX_PATTERN_CORE.key())));
    }

    public record ExtendedPatternFilter(Supplier<Level> world) implements IAEItemFilter {
        @Override
        public boolean allowInsert(InternalInventory inv, int slot, ItemStack stack) {
            return stack.getItem() instanceof EncodedPatternItem<?>
                    && PatternDetailsHelper.decodePattern(stack, world.get()) instanceof ExtendedTableCraftingPattern;
        }
    }
}
