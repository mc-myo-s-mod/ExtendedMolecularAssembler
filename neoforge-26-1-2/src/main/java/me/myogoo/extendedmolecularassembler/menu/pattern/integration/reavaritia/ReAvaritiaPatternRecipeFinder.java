package me.myogoo.extendedmolecularassembler.menu.pattern.integration.reavaritia;

import appeng.crafting.RecipeAccess;
import committee.nova.mods.avaritia.api.common.crafting.TierInput;
import committee.nova.mods.avaritia.init.registry.ModRecipeTypes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class ReAvaritiaPatternRecipeFinder {
    private ReAvaritiaPatternRecipeFinder() {
    }

    public static RecipeHolder<?> byKey(Level level, ResourceKey<Recipe<?>> recipeId) {
        return RecipeAccess.byKey(level, ModRecipeTypes.CRAFTING_TABLE_RECIPE.get(), recipeId);
    }

    public static Optional<RecipeHolder<?>> find(int side, List<ItemStack> input, Level level) {
        var tier = (side - 1) / 2;
        if (tier < 1 || tier > 4) {
            return Optional.empty();
        }

        var tierInput = TierInput.of(side, side, input, tier);
        return Optional.ofNullable(RecipeAccess.getRecipeFor(level, ModRecipeTypes.CRAFTING_TABLE_RECIPE.get(), tierInput))
                .map(holder -> (RecipeHolder<?>) holder);
    }

    public static List<RecipeHolder<?>> findAll(int side, List<ItemStack> input, Level level) {
        var tier = (side - 1) / 2;
        if (tier < 1 || tier > 4) {
            return List.of();
        }

        var tierInput = TierInput.of(side, side, input, tier);
        return new ArrayList<RecipeHolder<?>>(RecipeAccess.getRecipesFor(level,
                ModRecipeTypes.CRAFTING_TABLE_RECIPE.get(), tierInput)
                .map(holder -> (RecipeHolder<?>) holder)
                .toList());
    }
}
