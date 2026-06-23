package me.myogoo.extendedmolecularassembler.integration.jei.handler;

import me.myogoo.extendedmolecularassembler.menu.pattern.ExtendedPatternEncodingTermMenu;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import net.byAqua3.avaritia.loader.AvaritiaRecipes;
import net.byAqua3.avaritia.recipe.RecipeExtremeCrafting;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class ExtendedPatternAvaritiaNeoRecipeTransferHandler
        implements IRecipeTransferHandler<ExtendedPatternEncodingTermMenu, RecipeExtremeCrafting> {
    private final RecipeType<RecipeExtremeCrafting> recipeType;

    public ExtendedPatternAvaritiaNeoRecipeTransferHandler(RecipeType<RecipeExtremeCrafting> recipeType) {
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
    public @NotNull RecipeType<RecipeExtremeCrafting> getRecipeType() {
        return recipeType;
    }

    @Override
    public @Nullable IRecipeTransferError transferRecipe(ExtendedPatternEncodingTermMenu menu, RecipeExtremeCrafting recipe,
            IRecipeSlotsView recipeSlots, Player player, boolean maxTransfer, boolean doTransfer) {
        var holder = findRecipeHolder(menu, recipe);
        if (holder.isEmpty() || !ExtendedPatternRecipeTransfer.canTransfer(holder.get().value())) {
            return null;
        }

        if (doTransfer) {
            ExtendedPatternRecipeTransfer.transfer(menu, holder.get());
        }
        return null;
    }

    private Optional<RecipeHolder<RecipeExtremeCrafting>> findRecipeHolder(ExtendedPatternEncodingTermMenu menu,
            RecipeExtremeCrafting recipe) {
        return menu.getPlayerInventory().player.level().getRecipeManager()
                .getAllRecipesFor(AvaritiaRecipes.EXTREME_CRAFTING.get()).stream()
                .filter(holder -> holder.value() == recipe)
                .findFirst();
    }
}
