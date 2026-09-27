package me.myogoo.extendedmolecularassembler.menu.pattern.integration.reavaritia;

import net.minecraft.world.Container;
import committee.nova.mods.avaritia.init.registry.ModRecipeTypes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@me.myogoo.extendedmolecularassembler.api.annotation.ReAvaritia
public final class ReAvaritiaPatternRecipeFinder {
    private ReAvaritiaPatternRecipeFinder() {
    }

    public static Optional<Recipe<?>> find(int side, List<ItemStack> input, Level level) {
        var tier = (side - 1) / 2;
        if (tier < 1 || tier > 4) {
            return Optional.empty();
        }

        var tierInput = me.myogoo.extendedmolecularassembler.adapter.recipe.RecipeGridHelper.craftingContainer(side, side, input);
        return level.getRecipeManager()
                .getRecipeFor(ModRecipeTypes.CRAFTING_TABLE_RECIPE.get(), tierInput, level)
                .map(holder -> (Recipe<?>) holder);
    }

    public static List<Recipe<?>> findAll(int side, List<ItemStack> input, Level level) {
        var tier = (side - 1) / 2;
        if (tier < 1 || tier > 4) {
            return List.of();
        }

        var tierInput = me.myogoo.extendedmolecularassembler.adapter.recipe.RecipeGridHelper.craftingContainer(side, side, input);
        return new ArrayList<Recipe<?>>(level.getRecipeManager()
                .getRecipesFor(ModRecipeTypes.CRAFTING_TABLE_RECIPE.get(), tierInput, level));
    }
}
