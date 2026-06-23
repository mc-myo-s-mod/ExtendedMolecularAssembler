package me.myogoo.extendedmolecularassembler.integration.jei.handler;

import me.myogoo.extendedmolecularassembler.menu.pattern.ExtendedPatternEncodingTermMenu;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class ExtendedPatternHolderRecipeTransferHandler<R extends Recipe<?>>
        implements IRecipeTransferHandler<ExtendedPatternEncodingTermMenu, RecipeHolder<R>> {
    private final RecipeType<RecipeHolder<R>> recipeType;

    public ExtendedPatternHolderRecipeTransferHandler(RecipeType<RecipeHolder<R>> recipeType) {
        this.recipeType = recipeType;
    }

    @Override
    public @NotNull Class<? extends ExtendedPatternEncodingTermMenu> getContainerClass() {
        return ExtendedPatternEncodingTermMenu.class;
    }

    @Override
    public @NotNull Optional<MenuType<ExtendedPatternEncodingTermMenu>> getMenuType() {
        return Optional.of(ExtendedPatternEncodingTermMenu.TYPE);
    }

    @Override
    public @NotNull RecipeType<RecipeHolder<R>> getRecipeType() {
        return recipeType;
    }

    @Override
    public @Nullable IRecipeTransferError transferRecipe(ExtendedPatternEncodingTermMenu menu, RecipeHolder<R> recipe,
            IRecipeSlotsView recipeSlots, Player player, boolean maxTransfer, boolean doTransfer) {
        if (!ExtendedPatternRecipeTransfer.canTransfer(recipe.value())) {
            return null;
        }

        if (doTransfer) {
            ExtendedPatternRecipeTransfer.transfer(menu, recipe);
        }
        return null;
    }
}
