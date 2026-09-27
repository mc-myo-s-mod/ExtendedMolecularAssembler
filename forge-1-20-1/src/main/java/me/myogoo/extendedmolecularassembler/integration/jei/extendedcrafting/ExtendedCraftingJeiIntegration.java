package me.myogoo.extendedmolecularassembler.integration.jei.extendedcrafting;

import com.blakebr0.extendedcrafting.api.crafting.ITableRecipe;
import com.blakebr0.extendedcrafting.compat.jei.category.table.AdvancedTableCategory;
import com.blakebr0.extendedcrafting.compat.jei.category.table.BasicTableCategory;
import com.blakebr0.extendedcrafting.compat.jei.category.table.EliteTableCategory;
import com.blakebr0.extendedcrafting.compat.jei.category.table.UltimateTableCategory;
import me.myogoo.extendedmolecularassembler.integration.jei.handler.ExtendedPatternDirectRecipeTransferHandler;
import me.myogoo.extendedmolecularassembler.menu.pattern.ExtendedPatternEncodingTermMenu.RecipeProvider;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import mezz.jei.api.recipe.RecipeType;

@me.myogoo.extendedmolecularassembler.api.annotation.ExtendedCrafting
public final class ExtendedCraftingJeiIntegration {
    private ExtendedCraftingJeiIntegration() {
    }

    public static void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
        registration.addRecipeTransferHandler(
                new ExtendedPatternDirectRecipeTransferHandler<ITableRecipe>(BasicTableCategory.RECIPE_TYPE,
                        RecipeProvider.EXTENDED_CRAFTING, 1, 3),
                BasicTableCategory.RECIPE_TYPE);
        registration.addRecipeTransferHandler(
                new ExtendedPatternDirectRecipeTransferHandler<ITableRecipe>(AdvancedTableCategory.RECIPE_TYPE,
                        RecipeProvider.EXTENDED_CRAFTING, 2, 5),
                AdvancedTableCategory.RECIPE_TYPE);
        registration.addRecipeTransferHandler(
                new ExtendedPatternDirectRecipeTransferHandler<ITableRecipe>(EliteTableCategory.RECIPE_TYPE,
                        RecipeProvider.EXTENDED_CRAFTING, 3, 7),
                EliteTableCategory.RECIPE_TYPE);
        registration.addRecipeTransferHandler(
                new ExtendedPatternDirectRecipeTransferHandler<ITableRecipe>(UltimateTableCategory.RECIPE_TYPE,
                        RecipeProvider.EXTENDED_CRAFTING, 4, 9),
                UltimateTableCategory.RECIPE_TYPE);
        var epicRecipeType = RecipeType.create("extendedcrafting", "epic_crafting", ITableRecipe.class);
        registration.addRecipeTransferHandler(
                new ExtendedPatternDirectRecipeTransferHandler<ITableRecipe>(epicRecipeType,
                        RecipeProvider.EXTENDED_CRAFTING, 5, 11),
                epicRecipeType);
        var legendaryRecipeType = RecipeType.create("extendedcrafting", "legendary_crafting", ITableRecipe.class);
        registration.addRecipeTransferHandler(
                new ExtendedPatternDirectRecipeTransferHandler<ITableRecipe>(legendaryRecipeType,
                        RecipeProvider.EXTENDED_CRAFTING, 6, 13),
                legendaryRecipeType);
    }
}
