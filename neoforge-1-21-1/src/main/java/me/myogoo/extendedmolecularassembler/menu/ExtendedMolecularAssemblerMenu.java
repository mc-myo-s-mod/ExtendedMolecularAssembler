package me.myogoo.extendedmolecularassembler.menu;

import appeng.api.crafting.PatternDetailsHelper;
import appeng.api.stacks.AEItemKey;
import appeng.menu.SlotSemantics;
import appeng.menu.guisync.GuiSync;
import appeng.menu.guisync.PacketWritable;
import appeng.menu.implementations.MenuTypeBuilder;
import appeng.menu.implementations.UpgradeableMenu;
import appeng.menu.interfaces.IProgressProvider;
import appeng.menu.slot.AppEngSlot;
import it.unimi.dsi.fastutil.shorts.ShortSet;
import me.myogoo.extendedmolecularassembler.ExtendedMolecularAssembler;
import me.myogoo.extendedmolecularassembler.block.blockentity.ExtendedMolecularAssemblerBlockEntity;
import me.myogoo.extendedmolecularassembler.menu.slot.ExtendedMolecularAssemblerEncodedPatternSlot;
import me.myogoo.extendedmolecularassembler.menu.slot.ExtendedMolecularAssemblerOutputSlot;
import me.myogoo.extendedmolecularassembler.menu.slot.ExtendedMolecularAssemblerPatternSlot;
import me.myogoo.extendedmolecularassembler.pattern.ExtendedTableCraftingPattern;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ExtendedMolecularAssemblerMenu extends UpgradeableMenu<ExtendedMolecularAssemblerBlockEntity>
        implements IProgressProvider {
    public static final MenuType<ExtendedMolecularAssemblerMenu> TYPE = MenuTypeBuilder
            .create(ExtendedMolecularAssemblerMenu::new, ExtendedMolecularAssemblerBlockEntity.class)
            .buildUnregistered(ExtendedMolecularAssembler.makeId("extended_molecular_assembler"));

    private static final String ACTION_SET_PAGE = "setPage";
    private static final int MAX_CRAFT_PROGRESS = 100;
    private static final short PAGE_SYNC_ID = 7;

    @GuiSync(4)
    public int craftProgress = 0;
    @GuiSync(PAGE_SYNC_ID)
    public int page = 0;
    @GuiSync(8)
    public LanePatternSync lanePatterns = LanePatternSync.empty();

    private final ExtendedMolecularAssemblerBlockEntity assembler;
    private Slot encodedPatternSlot;
    private int shownPage = -1;
    private LanePatternSync decodedPatternSource = LanePatternSync.empty();
    private final boolean[] decodedPatternLoaded = new boolean[ExtendedMolecularAssemblerBlockEntity.PARALLEL_LANE_COUNT];
    private final ExtendedTableCraftingPattern[] decodedPatterns =
            new ExtendedTableCraftingPattern[ExtendedMolecularAssemblerBlockEntity.PARALLEL_LANE_COUNT];

    public ExtendedMolecularAssemblerMenu(int id, Inventory playerInv, ExtendedMolecularAssemblerBlockEntity be) {
        super(TYPE, id, playerInv, be);
        this.assembler = be;
        registerClientAction(ACTION_SET_PAGE, Integer.class, this::setPage);
        this.showPage();
    }

    public boolean isValidItemForSlot(int slotIndex, ItemStack stack) {
        return this.isValidItemForSlot(0, slotIndex, stack);
    }

    public boolean isValidItemForSlot(int laneIndex, int slotIndex, ItemStack stack) {
        var details = this.getCurrentPattern(laneIndex);
        return details != null && details.isItemValid(slotIndex, AEItemKey.of(stack), getHost().getLevel());
    }

    @Override
    public boolean isValidForSlot(Slot slot, ItemStack stack) {
        if (slot == this.encodedPatternSlot) {
            return PatternDetailsHelper.decodePattern(stack, getPlayer().level()) instanceof ExtendedTableCraftingPattern;
        }

        return super.isValidForSlot(slot, stack);
    }

    @Override
    protected void setupConfig() {
        for (int lane = 0; lane < this.getHost().getLaneCount(); lane++) {
            var inventory = this.getHost().getCraftInventory(lane);
            var gridSemantic = EMASlotSemantics.EXTENDED_MOLECULAR_ASSEMBLER_GRID[lane];

            for (int i = 0; i < ExtendedMolecularAssemblerBlockEntity.GRID_SIZE; i++) {
                this.addSlot(new ExtendedMolecularAssemblerPatternSlot(this, inventory, i, lane),
                        gridSemantic);
            }

            this.addSlot(new ExtendedMolecularAssemblerOutputSlot(this, inventory,
                            ExtendedMolecularAssemblerBlockEntity.OUTPUT_SLOT, lane),
                    EMASlotSemantics.EXTENDED_MOLECULAR_ASSEMBLER_OUTPUT[lane]);
        }

        if (this.getHost().getLaneCount() == 1) {
            this.encodedPatternSlot = this.addSlot(
                    new ExtendedMolecularAssemblerEncodedPatternSlot(this.getHost().getPatternInventory(), 0),
                    SlotSemantics.ENCODED_PATTERN);
        }
    }

    @Override
    public void broadcastChanges() {
        if (!this.lanePatterns.matchesCurrentPatterns(this.assembler)) {
            this.lanePatterns = LanePatternSync.from(this.assembler);
        }
        this.craftProgress = this.assembler.getCraftingProgress(this.page);
        this.standardDetectAndSendChanges();
    }

    @Override
    public void onServerDataSync(ShortSet updatedFields) {
        super.onServerDataSync(updatedFields);
        if (updatedFields.contains(PAGE_SYNC_ID)) {
            this.showPage();
        }
    }

    @Override
    public int getCurrentProgress() {
        return this.craftProgress;
    }

    @Override
    public int getMaxProgress() {
        return MAX_CRAFT_PROGRESS;
    }

    @Override
    public void onSlotChange(Slot slot) {
        if (slot == this.encodedPatternSlot) {
            for (Slot otherSlot : slots) {
                if (otherSlot != slot && otherSlot instanceof AppEngSlot appEngSlot) {
                    appEngSlot.resetCachedValidation();
                }
            }
        }
    }

    public int getPage() {
        return this.page;
    }

    public int getPageCount() {
        return this.getHost().getLaneCount();
    }

    public void showPage() {
        if (this.shownPage == this.page) {
            return;
        }
        this.shownPage = this.page;

        for (Slot slot : this.slots) {
            if (slot instanceof ExtendedMolecularAssemblerPatternSlot patternSlot) {
                var active = patternSlot.getLaneIndex() == this.page;
                patternSlot.setActive(active);
                patternSlot.setSlotEnabled(active);
                patternSlot.resetCachedValidation();
            } else if (slot instanceof ExtendedMolecularAssemblerOutputSlot outputSlot) {
                var active = outputSlot.getLaneIndex() == this.page;
                outputSlot.setActive(active);
                outputSlot.setSlotEnabled(active);
            }
        }
    }

    public ExtendedTableCraftingPattern getCurrentPattern(int laneIndex) {
        // Client block entities decode on every lookup; use the synchronized menu cache instead.
        if (!this.isClientSide()) {
            var hostPattern = this.getHost().getCurrentPattern(laneIndex);
            if (hostPattern != null) {
                return hostPattern;
            }
        }

        this.refreshDecodedPatternCache();
        if (laneIndex < 0 || laneIndex >= this.decodedPatterns.length) {
            return null;
        }

        if (!this.decodedPatternLoaded[laneIndex]) {
            this.decodedPatternLoaded[laneIndex] = true;
            var patternStack = this.lanePatterns.patternAt(laneIndex);
            var pattern = PatternDetailsHelper.decodePattern(patternStack, this.getPlayer().level());
            if (pattern instanceof ExtendedTableCraftingPattern extendedPattern) {
                this.decodedPatterns[laneIndex] = extendedPattern;
            }
        }
        return this.decodedPatterns[laneIndex];
    }

    public void selectPage(int page) {
        this.setPage(page);
        if (this.isClientSide()) {
            sendClientAction(ACTION_SET_PAGE, this.page);
        }
    }

    private void setPage(Integer page) {
        var maxPage = Math.max(0, this.getPageCount() - 1);
        var newPage = Mth.clamp(page, 0, maxPage);
        if (this.page == newPage) {
            return;
        }
        this.page = newPage;
        this.showPage();
    }

    private void refreshDecodedPatternCache() {
        if (this.decodedPatternSource == this.lanePatterns) {
            return;
        }

        for (int lane = 0; lane < this.decodedPatterns.length; lane++) {
            if (!ItemStack.matches(this.decodedPatternSource.patternAt(lane), this.lanePatterns.patternAt(lane))) {
                this.decodedPatternLoaded[lane] = false;
                this.decodedPatterns[lane] = null;
            }
        }
        this.decodedPatternSource = this.lanePatterns;
    }

    public record LanePatternSync(List<ItemStack> patterns) implements PacketWritable {
        public LanePatternSync {
            patterns = List.copyOf(patterns);
        }

        public LanePatternSync(RegistryFriendlyByteBuf data) {
            this(readPatterns(data));
        }

        public static LanePatternSync empty() {
            return new LanePatternSync(List.of());
        }

        public static LanePatternSync from(ExtendedMolecularAssemblerBlockEntity assembler) {
            var patterns = new ArrayList<ItemStack>(assembler.getLaneCount());
            for (int lane = 0; lane < assembler.getLaneCount(); lane++) {
                patterns.add(assembler.getCurrentPatternStack(lane));
            }
            return new LanePatternSync(patterns);
        }

        public boolean matchesCurrentPatterns(ExtendedMolecularAssemblerBlockEntity assembler) {
            if (this.patterns.size() != assembler.getLaneCount()) {
                return false;
            }

            for (int lane = 0; lane < assembler.getLaneCount(); lane++) {
                var pattern = assembler.getCurrentPattern(lane);
                var syncedStack = this.patterns.get(lane);
                if (pattern != null) {
                    if (syncedStack.getCount() != 1
                            || !ItemStack.isSameItemSameComponents(
                                    syncedStack, pattern.getDefinition().getReadOnlyStack())) {
                        return false;
                    }
                } else if (!ItemStack.matches(
                        syncedStack,
                        lane == 0 ? assembler.getPatternInventory().getStackInSlot(0) : ItemStack.EMPTY)) {
                    return false;
                }
            }
            return true;
        }

        public ItemStack patternAt(int laneIndex) {
            if (laneIndex < 0 || laneIndex >= this.patterns.size()) {
                return ItemStack.EMPTY;
            }
            return this.patterns.get(laneIndex);
        }

        @Override
        public void writeToPacket(RegistryFriendlyByteBuf data) {
            data.writeVarInt(this.patterns.size());
            for (var pattern : this.patterns) {
                ItemStack.OPTIONAL_STREAM_CODEC.encode(data, pattern);
            }
        }

        @Override
        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            if (!(object instanceof LanePatternSync other) || this.patterns.size() != other.patterns.size()) {
                return false;
            }
            for (int i = 0; i < this.patterns.size(); i++) {
                if (!ItemStack.matches(this.patterns.get(i), other.patterns.get(i))) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public int hashCode() {
            var result = 1;
            for (var pattern : this.patterns) {
                result = 31 * result + ItemStack.hashItemAndComponents(pattern);
                result = 31 * result + pattern.getCount();
            }
            return result;
        }

        private static List<ItemStack> readPatterns(RegistryFriendlyByteBuf data) {
            var count = data.readVarInt();
            var patterns = new ArrayList<ItemStack>(count);
            for (int i = 0; i < count; i++) {
                patterns.add(ItemStack.OPTIONAL_STREAM_CODEC.decode(data));
            }
            return patterns;
        }
    }
}
