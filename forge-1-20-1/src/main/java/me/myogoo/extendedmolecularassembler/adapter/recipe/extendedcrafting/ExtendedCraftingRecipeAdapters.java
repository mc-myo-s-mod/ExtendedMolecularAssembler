package me.myogoo.extendedmolecularassembler.adapter.recipe.extendedcrafting;

import com.blakebr0.extendedcrafting.crafting.recipe.ShapedTableRecipe;
import com.blakebr0.extendedcrafting.crafting.recipe.ShapelessTableRecipe;
import me.myogoo.myotus.api.recipe.IMyotusTableRecipe;
import net.minecraft.world.item.crafting.Recipe;

@me.myogoo.extendedmolecularassembler.api.annotation.ExtendedCrafting
public final class ExtendedCraftingRecipeAdapters {
    private ExtendedCraftingRecipeAdapters() {
    }

    public static boolean supports(Recipe<?> recipe) {
        return recipe instanceof ShapedTableRecipe || recipe instanceof ShapelessTableRecipe;
    }

    public static IMyotusTableRecipe<?> of(Recipe<?> recipe) {
        if (recipe instanceof ShapedTableRecipe shapedRecipe) {
            return new ShapedTableRecipeAdapter(shapedRecipe);
        }
        if (recipe instanceof ShapelessTableRecipe shapelessRecipe) {
            return new ShapelessTableRecipeAdapter(shapelessRecipe);
        }
        throw new IllegalArgumentException("Unsupported Extended Crafting recipe: " + recipe.getClass().getName());
    }
}
