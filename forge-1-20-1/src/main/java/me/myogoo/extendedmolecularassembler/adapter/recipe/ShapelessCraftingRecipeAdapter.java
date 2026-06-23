package me.myogoo.extendedmolecularassembler.adapter.recipe;

import me.myogoo.extendedmolecularassembler.pattern.ExtendedPatternTableTypes;
import me.myogoo.myotus.api.recipe.IMyotusShapelessTableRecipe;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;

import java.util.List;

public class ShapelessCraftingRecipeAdapter extends AbstractTableRecipeAdapter<CraftingContainer, CraftingRecipe>
        implements IMyotusShapelessTableRecipe<CraftingContainer> {
    public ShapelessCraftingRecipeAdapter(CraftingRecipe recipe) {
        super(recipe, ExtendedPatternTableTypes.VANILLA_CRAFTING, 1, 3);
    }

    @Override public CraftingContainer createInput(List<ItemStack> items) {
        return RecipeGridHelper.craftingContainer(sideLength(), sideLength(), items);
    }
}
