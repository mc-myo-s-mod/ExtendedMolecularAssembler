package me.myogoo.extendedmolecularassembler.menu.pattern;

import me.myogoo.extendedmolecularassembler.pattern.ExtendedTableCraftingPattern;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapelessRecipe;

import java.util.List;

public final class ExtendedPatternRecipeFinderChecks {
    private ExtendedPatternRecipeFinderChecks() {
    }

    public static void assertDistinctCandidates(GameTestHelper helper) {
        var grid = NonNullList.withSize(ExtendedTableCraftingPattern.MACHINE_GRID_SIZE, ItemStack.EMPTY);
        grid.set(40, new ItemStack(Items.IRON_INGOT));
        var fixed = recipe("fixed", false, List.of());
        var dynamic = recipe("dynamic", true, grid);
        var matches = ExtendedPatternRecipeFinder.findAllWithLookup(grid, helper.getLevel(), new int[] {3},
                (side, input, level) -> List.of(fixed, fixed, recipe("dynamic", true, input)));
        helper.assertTrue(matches.size() == 4, "Expected one fixed and three distinct dynamic recipe results");
        helper.assertTrue(matches.getFirst().recipe().id().equals(fixed.id()),
                "Candidate deduplication changed first-match order");
        helper.assertTrue(matches.stream().filter(match -> match.recipe().id().equals(fixed.id())).count() == 1,
                "Repeated windows duplicated a fixed recipe ID");
        helper.assertTrue(matches.stream().filter(match -> match.recipe().id().equals(dynamic.id())).count() == 3,
                "Same-ID recipes with different counts or components were collapsed");
        grid.clear();
        helper.assertTrue(ExtendedPatternRecipeFinder.findAllWithLookup(grid, helper.getLevel(), new int[] {3},
                (side, input, level) -> List.of(fixed)).isEmpty(), "Invalid grid was accepted");
    }

    private static RecipeHolder<?> recipe(String path, boolean dynamic, List<ItemStack> window) {
        var value = new ShapelessRecipe("", CraftingBookCategory.MISC, new ItemStack(Items.DIAMOND),
                NonNullList.of(Ingredient.EMPTY, Ingredient.of(Items.IRON_INGOT))) {
            @Override
            public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
                return result(window, dynamic);
            }
        };
        return new RecipeHolder<>(ResourceLocation.fromNamespaceAndPath(
                "extendedmolecularassembler", "gametest/" + path), value);
    }

    private static ItemStack result(List<ItemStack> input, boolean dynamic) {
        var result = new ItemStack(Items.DIAMOND, dynamic && input.get(0).isEmpty() ? 2 : 1);
        if (dynamic && !input.get(4).isEmpty()) {
            result.set(DataComponents.CUSTOM_NAME, Component.literal("Component-sensitive result"));
        }
        return result;
    }
}
