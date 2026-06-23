package me.myogoo.extendedmolecularassembler.adapter.recipe.avaritianeo;

import me.myogoo.myotus.api.recipe.IMyotusTableRecipe;
import net.byAqua3.avaritia.recipe.RecipeExtremeShaped;
import net.byAqua3.avaritia.recipe.RecipeExtremeShapeless;
import net.minecraft.world.item.crafting.Recipe;

public final class AvaritiaNeoRecipeAdapters {
    private AvaritiaNeoRecipeAdapters() {
    }

    public static IMyotusTableRecipe<?> of(Recipe<?> recipe) {
        if (recipe instanceof RecipeExtremeShaped shapedRecipe) {
            return new ShapedExtremeRecipeAdapter(shapedRecipe);
        }
        if (recipe instanceof RecipeExtremeShapeless shapelessRecipe) {
            return new ShapelessExtremeRecipeAdapter(shapelessRecipe);
        }
        throw new IllegalArgumentException("Unsupported AvaritiaNeo recipe: " + recipe.getClass().getName());
    }
}
