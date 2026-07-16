package me.myogoo.extendedmolecularassembler.integration.jei.handler;

import me.myogoo.extendedmolecularassembler.menu.pattern.ExtendedPatternEncodingTermMenu;
import me.myogoo.extendedmolecularassembler.menu.pattern.ExtendedPatternEncodingTermMenu.RecipeProvider;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.crafting.Recipe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class ExtendedPatternDirectRecipeTransferHandler<R extends Recipe<?>>
        implements IRecipeTransferHandler<ExtendedPatternEncodingTermMenu, R> {
    private final RecipeType<R> recipeType;
    @Nullable
    private final RecipeProvider transferredRecipeProvider;
    private final int transferredRecipeTier;
    private final int transferredRecipeSide;

    public ExtendedPatternDirectRecipeTransferHandler(RecipeType<R> recipeType,
            @Nullable RecipeProvider transferredRecipeProvider, int transferredRecipeTier, int transferredRecipeSide) {
        this.recipeType = recipeType;
        this.transferredRecipeProvider = transferredRecipeProvider;
        this.transferredRecipeTier = transferredRecipeTier;
        this.transferredRecipeSide = transferredRecipeSide;
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
    public @NotNull RecipeType<R> getRecipeType() {
        return recipeType;
    }

    @Override
    public @Nullable IRecipeTransferError transferRecipe(ExtendedPatternEncodingTermMenu menu, R recipe,
            IRecipeSlotsView recipeSlots, Player player, boolean maxTransfer, boolean doTransfer) {
        if (!ExtendedPatternRecipeTransfer.canTransfer(recipe)) {
            return null;
        }

        if (doTransfer) {
            ExtendedPatternRecipeTransfer.transfer(menu, recipe, transferredRecipeProvider,
                    transferredRecipeTier, transferredRecipeSide);
        }
        return null;
    }
}
