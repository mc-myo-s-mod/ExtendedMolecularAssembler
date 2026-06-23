package me.myogoo.extendedmolecularassembler.integration.jei.extendedcrafting;

import com.blakebr0.extendedcrafting.api.crafting.ITableRecipe;
import com.blakebr0.extendedcrafting.compat.jei.category.table.AdvancedTableCategory;
import com.blakebr0.extendedcrafting.compat.jei.category.table.BasicTableCategory;
import com.blakebr0.extendedcrafting.compat.jei.category.table.EliteTableCategory;
import com.blakebr0.extendedcrafting.compat.jei.category.table.UltimateTableCategory;
import me.myogoo.extendedmolecularassembler.init.EMAParts;
import me.myogoo.extendedmolecularassembler.integration.jei.handler.ExtendedPatternHolderRecipeTransferHandler;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeTransferRegistration;

public final class ExtendedCraftingJeiIntegration {
    private ExtendedCraftingJeiIntegration() {
    }

    public static void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(EMAParts.EXTENDED_PATTERN_ENCODING_TERMINAL, BasicTableCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(EMAParts.EXTENDED_PATTERN_ENCODING_TERMINAL, AdvancedTableCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(EMAParts.EXTENDED_PATTERN_ENCODING_TERMINAL, EliteTableCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(EMAParts.EXTENDED_PATTERN_ENCODING_TERMINAL, UltimateTableCategory.RECIPE_TYPE);
    }

    public static void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
        registration.addRecipeTransferHandler(
                new ExtendedPatternHolderRecipeTransferHandler<ITableRecipe>(BasicTableCategory.RECIPE_TYPE),
                BasicTableCategory.RECIPE_TYPE);
        registration.addRecipeTransferHandler(
                new ExtendedPatternHolderRecipeTransferHandler<ITableRecipe>(AdvancedTableCategory.RECIPE_TYPE),
                AdvancedTableCategory.RECIPE_TYPE);
        registration.addRecipeTransferHandler(
                new ExtendedPatternHolderRecipeTransferHandler<ITableRecipe>(EliteTableCategory.RECIPE_TYPE),
                EliteTableCategory.RECIPE_TYPE);
        registration.addRecipeTransferHandler(
                new ExtendedPatternHolderRecipeTransferHandler<ITableRecipe>(UltimateTableCategory.RECIPE_TYPE),
                UltimateTableCategory.RECIPE_TYPE);
    }
}
