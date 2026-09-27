package me.myogoo.extendedmolecularassembler.menu.pattern.integration.avaritianeo;

import net.byAqua3.avaritia.loader.AvaritiaRecipes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@me.myogoo.extendedmolecularassembler.api.annotation.AvaritiaNeo
public final class AvaritiaNeoPatternRecipeFinder {
    private AvaritiaNeoPatternRecipeFinder() {
    }

    public static Optional<Recipe<?>> find(int side, List<ItemStack> input, Level level) {
        if (side != 9) {
            return Optional.empty();
        }

        return level.getRecipeManager()
                .getRecipeFor(AvaritiaRecipes.EXTREME_CRAFTING.get(), me.myogoo.extendedmolecularassembler.adapter.recipe.RecipeGridHelper.craftingContainer(side, side, input), level)
                .map(holder -> (Recipe<?>) holder);
    }

    public static List<Recipe<?>> findAll(int side, List<ItemStack> input, Level level) {
        if (side != 9) {
            return List.of();
        }

        return new ArrayList<Recipe<?>>(level.getRecipeManager()
                .getRecipesFor(AvaritiaRecipes.EXTREME_CRAFTING.get(), me.myogoo.extendedmolecularassembler.adapter.recipe.RecipeGridHelper.craftingContainer(side, side, input), level));
    }
}
