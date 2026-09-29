package me.myogoo.extendedmolecularassembler.adapter.recipe;

import me.myogoo.myotus.api.recipe.IMyotusTableRecipe;
import me.myogoo.myotus.api.recipe.IMyotusShapedTableRecipe;
import me.myogoo.extendedmolecularassembler.pattern.ExtendedPatternTableTypes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.ShapedRecipe;

import java.util.List;

public class ShapedCraftingRecipeAdapter extends AbstractTableRecipeAdapter<CraftingInput, ShapedRecipe>
        implements IMyotusShapedTableRecipe<CraftingInput> {
    public ShapedCraftingRecipeAdapter(ShapedRecipe recipe) {
        super(recipe, ExtendedPatternTableTypes.VANILLA_CRAFTING, 1, 3);
    }

    @Override
    public CraftingInput createInput(List<ItemStack> items) {
        return CraftingInput.of(sideLength(), sideLength(), RecipeGridHelper.copyItems(items, gridSize()));
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
