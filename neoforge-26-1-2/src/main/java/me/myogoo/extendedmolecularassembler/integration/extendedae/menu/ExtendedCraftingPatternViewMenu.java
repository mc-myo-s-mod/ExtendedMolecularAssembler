package me.myogoo.extendedmolecularassembler.integration.extendedae.menu;

import appeng.api.stacks.GenericStack;
import appeng.client.Point;
import appeng.menu.slot.IOptionalSlot;
import com.glodblock.github.extendedae.container.pattern.ContainerPattern;
import com.glodblock.github.extendedae.container.pattern.PatternGuiHandler;
import me.myogoo.extendedmolecularassembler.ExtendedMolecularAssembler;
import me.myogoo.extendedmolecularassembler.pattern.ExtendedPatternTableTypes;
import me.myogoo.extendedmolecularassembler.pattern.ExtendedTableCraftingPattern;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public class ExtendedCraftingPatternViewMenu extends ContainerPattern {
    public static final Identifier ID = ExtendedMolecularAssembler.makeId("extended_crafting_pattern_view");
    public static final MenuType<ExtendedCraftingPatternViewMenu> TYPE =
            PatternGuiHandler.register(ID, ExtendedCraftingPatternViewMenu::new);

    public static final int GRID_SLOT_COUNT = ExtendedTableCraftingPattern.MACHINE_GRID_SIZE;
    public static final int OUTPUT_SLOT_INDEX = GRID_SLOT_COUNT;
    private static volatile Supplier<PatternViewLayout> clientLayoutProvider = () -> null;

    // ContainerPattern calls analyse() from its constructor, before subclass field initializers run.
    // These fields must therefore be initialized inside analyse().
    private boolean[] enabledSlots;
    private ItemStack tableStack;
    private int tableSideLength;
    private boolean canSubstitute;
    private boolean canSubstituteFluids;

    public ExtendedCraftingPatternViewMenu(@Nullable MenuType<?> menuType, int id, Level world, ItemStack stack) {
        super(menuType, world, id, stack);

        var layout = world.isClientSide() ? clientLayoutProvider.get() : null;
        if (world.isClientSide() && layout == null) {
            throw new IllegalStateException("Extended crafting pattern view client layout was not initialized");
        }

        for (int row = 0; row < ExtendedTableCraftingPattern.MACHINE_GRID_SIDE; row++) {
            for (int col = 0; col < ExtendedTableCraftingPattern.MACHINE_GRID_SIDE; col++) {
                var slot = row * ExtendedTableCraftingPattern.MACHINE_GRID_SIDE + col;
                var position = layout == null ? Point.ZERO : layout.craftingGrid().get(slot);
                var displaySlot = new OptionalDisplayOnlySlot(
                        this,
                        this.inputs,
                        slot,
                        position.getX(),
                        position.getY());
                displaySlot.setSlotEnabled(this.isSlotEnabled(slot));
                this.addSlot(displaySlot);
            }
        }
        var outputPosition = layout == null ? Point.ZERO : layout.output();
        this.addSlot(new OptionalDisplayOnlySlot(
                this,
                this.outputs,
                0,
                outputPosition.getX(),
                outputPosition.getY()));
    }

    public static void setClientLayoutProvider(Supplier<PatternViewLayout> provider) {
        clientLayoutProvider = Objects.requireNonNull(provider, "provider");
    }

    @Override
    protected void analyse() {
        this.enabledSlots = new boolean[ExtendedTableCraftingPattern.MACHINE_GRID_SIZE];
        this.tableStack = new ItemStack(Items.CRAFTING_TABLE);
        this.tableSideLength = ExtendedTableCraftingPattern.MACHINE_GRID_SIDE;

        if (!(this.details instanceof ExtendedTableCraftingPattern pattern)) {
            this.invalidate();
            return;
        }

        this.canSubstitute = pattern.canSubstitute();
        this.canSubstituteFluids = pattern.canSubstituteFluids();
        this.tableSideLength = pattern.tableSideLength();
        this.tableStack = tableStack(pattern.tableType());

        for (int slot = 0; slot < ExtendedTableCraftingPattern.MACHINE_GRID_SIZE; slot++) {
            this.enabledSlots[slot] = pattern.isMachineSlotInTable(slot);
            this.inputs.add(pattern.getDisplayInputsForMachineSlot(slot));
        }

        var rawOutputs = pattern.getSparseOutputs();
        if (rawOutputs.isEmpty()) {
            this.invalidate();
            return;
        }
        this.outputs.add(new GenericStack[] { rawOutputs.getFirst() });
    }

    public boolean canSubstitute() {
        return this.canSubstitute;
    }

    public boolean canSubstituteFluids() {
        return this.canSubstituteFluids;
    }

    public boolean isSlotEnabled(int machineSlot) {
        return this.enabledSlots != null
                && machineSlot >= 0
                && machineSlot < this.enabledSlots.length
                && this.enabledSlots[machineSlot];
    }

    public int tableSideLength() {
        return this.tableSideLength;
    }

    public ItemStack tableStack() {
        return this.tableStack;
    }

    public record PatternViewLayout(List<Point> craftingGrid, Point output) {
        public PatternViewLayout {
            craftingGrid = List.copyOf(craftingGrid);
            if (craftingGrid.size() != GRID_SLOT_COUNT) {
                throw new IllegalArgumentException(
                        "Expected " + GRID_SLOT_COUNT + " crafting grid positions, got " + craftingGrid.size());
            }
            output = Objects.requireNonNull(output, "output");
        }
    }

    private static ItemStack tableStack(Identifier tableType) {
        if (tableType.equals(ExtendedPatternTableTypes.VANILLA_CRAFTING)) {
            return new ItemStack(Items.CRAFTING_TABLE);
        }
        if (tableType.getNamespace().equals("extendedcrafting")) {
            return stackOrCraftingTable(tableType);
        }
        if (tableType.equals(ExtendedPatternTableTypes.RE_AVARITIA_SCULK)) {
            return stackOrCraftingTable(Identifier.fromNamespaceAndPath("avaritia", "sculk_crafting_table"));
        }
        if (tableType.equals(ExtendedPatternTableTypes.RE_AVARITIA_NETHER)) {
            return stackOrCraftingTable(Identifier.fromNamespaceAndPath("avaritia", "nether_crafting_table"));
        }
        if (tableType.equals(ExtendedPatternTableTypes.RE_AVARITIA_END)) {
            return stackOrCraftingTable(Identifier.fromNamespaceAndPath("avaritia", "end_crafting_table"));
        }
        if (tableType.equals(ExtendedPatternTableTypes.RE_AVARITIA_EXTREME)) {
            return stackOrCraftingTable(Identifier.fromNamespaceAndPath("avaritia", "extreme_crafting_table"));
        }
        return new ItemStack(Items.CRAFTING_TABLE);
    }

    private static ItemStack stackOrCraftingTable(Identifier id) {
        Item item = BuiltInRegistries.ITEM.getValue(id);
        return item == Items.AIR ? new ItemStack(Items.CRAFTING_TABLE) : new ItemStack(item);
    }

    private static final class OptionalDisplayOnlySlot extends DisplayOnlySlot implements IOptionalSlot {
        private boolean slotEnabled = true;

        private OptionalDisplayOnlySlot(
                ContainerPattern container,
                List<GenericStack[]> stacks,
                int index,
                int x,
                int y) {
            super(container, stacks, index, x, y);
        }

        private void setSlotEnabled(boolean slotEnabled) {
            this.slotEnabled = slotEnabled;
        }

        @Override
        public boolean isActive() {
            return this.slotEnabled;
        }

        @Override
        public ItemStack getItem() {
            return this.slotEnabled ? super.getItem() : ItemStack.EMPTY;
        }

        @Override
        public boolean isRenderDisabled() {
            return true;
        }

        @Override
        public boolean isSlotEnabled() {
            return this.slotEnabled;
        }

        @Override
        public Point getBackgroundPos() {
            return new Point(this.x - 1, this.y - 1);
        }
    }
}
