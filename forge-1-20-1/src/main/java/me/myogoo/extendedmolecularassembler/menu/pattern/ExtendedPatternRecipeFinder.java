package me.myogoo.extendedmolecularassembler.menu.pattern;

import me.myogoo.extendedmolecularassembler.adapter.recipe.TableRecipeAdapters;
import me.myogoo.myotus.api.recipe.IMyotusTableRecipe;
import me.myogoo.extendedmolecularassembler.pattern.ExtendedTableCraftingPattern;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public final class ExtendedPatternRecipeFinder {
    private static final int[] TABLE_SIDES = { 3, 5, 7, 9 };

    private ExtendedPatternRecipeFinder() {
    }

    public static Optional<ExtendedPatternRecipeMatch> find(List<ItemStack> machineGrid, Level level) {
        return findAll(machineGrid, level).stream().findFirst();
    }

    public static List<ExtendedPatternRecipeMatch> findAll(List<ItemStack> machineGrid, Level level) {
        var matches = new ArrayList<ExtendedPatternRecipeMatch>();
        if (machineGrid.size() != ExtendedTableCraftingPattern.MACHINE_GRID_SIZE || isEmpty(machineGrid)) {
            return matches;
        }

        var matchedRecipeIds = new HashSet<ResourceLocation>();
        var candidatesBySide = candidatesBySide(level.getRecipeManager());

        for (var side : TABLE_SIDES) {
            for (var offsetY : orderedOffsets(side)) {
                for (var offsetX : orderedOffsets(side)) {
                    if (!isOutsideWindowEmpty(machineGrid, side, offsetX, offsetY)) {
                        continue;
                    }

                    var input = copyWindow(machineGrid, side, offsetX, offsetY);
                    if (isEmpty(input)) {
                        continue;
                    }

                    findMatchesForInput(input, side, level, candidatesBySide, matches, matchedRecipeIds);
                }
            }
        }

        return matches;
    }

    private static void findMatchesForInput(List<ItemStack> input, int side, Level level,
            Map<Integer, List<RecipeCandidate>> candidatesBySide,
            List<ExtendedPatternRecipeMatch> matches, Set<ResourceLocation> matchedRecipeIds) {
        for (var candidate : candidatesBySide.getOrDefault(side, List.of())) {
            if (matchedRecipeIds.contains(candidate.recipe().getId()) || !candidate.adapter().matches(input, level)) {
                continue;
            }

            var result = candidate.adapter().assemble(input, level);
            if (!result.isEmpty()) {
                matchedRecipeIds.add(candidate.recipe().getId());
                matches.add(new ExtendedPatternRecipeMatch(candidate.recipe(), toArray(input), result));
            }
        }
    }

    private static Map<Integer, List<RecipeCandidate>> candidatesBySide(RecipeManager recipeManager) {
        var recipes = recipeManager.getRecipes();
        var candidatesBySide = new HashMap<Integer, List<RecipeCandidate>>();
        for (Recipe<?> recipe : recipes) {
            var adapter = tryCreateAdapter(recipe);
            if (adapter == null) {
                continue;
            }
            candidatesBySide.computeIfAbsent(adapter.sideLength(), ignored -> new ArrayList<>())
                    .add(new RecipeCandidate(recipe, adapter));
        }

        return Map.copyOf(candidatesBySide);
    }

    private static IMyotusTableRecipe<?> tryCreateAdapter(Recipe<?> recipe) {
        var className = recipe.getClass().getName();
        if (!className.startsWith("com.blakebr0.extendedcrafting.")
                && !className.startsWith("net.byAqua3.avaritia.")
                && !className.startsWith("committee.nova.mods.avaritia.")) {
            return null;
        }

        try {
            return TableRecipeAdapters.of(recipe);
        } catch (RuntimeException ignored) {
            return null;
        }
    }

    private record RecipeCandidate(Recipe<?> recipe, IMyotusTableRecipe<?> adapter) {
    }

    private static List<ItemStack> copyWindow(List<ItemStack> machineGrid, int side, int offsetX, int offsetY) {
        var result = NonNullList.withSize(side * side, ItemStack.EMPTY);
        for (int y = 0; y < side; y++) {
            for (int x = 0; x < side; x++) {
                result.set(x + y * side, machineGrid.get((x + offsetX)
                        + (y + offsetY) * ExtendedTableCraftingPattern.MACHINE_GRID_SIDE).copy());
            }
        }
        return result;
    }

    private static boolean isOutsideWindowEmpty(List<ItemStack> machineGrid, int side, int offsetX, int offsetY) {
        for (int y = 0; y < ExtendedTableCraftingPattern.MACHINE_GRID_SIDE; y++) {
            for (int x = 0; x < ExtendedTableCraftingPattern.MACHINE_GRID_SIDE; x++) {
                var inside = x >= offsetX && x < offsetX + side && y >= offsetY && y < offsetY + side;
                if (!inside && !machineGrid.get(x + y * ExtendedTableCraftingPattern.MACHINE_GRID_SIDE).isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean isEmpty(List<ItemStack> input) {
        for (var stack : input) {
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private static List<Integer> orderedOffsets(int side) {
        var maxOffset = ExtendedTableCraftingPattern.MACHINE_GRID_SIDE - side;
        var center = Math.floorDiv(maxOffset, 2);
        var result = new ArrayList<Integer>(maxOffset + 1);
        result.add(center);
        for (int offset = 0; offset <= maxOffset; offset++) {
            if (offset != center) {
                result.add(offset);
            }
        }
        return result;
    }

    private static ItemStack[] toArray(List<ItemStack> input) {
        var result = new ItemStack[input.size()];
        for (int i = 0; i < input.size(); i++) {
            result[i] = input.get(i).copy();
        }
        return result;
    }
}
