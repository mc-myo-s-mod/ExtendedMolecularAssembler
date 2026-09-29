package me.myogoo.extendedmolecularassembler.menu.pattern.integration.extendedcrafting;

import appeng.crafting.RecipeAccess;
import com.blakebr0.extendedcrafting.api.TableCraftingInput;
import com.blakebr0.extendedcrafting.init.ModRecipeTypes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class ExtendedCraftingPatternRecipeFinder {
    private ExtendedCraftingPatternRecipeFinder() {
    }

    public static RecipeHolder<?> byKey(Level level, ResourceKey<Recipe<?>> recipeId) {
        return RecipeAccess.byKey(level, ModRecipeTypes.TABLE.get(), recipeId);
    }

    public static Optional<RecipeHolder<?>> find(int side, List<ItemStack> input, Level level) {
        var tier = (side - 1) / 2;
        if (tier < 1 || tier > 4) {
            return Optional.empty();
        }

        var tableInput = TableCraftingInput.of(side, side, input, tier);
        return Optional.ofNullable(RecipeAccess.getRecipeFor(level, ModRecipeTypes.TABLE.get(), tableInput))
                .map(holder -> (RecipeHolder<?>) holder);
    }

    public static List<RecipeHolder<?>> findAll(int side, List<ItemStack> input, Level level) {
        var tier = (side - 1) / 2;
        if (tier < 1 || tier > 4) {
            return List.of();
        }

        var tableInput = TableCraftingInput.of(side, side, input, tier);
        return new ArrayList<RecipeHolder<?>>(RecipeAccess.getRecipesFor(level, ModRecipeTypes.TABLE.get(), tableInput)
                .map(holder -> (RecipeHolder<?>) holder)
                .toList());
    }
}
