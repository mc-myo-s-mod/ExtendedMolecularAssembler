package me.myogoo.extendedmolecularassembler.integration.extendedae;

import appeng.api.crafting.IPatternDetails;
import appeng.api.networking.crafting.ICraftingPlan;
import appeng.api.networking.crafting.ICraftingProvider;
import appeng.api.networking.crafting.CraftingSubmitErrorCode;
import appeng.me.helpers.MachineSource;
import appeng.blockentity.crafting.PatternProviderBlockEntity;
import appeng.core.definitions.AEBlocks;
import appeng.crafting.CraftingPlan;
import appeng.me.service.CraftingService;
import java.util.LinkedHashMap;
import java.util.Map;
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

        var firstCore = placeOptionalBlockEntity(helper, "extended_assembler_matrix_pattern_core",
                new BlockPos(2, 0, 1), ExtendedAssemblerMatrixPatternCoreBlockEntity.class);
        for (var duplicateFirst : new boolean[] {false, true}) {
            core.getPatternInventory().clear();
            firstCore.getPatternInventory().clear();
            (duplicateFirst ? firstCore : core).getPatternInventory().setItemDirect(0, patternStack.copy());
            for (var simulate : new boolean[] {true, false}) {
                assertStackMatches(helper, patternStack, uploaderHandler.insertItem(0, patternStack.copy(), simulate),
                        "All-core duplicate preflight");
            }
            assertStackMatches(helper, patternStack,
                    (duplicateFirst ? firstCore : core).getPatternInventory().getStackInSlot(0),
                    "Preflight changed existing pattern");
            helper.assertTrue((duplicateFirst ? core : firstCore).getPatternInventory().isEmpty(),
                    "Preflight inserted a duplicate into the empty core");
        }
    }

    public static void assertMatrixCraftingCoreTracksAndCancelsExtendedJobs(
            GameTestHelper helper,
            ExtendedTableCraftingPattern pattern) {
        var core = placeOptionalBlockEntity(
                helper,
                "extended_assembler_matrix_crafting_core",
                new BlockPos(1, 1, 1),
                ExtendedAssemblerMatrixCraftingCoreBlockEntity.class);
        for (int thread = 0; thread < 8; thread++) {
            helper.assertTrue(pushExtendedMatrixJob(core, pattern),
                    "ExtendedAE matrix crafting core did not accept job for extended thread " + thread);
            assertEqual(helper, thread + 1, core.usedThreadCount(),
                    "ExtendedAE matrix crafting core used thread count after push " + thread);
        }

        helper.assertFalse(pushExtendedMatrixJob(core, pattern),
                "ExtendedAE matrix crafting core accepted a ninth extended job");
        var registry = helper.getLevel().registryAccess();
        var saved = new net.minecraft.nbt.CompoundTag();
        core.saveAdditional(saved, registry);
        var restored = (ExtendedAssemblerMatrixCraftingCoreBlockEntity) core.getType()
                .create(core.getBlockPos(), core.getBlockState());
        restored.setLevel(helper.getLevel());
        restored.loadTag(saved, registry);
        assertEqual(helper, 8, restored.usedThreadCount(), "Matrix persisted active job count");
        var beforeDrops = new ArrayList<ItemStack>();
        var afterDrops = new ArrayList<ItemStack>();
        core.addAdditionalDrops(helper.getLevel(), core.getBlockPos(), beforeDrops);
        restored.addAdditionalDrops(helper.getLevel(), restored.getBlockPos(), afterDrops);
        assertEqual(helper, beforeDrops.size(), afterDrops.size(), "Matrix persisted input count");
        for (int slot = 0; slot < beforeDrops.size(); slot++) {
            assertStackMatches(helper, beforeDrops.get(slot), afterDrops.get(slot), "Matrix persisted input " + slot);
        }
        var savedAgain = new net.minecraft.nbt.CompoundTag();
        restored.saveAdditional(savedAgain, registry);
        for (int thread = 0; thread < 8; thread++) {
            var key = "ema_extended_thread_" + thread;
            helper.assertTrue(saved.getCompound(key).equals(savedAgain.getCompound(key)),
                    "Matrix job pattern/grid/progress changed in thread " + thread);
        }
        helper.assertFalse(pushExtendedMatrixJob(restored, pattern), "Restored full Matrix accepted a ninth job");
        restored.cancelJobs();
        assertEqual(helper, 0, restored.usedThreadCount(), "Restored Matrix did not cancel all jobs");
        core.cancelJobs();
        assertEqual(helper, 0, core.usedThreadCount(),
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
        assertEqual(helper, 1, craftingCore.usedThreadCount(),
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
        helper.assertTrue(core.pushJob(pattern, countersForPattern(pattern)),
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

    public static void assertCraftingPlanRequiresLocalCore(GameTestHelper helper,
            List<ExtendedTableCraftingPattern> patterns) {
        var host = placeRequestTestMatrix(helper, new BlockPos(1, 1, 1), false);
        var other = placeRequestTestMatrix(helper, new BlockPos(6, 1, 1), true);
        var craftingPos = new BlockPos(3, 2, 2);
        helper.setBlock(new BlockPos(5, 2, 2), AEBlocks.CREATIVE_ENERGY_CELL.block());
        helper.setBlock(new BlockPos(5, 2, 3), AEBlocks.PATTERN_PROVIDER.block());
        var alternative = (PatternProviderBlockEntity) helper.getBlockEntity(new BlockPos(5, 2, 3));
        var times = new LinkedHashMap<IPatternDetails, Long>();
        for (int i = 0; i < patterns.size(); i++) {
            host.getPatternInventory().setItemDirect(i, patterns.get(i).getDefinition().toStack());
            times.put(patterns.get(i), 1L);
        }
        host.updatePatterns();
        var plan = new CraftingPlan(patterns.get(patterns.size() - 1).getPrimaryOutput(), 1,
                false, false, new KeyCounter(), new KeyCounter(), new KeyCounter(), Map.copyOf(times));

        helper.runAfterDelay(40, () -> {
            helper.assertTrue(host.getCluster() != null && other.getCluster() != null
                            && host.getCluster() != other.getCluster(),
                    "Request gate test Matrices did not form separately");
            helper.assertTrue(host.getMainNode().isActive() && other.getMainNode().isActive(),
                    "Request gate test Matrices are not powered and active");
            helper.assertTrue(host.getMainNode().getGrid() == other.getMainNode().getGrid(),
                    "Request gate test Matrices are not on the same AE2 network");
            var grid = host.getMainNode().getGrid();
            for (var pattern : patterns) {
                helper.assertTrue(grid.getCraftingService().getCraftingFor(pattern.getPrimaryOutput().what())
                                .contains(pattern),
                        "Matrix pattern was not registered with AE2 before the request");
            }
            assertPlanSubmission(helper, host, plan, true, "capable core only in another Matrix");

            helper.setBlock(craftingPos, EMAExtendedAEIntegration.EXTENDED_ASSEMBLER_MATRIX_CRAFTING_CORE.get());
            helper.runAfterDelay(40, () -> {
                assertPlanSubmission(helper, host, plan, false,
                        "9x9 core with a mixed-size plan");
                helper.setBlock(craftingPos,
                        EMAExtendedAEIntegration.EXTENDED_ASSEMBLER_MATRIX_CRAFTING_CORE.get());
                helper.runAfterDelay(40, () -> {
                    assertPlanSubmission(helper, host, plan, false, "sufficient local core");
                    // Keep the Matrix valid while removing its only EMA-capable worker. Reuse the same plan object.
                    helper.setBlock(craftingPos, BuiltInRegistries.BLOCK.get(
                            ResourceLocation.fromNamespaceAndPath("extendedae", "assembler_matrix_crafter")));
                    helper.runAfterDelay(40, () -> {
                        assertPlanSubmission(helper, host, plan, true, "stale plan after local core removal");
                        for (int i = 0; i < patterns.size(); i++) {
                            alternative.getLogic().getPatternInv()
                                    .setItemDirect(i, patterns.get(i).getDefinition().toStack());
                        }
                        alternative.getLogic().updatePatterns();
                        helper.runAfterDelay(1, () -> {
                            helper.assertTrue(alternative.getMainNode().isActive()
                                            && alternative.getMainNode().getGrid() == host.getMainNode().getGrid(),
                                    "Non-Matrix alternative is not active on the same network");
                            assertProviderCursorUnchanged(helper, host, plan, patterns.get(0), alternative);
                            assertPlanSubmission(helper, host, plan, false, "non-Matrix alternative provider");
                            helper.succeed();
                        });
                    });
                });
            });
        });
    }

    private static ExtendedAssemblerMatrixPatternCoreBlockEntity placeRequestTestMatrix(
            GameTestHelper helper, BlockPos min, boolean capable) {
        var max = min.offset(3, 2, 2);
        for (var pos : BlockPos.betweenClosed(min, max)) {
            var boundaryAxes = (pos.getX() == min.getX() || pos.getX() == max.getX() ? 1 : 0)
                    + (pos.getY() == min.getY() || pos.getY() == max.getY() ? 1 : 0)
                    + (pos.getZ() == min.getZ() || pos.getZ() == max.getZ() ? 1 : 0);
            var path = boundaryAxes >= 2 ? "assembler_matrix_frame"
                    : boundaryAxes == 1 ? "assembler_matrix_wall" : "assembler_matrix_crafter";
            var block = BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath("extendedae", path));
            helper.assertTrue(block != Blocks.AIR, "Missing Matrix test block " + path);
            helper.setBlock(pos, block);
        }
        if (capable) {
            helper.setBlock(min.offset(2, 1, 1),
                    EMAExtendedAEIntegration.EXTENDED_ASSEMBLER_MATRIX_CRAFTING_CORE.get());
        }
        return placeOptionalBlockEntity(helper, "extended_assembler_matrix_pattern_core", min.offset(1, 1, 1),
                ExtendedAssemblerMatrixPatternCoreBlockEntity.class);
    }

    private static void assertPlanSubmission(GameTestHelper helper,
            ExtendedAssemblerMatrixPatternCoreBlockEntity host, ICraftingPlan plan, boolean blocked, String stage) {
        helper.assertTrue(host.isFormed() && host.getMainNode().isActive(), stage + ": Matrix is not active");
        var grid = host.getMainNode().getGrid();
        helper.assertTrue(grid.getCraftingService().getCpus().isEmpty(), stage + ": test unexpectedly has a CPU");
        assertEqual(helper, blocked, ExtendedAEAssemblerMatrixBridge.getPlanBlockReason(grid, plan) != null,
                stage + ": plan block reason");
        var result = grid.getCraftingService().submitJob(plan, null, null, true, new MachineSource(host));
        // Without the submitJob mixin, even unsupported plans return NO_CPU_FOUND, making this assertion fail.
        assertEqual(helper, blocked ? CraftingSubmitErrorCode.INCOMPLETE_PLAN : CraftingSubmitErrorCode.NO_CPU_FOUND,
                result.errorCode(), stage + ": actual AE2 submission result");
    }

    private static void assertProviderCursorUnchanged(GameTestHelper helper,
            ExtendedAssemblerMatrixPatternCoreBlockEntity host, ICraftingPlan plan,
            ExtendedTableCraftingPattern pattern, PatternProviderBlockEntity alternative) {
        var grid = host.getMainNode().getGrid();
        var service = (CraftingService) grid.getCraftingService();
        var before = new ArrayList<ICraftingProvider>();
        service.getProviders(pattern).forEach(before::add);
        helper.assertTrue(before.size() == 2 && before.contains(host) && before.contains(alternative.getLogic()),
                "Cursor test requires exactly the Matrix and ordinary providers");
        // Start the live round-robin at the executable provider, where an early return would advance it by one.
        var iterator = service.getProviders(pattern).iterator();
        for (int i = 0; i < before.indexOf(alternative.getLogic()); i++) {
            iterator.next();
        }
        before.clear();
        service.getProviders(pattern).forEach(before::add);
        helper.assertTrue(ExtendedAEAssemblerMatrixBridge.getPlanBlockReason(grid, plan) == null,
                "Alternative provider did not unblock the plan during cursor check");
        var after = new ArrayList<ICraftingProvider>();
        service.getProviders(pattern).forEach(after::add);
        assertEqual(helper, before, after, "Plan guard changed AE2's round-robin provider cursor");
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
            ExtendedAssemblerMatrixCraftingCoreBlockEntity core,
            ExtendedTableCraftingPattern pattern) {
        return core.pushJob(pattern, countersForPattern(pattern));
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
