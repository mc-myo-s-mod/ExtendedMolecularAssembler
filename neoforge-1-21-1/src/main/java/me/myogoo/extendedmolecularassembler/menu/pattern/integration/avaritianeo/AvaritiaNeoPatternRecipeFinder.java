package me.myogoo.extendedmolecularassembler.menu.pattern.integration.avaritianeo;

import net.byAqua3.avaritia.loader.AvaritiaRecipes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class AvaritiaNeoPatternRecipeFinder {
    private AvaritiaNeoPatternRecipeFinder() {
    }

    public static Optional<RecipeHolder<?>> find(int side, List<ItemStack> input, Level level) {
        if (side != 9) {
            return Optional.empty();
        }

        return level.getRecipeManager()
                .getRecipeFor(AvaritiaRecipes.EXTREME_CRAFTING.get(), CraftingInput.of(side, side, input), level)
                .map(holder -> (RecipeHolder<?>) holder);
    }

    public static List<RecipeHolder<?>> findAll(int side, List<ItemStack> input, Level level) {
        if (side != 9) {
            return List.of();
        }

        return new ArrayList<RecipeHolder<?>>(level.getRecipeManager()
                .getRecipesFor(AvaritiaRecipes.EXTREME_CRAFTING.get(), CraftingInput.of(side, side, input), level));
    }
}
