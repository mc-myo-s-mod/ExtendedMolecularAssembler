package me.myogoo.extendedmolecularassembler.integration.jei.extendedcrafting;

import com.blakebr0.extendedcrafting.compat.jei.category.table.AdvancedTableCategory;
import com.blakebr0.extendedcrafting.compat.jei.category.table.BasicTableCategory;
import com.blakebr0.extendedcrafting.compat.jei.category.table.EliteTableCategory;
import com.blakebr0.extendedcrafting.compat.jei.category.table.UltimateTableCategory;
import me.myogoo.extendedmolecularassembler.init.EMAItems;
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
        // The 1.21 handlers fill ExtendedPatternEncodingTermMenu, which is not present in the
        // Forge 1.20.1 port. Keep this as an explicit no-op until that menu is available.
    }

    private static void addTableCatalysts(IRecipeCatalystRegistration registration, ItemStack catalyst) {
        registration.addRecipeCatalyst(catalyst, BasicTableCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(catalyst, AdvancedTableCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(catalyst, EliteTableCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(catalyst, UltimateTableCategory.RECIPE_TYPE);
    }
}
