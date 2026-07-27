package me.myogoo.extendedmolecularassembler.integration.extendedae;

import appeng.api.crafting.IPatternDetails;
import appeng.api.stacks.KeyCounter;
import me.myogoo.extendedmolecularassembler.ExtendedMolecularAssembler;
import me.myogoo.extendedmolecularassembler.pattern.ExtendedTableCraftingPattern;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.items.IItemHandler;

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
