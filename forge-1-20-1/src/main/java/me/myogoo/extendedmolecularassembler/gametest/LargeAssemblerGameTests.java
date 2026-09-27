package me.myogoo.extendedmolecularassembler.gametest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import appeng.api.crafting.PatternDetailsHelper;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.GenericStack;
import appeng.api.stacks.KeyCounter;
import com.mojang.authlib.GameProfile;
import me.myogoo.extendedmolecularassembler.ExtendedMolecularAssembler;
import me.myogoo.extendedmolecularassembler.adapter.recipe.TableRecipeAdapters;
import me.myogoo.extendedmolecularassembler.api.ExtendedPatternDetailsHelper;
import me.myogoo.extendedmolecularassembler.api.annotation.ExtendedCrafting;
import me.myogoo.extendedmolecularassembler.block.blockentity.ExtendedMolecularAssemblerBlockEntity;
import me.myogoo.extendedmolecularassembler.init.EMABlockEntities;
import me.myogoo.extendedmolecularassembler.init.EMABlocks;
import me.myogoo.extendedmolecularassembler.menu.ExtendedMolecularAssemblerMenu;
import me.myogoo.extendedmolecularassembler.menu.slot.ExtendedMolecularAssemblerOutputSlot;
import me.myogoo.extendedmolecularassembler.menu.slot.ExtendedMolecularAssemblerPatternSlot;
import me.myogoo.extendedmolecularassembler.pattern.ExtendedTableCraftingPattern;
import me.myogoo.myotus.api.MyotusAPI;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(ExtendedMolecularAssembler.MODID)
@PrefixGameTestTemplate(false)
public final class LargeAssemblerGameTests {
    private LargeAssemblerGameTests() {
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void assemblerGridSizesMenusAndInventoriesSurviveReload(GameTestHelper helper) {
        var player = new FakePlayer(helper.getLevel(), new GameProfile(UUID.randomUUID(), "ema_large_assembler"));
        var variants = assemblerVariants();
        for (int i = 0; i < variants.size(); i++) {
            var variant = variants.get(i);
            var pos = assemblerTestPos(i);
            var assembler = placeAssembler(helper, variant.block(), pos);
            var gridSize = variant.side() * variant.side();

            helper.assertTrue(assembler.getGridSide() == variant.side(), "Wrong BE grid side: " + variant.side());
            helper.assertTrue(assembler.getGridSize() == gridSize, "Wrong BE grid size: " + variant.side());
            helper.assertTrue(assembler.getLaneCount() == variant.laneCount(), "Wrong lane count: " + variant.side());
            helper.assertTrue(assembler.getOutputSlot() == gridSize, "Wrong output slot: " + variant.side());
            helper.assertTrue(ExtendedMolecularAssemblerBlockEntity.GRID_SIZE == 81
                            && ExtendedMolecularAssemblerBlockEntity.OUTPUT_SLOT == 81,
                    "Legacy 9x9 slot constants changed");

            for (int lane = 0; lane < variant.laneCount(); lane++) {
                var inventory = assembler.getCraftInventory(lane);
                helper.assertTrue(inventory.size() == gridSize + 1,
                        "Wrong lane inventory size: " + variant.side() + " lane " + lane);
                inventory.setItemDirect(gridSize - 1, new ItemStack(Items.DIAMOND, lane + 1));
                inventory.setItemDirect(gridSize, new ItemStack(Items.NETHER_STAR, lane + 1));
            }

            var menu = createMenu(player, assembler);
            helper.assertTrue(menu.getGridSide() == variant.side(), "Wrong menu grid side: " + variant.side());
            var expectedGridSlots = (long) gridSize * variant.laneCount();
            var gridSlots = menu.slots.stream().filter(ExtendedMolecularAssemblerPatternSlot.class::isInstance).count();
            var outputSlots = menu.slots.stream().filter(ExtendedMolecularAssemblerOutputSlot.class::isInstance).count();
            helper.assertTrue(gridSlots == expectedGridSlots,
                    "Wrong menu grid slot count: " + variant.side() + " expected " + expectedGridSlots
                            + " got " + gridSlots);
            helper.assertTrue(outputSlots == variant.laneCount(), "Wrong menu output slot count: " + variant.side());

            var saved = new CompoundTag();
            assembler.saveAdditional(saved);
            var reloaded = new ExtendedMolecularAssemblerBlockEntity(
                    variant.blockEntityType(), pos, variant.block().defaultBlockState());
            reloaded.setLevel(helper.getLevel());
            reloaded.loadTag(saved);
            helper.assertTrue(reloaded.getGridSide() == variant.side(), "Reloaded BE lost grid side: " + variant.side());
            for (int lane = 0; lane < variant.laneCount(); lane++) {
                assertStack(helper, new ItemStack(Items.DIAMOND, lane + 1),
                        reloaded.getCraftInventory(lane).getStackInSlot(gridSize - 1),
                        "Reloaded last grid slot: " + variant.side() + " lane " + lane);
                assertStack(helper, new ItemStack(Items.NETHER_STAR, lane + 1),
                        reloaded.getCraftInventory(lane).getStackInSlot(gridSize),
                        "Reloaded output slot: " + variant.side() + " lane " + lane);
            }
        }
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 40)
    public static void assemblerPatternsFitAndMapAcrossGridSizes(GameTestHelper helper) {
        if (!MyotusAPI.integrations().isLoaded(ExtendedCrafting.class)) {
            helper.succeed();
            return;
        }

        var pattern = makeTierFourPattern(helper.getLevel());
        helper.assertTrue(pattern.tableSideLength() == 9, "The test recipe should be 9x9");
        var tooLargeForEpic = withTableSide(pattern, 13, helper.getLevel());
        var tooLargeForNormal = withTableSide(pattern, 11, helper.getLevel());
        var player = new FakePlayer(helper.getLevel(), new GameProfile(UUID.randomUUID(), "ema_assembler_pattern"));
        var variants = assemblerVariants();

        for (int i = 0; i < variants.size(); i++) {
            var variant = variants.get(i);
            var pos = assemblerTestPos(i);
            var assembler = placeAssembler(helper, variant.block(), pos);

            if (variant.side() == 9) {
                helper.assertFalse(assembler.canUsePattern(tooLargeForNormal),
                        "A 9x9 assembler accepted an 11x11 pattern");
            } else if (variant.side() == 11) {
                helper.assertTrue(assembler.canUsePattern(tooLargeForNormal),
                        "An 11x11 assembler rejected an 11x11 pattern");
                helper.assertFalse(assembler.canUsePattern(tooLargeForEpic),
                        "An 11x11 assembler accepted a 13x13 pattern");
            } else {
                helper.assertTrue(assembler.canUsePattern(tooLargeForEpic),
                        "A 13x13 assembler rejected a 13x13 pattern");
            }

            helper.assertTrue(assembler.canUsePattern(pattern),
                    "Assembler rejected a fitting 9x9 pattern on a " + variant.side() + "x" + variant.side() + " grid");
            helper.assertTrue(assembler.pushPattern(pattern, countersForPattern(pattern), Direction.NORTH),
                    "Assembler rejected a fitting 9x9 pattern on a " + variant.side() + "x" + variant.side() + " grid");

            var inventory = assembler.getCraftInventory(0);
            for (int patternSlot = 0; patternSlot < pattern.tableSideLength() * pattern.tableSideLength(); patternSlot++) {
                var machineSlot = pattern.toMachineGridIndex(patternSlot, variant.side());
                var input = pattern.getSparseInputs().get(patternSlot);
                var expected = input == null ? ItemStack.EMPTY : asItemStack(helper, input, "Pattern input");
                assertStack(helper, expected, inventory.getStackInSlot(machineSlot),
                        "Pattern mapping on " + variant.side() + "x" + variant.side() + " slot " + patternSlot);
                helper.assertTrue((pattern.getDisplayInputsForMachineSlot(machineSlot, variant.side()).length > 0)
                                == (input != null),
                        "Display input mapping on " + variant.side() + "x" + variant.side() + " slot " + patternSlot);
            }

            var expectedOutput = asItemStack(helper, pattern.getOutputs()[0], "Pattern output");
            assertStack(helper, expectedOutput,
                    pattern.assembleFromMachineGrid(inventory::getStackInSlot, helper.getLevel(), variant.side()),
                    "Large assembler recipe combination on " + variant.side() + "x" + variant.side() + " grid");

            var menu = createMenu(player, assembler);
            if (variant.side() > pattern.tableSideLength()) {
                var outsideTable = findPatternSlot(menu, 0);
                var centeredLastInput = findPatternSlot(menu,
                        pattern.toMachineGridIndex(pattern.tableSideLength() * pattern.tableSideLength() - 1,
                                variant.side()));
                var centeredLastInputIndex = centeredLastInput.getSlotIndex();
                helper.assertFalse(outsideTable.isSlotEnabled(),
                        "The large-grid menu enabled a slot outside the centered pattern");
                helper.assertTrue(centeredLastInput.isSlotEnabled(),
                        "The large-grid menu hid the centered pattern's last input");
                helper.assertFalse(menu.isValidItemForSlot(0, outsideTable.getSlotIndex(),
                                new ItemStack(Items.COPPER_INGOT)),
                        "The large-grid menu accepted an item outside the centered pattern");
                helper.assertTrue(menu.isValidItemForSlot(0, centeredLastInputIndex,
                                new ItemStack(Items.COPPER_INGOT)),
                        "The large-grid menu rejected a valid centered pattern input");
            }
        }
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 40)
    public static void assemblerTierFiveAndSixRecipesCraftUsingEdgeSlots(GameTestHelper helper) {
        if (!MyotusAPI.integrations().isLoaded(ExtendedCrafting.class)) {
            helper.succeed();
            return;
        }

        var level = helper.getLevel();
        var tierFiveRecipe = level.getRecipeManager()
                .byKey(ExtendedMolecularAssembler.makeId("gametest/ec_tier_5"))
                .orElse(null);
        var tierSixRecipe = level.getRecipeManager()
                .byKey(ExtendedMolecularAssembler.makeId("gametest/ec_tier_6"))
                .orElse(null);
        var hasEpicTable = BuiltInRegistries.ITEM.containsKey(new ResourceLocation("extendedcrafting", "epic_table"));
        var hasLegendaryTable = BuiltInRegistries.ITEM.containsKey(
                new ResourceLocation("extendedcrafting", "legendary_table"));
        helper.assertTrue((tierFiveRecipe != null) == hasEpicTable,
                "Tier-five fixture registration did not match Extended Crafting epic table availability");
        helper.assertTrue((tierSixRecipe != null) == hasLegendaryTable,
                "Tier-six fixture registration did not match Extended Crafting legendary table availability");

        var recipes = new ArrayList<LargeRecipeCase>();
        if (hasEpicTable && tierFiveRecipe != null) {
            recipes.add(new LargeRecipeCase(11, tierFiveRecipe, Items.DIAMOND_BLOCK));
        }
        if (hasLegendaryTable && tierSixRecipe != null) {
            recipes.add(new LargeRecipeCase(13, tierSixRecipe, Items.NETHERITE_BLOCK));
        }
        var variants = assemblerVariants();
        var assemblerIndex = 0;
        for (int i = 0; i < recipes.size(); i++) {
            var testCase = recipes.get(i);
            var pattern = makePattern(level, testCase.recipe());
            helper.assertTrue(pattern.tableSideLength() == testCase.side(),
                    "Fixture recipe did not decode to a " + testCase.side() + "x" + testCase.side() + " pattern");

            for (var variant : variants.stream().filter(candidate -> candidate.side() == testCase.side()).toList()) {
                var assembler = placeAssembler(helper, variant.block(), assemblerTestPos(assemblerIndex++));
                var craftsToCheck = variant.laneCount();
                for (int lane = 0; lane < craftsToCheck; lane++) {
                    helper.assertTrue(assembler.pushPattern(pattern, countersForPattern(pattern), Direction.NORTH),
                            "Assembler rejected the tier " + pattern.tableTier() + " fixture pattern on lane " + lane);
                    helper.assertTrue(assembler.getCurrentPattern(lane) != null,
                            "Tier " + pattern.tableTier() + " fixture did not occupy lane " + lane);

                    var inventory = assembler.getCraftInventory(lane);
                    var edgeSlot = variant.side() * variant.side() - 1;
                    assertStack(helper, new ItemStack(Items.COPPER_INGOT), inventory.getStackInSlot(edgeSlot),
                            "Tier " + pattern.tableTier() + " bottom-right edge input on lane " + lane);
                    assertStack(helper, new ItemStack(Items.NETHER_STAR),
                            inventory.getStackInSlot((variant.side() / 2) * variant.side() + variant.side() / 2),
                            "Tier " + pattern.tableTier() + " center input on lane " + lane);

                    var assembled = pattern.assembleFromMachineGrid(inventory::getStackInSlot, level,
                            assembler.getGridSide());
                    assertStack(helper, new ItemStack(testCase.expectedOutput()), assembled,
                            "Tier " + pattern.tableTier() + " recipe assembly from the real edge slot on lane " + lane);
                    var remainders = pattern.getRemainingItemsFromMachineGrid(inventory::getStackInSlot,
                            assembler.getGridSide());
                    helper.assertTrue(remainders.size() == variant.side() * variant.side(),
                            "Tier " + pattern.tableTier() + " remainder grid size mismatch");
                }
                helper.assertFalse(assembler.pushPattern(pattern, countersForPattern(pattern), Direction.NORTH),
                        "Assembler accepted a tier " + pattern.tableTier() + " fixture after all lanes were occupied");
            }
        }
        helper.succeed();
    }

    private static List<AssemblerVariant> assemblerVariants() {
        var variants = new ArrayList<>(List.of(
                new AssemblerVariant(9, 1, EMABlocks.EXTENDED_MOLECULAR_ASSEMBLER.get(),
                        EMABlockEntities.EXTENDED_MOLECULAR_ASSEMBLER.get()),
                new AssemblerVariant(11, 1, EMABlocks.EPIC_MOLECULAR_ASSEMBLER.get(),
                        EMABlockEntities.EPIC_MOLECULAR_ASSEMBLER.get()),
                new AssemblerVariant(13, 1, EMABlocks.LEGENDARY_MOLECULAR_ASSEMBLER.get(),
                        EMABlockEntities.LEGENDARY_MOLECULAR_ASSEMBLER.get())));
        if (EMABlocks.EX_EXTENDED_MOLECULAR_ASSEMBLER != null
                && EMABlockEntities.EX_EXTENDED_MOLECULAR_ASSEMBLER != null) {
            variants.add(new AssemblerVariant(9, ExtendedMolecularAssemblerBlockEntity.PARALLEL_LANE_COUNT,
                    EMABlocks.EX_EXTENDED_MOLECULAR_ASSEMBLER.get(),
                    EMABlockEntities.EX_EXTENDED_MOLECULAR_ASSEMBLER.get()));
        }
        if (EMABlocks.EX_EPIC_MOLECULAR_ASSEMBLER != null
                && EMABlockEntities.EX_EPIC_MOLECULAR_ASSEMBLER != null) {
            variants.add(new AssemblerVariant(11, ExtendedMolecularAssemblerBlockEntity.PARALLEL_LANE_COUNT,
                    EMABlocks.EX_EPIC_MOLECULAR_ASSEMBLER.get(),
                    EMABlockEntities.EX_EPIC_MOLECULAR_ASSEMBLER.get()));
        }
        if (EMABlocks.EX_LEGENDARY_MOLECULAR_ASSEMBLER != null
                && EMABlockEntities.EX_LEGENDARY_MOLECULAR_ASSEMBLER != null) {
            variants.add(new AssemblerVariant(13, ExtendedMolecularAssemblerBlockEntity.PARALLEL_LANE_COUNT,
                    EMABlocks.EX_LEGENDARY_MOLECULAR_ASSEMBLER.get(),
                    EMABlockEntities.EX_LEGENDARY_MOLECULAR_ASSEMBLER.get()));
        }
        return variants;
    }

    private static ExtendedMolecularAssemblerBlockEntity placeAssembler(GameTestHelper helper, Block block,
            BlockPos pos) {
        helper.setBlock(pos, block);
        var blockEntity = helper.getBlockEntity(pos);
        helper.assertTrue(blockEntity instanceof ExtendedMolecularAssemblerBlockEntity,
                "Assembler did not create its expected block entity");
        return (ExtendedMolecularAssemblerBlockEntity) blockEntity;
    }

    private static BlockPos assemblerTestPos(int index) {
        return new BlockPos((index % 2) * 2, 1, (index / 2) * 2);
    }

    private static ExtendedMolecularAssemblerMenu createMenu(FakePlayer player,
            ExtendedMolecularAssemblerBlockEntity assembler) {
        return new ExtendedMolecularAssemblerMenu(0, player.getInventory(), assembler) {
            @Override
            public boolean isClientSide() {
                return true;
            }
        };
    }

    private static ExtendedMolecularAssemblerPatternSlot findPatternSlot(ExtendedMolecularAssemblerMenu menu,
            int inventorySlot) {
        return menu.slots.stream()
                .filter(ExtendedMolecularAssemblerPatternSlot.class::isInstance)
                .map(ExtendedMolecularAssemblerPatternSlot.class::cast)
                .filter(slot -> slot.getLaneIndex() == 0 && slot.getSlotIndex() == inventorySlot)
                .findFirst()
                .orElseThrow();
    }

    private static ExtendedTableCraftingPattern makeTierFourPattern(Level level) {
        return makePattern(level, level.getRecipeManager()
                .byKey(ExtendedMolecularAssembler.makeId("gametest/ec_tier_4"))
                .orElseThrow());
    }

    private static ExtendedTableCraftingPattern makePattern(Level level, Recipe<?> recipe) {
        var adapter = TableRecipeAdapters.of(recipe);
        var ingredients = adapter.slotIngredients();
        var sparseInputs = new ItemStack[adapter.gridSize()];
        for (int slot = 0; slot < sparseInputs.length; slot++) {
            var matches = ingredients.get(slot).getItems();
            sparseInputs[slot] = matches.length == 0 ? ItemStack.EMPTY : matches[0].copy();
        }
        var inputList = Arrays.asList(sparseInputs);
        if (!adapter.matches(inputList, level)) {
            throw new IllegalStateException("The GameTest recipe inputs no longer match");
        }
        var output = adapter.assemble(inputList, level);
        var patternStack = ExtendedPatternDetailsHelper.encodeExtendedCraftingPattern(
                recipe, sparseInputs, output, false, true);
        var decoded = PatternDetailsHelper.decodePattern(patternStack, level);
        if (!(decoded instanceof ExtendedTableCraftingPattern pattern)) {
            throw new IllegalStateException("The recipe did not decode as an extended table pattern");
        }
        return pattern;
    }

    private static ItemStack asItemStack(GameTestHelper helper, GenericStack stack, String label) {
        helper.assertTrue(stack.what() instanceof AEItemKey, label + " is not an item key");
        if (!(stack.what() instanceof AEItemKey itemKey)) {
            return ItemStack.EMPTY;
        }
        return itemKey.toStack(Math.toIntExact(stack.amount()));
    }

    private static ExtendedTableCraftingPattern withTableSide(ExtendedTableCraftingPattern pattern, int side,
            Level level) {
        return new ExtendedTableCraftingPattern(pattern.getDefinition(), level) {
            @Override
            public int tableSideLength() {
                return side;
            }
        };
    }

    private static KeyCounter[] countersForPattern(ExtendedTableCraftingPattern pattern) {
        var inputs = pattern.getInputs();
        var counters = new KeyCounter[inputs.length];
        for (int i = 0; i < inputs.length; i++) {
            counters[i] = new KeyCounter();
            var input = inputs[i].getPossibleInputs()[0];
            counters[i].add(input.what(), input.amount() * inputs[i].getMultiplier());
        }
        return counters;
    }

    private static void assertStack(GameTestHelper helper, ItemStack expected, ItemStack actual, String name) {
        if (!ItemStack.matches(expected, actual)) {
            helper.fail(name + ": expected " + expected + ", got " + actual);
        }
    }

    private record AssemblerVariant(int side, int laneCount, Block block,
            BlockEntityType<ExtendedMolecularAssemblerBlockEntity> blockEntityType) {
    }

    private record LargeRecipeCase(int side, Recipe<?> recipe,
            net.minecraft.world.item.Item expectedOutput) {
    }
}
