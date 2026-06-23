package me.myogoo.extendedmolecularassembler.integration.jei.reavaritia;

import committee.nova.mods.avaritia.api.common.crafting.ITierCraftingRecipe;
import committee.nova.mods.avaritia.init.compat.jei.category.tables.EndCraftingTableCategory;
import committee.nova.mods.avaritia.init.compat.jei.category.tables.ExtremeCraftingTableCategory;
import committee.nova.mods.avaritia.init.compat.jei.category.tables.NetherCraftingTableCategory;
import committee.nova.mods.avaritia.init.compat.jei.category.tables.SculkCraftingTableCategory;
import me.myogoo.extendedmolecularassembler.init.EMAParts;
import me.myogoo.extendedmolecularassembler.integration.jei.handler.ExtendedPatternHolderRecipeTransferHandler;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeTransferRegistration;

public final class ReAvaritiaJeiIntegration {
    private ReAvaritiaJeiIntegration() {
    }

    public static void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(EMAParts.EXTENDED_PATTERN_ENCODING_TERMINAL, SculkCraftingTableCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(EMAParts.EXTENDED_PATTERN_ENCODING_TERMINAL, NetherCraftingTableCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(EMAParts.EXTENDED_PATTERN_ENCODING_TERMINAL, EndCraftingTableCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(EMAParts.EXTENDED_PATTERN_ENCODING_TERMINAL, ExtremeCraftingTableCategory.RECIPE_TYPE);
    }

    public static void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
        registration.addRecipeTransferHandler(
                new ExtendedPatternHolderRecipeTransferHandler<ITierCraftingRecipe>(
                        SculkCraftingTableCategory.RECIPE_TYPE),
                SculkCraftingTableCategory.RECIPE_TYPE);
        registration.addRecipeTransferHandler(
                new ExtendedPatternHolderRecipeTransferHandler<ITierCraftingRecipe>(
                        NetherCraftingTableCategory.RECIPE_TYPE),
                NetherCraftingTableCategory.RECIPE_TYPE);
        registration.addRecipeTransferHandler(
                new ExtendedPatternHolderRecipeTransferHandler<ITierCraftingRecipe>(
                        EndCraftingTableCategory.RECIPE_TYPE),
                EndCraftingTableCategory.RECIPE_TYPE);
        registration.addRecipeTransferHandler(
                new ExtendedPatternHolderRecipeTransferHandler<ITierCraftingRecipe>(
                        ExtremeCraftingTableCategory.RECIPE_TYPE),
                ExtremeCraftingTableCategory.RECIPE_TYPE);
    }
}
