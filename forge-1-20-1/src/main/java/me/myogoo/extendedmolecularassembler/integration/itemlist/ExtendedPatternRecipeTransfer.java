package me.myogoo.extendedmolecularassembler.integration.itemlist;

import appeng.api.stacks.AEItemKey;
import appeng.core.sync.network.NetworkHandler;
import appeng.core.sync.packets.InventoryActionPacket;
import appeng.helpers.InventoryAction;
import me.myogoo.extendedmolecularassembler.adapter.recipe.AbstractTableRecipeAdapter;
import me.myogoo.extendedmolecularassembler.adapter.recipe.TableRecipeAdapters;
import me.myogoo.extendedmolecularassembler.menu.pattern.ExtendedPatternEncodingTermMenu;
import me.myogoo.extendedmolecularassembler.menu.pattern.ExtendedPatternEncodingTermMenu.RecipeProvider;
import me.myogoo.extendedmolecularassembler.pattern.ExtendedTableCraftingPattern;
import me.myogoo.myotus.api.recipe.IMyotusTableRecipe;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;

public final class ExtendedPatternRecipeTransfer {
    private ExtendedPatternRecipeTransfer() {
    }

    public static boolean canTransfer(Recipe<?> recipe) {
        return canTransfer(recipe, AbstractTableRecipeAdapter.MAX_SIDE_LENGTH, false);
    }

    public static boolean canTransfer(Recipe<?> recipe, int gridSide) {
        return canTransfer(recipe, gridSide, true);
    }

    private static boolean canTransfer(Recipe<?> recipe, int gridSide, boolean matchLargeGridExactly) {
        if (!TableRecipeAdapters.isExtended(recipe)) {
            return false;
        }

        try {
            var adapter = TableRecipeAdapters.of(recipe);
            var side = adapter.sideLength();
            var ingredients = adapter.slotIngredients();
            var gridCompatible = matchLargeGridExactly
                    ? supportsGridSide(side, gridSide)
                    : side > 0 && side <= gridSide;
            return gridCompatible
                    && !ingredients.isEmpty()
                    && ingredients.size() <= side * side;
        } catch (IllegalArgumentException ignored) {
            return false;
        }
    }

    public static void transfer(ExtendedPatternEncodingTermMenu menu, Recipe<?> recipe) {
        transfer(menu, recipe, null, 0, 0);
    }

    public static void transfer(ExtendedPatternEncodingTermMenu menu, Recipe<?> recipe,
            @Nullable RecipeProvider recipeProvider, int tableTier, int tableSide) {
        var adapter = TableRecipeAdapters.of(recipe);
        if (!supportsGridSide(adapter.sideLength(), menu.getGridSide())) {
            return;
        }
        var encodedInputs = buildMachineInputs(menu, adapter);
        var slots = menu.getCraftingGridSlots();
        for (int i = 0; i < slots.length; i++) {
            var message = new InventoryActionPacket(
                    InventoryAction.SET_FILTER, slots[i].index, encodedInputs.get(i));
            NetworkHandler.instance().sendToServer(message);
        }
        if (recipeProvider == null) {
            menu.selectTransferredRecipe(recipe.getId());
        } else {
            menu.selectTransferredRecipe(recipe.getId(), recipeProvider, tableTier, tableSide);
        }
    }

    public static boolean supportsGridSide(int recipeSide, int gridSide) {
        return recipeSide > 0 && (gridSide > ExtendedTableCraftingPattern.MACHINE_GRID_SIDE
                ? recipeSide == gridSide
                : recipeSide <= gridSide);
    }

    private static NonNullList<ItemStack> buildMachineInputs(ExtendedPatternEncodingTermMenu menu,
            IMyotusTableRecipe<?> adapter) {
        var gridSide = menu.getGridSide();
        var result = NonNullList.withSize(gridSide * gridSide, ItemStack.EMPTY);
        var side = adapter.sideLength();
        var ingredients = adapter.slotIngredients();

        for (int patternSlot = 0; patternSlot < ingredients.size(); patternSlot++) {
            var ingredient = ingredients.get(patternSlot);
            if (ingredient.isEmpty()) {
                continue;
            }

            var machineSlot = ExtendedTableCraftingPattern.toMachineGridIndex(patternSlot, side, gridSide);
            result.set(machineSlot, chooseTemplate(menu, ingredient));
        }
        return result;
    }

    private static ItemStack chooseTemplate(ExtendedPatternEncodingTermMenu menu, Ingredient ingredient) {
        var repo = menu.getClientRepo();
        if (repo != null) {
            var bestNetworkStack = repo.getByIngredient(ingredient).stream()
                    .filter(entry -> entry.getWhat() instanceof AEItemKey)
                    .max(Comparator
                            .comparing((appeng.menu.me.common.GridInventoryEntry entry) -> entry.isCraftable())
                            .thenComparingLong(appeng.menu.me.common.GridInventoryEntry::getStoredAmount))
                    .map(entry -> ((AEItemKey) entry.getWhat()).toStack());
            if (bestNetworkStack.isPresent()) {
                return bestNetworkStack.get();
            }
        }

        var stacks = ingredient.getItems();
        if (stacks.length == 0) {
            return ItemStack.EMPTY;
        }
        return stacks[0].copyWithCount(1);
    }
}
