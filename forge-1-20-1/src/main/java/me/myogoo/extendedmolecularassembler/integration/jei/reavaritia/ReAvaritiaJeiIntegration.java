package me.myogoo.extendedmolecularassembler.integration.jei.reavaritia;

import committee.nova.mods.avaritia.common.crafting.recipe.ITierCraftingRecipe;
import committee.nova.mods.avaritia.init.compat.jei.category.tables.EndCraftingTableCategory;
import committee.nova.mods.avaritia.init.compat.jei.category.tables.ExtremeCraftingTableCategory;
import committee.nova.mods.avaritia.init.compat.jei.category.tables.NetherCraftingTableCategory;
import committee.nova.mods.avaritia.init.compat.jei.category.tables.SculkCraftingTableCategory;
import me.myogoo.extendedmolecularassembler.integration.jei.handler.ExtendedPatternDirectRecipeTransferHandler;
import mezz.jei.api.registration.IRecipeTransferRegistration;

@me.myogoo.extendedmolecularassembler.api.annotation.ReAvaritia
public final class ReAvaritiaJeiIntegration {
    private ReAvaritiaJeiIntegration() {
    }

    public static void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
        registration.addRecipeTransferHandler(
                new ExtendedPatternDirectRecipeTransferHandler<ITierCraftingRecipe>(
                        SculkCraftingTableCategory.RECIPE_TYPE),
                SculkCraftingTableCategory.RECIPE_TYPE);
        registration.addRecipeTransferHandler(
                new ExtendedPatternDirectRecipeTransferHandler<ITierCraftingRecipe>(
                        NetherCraftingTableCategory.RECIPE_TYPE),
                NetherCraftingTableCategory.RECIPE_TYPE);
        registration.addRecipeTransferHandler(
                new ExtendedPatternDirectRecipeTransferHandler<ITierCraftingRecipe>(
                        EndCraftingTableCategory.RECIPE_TYPE),
                EndCraftingTableCategory.RECIPE_TYPE);
        registration.addRecipeTransferHandler(
                new ExtendedPatternDirectRecipeTransferHandler<ITierCraftingRecipe>(
                        ExtremeCraftingTableCategory.RECIPE_TYPE),
                ExtremeCraftingTableCategory.RECIPE_TYPE);
    }
}
