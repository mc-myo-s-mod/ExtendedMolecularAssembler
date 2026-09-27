package me.myogoo.extendedmolecularassembler.menu.pattern.integration.extendedcrafting;

import net.minecraft.world.Container;
import com.blakebr0.extendedcrafting.init.ModRecipeTypes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@me.myogoo.extendedmolecularassembler.api.annotation.ExtendedCrafting
public final class ExtendedCraftingPatternRecipeFinder {
    private ExtendedCraftingPatternRecipeFinder() {
    }

    public static Optional<Recipe<?>> find(int side, List<ItemStack> input, Level level) {
        var tier = (side - 1) / 2;
        if (tier < 1 || tier > 6) {
            return Optional.empty();
        }

        var tableInput = me.myogoo.extendedmolecularassembler.adapter.recipe.RecipeGridHelper.craftingContainer(side, side, input);
        return level.getRecipeManager()
                .getRecipeFor(ModRecipeTypes.TABLE.get(), tableInput, level)
                .map(holder -> (Recipe<?>) holder);
    }

    public static List<Recipe<?>> findAll(int side, List<ItemStack> input, Level level) {
        var tier = (side - 1) / 2;
        if (tier < 1 || tier > 6) {
            return List.of();
        }

        var tableInput = me.myogoo.extendedmolecularassembler.adapter.recipe.RecipeGridHelper.craftingContainer(side, side, input);
        return new ArrayList<Recipe<?>>(level.getRecipeManager()
                .getRecipesFor(ModRecipeTypes.TABLE.get(), tableInput, level));
    }
}
