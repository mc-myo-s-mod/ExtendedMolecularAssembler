package me.myogoo.extendedmolecularassembler.gametest;

import appeng.api.AECapabilities;
import appeng.api.crafting.IPatternDetails;
import appeng.api.implementations.blockentities.ICraftingMachine;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.KeyCounter;
import appeng.api.util.AECableType;
import appeng.menu.AEBaseMenu;
import me.myogoo.extendedmolecularassembler.ExtendedMolecularAssembler;
import me.myogoo.extendedmolecularassembler.api.ExtendedPatternDetailsHelper;
import me.myogoo.extendedmolecularassembler.block.blockentity.ExtendedMolecularAssemblerBlockEntity;
import me.myogoo.extendedmolecularassembler.init.EMABlocks;
import me.myogoo.extendedmolecularassembler.init.EMADataComponents;
import me.myogoo.extendedmolecularassembler.init.EMAModPresence;
import me.myogoo.extendedmolecularassembler.integration.extendedae.ExtendedAEAssemblerMatrixCrafterAccess;
import me.myogoo.extendedmolecularassembler.integration.extendedae.ExtendedAssemblerMatrixPatternCoreBlockEntity;
import me.myogoo.extendedmolecularassembler.integration.extendedae.ExtendedAssemblerMatrixPatternUploaderBlockEntity;
import me.myogoo.extendedmolecularassembler.menu.pattern.ExtendedPatternRecipeFinder;
import me.myogoo.extendedmolecularassembler.menu.pattern.ExtendedPatternRecipeMatch;
import me.myogoo.extendedmolecularassembler.pattern.EncodedExtendedCraftingPattern;
import me.myogoo.extendedmolecularassembler.pattern.ExtendedPatternTableTypes;
import me.myogoo.extendedmolecularassembler.pattern.ExtendedTableCraftingPattern;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import net.pedroksl.advanced_ae.common.entities.QuantumCrafterEntity;
import net.pedroksl.advanced_ae.gui.QuantumCrafterMenu;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@GameTestHolder(ExtendedMolecularAssembler.MODID)
@PrefixGameTestTemplate(false)
public final class ExtendedPatternGameTests {
    private static final List<PatternCase> EXTENDED_CRAFTING_CASES = List.of(
            new PatternCase("Extended Crafting tier 1", "ec_tier_1", ExtendedPatternTableTypes.extendedCrafting(1), 1,
                    Items.DIAMOND,
                    new String[] {
                            "CIC",
                            "IRI",
                            "CIC"
                    },
                    Map.of('C', Items.COPPER_INGOT, 'I', Items.IRON_INGOT, 'R', Items.REDSTONE)),
            new PatternCase("Extended Crafting tier 2", "ec_tier_2", ExtendedPatternTableTypes.extendedCrafting(2), 2,
                    Items.EMERALD,
                    new String[] {
                            "GGGGG",
                            "GLLLG",
                            "GLRLG",
                            "GLLLG",
                            "GGGGG"
                    },
                    Map.of('G', Items.GOLD_INGOT, 'L', Items.LAPIS_LAZULI, 'R', Items.REDSTONE)),
            new PatternCase("Extended Crafting tier 3", "ec_tier_3", ExtendedPatternTableTypes.extendedCrafting(3), 3,
                    Items.NETHERITE_SCRAP,
                    new String[] {
                            "CCCCCCC",
                            "CDDDDDC",
                            "CDGGGDC",
                            "CDGRGDC",
                            "CDGGGDC",
                            "CDDDDDC",
                            "CCCCCCC"
                    },
                    Map.of('C', Items.COBBLESTONE, 'D', Items.DIAMOND, 'G', Items.GOLD_INGOT, 'R', Items.REDSTONE)),
            new PatternCase("Extended Crafting tier 4", "ec_tier_4", ExtendedPatternTableTypes.extendedCrafting(4), 4,
                    Items.EMERALD_BLOCK,
                    new String[] {
                            "CCCCCCCCC",
                            "CLLLLLLLC",
                            "CLRRRRRLC",
                            "CLRIIIRLC",
                            "CLRIGIRLC",
                            "CLRIIIRLC",
                            "CLRRRRRLC",
                            "CLLLLLLLC",
                            "CCCCCCCCC"
                    },
                    Map.of('C', Items.COPPER_INGOT, 'L', Items.LAPIS_LAZULI, 'R', Items.REDSTONE,
                            'I', Items.IRON_INGOT, 'G', Items.GOLD_INGOT)));

    private static final List<PatternCase> RE_AVARITIA_CASES = List.of(
            new PatternCase("Re:Avaritia tier 1", "re_tier_1", ExtendedPatternTableTypes.reAvaritia(1), 1,
                    Items.AMETHYST_SHARD,
                    new String[] {
                            "STS",
                            "TBT",
                            "STS"
                    },
                    Map.of('S', Items.STONE, 'T', Items.STICK, 'B', Items.BONE)),
            new PatternCase("Re:Avaritia tier 2", "re_tier_2", ExtendedPatternTableTypes.reAvaritia(2), 2,
                    Items.QUARTZ,
                    new String[] {
                            "OOOOO",
                            "ORRRO",
                            "ORERO",
                            "ORRRO",
                            "OOOOO"
                    },
                    Map.of('O', Items.OBSIDIAN, 'R', Items.REDSTONE, 'E', Items.ENDER_PEARL)),
            new PatternCase("Re:Avaritia tier 3", "re_tier_3", ExtendedPatternTableTypes.reAvaritia(3), 3,
                    Items.DIAMOND_BLOCK,
                    new String[] {
                            "BBBBBBB",
                            "BQQQQQB",
                            "BQEEEQB",
                            "BQENEQB",
                            "BQEEEQB",
                            "BQQQQQB",
                            "BBBBBBB"
                    },
                    Map.of('B', Items.BLACKSTONE, 'Q', Items.QUARTZ, 'E', Items.ENDER_PEARL, 'N', Items.NETHER_STAR)),
            new PatternCase("Re:Avaritia tier 4", "re_tier_4", ExtendedPatternTableTypes.reAvaritia(4), 4,
                    Items.NETHERITE_INGOT,
                    new String[] {
                            "AAAAAAAAA",
                            "AQQQQQQQA",
                            "AQRRRRRQA",
                            "AQRDDDRQA",
                            "AQRDNDRQA",
                            "AQRDDDRQA",
                            "AQRRRRRQA",
                            "AQQQQQQQA",
                            "AAAAAAAAA"
                    },
                    Map.of('A', Items.AMETHYST_SHARD, 'Q', Items.QUARTZ, 'R', Items.REDSTONE,
                            'D', Items.DIAMOND, 'N', Items.NETHER_STAR)));

    private static final List<PatternCase> AVARITIA_NEO_CASES = List.of(
            new PatternCase("AvaritiaNeo extreme", "dev/avaritianeo_xtreme_vanilla_test",
                    ExtendedPatternTableTypes.AVARITIA_NEO_EXTREME, 4,
                    Items.NETHERITE_INGOT,
                    new String[] {
                            "OOOOOOOOO",
                            "OIIIIIIIO",
                            "OIGGGGGIO",
                            "OIGDDDGIO",
                            "OIGDNDGIO",
                            "OIGDDDGIO",
                            "OIGGGGGIO",
                            "OIIIIIIIO",
                            "OOOOOOOOO"
                    },
                    Map.of('O', Items.OBSIDIAN, 'I', Items.IRON_INGOT, 'G', Items.GOLD_INGOT,
                            'D', Items.DIAMOND, 'N', Items.NETHER_STAR)));

    private ExtendedPatternGameTests() {
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void assemblerConnectionCapabilitiesFollowAe2(GameTestHelper helper) {
        assertAssemblerConnection(helper, EMABlocks.EXTENDED_MOLECULAR_ASSEMBLER.get(), new BlockPos(1, 1, 1),
                "Extended Molecular Assembler");
        if (EMAModPresence.isExtendedAELoaded()) {
            assertAssemblerConnection(helper, EMABlocks.EX_EXTENDED_MOLECULAR_ASSEMBLER.get(), new BlockPos(2, 1, 1),
                    "Ex Extended Molecular Assembler");
        }
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void extendedAssemblerAcceptsOnePushedJob(GameTestHelper helper) {
        if (!EMAModPresence.isExtendedCraftingLoaded()) {
            helper.succeed();
            return;
        }

        var testCase = EXTENDED_CRAFTING_CASES.getFirst();
        var pattern = decodePatternForCase(helper, testCase);
        var assembler = placeAssembler(helper, EMABlocks.EXTENDED_MOLECULAR_ASSEMBLER.get(), new BlockPos(1, 1, 1),
                "Extended Molecular Assembler");

        helper.assertTrue(assembler.acceptsPlans(), "fresh single-lane assembler should accept plans");
        helper.assertTrue(assembler.pushPattern(pattern, countersForPattern(pattern), Direction.NORTH),
                "single-lane assembler did not accept a supported extended pattern");
        assertLaneGrid(helper, assembler, 0, testCase, "single-lane assembler pushed job");
        helper.assertFalse(assembler.acceptsPlans(), "busy single-lane assembler should not accept another plan");
        helper.assertFalse(assembler.pushPattern(pattern, countersForPattern(pattern), Direction.NORTH),
                "busy single-lane assembler accepted a second job");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void exAssemblerAcceptsOneJobPerLane(GameTestHelper helper) {
        if (!EMAModPresence.isExtendedAELoaded() || !EMAModPresence.isExtendedCraftingLoaded()) {
            helper.succeed();
            return;
        }

        var testCase = EXTENDED_CRAFTING_CASES.getFirst();
        var pattern = decodePatternForCase(helper, testCase);
        var assembler = placeAssembler(helper, EMABlocks.EX_EXTENDED_MOLECULAR_ASSEMBLER.get(), new BlockPos(1, 1, 1),
                "Ex Extended Molecular Assembler");

        assertEqual(helper, ExtendedMolecularAssemblerBlockEntity.PARALLEL_LANE_COUNT, assembler.getLaneCount(),
                "Ex assembler lane count");
        for (int lane = 0; lane < assembler.getLaneCount(); lane++) {
            helper.assertTrue(assembler.acceptsPlans(), "Ex assembler should accept lane " + lane);
            helper.assertTrue(assembler.pushPattern(pattern, countersForPattern(pattern), Direction.NORTH),
                    "Ex assembler did not accept job for lane " + lane);
            assertLaneGrid(helper, assembler, lane, testCase, "Ex assembler lane " + lane + " pushed job");
        }

        helper.assertFalse(assembler.acceptsPlans(), "full Ex assembler should not advertise free lanes");
        helper.assertFalse(assembler.pushPattern(pattern, countersForPattern(pattern), Direction.NORTH),
                "full Ex assembler accepted a ninth job");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 40)
    public static void advancedAEQuantumCrafterMenuRejectsExtendedPattern(GameTestHelper helper) {
        if (!EMAModPresence.isAdvancedAELoaded() || !EMAModPresence.isExtendedCraftingLoaded()) {
            helper.succeed();
            return;
        }

        var patternStack = encodePatternStackForCase(helper, EXTENDED_CRAFTING_CASES.getFirst());
        var menu = createAdvancedAEQuantumCrafterMenu(helper);
        var patternSlot = getAdvancedAEQuantumCrafterPatternSlot(helper, menu);

        helper.assertFalse(menu.isValidForSlot(patternSlot, patternStack),
                "AdvancedAE Quantum Crafter menu accepted an EMA extended encoded pattern");
        helper.assertFalse(patternSlot.mayPlace(patternStack),
                "AdvancedAE Quantum Crafter pattern slot accepted an EMA extended encoded pattern");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 40)
    public static void extendedQuantumCrafterMenuAcceptsExtendedPattern(GameTestHelper helper) {
        if (!EMAModPresence.isAdvancedAELoaded() || !EMAModPresence.isExtendedCraftingLoaded()) {
            helper.succeed();
            return;
        }

        var patternStack = encodePatternStackForCase(helper, EXTENDED_CRAFTING_CASES.getFirst());
        var menu = createQuantumCrafterMenu(helper,
                ExtendedMolecularAssembler.makeId("extended_quantum_crafter"),
                "EMA Extended Quantum Crafter");
        var patternSlot = getAdvancedAEQuantumCrafterPatternSlot(helper, menu);

        helper.assertTrue(menu.isValidForSlot(patternSlot, patternStack),
                "EMA Extended Quantum Crafter menu rejected an EMA extended encoded pattern");
        helper.assertTrue(patternSlot.mayPlace(patternStack),
                "EMA Extended Quantum Crafter pattern slot rejected an EMA extended encoded pattern");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 40)
    public static void extendedAEPatternCoreAcceptsOnlyExtendedEncodedPatterns(GameTestHelper helper) {
        if (!EMAModPresence.isExtendedAELoaded() || !EMAModPresence.isExtendedCraftingLoaded()) {
            helper.succeed();
            return;
        }

        var patternStack = encodePatternStackForCase(helper, EXTENDED_CRAFTING_CASES.getFirst());
        var core = placeOptionalBlockEntity(helper, "extended_assembler_matrix_pattern_core", new BlockPos(1, 1, 1),
                ExtendedAssemblerMatrixPatternCoreBlockEntity.class);
        var patternInventory = core.getPatternInv(Direction.NORTH);
        helper.assertTrue(patternInventory instanceof net.neoforged.neoforge.items.IItemHandler,
                "ExtendedAE pattern core did not expose an item handler");
        var itemHandler = (net.neoforged.neoforge.items.IItemHandler) patternInventory;

        var diamondRemainder = itemHandler.insertItem(0, new ItemStack(Items.DIAMOND), true);
        helper.assertTrue(!diamondRemainder.isEmpty(),
                "ExtendedAE pattern core accepted a non-pattern item into its pattern inventory");
        var patternRemainder = itemHandler.insertItem(0, patternStack.copy(), false);
        helper.assertTrue(patternRemainder.isEmpty(),
                "ExtendedAE pattern core rejected an EMA extended encoded pattern");

        core.updatePatterns();
        assertEqual(helper, 1, core.getAvailablePatterns().size(),
                "ExtendedAE pattern core available extended pattern count");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 40)
    public static void extendedAEPatternUploaderUploadsIntoExtendedPatternCore(GameTestHelper helper) {
        if (!EMAModPresence.isExtendedAELoaded() || !EMAModPresence.isExtendedAEPlusLoaded()
                || !EMAModPresence.isExtendedCraftingLoaded()) {
            helper.succeed();
            return;
        }

        var patternStack = encodePatternStackForCase(helper, EXTENDED_CRAFTING_CASES.getFirst());
        var core = placeOptionalBlockEntity(helper, "extended_assembler_matrix_pattern_core", new BlockPos(1, 1, 1),
                ExtendedAssemblerMatrixPatternCoreBlockEntity.class);
        var uploader = placeOptionalBlockEntity(helper, "extended_assembler_matrix_pattern_uploader",
                new BlockPos(2, 1, 1), ExtendedAssemblerMatrixPatternUploaderBlockEntity.class);

        var uploaderInventory = uploader.getPatternInv(Direction.WEST);
        helper.assertTrue(uploaderInventory instanceof net.neoforged.neoforge.items.IItemHandler,
                "ExtendedAE pattern uploader did not expose an item handler");
        var uploaderHandler = (net.neoforged.neoforge.items.IItemHandler) uploaderInventory;

        var diamondRemainder = uploaderHandler.insertItem(0, new ItemStack(Items.DIAMOND), true);
        helper.assertTrue(!diamondRemainder.isEmpty(),
                "ExtendedAE pattern uploader accepted a non-pattern item");
        var patternRemainder = uploaderHandler.insertItem(0, patternStack.copy(), false);
        helper.assertTrue(patternRemainder.isEmpty(),
                "ExtendedAE pattern uploader did not upload an EMA extended encoded pattern");

        var coreInventory = core.getPatternInv(Direction.EAST);
        helper.assertTrue(coreInventory instanceof net.neoforged.neoforge.items.IItemHandler,
                "ExtendedAE pattern core did not expose an item handler after uploader insert");
        var coreHandler = (net.neoforged.neoforge.items.IItemHandler) coreInventory;
        assertStackMatches(helper, patternStack, coreHandler.getStackInSlot(0),
                "ExtendedAE pattern uploader target pattern core slot 0");
        var duplicateRemainder = uploaderHandler.insertItem(0, patternStack.copy(), false);
        assertStackMatches(helper, patternStack, duplicateRemainder,
                "ExtendedAE pattern uploader duplicate remainder");
        helper.assertTrue(coreHandler.getStackInSlot(1).isEmpty(),
                "ExtendedAE pattern uploader inserted duplicate pattern into another core slot");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 40)
    public static void extendedAEMatrixCraftingCoreTracksAndCancelsExtendedJobs(GameTestHelper helper) {
        if (!EMAModPresence.isExtendedAELoaded() || !EMAModPresence.isExtendedCraftingLoaded()) {
            helper.succeed();
            return;
        }

        var pattern = decodePatternForCase(helper, EXTENDED_CRAFTING_CASES.getFirst());
        var core = placeOptionalBlockEntity(helper, "extended_assembler_matrix_crafting_core", new BlockPos(1, 1, 1),
                ExtendedAEAssemblerMatrixCrafterAccess.class);
        for (int thread = 0; thread < 8; thread++) {
            helper.assertTrue(pushExtendedMatrixJob(helper, core, pattern),
                    "ExtendedAE matrix crafting core did not accept job for extended thread " + thread);
            assertEqual(helper, thread + 1, getExtendedMatrixUsedThreads(helper, core),
                    "ExtendedAE matrix crafting core used thread count after push " + thread);
        }

        helper.assertFalse(pushExtendedMatrixJob(helper, core, pattern),
                "ExtendedAE matrix crafting core accepted a ninth extended job");
        core.extendedmolecularassembler$cancelExtendedJobs();
        assertEqual(helper, 0, getExtendedMatrixUsedThreads(helper, core),
                "ExtendedAE matrix crafting core used thread count after cancel");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 400)
    public static void extendedCraftingTiersEncodeAndCraft(GameTestHelper helper) {
        if (!EMAModPresence.isExtendedCraftingLoaded()) {
            helper.succeed();
            return;
        }

        validateCases(helper, EXTENDED_CRAFTING_CASES);
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 400)
    public static void reAvaritiaTiersEncodeAndCraft(GameTestHelper helper) {
        if (!EMAModPresence.isReAvaritiaLoaded()) {
            helper.succeed();
            return;
        }

        validateCases(helper, RE_AVARITIA_CASES);
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 400)
    public static void avaritiaNeoExtremeEncodeAndCraft(GameTestHelper helper) {
        if (!EMAModPresence.isAvaritiaNeoLoaded()) {
            helper.succeed();
            return;
        }

        validateCases(helper, AVARITIA_NEO_CASES);
        helper.succeed();
    }

    private static void assertAssemblerConnection(GameTestHelper helper, Block block, BlockPos pos, String name) {
        helper.setBlock(pos, block);
        var level = helper.getLevel();
        var absolutePos = helper.absolutePos(pos);
        var nodeHost = level.getCapability(AECapabilities.IN_WORLD_GRID_NODE_HOST, absolutePos, null);
        helper.assertTrue(nodeHost != null, name + " does not expose AE2 grid-node host capability");
        helper.assertTrue(nodeHost.getCableConnectionType(Direction.NORTH) == AECableType.COVERED,
                name + " must expose covered cable connection type like AE2 Molecular Assembler");
        helper.assertTrue(ICraftingMachine.of(level, absolutePos, Direction.NORTH) != null,
                name + " does not expose AE2 crafting-machine capability");
    }

    private static ExtendedMolecularAssemblerBlockEntity placeAssembler(GameTestHelper helper, Block block, BlockPos pos,
            String name) {
        helper.setBlock(pos, block);
        var blockEntity = helper.getBlockEntity(pos);
        helper.assertTrue(blockEntity instanceof ExtendedMolecularAssemblerBlockEntity,
                name + " did not create an ExtendedMolecularAssemblerBlockEntity");
        return (ExtendedMolecularAssemblerBlockEntity) blockEntity;
    }

    private static <T> T placeOptionalBlockEntity(GameTestHelper helper, String blockPath, BlockPos pos,
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

    private static boolean pushExtendedMatrixJob(GameTestHelper helper, ExtendedAEAssemblerMatrixCrafterAccess core,
            ExtendedTableCraftingPattern pattern) {
        return core.extendedmolecularassembler$pushExtendedJob(pattern, countersForPattern(pattern));
    }

    private static int getExtendedMatrixUsedThreads(GameTestHelper helper, ExtendedAEAssemblerMatrixCrafterAccess core) {
        return core.extendedmolecularassembler$getExtendedUsedThreadCount();
    }

    private static void validateCases(GameTestHelper helper, List<PatternCase> cases) {
        for (var testCase : cases) {
            validateCase(helper, testCase);
        }
    }

    private static void validateCase(GameTestHelper helper, PatternCase testCase) {
        var level = helper.getLevel();
        var expectedRecipe = testCase.recipeId();
        helper.assertTrue(level.getRecipeManager().byKey(expectedRecipe).isPresent(),
                "Missing test recipe " + expectedRecipe);

        var machineGrid = testCase.machineGrid();
        var match = requireMatch(helper, ExtendedPatternRecipeFinder.find(machineGrid, level), testCase);
        assertEqual(helper, expectedRecipe, match.recipe().id(), testCase.label() + " recipe lookup");
        assertStackMatches(helper, testCase.outputStack(), match.result(), testCase.label() + " lookup output");
        assertSparseInputs(helper, testCase, match.inputs(), testCase.label() + " lookup inputs");

        var patternStack = ExtendedPatternDetailsHelper.encodeExtendedCraftingPattern(match.recipe(), match.inputs(),
                match.result(), false, true);
        var encoded = patternStack.get(EMADataComponents.ENCODED_EXTENDED_CRAFTING_PATTERN);
        helper.assertTrue(encoded != null, testCase.label() + " did not write encoded pattern data");
        assertEncoded(helper, testCase, encoded);

        var decoded = appeng.api.crafting.PatternDetailsHelper.decodePattern(patternStack, level);
        helper.assertTrue(decoded instanceof ExtendedTableCraftingPattern,
                testCase.label() + " did not decode to ExtendedTableCraftingPattern");
        var pattern = (ExtendedTableCraftingPattern) decoded;
        assertPattern(helper, testCase, pattern);

        assertStackMatches(helper, testCase.outputStack(), pattern.assembleFromMachineGrid(machineGrid::get, level),
                testCase.label() + " assembly from source grid");
        assertFillCraftingGrid(helper, testCase, pattern);
    }

    private static ExtendedTableCraftingPattern decodePatternForCase(GameTestHelper helper, PatternCase testCase) {
        var level = helper.getLevel();
        var match = requireMatch(helper, ExtendedPatternRecipeFinder.find(testCase.machineGrid(), level), testCase);
        var patternStack = ExtendedPatternDetailsHelper.encodeExtendedCraftingPattern(match.recipe(), match.inputs(),
                match.result(), false, true);
        var decoded = appeng.api.crafting.PatternDetailsHelper.decodePattern(patternStack, level);
        helper.assertTrue(decoded instanceof ExtendedTableCraftingPattern,
                testCase.label() + " did not decode to ExtendedTableCraftingPattern");
        return (ExtendedTableCraftingPattern) decoded;
    }

    private static ItemStack encodePatternStackForCase(GameTestHelper helper, PatternCase testCase) {
        var level = helper.getLevel();
        var match = requireMatch(helper, ExtendedPatternRecipeFinder.find(testCase.machineGrid(), level), testCase);
        return ExtendedPatternDetailsHelper.encodeExtendedCraftingPattern(match.recipe(), match.inputs(),
                match.result(), false, true);
    }

    private static AEBaseMenu createAdvancedAEQuantumCrafterMenu(GameTestHelper helper) {
        return createQuantumCrafterMenu(helper, ResourceLocation.fromNamespaceAndPath("advanced_ae", "quantum_crafter"),
                "AdvancedAE Quantum Crafter");
    }

    private static AEBaseMenu createQuantumCrafterMenu(GameTestHelper helper, ResourceLocation blockId, String name) {
        var block = BuiltInRegistries.BLOCK.get(blockId);
        helper.assertTrue(block != Blocks.AIR, "Missing " + name + " block " + blockId);

        var pos = new BlockPos(1, 1, 1);
        helper.setBlock(pos, block);
        var blockEntity = helper.getBlockEntity(pos);
        helper.assertTrue(blockEntity instanceof QuantumCrafterEntity,
                name + " block " + blockId + " did not create a QuantumCrafterEntity");

        var player = FakePlayerFactory.getMinecraft((ServerLevel) helper.getLevel());
        return new QuantumCrafterMenu(1, player.getInventory(), (QuantumCrafterEntity) blockEntity);
    }

    private static Slot getAdvancedAEQuantumCrafterPatternSlot(GameTestHelper helper, AEBaseMenu menu) {
        helper.assertTrue(!menu.slots.isEmpty(), "AdvancedAE Quantum Crafter menu has no slots");
        return menu.slots.getFirst();
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

    private static void assertLaneGrid(GameTestHelper helper, ExtendedMolecularAssemblerBlockEntity assembler,
            int laneIndex, PatternCase testCase, String name) {
        var expectedGrid = testCase.machineGrid();
        var laneInventory = assembler.getCraftInventory(laneIndex);
        for (int slot = 0; slot < ExtendedTableCraftingPattern.MACHINE_GRID_SIZE; slot++) {
            assertStackMatches(helper, expectedGrid.get(slot), laneInventory.getStackInSlot(slot),
                    name + " slot " + slot);
        }
        assertStackMatches(helper, ItemStack.EMPTY,
                laneInventory.getStackInSlot(ExtendedMolecularAssemblerBlockEntity.OUTPUT_SLOT),
                name + " output slot should stay empty before ticking");
    }

    private static ExtendedPatternRecipeMatch requireMatch(GameTestHelper helper,
            Optional<ExtendedPatternRecipeMatch> match, PatternCase testCase) {
        if (match.isEmpty()) {
            helper.fail(testCase.label() + " was not found from a centered 9x9 machine grid");
        }
        return match.orElseThrow();
    }

    private static void assertEncoded(GameTestHelper helper, PatternCase testCase,
            EncodedExtendedCraftingPattern encoded) {
        assertEqual(helper, testCase.recipeId(), encoded.recipeId(), testCase.label() + " encoded recipe id");
        assertEqual(helper, testCase.tableType(), encoded.tableType(), testCase.label() + " encoded table type");
        assertEqual(helper, testCase.tier(), encoded.tableTier(), testCase.label() + " encoded table tier");
        assertEqual(helper, testCase.side(), encoded.tableSideLength(), testCase.label() + " encoded table side");
        helper.assertFalse(encoded.canSubstitute(), testCase.label() + " unexpectedly allows substitutions");
        helper.assertTrue(encoded.canSubstituteFluids(), testCase.label() + " did not allow fluid substitutions");
        assertStackMatches(helper, testCase.outputStack(), encoded.result(), testCase.label() + " encoded output");
        assertEqual(helper, testCase.side() * testCase.side(), encoded.inputs().size(),
                testCase.label() + " encoded input size");
        assertSparseInputs(helper, testCase, encoded.inputs().toArray(ItemStack[]::new),
                testCase.label() + " encoded inputs");
    }

    private static void assertPattern(GameTestHelper helper, PatternCase testCase, ExtendedTableCraftingPattern pattern) {
        assertEqual(helper, testCase.tableType(), pattern.tableType(), testCase.label() + " decoded table type");
        assertEqual(helper, testCase.tier(), pattern.tableTier(), testCase.label() + " decoded table tier");
        assertEqual(helper, testCase.side(), pattern.tableSideLength(), testCase.label() + " decoded table side");
        var output = pattern.getSparseOutputs().getFirst();
        helper.assertTrue(output.what() instanceof AEItemKey, testCase.label() + " decoded output is not an item");
        assertStackMatches(helper, testCase.outputStack(),
                ((AEItemKey) output.what()).toStack(Math.toIntExact(output.amount())),
                testCase.label() + " decoded output");
    }

    private static void assertFillCraftingGrid(GameTestHelper helper, PatternCase testCase,
            ExtendedTableCraftingPattern pattern) {
        var counters = countersForPattern(pattern);

        var filledGrid = NonNullList.withSize(ExtendedTableCraftingPattern.MACHINE_GRID_SIZE, ItemStack.EMPTY);
        pattern.fillCraftingGrid(counters, filledGrid::set);
        assertCountersEmpty(helper, counters, testCase.label() + " fillCraftingGrid");
        assertStackMatches(helper, testCase.outputStack(),
                pattern.assembleFromMachineGrid(filledGrid::get, helper.getLevel()),
                testCase.label() + " assembly from filled AE counters");

        var expectedGrid = testCase.machineGrid();
        for (int slot = 0; slot < expectedGrid.size(); slot++) {
            assertStackMatches(helper, expectedGrid.get(slot), filledGrid.get(slot),
                    testCase.label() + " filled machine slot " + slot);
        }
    }

    private static void assertCountersEmpty(GameTestHelper helper, KeyCounter[] counters, String name) {
        for (int i = 0; i < counters.length; i++) {
            counters[i].removeZeros();
            if (!counters[i].isEmpty()) {
                helper.fail(name + " left over AE input counter " + i + ": " + counters[i].iterator().next());
            }
        }
    }

    private static void assertSparseInputs(GameTestHelper helper, PatternCase testCase, ItemStack[] actual,
            String name) {
        var expected = testCase.sparseInputs();
        assertEqual(helper, expected.length, actual.length, name + " length");
        for (int slot = 0; slot < expected.length; slot++) {
            assertStackMatches(helper, expected[slot], actual[slot], name + " slot " + slot);
        }
    }

    private static void assertStackMatches(GameTestHelper helper, ItemStack expected, ItemStack actual, String name) {
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

    private record PatternCase(String label, String recipePath, ResourceLocation tableType, int tier, Item output,
            String[] pattern, Map<Character, Item> keys) {
        private ResourceLocation recipeId() {
            return ExtendedMolecularAssembler.makeId(recipePath.contains("/") ? recipePath : "gametest/" + recipePath);
        }

        private int side() {
            return tier * 2 + 1;
        }

        private ItemStack outputStack() {
            return new ItemStack(output);
        }

        private NonNullList<ItemStack> machineGrid() {
            var result = NonNullList.withSize(ExtendedTableCraftingPattern.MACHINE_GRID_SIZE, ItemStack.EMPTY);
            var offset = Math.floorDiv(ExtendedTableCraftingPattern.MACHINE_GRID_SIDE - side(), 2);
            for (int y = 0; y < side(); y++) {
                for (int x = 0; x < side(); x++) {
                    result.set(x + offset + (y + offset) * ExtendedTableCraftingPattern.MACHINE_GRID_SIDE,
                            stackFor(pattern[y].charAt(x)));
                }
            }
            return result;
        }

        private ItemStack[] sparseInputs() {
            var result = new ItemStack[side() * side()];
            for (int y = 0; y < side(); y++) {
                for (int x = 0; x < side(); x++) {
                    result[x + y * side()] = stackFor(pattern[y].charAt(x));
                }
            }
            return result;
        }

        private ItemStack stackFor(char symbol) {
            if (symbol == ' ') {
                return ItemStack.EMPTY;
            }
            var item = keys.get(symbol);
            if (item == null) {
                throw new IllegalArgumentException("No key for symbol " + symbol + " in " + label);
            }
            return new ItemStack(item);
        }
    }
}
