package me.myogoo.extendedmolecularassembler.integration.jei.handler;

import appeng.api.stacks.AEItemKey;
import appeng.core.network.ServerboundPacket;
import appeng.core.network.serverbound.InventoryActionPacket;
import appeng.helpers.InventoryAction;
import me.myogoo.extendedmolecularassembler.adapter.recipe.TableRecipeAdapters;
import me.myogoo.myotus.api.recipe.IMyotusTableRecipe;
import me.myogoo.extendedmolecularassembler.menu.pattern.ExtendedPatternEncodingTermMenu;
import me.myogoo.extendedmolecularassembler.pattern.ExtendedTableCraftingPattern;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Comparator;

final class ExtendedPatternRecipeTransfer {
    private ExtendedPatternRecipeTransfer() {
    }

    static boolean canTransfer(Recipe<?> recipe) {
        try {
            TableRecipeAdapters.of(recipe);
            return !recipe.getIngredients().isEmpty();
        } catch (IllegalArgumentException ignored) {
            return false;
        }
    }

    static void transfer(ExtendedPatternEncodingTermMenu menu, Recipe<?> recipe) {
        var adapter = TableRecipeAdapters.of(recipe);
        var encodedInputs = buildMachineInputs(menu, adapter);
        var slots = menu.getCraftingGridSlots();
        for (int i = 0; i < slots.length; i++) {
            ServerboundPacket message = new InventoryActionPacket(
                    InventoryAction.SET_FILTER, slots[i].index, encodedInputs.get(i));
            PacketDistributor.sendToServer(message);
        }
    }

    static void transfer(ExtendedPatternEncodingTermMenu menu, RecipeHolder<?> recipe) {
        transfer(menu, recipe.value());
        menu.selectTransferredRecipe(recipe.id());
    }

    private static NonNullList<ItemStack> buildMachineInputs(ExtendedPatternEncodingTermMenu menu,
            IMyotusTableRecipe<?> adapter) {
        var result = NonNullList.withSize(ExtendedTableCraftingPattern.MACHINE_GRID_SIZE, ItemStack.EMPTY);
        var side = adapter.sideLength();
        var offset = Math.floorDiv(ExtendedTableCraftingPattern.MACHINE_GRID_SIDE - side, 2);
        var ingredients = adapter.slotIngredients();

        for (int patternSlot = 0; patternSlot < ingredients.size(); patternSlot++) {
            var ingredient = ingredients.get(patternSlot);
            if (ingredient.isEmpty()) {
                continue;
            }

            var x = patternSlot % side + offset;
            var y = patternSlot / side + offset;
            result.set(x + y * ExtendedTableCraftingPattern.MACHINE_GRID_SIDE,
                    chooseTemplate(menu, ingredient));
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
