package me.myogoo.extendedmolecularassembler.adapter.recipe.reavaritia;

import committee.nova.mods.avaritia.api.common.crafting.TierInput;
import committee.nova.mods.avaritia.common.crafting.recipe.ShapedTableCraftingRecipe;
import me.myogoo.extendedmolecularassembler.adapter.recipe.AbstractTableRecipeAdapter;
import me.myogoo.extendedmolecularassembler.adapter.recipe.RecipeGridHelper;
import me.myogoo.myotus.api.recipe.IMyotusTableRecipe;
import me.myogoo.myotus.api.recipe.IMyotusShapedTableRecipe;
import me.myogoo.extendedmolecularassembler.pattern.ExtendedPatternTableTypes;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class ShapedTierRecipeAdapter extends AbstractTableRecipeAdapter<TierInput, ShapedTableCraftingRecipe>
        implements IMyotusShapedTableRecipe<TierInput> {
    public ShapedTierRecipeAdapter(ShapedTableCraftingRecipe recipe) {
        super(recipe, ExtendedPatternTableTypes.reAvaritia(recipe.getTier()), recipe.getTier(),
                sideLengthForTier(recipe.getTier()));
    }

    @Override
    public TierInput createInput(List<ItemStack> items) {
        return TierInput.of(sideLength(), sideLength(), RecipeGridHelper.copyItems(items, gridSize()), tier());
    }

    @Override
    public int width() {
        return recipe().getWidth();
    }

    @Override
    public int height() {
        return recipe().getHeight();
    }
}
