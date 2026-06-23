package me.myogoo.extendedmolecularassembler.adapter.recipe.reavaritia;

import me.myogoo.myotus.api.recipe.IMyotusTableRecipe;
import net.minecraft.world.item.crafting.Recipe;

/**
 * Re:Avaritia's currently configured Forge artifact ({@code ReAvaritia_file=7695278}) exposes
 * the 1.21+ recipe API (RecipeInput/CraftingInput and HolderLookup.Provider based signatures),
 * so the concrete 1.21.1 adapters cannot be compiled against this 1.20.1 Forge module.
 *
 * Keep this package as an isolated safe stub until a 1.20.1-compatible Re:Avaritia artifact/API is
 * available for the module.
 */
public final class ReAvaritiaRecipeAdapters {
    private ReAvaritiaRecipeAdapters() {
    }

    public static IMyotusTableRecipe<?> of(Recipe<?> recipe) {
        throw new UnsupportedOperationException("Re:Avaritia recipe adapters are blocked in forge-1-20-1: "
                + "the configured Re:Avaritia artifact uses the 1.21+ recipe API ("
                + recipe.getClass().getName() + ")");
    }
}
