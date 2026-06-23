package me.myogoo.extendedmolecularassembler.adapter.recipe;

import me.myogoo.extendedmolecularassembler.pattern.ExtendedPatternTableTypes;
import me.myogoo.myotus.api.recipe.IMyotusShapedTableRecipe;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.ShapedRecipe;

import java.util.List;

public class ShapedCraftingRecipeAdapter extends AbstractTableRecipeAdapter<CraftingContainer, ShapedRecipe>
        implements IMyotusShapedTableRecipe<CraftingContainer> {
    public ShapedCraftingRecipeAdapter(ShapedRecipe recipe) {
        super(recipe, ExtendedPatternTableTypes.VANILLA_CRAFTING, 1, 3);
    }

    @Override public CraftingContainer createInput(List<ItemStack> items) {
        return RecipeGridHelper.craftingContainer(sideLength(), sideLength(), items);
    }
    @Override public int width() { return recipe().getWidth(); }
    @Override public int height() { return recipe().getHeight(); }
}
