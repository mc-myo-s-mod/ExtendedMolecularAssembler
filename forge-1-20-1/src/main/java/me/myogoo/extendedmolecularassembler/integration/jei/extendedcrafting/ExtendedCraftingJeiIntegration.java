package me.myogoo.extendedmolecularassembler.integration.jei.extendedcrafting;

import com.blakebr0.extendedcrafting.compat.jei.category.table.AdvancedTableCategory;
import com.blakebr0.extendedcrafting.compat.jei.category.table.BasicTableCategory;
import com.blakebr0.extendedcrafting.compat.jei.category.table.EliteTableCategory;
import com.blakebr0.extendedcrafting.compat.jei.category.table.UltimateTableCategory;
import com.blakebr0.extendedcrafting.api.crafting.ITableRecipe;
import me.myogoo.extendedmolecularassembler.init.EMAItems;
import me.myogoo.extendedmolecularassembler.integration.jei.handler.ExtendedPatternDirectRecipeTransferHandler;
import me.myogoo.extendedmolecularassembler.menu.pattern.ExtendedPatternEncodingTermMenu.RecipeProvider;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import net.minecraft.world.item.ItemStack;

public final class ExtendedCraftingJeiIntegration {
    private ExtendedCraftingJeiIntegration() {
    }

    public static void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        var assembler = new ItemStack(EMAItems.EXTENDED_MOLECULAR_ASSEMBLER.get());
        var exAssembler = new ItemStack(EMAItems.EX_EXTENDED_MOLECULAR_ASSEMBLER.get());

        addTableCatalysts(registration, assembler);
        addTableCatalysts(registration, exAssembler);
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
    }

    private static void addTableCatalysts(IRecipeCatalystRegistration registration, ItemStack catalyst) {
        registration.addRecipeCatalyst(catalyst, BasicTableCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(catalyst, AdvancedTableCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(catalyst, EliteTableCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(catalyst, UltimateTableCategory.RECIPE_TYPE);
    }
}
