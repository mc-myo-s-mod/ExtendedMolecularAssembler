package me.myogoo.extendedmolecularassembler.adapter.recipe.reavaritia;

import committee.nova.mods.avaritia.common.crafting.recipe.ShapelessTableCraftingRecipe;
import me.myogoo.extendedmolecularassembler.adapter.recipe.AbstractTableRecipeAdapter;
import me.myogoo.extendedmolecularassembler.adapter.recipe.RecipeGridHelper;
import me.myogoo.extendedmolecularassembler.pattern.ExtendedPatternTableTypes;
import me.myogoo.myotus.api.recipe.IMyotusShapelessTableRecipe;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class ShapelessTierRecipeAdapter extends AbstractTableRecipeAdapter<Container, ShapelessTableCraftingRecipe>
        implements IMyotusShapelessTableRecipe<Container> {
    public ShapelessTierRecipeAdapter(ShapelessTableCraftingRecipe recipe) {
        super(recipe, ExtendedPatternTableTypes.reAvaritia(recipe.getTier()), recipe.getTier(),
                sideLengthForTier(recipe.getTier()));
    }

    @Override
    public Container createInput(List<ItemStack> items) {
        return RecipeGridHelper.craftingContainer(sideLength(), sideLength(), items);
    }
}
