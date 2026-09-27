package me.myogoo.extendedmolecularassembler.adapter.recipe.reavaritia;

import net.minecraft.world.Container;
import committee.nova.mods.avaritia.common.crafting.recipe.ShapedTableCraftingRecipe;
import me.myogoo.extendedmolecularassembler.adapter.recipe.AbstractTableRecipeAdapter;
import me.myogoo.extendedmolecularassembler.adapter.recipe.RecipeGridHelper;
import me.myogoo.myotus.api.recipe.IMyotusTableRecipe;
import me.myogoo.myotus.api.recipe.IMyotusShapedTableRecipe;
import me.myogoo.extendedmolecularassembler.pattern.ExtendedPatternTableTypes;
import net.minecraft.world.item.ItemStack;

import java.util.List;

@me.myogoo.extendedmolecularassembler.api.annotation.ReAvaritia
public class ShapedTierRecipeAdapter extends AbstractTableRecipeAdapter<Container, ShapedTableCraftingRecipe>
        implements IMyotusShapedTableRecipe<Container> {
    public ShapedTierRecipeAdapter(ShapedTableCraftingRecipe recipe) {
        super(recipe, ExtendedPatternTableTypes.reAvaritia(recipe.getTier()), recipe.getTier(),
                sideLengthForTier(recipe.getTier()));
    }

    @Override
    public Container createInput(List<ItemStack> items) {
        return RecipeGridHelper.craftingContainer(sideLength(), sideLength(), items);
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
