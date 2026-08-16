package me.myogoo.extendedmolecularassembler.integration.extendedae;

import appeng.api.crafting.IPatternDetails;
import appeng.api.stacks.KeyCounter;
import appeng.menu.slot.IOptionalSlot;
import com.glodblock.github.extendedae.common.me.matrix.ClusterAssemblerMatrix;
import me.myogoo.extendedmolecularassembler.ExtendedMolecularAssembler;
import me.myogoo.extendedmolecularassembler.integration.extendedae.menu.ExtendedCraftingPatternViewMenu;
import me.myogoo.extendedmolecularassembler.pattern.ExtendedTableCraftingPattern;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.items.IItemHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class ExtendedAEGameTestHelper {
    private ExtendedAEGameTestHelper() {
    }

    public static void assertPatternCoreAcceptsOnlyExtendedEncodedPatterns(
            GameTestHelper helper,
            ItemStack patternStack) {
        var core = placeOptionalBlockEntity(
                helper,
                "extended_assembler_matrix_pattern_core",
                new BlockPos(1, 1, 1),
                ExtendedAssemblerMatrixPatternCoreBlockEntity.class);
        var patternInventory = core.getPatternInv(Direction.NORTH);
        helper.assertTrue(patternInventory instanceof IItemHandler,
                "ExtendedAE pattern core did not expose an item handler");
        var itemHandler = (IItemHandler) patternInventory;

        var diamondRemainder = itemHandler.insertItem(0, new ItemStack(Items.DIAMOND), true);
        helper.assertTrue(!diamondRemainder.isEmpty(),
                "ExtendedAE pattern core accepted a non-pattern item into its pattern inventory");
        var patternRemainder = itemHandler.insertItem(0, patternStack.copy(), false);
        helper.assertTrue(patternRemainder.isEmpty(),
                "ExtendedAE pattern core rejected an EMA extended encoded pattern");

        core.updatePatterns();
        assertEqual(helper, 1, core.getAvailablePatterns().size(),
                "ExtendedAE pattern core available extended pattern count");
    }

    public static void assertExtendedPatternViewLayout(
            GameTestHelper helper,
            ItemStack patternStack,
            int expectedSideLength,
            ResourceLocation expectedTableItem) {
        var menu = new ExtendedCraftingPatternViewMenu(
                ExtendedCraftingPatternViewMenu.TYPE,
                0,
                helper.getLevel(),
                patternStack);

        assertEqual(helper, expectedSideLength, menu.tableSideLength(),
                "extended pattern view table side length");
        assertEqual(helper, ExtendedCraftingPatternViewMenu.OUTPUT_SLOT_INDEX + 1, menu.slots.size(),
                "extended pattern view display slot count");
        helper.assertFalse(menu.canSubstitute(),
                "extended pattern view unexpectedly enabled item substitution");
        helper.assertTrue(menu.canSubstituteFluids(),
                "extended pattern view did not expose fluid substitution");
        assertEqual(helper, expectedTableItem, BuiltInRegistries.ITEM.getKey(menu.tableStack().getItem()),
                "extended pattern view table preview item");

        var offset = Math.floorDiv(ExtendedTableCraftingPattern.MACHINE_GRID_SIDE - expectedSideLength, 2);
        var enabledCount = 0;
        for (int row = 0; row < ExtendedTableCraftingPattern.MACHINE_GRID_SIDE; row++) {
            for (int column = 0; column < ExtendedTableCraftingPattern.MACHINE_GRID_SIDE; column++) {
                var expectedEnabled = row >= offset
                        && row < offset + expectedSideLength
                        && column >= offset
                        && column < offset + expectedSideLength;
                var machineSlot = row * ExtendedTableCraftingPattern.MACHINE_GRID_SIDE + column;
                assertEqual(helper, expectedEnabled, menu.isSlotEnabled(machineSlot),
                        "extended pattern view machine slot " + machineSlot);
                var displaySlot = menu.slots.get(machineSlot);
                helper.assertTrue(displaySlot instanceof IOptionalSlot,
                        "extended pattern view slot " + machineSlot + " is not an AE2 optional slot");
                var optionalSlot = (IOptionalSlot) displaySlot;
                assertEqual(helper, expectedEnabled, optionalSlot.isSlotEnabled(),
                        "extended pattern view rendered slot enabled state " + machineSlot);
                assertEqual(helper, expectedEnabled, displaySlot.isActive(),
                        "extended pattern view active slot state " + machineSlot);
                if (expectedEnabled) {
                    enabledCount++;
                }
            }
        }
        assertEqual(helper, expectedSideLength * expectedSideLength, enabledCount,
                "extended pattern view enabled cell count");

        var outputSlot = menu.slots.get(ExtendedCraftingPatternViewMenu.OUTPUT_SLOT_INDEX);
        helper.assertTrue(outputSlot instanceof IOptionalSlot,
                "extended pattern view output is not an AE2 optional slot");
        helper.assertTrue(outputSlot.isActive(),
                "extended pattern view output slot is disabled");
    }

    public static void assertPatternUploaderUploadsIntoExtendedPatternCore(
            GameTestHelper helper,
            ItemStack patternStack) {
        var core = placeOptionalBlockEntity(
                helper,
                "extended_assembler_matrix_pattern_core",
                new BlockPos(1, 1, 1),
                ExtendedAssemblerMatrixPatternCoreBlockEntity.class);
        var uploader = placeOptionalBlockEntity(
                helper,
                "extended_assembler_matrix_pattern_uploader",
                new BlockPos(2, 1, 1),
                ExtendedAssemblerMatrixPatternUploaderBlockEntity.class);

        var uploaderInventory = uploader.getPatternInv(Direction.WEST);
        helper.assertTrue(uploaderInventory instanceof IItemHandler,
                "ExtendedAE pattern uploader did not expose an item handler");
        var uploaderHandler = (IItemHandler) uploaderInventory;

        var diamondRemainder = uploaderHandler.insertItem(0, new ItemStack(Items.DIAMOND), true);
        helper.assertTrue(!diamondRemainder.isEmpty(),
                "ExtendedAE pattern uploader accepted a non-pattern item");
        var patternRemainder = uploaderHandler.insertItem(0, patternStack.copy(), false);
        helper.assertTrue(patternRemainder.isEmpty(),
                "ExtendedAE pattern uploader did not upload an EMA extended encoded pattern");

        var coreInventory = core.getPatternInv(Direction.EAST);
        helper.assertTrue(coreInventory instanceof IItemHandler,
                "ExtendedAE pattern core did not expose an item handler after uploader insert");
        var coreHandler = (IItemHandler) coreInventory;
        assertStackMatches(helper, patternStack, coreHandler.getStackInSlot(0),
                "ExtendedAE pattern uploader target pattern core slot 0");
        var duplicateRemainder = uploaderHandler.insertItem(0, patternStack.copy(), false);
        assertStackMatches(helper, patternStack, duplicateRemainder,
                "ExtendedAE pattern uploader duplicate remainder");
        helper.assertTrue(coreHandler.getStackInSlot(1).isEmpty(),
                "ExtendedAE pattern uploader inserted duplicate pattern into another core slot");
    }

    public static void assertMatrixCraftingCoreTracksAndCancelsExtendedJobs(
            GameTestHelper helper,
            ExtendedTableCraftingPattern pattern) {
        var core = placeOptionalBlockEntity(
                helper,
                "extended_assembler_matrix_crafting_core",
                new BlockPos(1, 1, 1),
                ExtendedAEAssemblerMatrixCrafterAccess.class);
        for (int thread = 0; thread < 8; thread++) {
            helper.assertTrue(pushExtendedMatrixJob(core, pattern),
                    "ExtendedAE matrix crafting core did not accept job for extended thread " + thread);
            assertEqual(helper, thread + 1, core.extendedmolecularassembler$getExtendedUsedThreadCount(),
                    "ExtendedAE matrix crafting core used thread count after push " + thread);
        }

        helper.assertFalse(pushExtendedMatrixJob(core, pattern),
                "ExtendedAE matrix crafting core accepted a ninth extended job");
        core.extendedmolecularassembler$cancelExtendedJobs();
        assertEqual(helper, 0, core.extendedmolecularassembler$getExtendedUsedThreadCount(),
                "ExtendedAE matrix crafting core used thread count after cancel");
    }

    public static void assertClusterDispatchesExtendedJob(
            GameTestHelper helper,
            ExtendedTableCraftingPattern pattern) {
        var patternCore = placeOptionalBlockEntity(
                helper,
                "extended_assembler_matrix_pattern_core",
                new BlockPos(1, 1, 1),
                ExtendedAssemblerMatrixPatternCoreBlockEntity.class);
        var craftingCore = placeOptionalBlockEntity(
                helper,
                "extended_assembler_matrix_crafting_core",
                new BlockPos(2, 1, 1),
                ExtendedAssemblerMatrixCraftingCoreBlockEntity.class);
        var cluster = new ClusterAssemblerMatrix(patternCore.getBlockPos(), craftingCore.getBlockPos());
        cluster.addTileEntity(patternCore);
        cluster.addTileEntity(craftingCore);

        helper.assertTrue(cluster.pushCraftingJob(pattern, countersForPattern(pattern)),
                "ExtendedAE matrix cluster did not dispatch an EMA extended job");
        assertEqual(helper, 1, craftingCore.extendedmolecularassembler$getExtendedUsedThreadCount(),
                "ExtendedAE matrix cluster-dispatched used thread count");
    }

    public static void assertMatrixCraftingCoreDropsActiveExtendedJobInputs(
            GameTestHelper helper,
            ExtendedTableCraftingPattern pattern) {
        var core = placeOptionalBlockEntity(
                helper,
                "extended_assembler_matrix_crafting_core",
                new BlockPos(1, 1, 1),
                ExtendedAssemblerMatrixCraftingCoreBlockEntity.class);
        helper.assertTrue(core.extendedmolecularassembler$pushExtendedJob(pattern, countersForPattern(pattern)),
                "ExtendedAE matrix crafting core did not accept an extended job for drop testing");

        var expectedDrops = filledInputGrid(pattern).stream()
                .filter(stack -> !stack.isEmpty())
                .map(ItemStack::copy)
                .toList();
        var actualDrops = new ArrayList<ItemStack>();
        core.addAdditionalDrops(helper.getLevel(), core.getBlockPos(), actualDrops);

        assertEqual(helper, expectedDrops.size(), actualDrops.size(),
                "ExtendedAE matrix crafting core active job drop count");
        for (int i = 0; i < expectedDrops.size(); i++) {
            assertStackMatches(helper, expectedDrops.get(i), actualDrops.get(i),
                    "ExtendedAE matrix crafting core active job drop " + i);
        }

        var patternStack = pattern.getDefinition().toStack();
        for (var drop : actualDrops) {
            helper.assertFalse(ItemStack.matches(patternStack, drop),
                    "ExtendedAE matrix crafting core dropped the encoded pattern snapshot");
        }
    }

    private static <T> T placeOptionalBlockEntity(
            GameTestHelper helper,
            String blockPath,
            BlockPos pos,
            Class<T> type) {
        var blockId = ExtendedMolecularAssembler.makeId(blockPath);
        var block = BuiltInRegistries.BLOCK.get(blockId);
        helper.assertTrue(block != Blocks.AIR, "Missing optional EMA block " + blockId);
        helper.setBlock(pos, block);
        var blockEntity = helper.getBlockEntity(pos);
        helper.assertTrue(type.isInstance(blockEntity),
                "Optional EMA block " + blockId + " did not create a " + type.getSimpleName());
        return type.cast(blockEntity);
    }

    private static boolean pushExtendedMatrixJob(
            ExtendedAEAssemblerMatrixCrafterAccess core,
            ExtendedTableCraftingPattern pattern) {
        return core.extendedmolecularassembler$pushExtendedJob(pattern, countersForPattern(pattern));
    }

    private static List<ItemStack> filledInputGrid(ExtendedTableCraftingPattern pattern) {
        var counters = countersForPattern(pattern);
        var grid = new ArrayList<ItemStack>(ExtendedTableCraftingPattern.MACHINE_GRID_SIZE);
        for (int i = 0; i < ExtendedTableCraftingPattern.MACHINE_GRID_SIZE; i++) {
            grid.add(ItemStack.EMPTY);
        }
        pattern.fillCraftingGrid(counters, (slot, stack) -> grid.set(slot, stack.copy()));
        return grid;
    }

    private static KeyCounter[] countersForPattern(ExtendedTableCraftingPattern pattern) {
        var counters = new KeyCounter[pattern.getInputs().length];
        IPatternDetails.IInput[] inputs = pattern.getInputs();
        for (int i = 0; i < inputs.length; i++) {
            counters[i] = new KeyCounter();
            var primaryInput = inputs[i].getPossibleInputs()[0];
            counters[i].add(primaryInput.what(), primaryInput.amount() * inputs[i].getMultiplier());
        }
        return counters;
    }

    private static void assertStackMatches(
            GameTestHelper helper,
            ItemStack expected,
            ItemStack actual,
            String name) {
        if (!ItemStack.matches(expected, actual)) {
            helper.fail(name + ": expected " + describe(expected) + ", got " + describe(actual));
        }
    }

    private static void assertEqual(GameTestHelper helper, Object expected, Object actual, String name) {
        if (!Objects.equals(expected, actual)) {
            helper.fail(name + ": expected " + expected + ", got " + actual);
        }
    }

    private static String describe(ItemStack stack) {
        if (stack.isEmpty()) {
            return "empty";
        }
        return stack.getCount() + "x" + BuiltInRegistries.ITEM.getKey(stack.getItem());
    }
}
