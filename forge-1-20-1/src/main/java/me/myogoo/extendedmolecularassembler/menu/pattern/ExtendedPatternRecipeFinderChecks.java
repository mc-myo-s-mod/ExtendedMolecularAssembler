package me.myogoo.extendedmolecularassembler.menu.pattern;

import java.util.List;

import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapelessRecipe;

public final class ExtendedPatternRecipeFinderChecks {
    private ExtendedPatternRecipeFinderChecks() {
    }

    public static void assertDistinctCandidates(GameTestHelper helper) {
        for (int machineSide : new int[] {9, 11, 13}) {
            var grid = NonNullList.withSize(machineSide * machineSide, ItemStack.EMPTY);
            grid.set(grid.size() / 2, new ItemStack(Items.IRON_INGOT));
            var fixed = recipe("fixed", false, List.of());
            var matches = ExtendedPatternRecipeFinder.findAllWithLookup(grid, helper.getLevel(), new int[] {3},
                    (side, input, level) -> List.of(fixed, fixed, recipe("dynamic", true, input)));
            helper.assertTrue(matches.size() == 4,
                    "Expected one fixed and three count/NBT-sensitive results on " + machineSide + " grid");
            helper.assertTrue(matches.get(0).recipe().getId().equals(fixed.getId()),
                    "Candidate deduplication changed first-match order");
            helper.assertTrue(matches.stream().filter(match -> match.recipe().getId().equals(fixed.getId())).count() == 1,
                    "Repeated windows duplicated the fixed result");
            helper.assertTrue(matches.stream().filter(match -> !match.recipe().getId().equals(fixed.getId())).count() == 3,
                    "Same-ID recipes with different counts or NBT were collapsed");
            grid.clear();
            helper.assertTrue(ExtendedPatternRecipeFinder.findAllWithLookup(grid, helper.getLevel(), new int[] {3},
                    (side, input, level) -> List.of(fixed)).isEmpty(), "Empty grid was accepted");
        }
        helper.assertTrue(ExtendedPatternRecipeFinder.findAllWithLookup(
                NonNullList.withSize(80, ItemStack.EMPTY), helper.getLevel(), new int[] {3},
                (side, input, level) -> List.of()).isEmpty(), "Invalid grid size was accepted");
    }

    private static ShapelessRecipe recipe(String path, boolean dynamic, List<ItemStack> window) {
        return new ShapelessRecipe(new ResourceLocation("extendedmolecularassembler", "gametest/" + path),
                "", CraftingBookCategory.MISC, new ItemStack(Items.DIAMOND),
                NonNullList.of(Ingredient.EMPTY, Ingredient.of(Items.IRON_INGOT))) {
            @Override
            public ItemStack assemble(CraftingContainer input, RegistryAccess registries) {
                var result = new ItemStack(Items.DIAMOND, dynamic && window.get(0).isEmpty() ? 2 : 1);
                if (dynamic && !window.get(4).isEmpty()) {
                    result.setHoverName(Component.literal("NBT-sensitive result"));
                }
                return result;
            }
        };
    }
}
