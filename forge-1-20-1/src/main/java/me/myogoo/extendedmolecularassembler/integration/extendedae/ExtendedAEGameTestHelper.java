package me.myogoo.extendedmolecularassembler.integration.extendedae;

import appeng.api.crafting.IPatternDetails;
import appeng.api.networking.crafting.CraftingSubmitErrorCode;
import appeng.api.networking.crafting.ICraftingPlan;
import appeng.api.networking.crafting.ICraftingProvider;
import appeng.api.stacks.KeyCounter;
import appeng.blockentity.crafting.PatternProviderBlockEntity;
import appeng.core.definitions.AEBlocks;
import appeng.crafting.CraftingPlan;
import appeng.me.helpers.MachineSource;
import appeng.me.service.CraftingService;
import appeng.menu.slot.IOptionalSlot;
import com.glodblock.github.extendedae.common.me.matrix.ClusterAssemblerMatrix;
import com.glodblock.github.extendedae.network.packet.CPatternKey;
import com.mojang.authlib.GameProfile;
import io.netty.buffer.Unpooled;
import me.myogoo.extendedmolecularassembler.ExtendedMolecularAssembler;
import me.myogoo.extendedmolecularassembler.init.EMAItems;
import me.myogoo.extendedmolecularassembler.integration.extendedae.menu.ExtendedCraftingPatternViewMenu;
import me.myogoo.extendedmolecularassembler.pattern.ExtendedTableCraftingPattern;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.common.util.FakePlayer;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

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

        var plusCore = placeOptionalBlockEntity(
                helper,
                "extended_assembler_matrix_pattern_core_plus",
                new BlockPos(2, 1, 1),
                ExtendedAssemblerMatrixPatternCoreBlockEntity.class);
        assertEqual(helper, 36, core.getPatternInventory().size(), "Pattern core capacity");
        assertEqual(helper, 72, plusCore.getPatternInventory().size(), "Plus pattern core capacity");
        helper.assertTrue(plusCore.getPatternInv(Direction.NORTH).insertItem(0, patternStack.copy(), true).isEmpty(),
                "Plus matrix pattern core rejected an EMA pattern");
    }

    public static void assertCraftCoreGridSizes(GameTestHelper helper) {
        helper.assertTrue(ExtendedAssemblerMatrixCraftingCoreBlockEntity.supportsGridSide(9, 9),
                "9x9 matrix crafting core rejected a 9x9 pattern");
        helper.assertFalse(ExtendedAssemblerMatrixCraftingCoreBlockEntity.supportsGridSide(9, 11),
                "9x9 matrix crafting core accepted an 11x11 pattern");
        helper.assertTrue(ExtendedAssemblerMatrixCraftingCoreBlockEntity.supportsGridSide(11, 9),
                "11x11 matrix crafting core rejected a smaller pattern");
        helper.assertTrue(ExtendedAssemblerMatrixCraftingCoreBlockEntity.supportsGridSide(11, 11),
                "11x11 matrix crafting core rejected an 11x11 pattern");
        helper.assertFalse(ExtendedAssemblerMatrixCraftingCoreBlockEntity.supportsGridSide(11, 13),
                "11x11 matrix crafting core accepted a 13x13 pattern");
        helper.assertTrue(ExtendedAssemblerMatrixCraftingCoreBlockEntity.supportsGridSide(13, 13),
                "13x13 matrix crafting core rejected a 13x13 pattern");
    }

    public static void assertMatrixCraftingCoreVariantGridSides(GameTestHelper helper) {
        var epicCore = placeOptionalBlockEntity(
                helper,
                "epic_assembler_matrix_crafting_core",
                new BlockPos(1, 1, 1),
                ExtendedAssemblerMatrixCraftingCoreBlockEntity.class);
        var legendaryCore = placeOptionalBlockEntity(
                helper,
                "legendary_assembler_matrix_crafting_core",
                new BlockPos(2, 1, 1),
                ExtendedAssemblerMatrixCraftingCoreBlockEntity.class);
        assertEqual(helper, 11, epicCore.getGridSide(), "Epic crafting core grid side");
        assertEqual(helper, 13, legendaryCore.getGridSide(), "Legendary crafting core grid side");
    }

    public static void assertExtendedPatternViewLayout(
            GameTestHelper helper,
            ItemStack patternStack,
            int expectedSideLength,
            ResourceLocation expectedTableItem) {
        var player = new FakePlayer(helper.getLevel(), new GameProfile(UUID.randomUUID(), "ema_pattern_view"));
        var originalMenu = player.containerMenu;
        new CPatternKey(new ItemStack(EMAItems.EXTENDED_CRAFTING_PATTERN.get())).onMessage(player);
        helper.assertTrue(player.containerMenu == originalMenu,
                "Unencoded EMA pattern unexpectedly opened a preview");

        var buffer = new FriendlyByteBuf(Unpooled.buffer());
        try {
            new CPatternKey(patternStack).toBytes(buffer);
            var packet = new CPatternKey();
            packet.fromBytes(buffer);
            packet.onMessage(player);
        } finally {
            buffer.release();
        }
        helper.assertTrue(player.containerMenu instanceof ExtendedCraftingPatternViewMenu,
                "ExtendedAE pattern preview key did not open the EMA preview menu");
        var menu = (ExtendedCraftingPatternViewMenu) player.containerMenu;

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

        var offset = Math.floorDiv(ExtendedCraftingPatternViewMenu.GRID_SIDE - expectedSideLength, 2);
        var enabledCount = 0;
        for (int row = 0; row < ExtendedCraftingPatternViewMenu.GRID_SIDE; row++) {
            for (int column = 0; column < ExtendedCraftingPatternViewMenu.GRID_SIDE; column++) {
                var expectedEnabled = row >= offset
                        && row < offset + expectedSideLength
                        && column >= offset
                        && column < offset + expectedSideLength;
                var machineSlot = row * ExtendedCraftingPatternViewMenu.GRID_SIDE + column;
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
                ExtendedAssemblerMatrixCraftingCoreBlockEntity.class);
        for (int thread = 0; thread < 8; thread++) {
            helper.assertTrue(pushExtendedMatrixJob(core, pattern),
                    "ExtendedAE matrix crafting core did not accept job for extended thread " + thread);
            assertEqual(helper, thread + 1, core.usedThreadCount(),
                    "ExtendedAE matrix crafting core used thread count after push " + thread);
        }

        helper.assertFalse(pushExtendedMatrixJob(core, pattern),
                "ExtendedAE matrix crafting core accepted a ninth extended job");
        core.cancelJobs();
        assertEqual(helper, 0, core.usedThreadCount(),
                "ExtendedAE matrix crafting core used thread count after cancel");
    }

    public static void assertPlusCoreAndUploader(GameTestHelper helper, ExtendedTableCraftingPattern pattern,
            String variant) {
        var uploaderPos = new BlockPos(2, 2, 2);
        var plusPos = uploaderPos.below();
        var normalPos = uploaderPos.north();
        var craftingPos = new BlockPos(1, 1, 1);
        var patternStack = pattern.getDefinition().toStack();
        var plus = placeOptionalBlockEntity(helper, "extended_assembler_matrix_pattern_core_plus", plusPos,
                ExtendedAssemblerMatrixPatternCoreBlockEntity.class);
        var normal = placeOptionalBlockEntity(helper, "extended_assembler_matrix_pattern_core", normalPos,
                ExtendedAssemblerMatrixPatternCoreBlockEntity.class);
        var uploader = placeOptionalBlockEntity(helper, "extended_assembler_matrix_pattern_uploader", uploaderPos,
                ExtendedAssemblerMatrixPatternUploaderBlockEntity.class);
        var inventory = plus.getPatternInventory();
        var handler = uploader.getPatternInv(Direction.WEST);
        assertEqual(helper, 72, inventory.size(), variant + " Plus pattern capacity");
        assertEqual(helper, 36, normal.getPatternInventory().size(), "Normal pattern capacity");

        // DOWN is visited before NORTH: a later duplicate must block an earlier empty core.
        normal.getPatternInventory().setItemDirect(0, patternStack.copy());
        for (var simulate : new boolean[] { true, false }) {
            assertStackMatches(helper, patternStack, handler.insertItem(0, patternStack.copy(), simulate),
                    variant + " duplicate across cores, simulate=" + simulate);
            helper.assertTrue(inventory.isEmpty(), "Duplicate upload modified the earlier Plus core");
        }
        normal.getPatternInventory().clear();
        helper.assertTrue(handler.insertItem(0, patternStack.copy(), true).isEmpty(),
                variant + " upload simulation failed");
        helper.assertTrue(inventory.isEmpty(), "Upload simulation mutated the Plus core");
        helper.assertTrue(handler.insertItem(0, patternStack.copy(), false).isEmpty(),
                variant + " upload did not reach its Plus core");
        assertStackMatches(helper, patternStack, inventory.getStackInSlot(0), variant + " uploaded pattern");
        inventory.clear();
        for (var core : List.of(plus, normal)) {
            var inv = core.getPatternInventory();
            for (int slot = 0; slot < inv.size(); slot++) {
                inv.setItemDirect(slot, new ItemStack(Items.PAPER));
            }
        }
        assertStackMatches(helper, patternStack, handler.insertItem(0, patternStack.copy(), false),
                variant + " full cores must return the pattern");
        inventory.setItemDirect(71, ItemStack.EMPTY);
        helper.assertTrue(handler.insertItem(0, patternStack.copy(), false).isEmpty(),
                variant + " uploader did not reach Plus slot 72");
        assertStackMatches(helper, patternStack, inventory.getStackInSlot(71), variant + " final Plus slot");

        var crafting = placeOptionalBlockEntity(helper, variant + "_assembler_matrix_crafting_core_plus", craftingPos,
                ExtendedAssemblerMatrixCraftingCoreBlockEntity.class);
        assertEqual(helper, pattern.tableSideLength(), crafting.getGridSide(), variant + " Plus crafting tier");
        for (int job = 0; job < 32; job++) {
            helper.assertTrue(pushExtendedMatrixJob(crafting, pattern), variant + " Plus rejected job " + job);
        }
        assertEqual(helper, 32, crafting.usedThreadCount(), variant + " Plus running job count");
        helper.assertFalse(pushExtendedMatrixJob(crafting, pattern), variant + " Plus accepted a 33rd job");
        crafting.cancelJobs();
        assertEqual(helper, 0, crafting.usedThreadCount(), variant + " Plus did not cancel all jobs");
        for (var pos : List.of(plusPos, normalPos, uploaderPos, craftingPos)) {
            helper.setBlock(pos, Blocks.AIR);
        }
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
        patternCore.getPatternInventory().setItemDirect(0, pattern.getDefinition().toStack());
        patternCore.updatePatterns();
        cluster.addTileEntity(patternCore);
        helper.assertFalse(ExtendedAEAssemblerMatrixBridge.hasCraftingCore(cluster, pattern),
                "Matrix without a crafting core passed the request capability check");
        helper.assertFalse(cluster.pushCraftingJob(pattern, countersForPattern(pattern)),
                "Matrix without a crafting core accepted a job");
        cluster.addTileEntity(craftingCore);
        helper.assertTrue(ExtendedAEAssemblerMatrixBridge.hasCraftingCore(cluster, pattern),
                "Matrix with a matching crafting core failed the request capability check");

        helper.assertTrue(cluster.pushCraftingJob(pattern, countersForPattern(pattern)),
                "ExtendedAE matrix cluster did not dispatch an EMA extended job");
        assertEqual(helper, 1, craftingCore.usedThreadCount(),
                "ExtendedAE matrix cluster-dispatched used thread count");
        for (int i = 1; i < 8; i++) {
            helper.assertTrue(cluster.pushCraftingJob(pattern, countersForPattern(pattern)),
                    "Matrix failed to fill its remaining threads");
        }
        helper.assertTrue(ExtendedAEAssemblerMatrixBridge.hasCraftingCore(cluster, pattern),
                "Busy crafting cores must not block new requests");
        craftingCore.cancelJobs();
        helper.setBlock(new BlockPos(2, 1, 1), Blocks.AIR);
        helper.assertFalse(ExtendedAEAssemblerMatrixBridge.hasCraftingCore(cluster, pattern),
                "Removed crafting core still passed the request capability check");
    }

    public static void assertClusterDispatchesExtendedJob(
            GameTestHelper helper,
            ExtendedTableCraftingPattern pattern,
            int expectedGridSide) {
        var variant = switch (expectedGridSide) {
            case 11 -> "epic";
            case 13 -> "legendary";
            default -> throw new IllegalArgumentException("Unsupported large matrix grid side " + expectedGridSide);
        };
        var patternCorePath = "extended_assembler_matrix_pattern_core";
        var craftingCorePath = variant + "_assembler_matrix_crafting_core";
        var patternCorePos = new BlockPos(1, 1, 1);
        var baseCorePos = new BlockPos(2, 1, 1);
        var largeCorePos = new BlockPos(3, 1, 1);
        assertEqual(helper, expectedGridSide, pattern.tableSideLength(),
                "Large ExtendedAE matrix pattern side length");

        var patternCore = placeOptionalBlockEntity(
                helper,
                patternCorePath,
                patternCorePos,
                ExtendedAssemblerMatrixPatternCoreBlockEntity.class);
        var patternHandler = (IItemHandler) patternCore.getPatternInv(Direction.NORTH);
        var patternStack = pattern.getDefinition().toStack();
        var remainder = patternHandler.insertItem(0, patternStack, false);
        helper.assertTrue(remainder.isEmpty(),
                "Large ExtendedAE matrix pattern core rejected its tier pattern");
        patternCore.updatePatterns();
        assertEqual(helper, 1, patternCore.getAvailablePatterns().size(),
                "Large ExtendedAE matrix pattern core available pattern count");
        var storedPatternDetails = patternCore.getAvailablePatterns().get(0);
        helper.assertTrue(storedPatternDetails instanceof ExtendedTableCraftingPattern,
                "Large ExtendedAE matrix pattern core exposed an unexpected pattern type");
        var storedPattern = (ExtendedTableCraftingPattern) storedPatternDetails;

        var baseCore = placeOptionalBlockEntity(
                helper,
                "extended_assembler_matrix_crafting_core",
                baseCorePos,
                ExtendedAssemblerMatrixCraftingCoreBlockEntity.class);
        var largeCore = placeOptionalBlockEntity(
                helper,
                craftingCorePath,
                largeCorePos,
                ExtendedAssemblerMatrixCraftingCoreBlockEntity.class);
        assertEqual(helper, 9, baseCore.getGridSide(), "Base matrix crafting core grid side");
        assertEqual(helper, expectedGridSide, largeCore.getGridSide(), "Large matrix crafting core grid side");

        var cluster = new ClusterAssemblerMatrix(patternCorePos, baseCorePos);
        cluster.addTileEntity(patternCore);
        cluster.addTileEntity(baseCore);
        helper.assertFalse(ExtendedAEAssemblerMatrixBridge.hasCraftingCore(cluster, storedPattern),
                "9x9 core passed a larger pattern request capability check");
        helper.assertFalse(cluster.pushCraftingJob(storedPattern, countersForPattern(storedPattern)),
                "9x9 core accepted a larger pattern job");
        var otherCluster = new ClusterAssemblerMatrix(largeCorePos, largeCorePos);
        otherCluster.addTileEntity(largeCore);
        helper.assertTrue(ExtendedAEAssemblerMatrixBridge.hasCraftingCore(otherCluster, storedPattern),
                "Matching core was not detected in the other Matrix");
        helper.assertFalse(ExtendedAEAssemblerMatrixBridge.hasCraftingCore(cluster, storedPattern),
                "A core in another Matrix must not satisfy the pattern-hosting Matrix");
        cluster.addTileEntity(largeCore);
        helper.assertTrue(ExtendedAEAssemblerMatrixBridge.hasCraftingCore(cluster, storedPattern),
                "Matching large core failed the request capability check");

        helper.assertTrue(cluster.pushCraftingJob(storedPattern, countersForPattern(storedPattern)),
                "ExtendedAE matrix cluster did not route its tier pattern");
        assertEqual(helper, 0, baseCore.usedThreadCount(),
                "Base matrix crafting core unexpectedly accepted a larger tier job");
        assertEqual(helper, 1, largeCore.usedThreadCount(),
                "Matching large matrix crafting core did not accept its tier job");
        largeCore.cancelJobs();
        helper.setBlock(largeCorePos, Blocks.AIR);
        helper.assertFalse(ExtendedAEAssemblerMatrixBridge.hasCraftingCore(cluster, storedPattern),
                "Removed large core still passed the request capability check");
        helper.setBlock(patternCorePos, Blocks.AIR);
        helper.setBlock(baseCorePos, Blocks.AIR);
        helper.setBlock(largeCorePos, Blocks.AIR);
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
                assertPlanSubmission(helper, host, plan, patterns.stream().anyMatch(p -> p.tableSideLength() > 9),
                        "9x9 core with a mixed-size plan");
                helper.setBlock(craftingPos,
                        EMAExtendedAEIntegration.LEGENDARY_ASSEMBLER_MATRIX_CRAFTING_CORE.get());
                helper.runAfterDelay(40, () -> {
                    assertPlanSubmission(helper, host, plan, false, "sufficient local core");
                    // Keep the Matrix valid while removing its only EMA-capable worker. Reuse the same plan object.
                    helper.setBlock(craftingPos, BuiltInRegistries.BLOCK.get(
                            new ResourceLocation("expatternprovider", "assembler_matrix_crafter")));
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
            var block = BuiltInRegistries.BLOCK.get(new ResourceLocation("expatternprovider", path));
            helper.assertTrue(block != Blocks.AIR, "Missing Matrix test block " + path);
            helper.setBlock(pos, block);
        }
        if (capable) {
            helper.setBlock(min.offset(2, 1, 1),
                    EMAExtendedAEIntegration.LEGENDARY_ASSEMBLER_MATRIX_CRAFTING_CORE.get());
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
        var grid = new ArrayList<ItemStack>(ExtendedCraftingPatternViewMenu.GRID_SLOT_COUNT);
        for (int i = 0; i < ExtendedCraftingPatternViewMenu.GRID_SLOT_COUNT; i++) {
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
