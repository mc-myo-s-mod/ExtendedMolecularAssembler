package me.myogoo.extendedmolecularassembler.menu.pattern;

import me.myogoo.extendedmolecularassembler.adapter.recipe.TableRecipeAdapters;
import me.myogoo.extendedmolecularassembler.pattern.ExtendedTableCraftingPattern;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

                    findMatchesForInput(input, side, level, matches);
                }
            }
        }

        return matches;
    }

    private static void findMatchesForInput(List<ItemStack> input, int side, Level level,
            List<ExtendedPatternRecipeMatch> matches) {
        for (Recipe<?> recipe : level.getRecipeManager().getRecipes()) {
            var adapter = tryCreateAdapter(recipe);
            if (adapter == null || adapter.sideLength() != side) {
                continue;
            }
            if (!adapter.matches(input, level)) {
                continue;
            }

            var result = adapter.assemble(input, level);
            if (!result.isEmpty()) {
                matches.add(new ExtendedPatternRecipeMatch(recipe, toArray(input), result));
            }
        }
    }

    private static me.myogoo.myotus.api.recipe.IMyotusTableRecipe<?> tryCreateAdapter(Recipe<?> recipe) {
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
