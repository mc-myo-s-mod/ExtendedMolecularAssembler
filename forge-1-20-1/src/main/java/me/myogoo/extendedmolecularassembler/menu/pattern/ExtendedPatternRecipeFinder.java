package me.myogoo.extendedmolecularassembler.menu.pattern;

import me.myogoo.extendedmolecularassembler.adapter.recipe.TableRecipeAdapters;
import me.myogoo.extendedmolecularassembler.api.annotation.AvaritiaNeo;
import me.myogoo.extendedmolecularassembler.api.annotation.ExtendedCrafting;
import me.myogoo.extendedmolecularassembler.api.annotation.ReAvaritia;
import me.myogoo.extendedmolecularassembler.menu.pattern.integration.avaritianeo.AvaritiaNeoPatternRecipeFinder;
import me.myogoo.extendedmolecularassembler.menu.pattern.integration.extendedcrafting.ExtendedCraftingPatternRecipeFinder;
import me.myogoo.extendedmolecularassembler.menu.pattern.integration.reavaritia.ReAvaritiaPatternRecipeFinder;
import me.myogoo.extendedmolecularassembler.pattern.ExtendedTableCraftingPattern;
import me.myogoo.myotus.api.MyotusAPI;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

public final class ExtendedPatternRecipeFinder {
    private static final int[] TABLE_SIDES = { 3, 5, 7, 9 };
    private static final int[] EXTENDED_CRAFTING_TABLE_SIDES = { 3, 5, 7, 9, 11, 13 };
    private static final int[] EXTREME_TABLE_SIDES = { 9 };

    private ExtendedPatternRecipeFinder() {
    }

    public static Optional<ExtendedPatternRecipeMatch> find(List<ItemStack> machineGrid, Level level) {
        return findAll(machineGrid, level).stream().findFirst();
    }

    public static List<ExtendedPatternRecipeMatch> findAll(List<ItemStack> machineGrid, Level level) {
        var matches = new ArrayList<ExtendedPatternRecipeMatch>();

        if (MyotusAPI.integrations().isLoaded(ExtendedCrafting.class)) {
            matches.addAll(findAllWithLookup(machineGrid, level, EXTENDED_CRAFTING_TABLE_SIDES,
                    ExtendedCraftingPatternRecipeFinder::findAll));
        }

        if (MyotusAPI.integrations().isLoaded(ReAvaritia.class)) {
            matches.addAll(findAllWithLookup(machineGrid, level, TABLE_SIDES, ReAvaritiaPatternRecipeFinder::findAll));
        }

        if (MyotusAPI.integrations().isLoaded(AvaritiaNeo.class)) {
            matches.addAll(findAllWithLookup(machineGrid, level, EXTREME_TABLE_SIDES,
                    AvaritiaNeoPatternRecipeFinder::findAll));
        }

        return matches;
    }

    private static Optional<ExtendedPatternRecipeMatch> findWithLookup(List<ItemStack> machineGrid, Level level,
            int[] sides, RecipeLookup lookup) {
        var machineSide = getMachineSide(machineGrid);
        if (machineSide < 0 || isEmpty(machineGrid)) {
            return Optional.empty();
        }

        for (var side : sides) {
            if (side > machineSide) {
                continue;
            }
            for (var offsetY : orderedOffsets(side, machineSide)) {
                for (var offsetX : orderedOffsets(side, machineSide)) {
                    if (!isOutsideWindowEmpty(machineGrid, side, offsetX, offsetY, machineSide)) {
                        continue;
                    }

                    var input = copyWindow(machineGrid, side, offsetX, offsetY, machineSide);
                    if (isEmpty(input)) {
                        continue;
                    }

                    var holder = lookup.find(side, input, level).orElse(null);
                    if (holder == null) {
                        continue;
                    }

                    var adapter = TableRecipeAdapters.of(holder);
                    if (adapter.sideLength() != side) {
                        continue;
                    }

                    var result = adapter.assemble(input, level);
                    if (!result.isEmpty()) {
                        return Optional.of(new ExtendedPatternRecipeMatch(holder, toArray(input), result));
                    }
                }
            }
        }

        return Optional.empty();
    }

    private static List<ExtendedPatternRecipeMatch> findAllWithLookup(List<ItemStack> machineGrid, Level level,
            int[] sides, RecipeLookupAll lookup) {
        var matches = new ArrayList<ExtendedPatternRecipeMatch>();
        var machineSide = getMachineSide(machineGrid);
        if (machineSide < 0 || isEmpty(machineGrid)) {
            return matches;
        }

        var matchedRecipeIds = new HashSet<ResourceLocation>();
        for (var side : sides) {
            if (side > machineSide) {
                continue;
            }
            for (var offsetY : orderedOffsets(side, machineSide)) {
                for (var offsetX : orderedOffsets(side, machineSide)) {
                    if (!isOutsideWindowEmpty(machineGrid, side, offsetX, offsetY, machineSide)) {
                        continue;
                    }

                    var input = copyWindow(machineGrid, side, offsetX, offsetY, machineSide);
                    if (isEmpty(input)) {
                        continue;
                    }

                    for (var holder : lookup.findAll(side, input, level)) {
                        if (matchedRecipeIds.contains(holder.getId())) {
                            continue;
                        }
                        var adapter = TableRecipeAdapters.of(holder);
                        if (adapter.sideLength() != side) {
                            continue;
                        }

                        var result = adapter.assemble(input, level);
                        if (!result.isEmpty()) {
                            matchedRecipeIds.add(holder.getId());
                            matches.add(new ExtendedPatternRecipeMatch(holder, toArray(input), result));
                        }
                    }
                }
            }
        }

        return matches;
    }

    private static List<ItemStack> copyWindow(List<ItemStack> machineGrid, int side, int offsetX, int offsetY,
            int machineSide) {
        var result = NonNullList.withSize(side * side, ItemStack.EMPTY);
        for (int y = 0; y < side; y++) {
            for (int x = 0; x < side; x++) {
                result.set(x + y * side, machineGrid.get((x + offsetX)
                        + (y + offsetY) * machineSide).copy());
            }
        }
        return result;
    }

    private static boolean isOutsideWindowEmpty(List<ItemStack> machineGrid, int side, int offsetX, int offsetY,
            int machineSide) {
        for (int y = 0; y < machineSide; y++) {
            for (int x = 0; x < machineSide; x++) {
                var inside = x >= offsetX && x < offsetX + side && y >= offsetY && y < offsetY + side;
                if (!inside && !machineGrid.get(x + y * machineSide).isEmpty()) {
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

    private static int[] orderedOffsets(int side, int machineSide) {
        if (side < 1 || side > machineSide) {
            return new int[0];
        }
        var maxOffset = machineSide - side;
        var center = Math.floorDiv(maxOffset, 2);
        var result = new int[maxOffset + 1];
        result[0] = center;
        var index = 1;
        for (int offset = 0; offset <= maxOffset; offset++) {
            if (offset != center) {
                result[index++] = offset;
            }
        }
        return result;
    }

    private static int getMachineSide(List<ItemStack> machineGrid) {
        var side = (int) Math.sqrt(machineGrid.size());
        if (side < ExtendedTableCraftingPattern.MACHINE_GRID_SIDE || side > ExtendedTableCraftingPattern.MAX_GRID_SIDE
                || (side & 1) == 0
                || side * side != machineGrid.size()) {
            return -1;
        }
        return side;
    }

    private static ItemStack[] toArray(List<ItemStack> input) {
        var result = new ItemStack[input.size()];
        for (int i = 0; i < input.size(); i++) {
            result[i] = input.get(i).copy();
        }
        return result;
    }

    @FunctionalInterface
    public interface RecipeLookup {
        Optional<Recipe<?>> find(int side, List<ItemStack> input, Level level);
    }

    @FunctionalInterface
    public interface RecipeLookupAll {
        List<Recipe<?>> findAll(int side, List<ItemStack> input, Level level);
    }
}
