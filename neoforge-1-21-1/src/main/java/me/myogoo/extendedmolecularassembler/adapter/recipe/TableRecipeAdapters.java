package me.myogoo.extendedmolecularassembler.adapter.recipe;

import me.myogoo.myotus.api.recipe.IMyotusTableRecipe;
import me.myogoo.extendedmolecularassembler.adapter.recipe.avaritianeo.AvaritiaNeoRecipeAdapters;
import me.myogoo.extendedmolecularassembler.adapter.recipe.extendedcrafting.ExtendedCraftingRecipeAdapters;
import me.myogoo.extendedmolecularassembler.adapter.recipe.reavaritia.ReAvaritiaRecipeAdapters;
import me.myogoo.extendedmolecularassembler.init.EMAModPresence;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.ShapedRecipe;

public final class TableRecipeAdapters {
    private TableRecipeAdapters() {
    }

    public static IMyotusTableRecipe<?> of(RecipeHolder<?> holder) {
        return of(holder.value());
    }

    public static IMyotusTableRecipe<?> of(Recipe<?> recipe) {
        if (recipe instanceof ShapedRecipe shapedRecipe) {
            return new ShapedCraftingRecipeAdapter(shapedRecipe);
        }
        if (recipe instanceof CraftingRecipe craftingRecipe) {
            return new ShapelessCraftingRecipeAdapter(craftingRecipe);
        }

        var className = recipe.getClass().getName();
        if (EMAModPresence.isExtendedCraftingLoaded()
                && className.startsWith("com.blakebr0.extendedcrafting.")) {
            return ExtendedCraftingRecipeAdapters.of(recipe);
        }
        if (EMAModPresence.isReAvaritiaLoaded()
                && className.startsWith("committee.nova.mods.avaritia.")) {
            return ReAvaritiaRecipeAdapters.of(recipe);
        }
        if (EMAModPresence.isAvaritiaNeoLoaded()
                && className.startsWith("net.byAqua3.avaritia.")) {
            return AvaritiaNeoRecipeAdapters.of(recipe);
        }

        throw new IllegalArgumentException("Unsupported table recipe implementation: " + className);
    }
}
