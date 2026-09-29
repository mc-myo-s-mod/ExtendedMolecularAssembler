package me.myogoo.extendedmolecularassembler.block.blockentity;

import appeng.api.config.Actionable;
import appeng.api.config.PowerMultiplier;
import appeng.api.crafting.IPatternDetails;
import appeng.api.crafting.PatternDetailsHelper;
import appeng.api.implementations.IPowerChannelState;
import appeng.api.implementations.blockentities.ICraftingMachine;
import appeng.api.implementations.blockentities.PatternContainerGroup;
import appeng.api.inventories.ISegmentedInventory;
import appeng.api.inventories.InternalInventory;
import appeng.api.networking.energy.IEnergyService;
import appeng.api.networking.IGridNode;
import appeng.api.networking.IGridNodeListener;
import appeng.api.networking.ticking.IGridTickable;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.networking.ticking.TickingRequest;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.KeyCounter;
import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.upgrades.IUpgradeableObject;
import appeng.api.upgrades.UpgradeInventories;
import appeng.api.util.AECableType;
import appeng.blockentity.grid.AENetworkedInvBlockEntity;
import appeng.blockentity.crafting.MolecularAssemblerAnimationStatus;
import appeng.core.AELog;
import appeng.core.definitions.AEItems;
import appeng.core.localization.GuiText;
import appeng.core.localization.Tooltips;
import appeng.crafting.CraftingEvent;
import appeng.menu.AutoCraftingMenu;
import appeng.util.inv.AppEngInternalInventory;
import appeng.util.inv.CombinedInternalInventory;
import appeng.util.inv.FilteredInternalInventory;
import appeng.util.inv.filter.IAEItemFilter;
import me.myogoo.extendedmolecularassembler.ExtendedMolecularAssembler;
import me.myogoo.extendedmolecularassembler.block.ExportMECraftingProviderTier;
import me.myogoo.extendedmolecularassembler.config.EMAConfig;
import me.myogoo.extendedmolecularassembler.init.EMABlocks;
import me.myogoo.extendedmolecularassembler.init.EMAOptionalIntegrations;
import me.myogoo.extendedmolecularassembler.network.clientbound.EMAAssemblerAnimationPacket;
import me.myogoo.extendedmolecularassembler.pattern.ExtendedTableCraftingPattern;
import me.myogoo.extendedmolecularassembler.lang.EMATranslationKey;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.TransientCraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import me.myogoo.extendedmolecularassembler.crafting.AssemblerSpeedProfile;

public class ExtendedMolecularAssemblerBlockEntity extends AENetworkedInvBlockEntity
        implements IUpgradeableObject, IGridTickable, ICraftingMachine, IPowerChannelState {
    public static final Identifier INV_MAIN = ExtendedMolecularAssembler.makeId("extended_molecular_assembler");
    public static final int GRID_SIZE = ExtendedTableCraftingPattern.MACHINE_GRID_SIZE;
    public static final int OUTPUT_SLOT = GRID_SIZE;
    public static final int PATTERN_SLOT = GRID_SIZE + 1;

    public static final int PARALLEL_LANE_COUNT = 8;
    private static final int LANE_SIZE = GRID_SIZE + 1;
    private static final Direction[] DIRECTIONS = Direction.values();
    private final CraftingLane[] lanes = new CraftingLane[PARALLEL_LANE_COUNT];
    private final int laneCount;
    private final Block machineBlock;
    private final AppEngInternalInventory patternInv = new AppEngInternalInventory(this, 1, 1);
    private final InternalInventory internalInv;
    private final InternalInventory gridInvExt;
    private final IUpgradeInventory upgrades;
    private boolean isPowered = false;
    private boolean isAwake = false;
    @Nullable
    private AEItemKey queuedAnimationItem;
    private byte queuedAnimationRate;
    @Nullable
    private Component lastTierRejectReason;
    private MolecularAssemblerAnimationStatus animationStatus;

    public ExtendedMolecularAssemblerBlockEntity(BlockEntityType<?> blockEntityType, BlockPos pos,
            BlockState blockState) {
        super(blockEntityType, pos, blockState);
        this.machineBlock = getMachineBlock(blockState);
        this.laneCount = isExAssemblerBlock(blockState) ? PARALLEL_LANE_COUNT : 1;
        getMainNode()
                .setIdlePowerUsage(EMAConfig.extendedMolecularAssemblerIdlePowerUsage(isExAssemblerBlock(blockState)))
                .addService(IGridTickable.class, this);
        this.upgrades = UpgradeInventories.forMachine(this.machineBlock, 5,
                this::saveChanges);

        for (int i = 0; i < PARALLEL_LANE_COUNT; i++) {
            this.lanes[i] = new CraftingLane(i);
        }

        var inventories = new InternalInventory[this.laneCount + 1];
        inventories[0] = this.lanes[0].gridInv;
        inventories[1] = this.patternInv;
        for (int i = 1; i < this.laneCount; i++) {
            inventories[i + 1] = this.lanes[i].gridInv;
        }
        this.internalInv = new CombinedInternalInventory(inventories);

        var exposedInventories = new InternalInventory[this.laneCount];
        for (int i = 0; i < this.laneCount; i++) {
            exposedInventories[i] = this.lanes[i].gridInvExt;
        }
        this.gridInvExt = new CombinedInternalInventory(exposedInventories);
    }

    private static Block getMachineBlock(BlockState blockState) {
        if (isExAssemblerBlock(blockState)) {
            return EMABlocks.EX_EXTENDED_MOLECULAR_ASSEMBLER.get();
        }
        return EMABlocks.EXTENDED_MOLECULAR_ASSEMBLER.get();
    }

    private static boolean isExAssemblerBlock(BlockState blockState) {
        return EMABlocks.EX_EXTENDED_MOLECULAR_ASSEMBLER != null
                && blockState.is(EMABlocks.EX_EXTENDED_MOLECULAR_ASSEMBLER.get());
    }

    private boolean isExAssembler() {
        return EMABlocks.EX_EXTENDED_MOLECULAR_ASSEMBLER != null
                && this.machineBlock == EMABlocks.EX_EXTENDED_MOLECULAR_ASSEMBLER.get();
    }

    @Override
    public PatternContainerGroup getCraftingMachineInfo() {
        var name = hasCustomName()
                ? getCustomName()
                : this.machineBlock.asItem().getDefaultInstance().getItemName();
        var icon = AEItemKey.of(this.machineBlock);

        List<Component> tooltip = new ArrayList<>();
        var accelerationCards = getInstalledUpgrades(AEItems.SPEED_CARD);
        if (accelerationCards != 0) {
            tooltip.add(GuiText.CompatibleUpgrade.text(
                    Tooltips.of(AEItems.SPEED_CARD.asItem().getDefaultInstance().getItemName()),
                    Tooltips.ofUnformattedNumber(accelerationCards)));
        }
        if (EMAConfig.exportMode()) {
            tooltip.add(Component.translatable(EMATranslationKey.TOOLTIP.EXPORT_MODE_ENABLED.key()));
            if (this.lastTierRejectReason != null) {
                tooltip.add(Component.translatable(EMATranslationKey.TOOLTIP.EXPORT_MODE_LAST_REJECT.key(),
                        this.lastTierRejectReason));
            }
        }

        return new PatternContainerGroup(icon, name, tooltip);
    }

    @Override
    public boolean pushPattern(IPatternDetails patternDetails, KeyCounter[] table, Direction where) {
        if (!(patternDetails instanceof ExtendedTableCraftingPattern pattern)) {
            return false;
        }

        if (!this.isTierAllowed(pattern)) {
            return false;
        }

        for (int i = 0; i < this.laneCount; i++) {
            var lane = this.lanes[i];
            if (lane.acceptJob(pattern, table, where)) {
                this.clearTierRejectReason();
                return true;
            }
        }
        return false;
    }

    private boolean isTierAllowed(ExtendedTableCraftingPattern pattern) {
        if (!EMAConfig.exportMode()) {
            this.clearTierRejectReason();
            return true;
        }

        final int tableTier = pattern.tableTier();
        final ExportMECraftingProviderTier providerTier;
        try {
            providerTier = ExportMECraftingProviderTier.requiredFor(pattern.tableType(), tableTier);
        } catch (IllegalArgumentException ignored) {
            this.setTierRejectReason(Component.translatable(
                    EMATranslationKey.TOOLTIP.EXPORT_MODE_UNSUPPORTED_TIER.key(),
                    ExportMECraftingProviderTier.tierName(tableTier), tableTier));
            return false;
        }

        var grid = this.getMainNode().getGrid();
        if (grid == null) {
            this.setTierRejectReason(Component.translatable(
                    EMATranslationKey.TOOLTIP.EXPORT_MODE_OFFLINE_GRID.key(),
                    providerTier.displayName(), tableTier));
            return false;
        }

        for (var provider : grid.getActiveMachines(ExportMECraftingProviderBlockEntity.class)) {
            if (provider.getTier().provides(pattern.tableType(), tableTier) && provider.isOnline()) {
                this.clearTierRejectReason();
                return true;
            }
        }

        this.setTierRejectReason(Component.translatable(
                EMATranslationKey.TOOLTIP.EXPORT_MODE_MISSING_PROVIDER.key(),
                providerTier.displayName(), tableTier));
        return false;
    }

    private void setTierRejectReason(Component reason) {
        this.lastTierRejectReason = reason;
        this.markForUpdate();
    }

    private void clearTierRejectReason() {
        if (this.lastTierRejectReason != null) {
            this.lastTierRejectReason = null;
            this.markForUpdate();
        }
    }

    private void updateSleepiness() {
        var awake = false;
        for (int i = 0; i < this.laneCount; i++) {
            var lane = this.lanes[i];
            if (lane.isAwake) {
                awake = true;
                break;
            }
        }

        final boolean wasEnabled = this.isAwake;
        this.isAwake = awake;
        if (wasEnabled != this.isAwake) {
            getMainNode().ifPresent((grid, node) -> {
                if (this.isAwake) {
                    grid.getTickManager().wakeDevice(node);
                } else {
                    grid.getTickManager().sleepDevice(node);
                }
            });
        }
    }

    @Override
    public boolean acceptsPlans() {
        for (int i = 0; i < this.laneCount; i++) {
            var lane = this.lanes[i];
            if (lane.canAcceptJob()) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected boolean readFromStream(RegistryFriendlyByteBuf data) {
        final boolean changed = super.readFromStream(data);
        final boolean oldPower = this.isPowered;
        this.isPowered = data.readBoolean();
        return this.isPowered != oldPower || changed;
    }

    @Override
    protected void writeToStream(RegistryFriendlyByteBuf data) {
        super.writeToStream(data);
        data.writeBoolean(this.isPowered);
    }

    @Override
    public void saveAdditional(ValueOutput data) {
        super.saveAdditional(data);
        this.lanes[0].savePlan(data, "myPlan", "pushDirection");
        for (int i = 1; i < this.laneCount; i++) {
            this.lanes[i].savePlan(data.child("lane" + i), "myPlan", "pushDirection");
        }
        this.upgrades.writeToNBT(data, "upgrades");
    }

    @Override
    public void loadTag(ValueInput data) {
        super.loadTag(data);

        this.lanes[0].loadPlan(data, "myPlan", "pushDirection");
        for (int i = 1; i < PARALLEL_LANE_COUNT; i++) {
            if (i < this.laneCount) {
                this.lanes[i].loadPlan(data.childOrEmpty("lane" + i), "myPlan", "pushDirection");
            } else {
                this.lanes[i].resetPlan();
            }
        }

        this.upgrades.readFromNBT(data, "upgrades");
        for (int i = 0; i < this.laneCount; i++) {
            var lane = this.lanes[i];
            lane.recalculatePlan();
        }
        this.updateSleepiness();
    }

    @Override
    public AECableType getCableConnectionType(Direction dir) {
        return AECableType.COVERED;
    }

    @Override
    public InternalInventory getSubInventory(Identifier id) {
        if (id.equals(ISegmentedInventory.UPGRADES)) {
            return this.upgrades;
        } else if (id.equals(INV_MAIN)) {
            return this.internalInv;
        }
        return super.getSubInventory(id);
    }

    @Override
    public InternalInventory getInternalInventory() {
        return this.internalInv;
    }

    @Override
    protected InternalInventory getExposedInventoryForSide(Direction side) {
        return this.gridInvExt;
    }

    @Override
    public void onChangeInventory(AppEngInternalInventory inv, int slot) {
        if (inv == this.patternInv) {
            this.lanes[0].recalculatePlan();
            return;
        }

        var lane = this.getLane(inv);
        if (lane != null) {
            if (lane.isBulkUpdatingGrid()) {
                return;
            }
            if (slot == OUTPUT_SLOT) {
                lane.updateSleepiness();
            } else {
                lane.recalculatePlan();
            }
        }
    }

    @Nullable
    private CraftingLane getLane(AppEngInternalInventory inv) {
        for (int i = 0; i < this.laneCount; i++) {
            var lane = this.lanes[i];
            if (lane.gridInv == inv) {
                return lane;
            }
        }
        return null;
    }

    public int getCraftingProgress() {
        return this.getCraftingProgress(0);
    }

    public int getCraftingProgress(int laneIndex) {
        var lane = this.getLane(laneIndex);
        return lane == null ? 0 : lane.getCraftingProgress();
    }

    public int getLaneCount() {
        return this.laneCount;
    }

    public boolean isParallelAssembler() {
        return this.laneCount > 1;
    }

    public InternalInventory getCraftInventory(int laneIndex) {
        var lane = this.getLane(laneIndex);
        return lane == null ? this.lanes[0].gridInv : lane.gridInv;
    }

    public InternalInventory getPatternInventory() {
        return this.patternInv;
    }

    @Override
    public void addAdditionalDrops(Level level, BlockPos pos, List<ItemStack> drops) {
        super.addAdditionalDrops(level, pos, drops);
        for (var upgrade : upgrades) {
            drops.add(upgrade);
        }
    }

    @Override
    public void clearContent() {
        super.clearContent();
        upgrades.clear();
    }

    @Override
    public TickingRequest getTickingRequest(IGridNode node) {
        for (int i = 0; i < this.laneCount; i++) {
            var lane = this.lanes[i];
            lane.recalculatePlan();
            lane.updateSleepiness();
        }
        this.updateSleepiness();
        return new TickingRequest(1, 1, !this.isAwake);
    }

    @Override
    public TickRateModulation tickingRequest(IGridNode node, int ticksSinceLastCall) {
        var rate = TickRateModulation.SLEEP;
        var speedProfile = getSpeedProfile();
        var energyService = node.getGrid().getEnergyService();
        var powerMultiplier = EMAConfig.extendedMolecularAssemblerCraftingPowerMultiplier(this.isExAssembler());
        this.queuedAnimationItem = null;
        for (int i = 0; i < this.laneCount; i++) {
            var lane = this.lanes[i];
            if (lane.isAwake) {
                var laneRate = lane.tick(node, ticksSinceLastCall, speedProfile, energyService, powerMultiplier);
                if (laneRate.ordinal() > rate.ordinal()) {
                    rate = laneRate;
                }
            }
        }
        this.flushCraftingAnimation(node);
        return rate;
    }

    private int userPower(IEnergyService energyService, int ticksPassed, int bonusValue, double acceleratorTax,
            double powerMultiplier) {
        var progress = ticksPassed * bonusValue;
        if (powerMultiplier <= 0) {
            return progress;
        }

        var requestedPower = progress * acceleratorTax * powerMultiplier;
        return (int) (energyService.extractAEPower(requestedPower,
                Actionable.MODULATE, PowerMultiplier.CONFIG) / acceleratorTax / powerMultiplier);
    }

    private AssemblerSpeedProfile getSpeedProfile() {
        return AssemblerSpeedProfile.forUpgrades(this.isExAssembler(), this.upgrades.getInstalledUpgrades(AEItems.SPEED_CARD));
    }

    @Override
    public void onMainNodeStateChanged(IGridNodeListener.State reason) {
        if (reason != IGridNodeListener.State.GRID_BOOT) {
            boolean newState = false;

            var grid = getMainNode().getGrid();
            if (grid != null) {
                newState = this.getMainNode().isPowered() && grid.getEnergyService().extractAEPower(1,
                        Actionable.SIMULATE, PowerMultiplier.CONFIG) > 0.0001;
            }

            if (newState != this.isPowered) {
                this.isPowered = newState;
                this.markForUpdate();
            }
        }
    }

    @Override
    public boolean isPowered() {
        return this.isPowered;
    }

    @Override
    public boolean isActive() {
        return this.isPowered;
    }
    public void setAnimationStatus(@Nullable MolecularAssemblerAnimationStatus status) {
        this.animationStatus = status;
    }
    @Nullable
    public MolecularAssemblerAnimationStatus getAnimationStatus() {
        return this.animationStatus;
    }

    @Override
    public IUpgradeInventory getUpgrades() {
        return upgrades;
    }

    @Nullable
    public ExtendedTableCraftingPattern getCurrentPattern() {
        return this.getCurrentPattern(0);
    }

    public ItemStack getCurrentPatternStack(int laneIndex) {
        var lane = this.getLane(laneIndex);
        if (lane == null) {
            return ItemStack.EMPTY;
        }

        if (lane.myPlan != null) {
            return lane.myPlan.getDefinition().toStack();
        }
        if (!lane.myPattern.isEmpty()) {
            return lane.myPattern.copy();
        }
        if (laneIndex == 0) {
            return this.patternInv.getStackInSlot(0).copy();
        }
        return ItemStack.EMPTY;
    }

    @Nullable
    public ExtendedTableCraftingPattern getCurrentPattern(int laneIndex) {
        var lane = this.getLane(laneIndex);
        if (lane == null) {
            return null;
        }

        if (isClientSide()) {
            if (laneIndex != 0) {
                return null;
            }
            var patternItem = patternInv.getStackInSlot(0);
            var pattern = PatternDetailsHelper.decodePattern(patternItem, level);
            if (pattern instanceof ExtendedTableCraftingPattern supportedPattern) {
                return supportedPattern;
            }
            return null;
        }
        return lane.myPlan;
    }

    @Nullable
    private CraftingLane getLane(int laneIndex) {
        if (laneIndex < 0 || laneIndex >= this.laneCount) {
            return null;
        }
        return this.lanes[laneIndex];
    }

    private class CraftingLane {
        private final int index;
        private final CraftingContainer craftingInv = new TransientCraftingContainer(new AutoCraftingMenu(),
                ExtendedTableCraftingPattern.MACHINE_GRID_SIDE,
                ExtendedTableCraftingPattern.MACHINE_GRID_SIDE);
        private final AppEngInternalInventory gridInv = new AppEngInternalInventory(
                ExtendedMolecularAssemblerBlockEntity.this, LANE_SIZE, 1);
        private final InternalInventory gridInvExt =
                new FilteredInternalInventory(this.gridInv, new CraftingGridFilter(this));
        private Direction pushDirection = null;
        private ItemStack myPattern = ItemStack.EMPTY;
        private ExtendedTableCraftingPattern myPlan = null;
        private double progress = 0;
        private boolean isAwake = false;
        private boolean forcePlan = false;
        private boolean reboot = true;
        private boolean bulkUpdatingGrid = false;
        private boolean materialsDirty = true;
        private boolean materialsReady = false;

        private CraftingLane(int index) {
            this.index = index;
        }

        private boolean canAcceptJob() {
            if (this.index == 0 && !ExtendedMolecularAssemblerBlockEntity.this.patternInv.isEmpty()) {
                return false;
            }
            return this.myPlan == null && !this.forcePlan && this.myPattern.isEmpty() && this.gridInv.isEmpty();
        }

        private boolean acceptJob(ExtendedTableCraftingPattern pattern, KeyCounter[] table, Direction where) {
            if (!this.canAcceptJob()) {
                return false;
            }

            try {
                this.forcePlan = true;
                this.myPlan = pattern;
                this.pushDirection = where;

                this.fillGrid(table, pattern);

                this.updateSleepiness();
                ExtendedMolecularAssemblerBlockEntity.this.saveChanges();
                return true;
            } catch (RuntimeException e) {
                this.forcePlan = false;
                this.myPlan = null;
                this.pushDirection = null;
                throw e;
            }
        }

        private void fillGrid(KeyCounter[] table, ExtendedTableCraftingPattern pattern) {
            this.bulkUpdateGrid(() -> pattern.fillCraftingGrid(table, this.gridInv::setItemDirect));
            this.markMaterialsDirty();

            for (var list : table) {
                list.removeZeros();
                if (!list.isEmpty()) {
                    throw new RuntimeException("Could not fill grid with some items, including "
                            + list.iterator().next());
                }
            }
        }

        private boolean hasMats() {
            if (this.myPlan == null) {
                this.materialsReady = false;
                this.materialsDirty = false;
                return false;
            }

            if (this.progress > 0 && !this.materialsDirty) {
                return true;
            }

            if (this.materialsDirty) {
                this.materialsReady = !this.myPlan.assembleFromMachineGrid(this.gridInv::getStackInSlot,
                        ExtendedMolecularAssemblerBlockEntity.this.getLevel()).isEmpty();
                this.materialsDirty = false;
            }

            return this.materialsReady;
        }

        private void updateSleepiness() {
            final boolean wasEnabled = this.isAwake;
            this.isAwake = !this.gridInv.getStackInSlot(OUTPUT_SLOT).isEmpty()
                    || this.myPlan != null && this.hasMats();
            if (wasEnabled != this.isAwake) {
                ExtendedMolecularAssemblerBlockEntity.this.updateSleepiness();
            }
        }

        private int getCraftingProgress() {
            return (int) this.progress;
        }

        private TickRateModulation tick(IGridNode node, int ticksSinceLastCall, AssemblerSpeedProfile speedProfile,
                IEnergyService energyService, double powerMultiplier) {
            if (!this.gridInv.getStackInSlot(OUTPUT_SLOT).isEmpty()) {
                this.pushOut(this.gridInv.getStackInSlot(OUTPUT_SLOT));

                if (this.gridInv.getStackInSlot(OUTPUT_SLOT).isEmpty()) {
                    ExtendedMolecularAssemblerBlockEntity.this.saveChanges();
                }

                this.ejectHeldItems();
                this.updateSleepiness();
                this.progress = 0;
                return this.isAwake ? TickRateModulation.IDLE : TickRateModulation.SLEEP;
            }

            if (this.myPlan == null) {
                this.updateSleepiness();
                return TickRateModulation.SLEEP;
            }

            if (this.reboot) {
                ticksSinceLastCall = 1;
            }

            if (!this.isAwake) {
                return TickRateModulation.SLEEP;
            }

            this.reboot = false;
            var speed = speedProfile.speed();
            this.progress += ExtendedMolecularAssemblerBlockEntity.this.userPower(energyService,
                    ticksSinceLastCall, speed, speedProfile.acceleratorTax(), powerMultiplier);

            if (this.progress >= 100) {
                this.progress = 0;
                final ItemStack output = this.myPlan.assembleFromMachineGrid(this.gridInv::getStackInSlot,
                        ExtendedMolecularAssemblerBlockEntity.this.getLevel());
                if (!output.isEmpty()) {
                    for (int i = 0; i < GRID_SIZE; i++) {
                        this.craftingInv.setItem(i, this.gridInv.getStackInSlot(i));
                    }
                    output.onCraftedBySystem(level);
                    CraftingEvent.fireAutoCraftingEvent(ExtendedMolecularAssemblerBlockEntity.this.getLevel(),
                            this.myPlan, output, this.craftingInv);

                    var craftingRemainders = this.myPlan.getRemainingItemsFromMachineGrid(this.gridInv::getStackInSlot);
                    this.pushOut(output.copy());
                    this.bulkUpdateGrid(() -> {
                        for (int i = 0; i < GRID_SIZE; i++) {
                            var remainder = i < craftingRemainders.size() ? craftingRemainders.get(i) : ItemStack.EMPTY;
                            this.gridInv.setItemDirect(i, remainder);
                        }
                    });
                    this.markMaterialsDirty();

                    if (this.index != 0 || ExtendedMolecularAssemblerBlockEntity.this.patternInv.isEmpty()) {
                        this.forcePlan = false;
                        this.myPlan = null;
                        this.pushDirection = null;
                    }

                    this.ejectHeldItems();
                    ExtendedMolecularAssemblerBlockEntity.this.queueCraftingAnimation(speed, output);

                    ExtendedMolecularAssemblerBlockEntity.this.saveChanges();
                    this.updateSleepiness();
                    return this.isAwake ? TickRateModulation.IDLE : TickRateModulation.SLEEP;
                }
            }

            return TickRateModulation.FASTER;
        }

        private void ejectHeldItems() {
            if (this.gridInv.getStackInSlot(OUTPUT_SLOT).isEmpty()) {
                for (int i = 0; i < GRID_SIZE; i++) {
                    final ItemStack stack = this.gridInv.getStackInSlot(i);
                    if (!stack.isEmpty()
                            && (this.myPlan == null || !this.myPlan.isItemValid(i, AEItemKey.of(stack), level))) {
                        final int slot = i;
                        this.bulkUpdateGrid(() -> {
                            this.gridInv.setItemDirect(OUTPUT_SLOT, stack);
                            this.gridInv.setItemDirect(slot, ItemStack.EMPTY);
                        });
                        this.markMaterialsDirty();
                        ExtendedMolecularAssemblerBlockEntity.this.saveChanges();
                        return;
                    }
                }
            }
        }

        private void pushOut(ItemStack output) {
            if (this.pushDirection == null) {
                for (Direction direction : DIRECTIONS) {
                    output = this.pushTo(output, direction);
                }
            } else {
                output = this.pushTo(output, this.pushDirection);
            }

            if (output.isEmpty() && this.forcePlan) {
                this.forcePlan = false;
                this.recalculatePlan();
            }

            this.gridInv.setItemDirect(OUTPUT_SLOT, output);
        }

        private ItemStack pushTo(ItemStack output, Direction direction) {
            if (output.isEmpty()) {
                return output;
            }

            final int matrixOutputSize = output.getCount();
            output = EMAOptionalIntegrations.tryInsertIntoExtendedAEAssemblerMatrix(
                    ExtendedMolecularAssemblerBlockEntity.this.getLevel(),
                    worldPosition.relative(direction),
                    output);
            if (output.isEmpty()) {
                ExtendedMolecularAssemblerBlockEntity.this.saveChanges();
                return output;
            } else if (output.getCount() != matrixOutputSize) {
                ExtendedMolecularAssemblerBlockEntity.this.saveChanges();
            }

            var adaptor = InternalInventory.wrapExternal(ExtendedMolecularAssemblerBlockEntity.this.getLevel(),
                    worldPosition.relative(direction), direction.getOpposite());
            if (adaptor == null) {
                return output;
            }

            final int size = output.getCount();
            output = adaptor.addItems(output);
            final int newSize = output.isEmpty() ? 0 : output.getCount();

            if (size != newSize) {
                ExtendedMolecularAssemblerBlockEntity.this.saveChanges();
            }

            return output;
        }

        private void savePlan(ValueOutput data, String planKey, String directionKey) {
            if (this.forcePlan) {
                var pattern = this.myPlan != null ? this.myPlan.getDefinition().toStack() : this.myPattern;
                if (!pattern.isEmpty()) {
                    data.store(planKey, ItemStack.OPTIONAL_CODEC, pattern);
                    data.putInt(directionKey, this.pushDirection == null ? -1 : this.pushDirection.ordinal());
                }
            }
        }

        private void loadPlan(ValueInput data, String planKey, String directionKey) {
            this.resetPlan();

            var pattern = data.read(planKey, ItemStack.OPTIONAL_CODEC).orElse(ItemStack.EMPTY);
            if (!pattern.isEmpty()) {
                this.forcePlan = true;
                this.myPattern = pattern;
                var direction = data.getIntOr(directionKey, -1);
                this.pushDirection = direction >= 0 && direction < DIRECTIONS.length
                        ? DIRECTIONS[direction]
                        : null;
            }
        }

        private void resetPlan() {
            this.forcePlan = false;
            this.myPattern = ItemStack.EMPTY;
            this.myPlan = null;
            this.pushDirection = null;
            this.progress = 0;
            this.materialsReady = false;
            this.materialsDirty = false;
        }

        private void recalculatePlan() {
            this.reboot = true;
            this.markMaterialsDirty();

            if (this.forcePlan) {
                if (getLevel() != null && this.myPlan == null) {
                    if (!this.myPattern.isEmpty()) {
                        if (PatternDetailsHelper.decodePattern(this.myPattern,
                                getLevel()) instanceof ExtendedTableCraftingPattern pattern) {
                            this.myPlan = pattern;
                        }
                    }

                    var unresolvedPattern = this.myPattern;
                    this.myPattern = ItemStack.EMPTY;

                    if (this.myPlan == null) {
                        AELog.warn("Unable to restore extended auto-crafting pattern after load: %s",
                                unresolvedPattern);
                        this.forcePlan = false;
                    }
                }

                return;
            }

            if (this.index != 0) {
                this.progress = 0;
                this.myPlan = null;
                this.myPattern = ItemStack.EMPTY;
                this.pushDirection = null;
                this.materialsReady = false;
                this.materialsDirty = false;
                this.updateSleepiness();
                return;
            }

            final ItemStack patternStack = ExtendedMolecularAssemblerBlockEntity.this.patternInv.getStackInSlot(0);
            boolean reset = true;

            if (!patternStack.isEmpty()) {
                if (ItemStack.isSameItemSameComponents(patternStack, this.myPattern)) {
                    reset = false;
                } else if (PatternDetailsHelper.decodePattern(patternStack,
                        getLevel()) instanceof ExtendedTableCraftingPattern pattern) {
                    reset = false;
                    this.progress = 0;
                    this.myPattern = patternStack;
                    this.myPlan = pattern;
                }
            }

            if (reset) {
                this.progress = 0;
                this.forcePlan = false;
                this.myPlan = null;
                this.myPattern = ItemStack.EMPTY;
                this.pushDirection = null;
                this.materialsReady = false;
                this.materialsDirty = false;
            }

            this.updateSleepiness();
        }

        private void markMaterialsDirty() {
            this.materialsDirty = true;
        }

        private boolean isBulkUpdatingGrid() {
            return this.bulkUpdatingGrid;
        }

        private void bulkUpdateGrid(Runnable updater) {
            var wasBulkUpdating = this.bulkUpdatingGrid;
            this.bulkUpdatingGrid = true;
            try {
                updater.run();
            } finally {
                this.bulkUpdatingGrid = wasBulkUpdating;
            }
        }
    }

    private void queueCraftingAnimation(int speed, ItemStack output) {
        var item = AEItemKey.of(output);
        if (item == null) {
            return;
        }
        this.queuedAnimationRate = (byte) speed;
        this.queuedAnimationItem = item;
    }

    private void flushCraftingAnimation(IGridNode node) {
        var item = this.queuedAnimationItem;
        if (item == null) {
            return;
        }
        this.queuedAnimationItem = null;
        var level = node.getLevel();
        PacketDistributor.sendToPlayersNear(level, null, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(),
                32, new EMAAssemblerAnimationPacket(worldPosition, this.queuedAnimationRate, item));
    }

    private class CraftingGridFilter implements IAEItemFilter {
        private final CraftingLane lane;

        private CraftingGridFilter(CraftingLane lane) {
            this.lane = lane;
        }

        private boolean hasPattern() {
            return this.lane.index == 0
                    && this.lane.myPlan != null
                    && !ExtendedMolecularAssemblerBlockEntity.this.patternInv.isEmpty();
        }

        @Override
        public boolean allowExtract(InternalInventory inv, int slot, int amount) {
            return slot == OUTPUT_SLOT;
        }

        @Override
        public boolean allowInsert(InternalInventory inv, int slot, ItemStack stack) {
            if (slot >= OUTPUT_SLOT) {
                return false;
            }

            if (this.hasPattern()) {
                return this.lane.myPlan.isItemValid(slot, AEItemKey.of(stack),
                        ExtendedMolecularAssemblerBlockEntity.this.getLevel());
            }
            return false;
        }
    }
}
