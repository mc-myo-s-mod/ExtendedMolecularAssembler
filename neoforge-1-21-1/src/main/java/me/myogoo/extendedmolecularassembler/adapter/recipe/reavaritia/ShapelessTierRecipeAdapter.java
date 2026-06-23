package me.myogoo.extendedmolecularassembler.adapter.recipe.reavaritia;

import committee.nova.mods.avaritia.api.common.crafting.TierInput;
import committee.nova.mods.avaritia.common.crafting.recipe.ShapelessTableCraftingRecipe;
import me.myogoo.extendedmolecularassembler.adapter.recipe.AbstractTableRecipeAdapter;
import me.myogoo.extendedmolecularassembler.adapter.recipe.RecipeGridHelper;
import me.myogoo.myotus.api.recipe.IMyotusTableRecipe;
import me.myogoo.myotus.api.recipe.IMyotusShapelessTableRecipe;
import me.myogoo.extendedmolecularassembler.pattern.ExtendedPatternTableTypes;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class ShapelessTierRecipeAdapter extends AbstractTableRecipeAdapter<TierInput, ShapelessTableCraftingRecipe>
        implements IMyotusShapelessTableRecipe<TierInput> {
    public ShapelessTierRecipeAdapter(ShapelessTableCraftingRecipe recipe) {
        super(recipe, ExtendedPatternTableTypes.reAvaritia(recipe.getTier()), recipe.getTier(),
                sideLengthForTier(recipe.getTier()));
    }

    @Override
    public TierInput createInput(List<ItemStack> items) {
        return TierInput.of(sideLength(), sideLength(), RecipeGridHelper.copyItems(items, gridSize()), tier());
    }
}
