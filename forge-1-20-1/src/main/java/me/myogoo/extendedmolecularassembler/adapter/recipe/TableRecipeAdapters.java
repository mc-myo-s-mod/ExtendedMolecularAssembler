package me.myogoo.extendedmolecularassembler.adapter.recipe;

import me.myogoo.extendedmolecularassembler.api.annotation.AvaritiaNeo;
import me.myogoo.extendedmolecularassembler.api.annotation.ExtendedCrafting;
import me.myogoo.extendedmolecularassembler.api.annotation.ReAvaritia;
import me.myogoo.extendedmolecularassembler.adapter.recipe.avaritianeo.AvaritiaNeoRecipeAdapters;
import me.myogoo.extendedmolecularassembler.adapter.recipe.extendedcrafting.ExtendedCraftingRecipeAdapters;
import me.myogoo.extendedmolecularassembler.adapter.recipe.reavaritia.ReAvaritiaRecipeAdapters;
import me.myogoo.myotus.api.MyotusAPI;
import me.myogoo.myotus.api.recipe.IMyotusTableRecipe;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.ShapedRecipe;

public final class TableRecipeAdapters {
    private TableRecipeAdapters() {
    }

    public static IMyotusTableRecipe<?> of(Recipe<?> recipe) {
        if (recipe instanceof ShapedRecipe shapedRecipe) {
            return new ShapedCraftingRecipeAdapter(shapedRecipe);
        }
        if (recipe instanceof CraftingRecipe craftingRecipe) {
            return new ShapelessCraftingRecipeAdapter(craftingRecipe);
        }

        if (MyotusAPI.integrations().isLoaded(ExtendedCrafting.class)
                && ExtendedCraftingRecipeAdapters.supports(recipe)) {
            return ExtendedCraftingRecipeAdapters.of(recipe);
        }
        if (MyotusAPI.integrations().isLoaded(ReAvaritia.class)
                && ReAvaritiaRecipeAdapters.supports(recipe)) {
            return ReAvaritiaRecipeAdapters.of(recipe);
        }
        if (MyotusAPI.integrations().isLoaded(AvaritiaNeo.class)
                && AvaritiaNeoRecipeAdapters.supports(recipe)) {
            return AvaritiaNeoRecipeAdapters.of(recipe);
        }

        throw new IllegalArgumentException("Unsupported table recipe implementation: " + recipe.getClass().getName());
    }

    public static boolean isExtended(Recipe<?> recipe) {
        return (MyotusAPI.integrations().isLoaded(ExtendedCrafting.class)
                && ExtendedCraftingRecipeAdapters.supports(recipe))
                || (MyotusAPI.integrations().isLoaded(ReAvaritia.class)
                        && ReAvaritiaRecipeAdapters.supports(recipe))
                || (MyotusAPI.integrations().isLoaded(AvaritiaNeo.class)
                        && AvaritiaNeoRecipeAdapters.supports(recipe));
    }
}
