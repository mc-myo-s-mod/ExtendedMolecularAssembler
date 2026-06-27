package me.myogoo.extendedmolecularassembler.menu.pattern.integration.extendedcrafting;

import com.blakebr0.extendedcrafting.api.TableCraftingInput;
import com.blakebr0.extendedcrafting.init.ModRecipeTypes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class ExtendedCraftingPatternRecipeFinder {
    private ExtendedCraftingPatternRecipeFinder() {
    }

    public static Optional<RecipeHolder<?>> find(int side, List<ItemStack> input, Level level) {
        var tier = (side - 1) / 2;
        if (tier < 1 || tier > 4) {
            return Optional.empty();
        }

        var tableInput = TableCraftingInput.of(side, side, input, tier);
        return level.getRecipeManager()
                .getRecipeFor(ModRecipeTypes.TABLE.get(), tableInput, level)
                .map(holder -> (RecipeHolder<?>) holder);
    }

    public static List<RecipeHolder<?>> findAll(int side, List<ItemStack> input, Level level) {
        var tier = (side - 1) / 2;
        if (tier < 1 || tier > 4) {
            return List.of();
        }

        var tableInput = TableCraftingInput.of(side, side, input, tier);
        return new ArrayList<RecipeHolder<?>>(level.getRecipeManager()
                .getRecipesFor(ModRecipeTypes.TABLE.get(), tableInput, level));
    }
}
