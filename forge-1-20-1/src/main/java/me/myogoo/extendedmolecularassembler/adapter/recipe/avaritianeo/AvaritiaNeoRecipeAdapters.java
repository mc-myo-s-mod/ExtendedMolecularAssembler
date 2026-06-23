package me.myogoo.extendedmolecularassembler.adapter.recipe.avaritianeo;

import me.myogoo.myotus.api.recipe.IMyotusTableRecipe;
import net.minecraft.world.item.crafting.Recipe;

/**
 * AvaritiaNeo 1.20.1 artifact currently contains Java 21 class files, while this
 * Forge 1.20.1 source set compiles for Java 17. Keep this adapter as a guarded
 * compile-time stub until a Java-17-compatible AvaritiaNeo API is available.
 */
public final class AvaritiaNeoRecipeAdapters {
    private AvaritiaNeoRecipeAdapters() {
    }

    public static IMyotusTableRecipe<?> of(Recipe<?> recipe) {
        throw new IllegalArgumentException("AvaritiaNeo recipe adapters are disabled for Forge 1.20.1 because "
                + "the available AvaritiaNeo artifact is compiled for Java 21: " + recipe.getClass().getName());
    }
}
