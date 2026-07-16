package me.myogoo.extendedmolecularassembler.adapter.recipe.reavaritia;

import committee.nova.mods.avaritia.common.crafting.recipe.ShapedTableCraftingRecipe;
import committee.nova.mods.avaritia.common.crafting.recipe.ShapelessTableCraftingRecipe;
import me.myogoo.myotus.api.recipe.IMyotusTableRecipe;
import net.minecraft.world.item.crafting.Recipe;

public final class ReAvaritiaRecipeAdapters {
    private ReAvaritiaRecipeAdapters() {
    }

    public static IMyotusTableRecipe<?> of(Recipe<?> recipe) {
        if (recipe instanceof ShapedTableCraftingRecipe shapedRecipe) {
            return new ShapedTierRecipeAdapter(shapedRecipe);
        }
        if (recipe instanceof ShapelessTableCraftingRecipe shapelessRecipe) {
            return new ShapelessTierRecipeAdapter(shapelessRecipe);
        }
        throw new IllegalArgumentException("Unsupported Re:Avaritia recipe: " + recipe.getClass().getName());
    }
}
