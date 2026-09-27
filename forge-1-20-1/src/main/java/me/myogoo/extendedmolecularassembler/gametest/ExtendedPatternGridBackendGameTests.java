package me.myogoo.extendedmolecularassembler.gametest;

import me.myogoo.extendedmolecularassembler.ExtendedMolecularAssembler;
import me.myogoo.extendedmolecularassembler.integration.itemlist.ExtendedPatternRecipeTransfer;
import me.myogoo.extendedmolecularassembler.pattern.EncodedExtendedCraftingPattern;
import me.myogoo.extendedmolecularassembler.pattern.ExtendedTableCraftingPattern;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

import java.util.ArrayList;

@GameTestHolder(ExtendedMolecularAssembler.MODID)
@PrefixGameTestTemplate(false)
public final class ExtendedPatternGridBackendGameTests {
    private ExtendedPatternGridBackendGameTests() {
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void machineGridMappingSupportsLegacyAndLargeSides(GameTestHelper helper) {
        helper.assertTrue(ExtendedTableCraftingPattern.toMachineGridIndex(0, 9, 9) == 0,
                "Legacy 9x9 mapping changed");
        helper.assertTrue(ExtendedTableCraftingPattern.toMachineGridIndex(80, 9, 9) == 80,
                "Legacy 9x9 final slot changed");
        helper.assertTrue(ExtendedTableCraftingPattern.toMachineGridIndex(0, 11, 11) == 0,
                "Epic 11x11 mapping is incorrect");
        helper.assertTrue(ExtendedTableCraftingPattern.toMachineGridIndex(120, 11, 11) == 120,
                "Epic 11x11 final slot is incorrect");
        helper.assertTrue(ExtendedTableCraftingPattern.toMachineGridIndex(0, 13, 13) == 0,
                "Legendary 13x13 mapping is incorrect");
        helper.assertTrue(ExtendedTableCraftingPattern.toMachineGridIndex(168, 13, 13) == 168,
                "Legendary 13x13 final slot is incorrect");
        helper.assertTrue(ExtendedTableCraftingPattern.toMachineGridIndex(0, 9, 13) == 28,
                "A 9x9 recipe was not centered in a 13x13 grid");
        helper.assertTrue(ExtendedTableCraftingPattern.toMachineGridIndex(0, 11, 9) == -1,
                "A larger recipe was accepted into a smaller grid");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void recipeTransfersRespectTerminalGridTier(GameTestHelper helper) {
        helper.assertTrue(ExtendedPatternRecipeTransfer.supportsGridSide(3, 9)
                        && ExtendedPatternRecipeTransfer.supportsGridSide(5, 9)
                        && ExtendedPatternRecipeTransfer.supportsGridSide(7, 9)
                        && ExtendedPatternRecipeTransfer.supportsGridSide(9, 9),
                "Legacy 9x9 terminal stopped accepting recipes up to 9x9");
        helper.assertTrue(ExtendedPatternRecipeTransfer.supportsGridSide(11, 11)
                        && !ExtendedPatternRecipeTransfer.supportsGridSide(9, 11)
                        && !ExtendedPatternRecipeTransfer.supportsGridSide(11, 13)
                        && ExtendedPatternRecipeTransfer.supportsGridSide(13, 13),
                "Large terminal accepted a recipe from the wrong table tier");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void patternSerializationKeepsLegacyAndAcceptsMaxGrid(GameTestHelper helper) {
        var legacy = encodeWithInputCount(81);
        var large = encodeWithInputCount(ExtendedTableCraftingPattern.MAX_GRID_SIZE);
        helper.assertTrue(EncodedExtendedCraftingPattern.get(legacy) != null
                        && EncodedExtendedCraftingPattern.get(legacy).inputs().size() == 81,
                "Legacy 9x9 encoded pattern was rejected");
        helper.assertTrue(EncodedExtendedCraftingPattern.get(large) != null
                        && EncodedExtendedCraftingPattern.get(large).inputs().size()
                                == ExtendedTableCraftingPattern.MAX_GRID_SIZE,
                "13x13 encoded pattern was rejected");
        helper.succeed();
    }

    private static ItemStack encodeWithInputCount(int inputCount) {
        var inputs = new ArrayList<ItemStack>(inputCount);
        for (int i = 0; i < inputCount; i++) {
            inputs.add(new ItemStack(Items.DIAMOND));
        }
        var encoded = new EncodedExtendedCraftingPattern(inputs, new ItemStack(Items.DIAMOND),
                new ResourceLocation(ExtendedMolecularAssembler.MODID, "grid_test"), null, 0, 0, false, false);
        var stack = new ItemStack(Items.PAPER);
        EncodedExtendedCraftingPattern.set(stack, encoded);
        return stack;
    }
}
