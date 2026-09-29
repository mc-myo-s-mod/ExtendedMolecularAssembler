package me.myogoo.extendedmolecularassembler.adapter.recipe;

import me.myogoo.myotus.api.recipe.IMyotusTableRecipe;
import me.myogoo.myotus.api.recipe.IMyotusShapelessTableRecipe;
import me.myogoo.extendedmolecularassembler.pattern.ExtendedPatternTableTypes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;

import java.util.List;

public class ShapelessCraftingRecipeAdapter extends AbstractTableRecipeAdapter<CraftingInput, CraftingRecipe>
        implements IMyotusShapelessTableRecipe<CraftingInput> {
    public ShapelessCraftingRecipeAdapter(CraftingRecipe recipe) {
        super(recipe, ExtendedPatternTableTypes.VANILLA_CRAFTING, 1, 3);
    }

    @Override
    public CraftingInput createInput(List<ItemStack> items) {
        return CraftingInput.of(sideLength(), sideLength(), RecipeGridHelper.copyItems(items, gridSize()));
    }
}
