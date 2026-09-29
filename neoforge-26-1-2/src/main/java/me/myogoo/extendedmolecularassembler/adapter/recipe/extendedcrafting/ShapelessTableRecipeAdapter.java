package me.myogoo.extendedmolecularassembler.adapter.recipe.extendedcrafting;

import com.blakebr0.extendedcrafting.api.TableCraftingInput;
import com.blakebr0.extendedcrafting.crafting.recipe.ShapelessTableRecipe;
import me.myogoo.extendedmolecularassembler.adapter.recipe.AbstractTableRecipeAdapter;
import me.myogoo.extendedmolecularassembler.adapter.recipe.RecipeGridHelper;
import me.myogoo.myotus.api.recipe.IMyotusTableRecipe;
import me.myogoo.myotus.api.recipe.IMyotusShapelessTableRecipe;
import me.myogoo.extendedmolecularassembler.pattern.ExtendedPatternTableTypes;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class ShapelessTableRecipeAdapter extends AbstractTableRecipeAdapter<TableCraftingInput, ShapelessTableRecipe>
        implements IMyotusShapelessTableRecipe<TableCraftingInput> {
    public ShapelessTableRecipeAdapter(ShapelessTableRecipe recipe) {
        super(recipe, ExtendedPatternTableTypes.extendedCrafting(recipe.getTier()), recipe.getTier(),
                sideLengthForTier(recipe.getTier()));
    }

    @Override
    public TableCraftingInput createInput(List<ItemStack> items) {
        return TableCraftingInput.of(sideLength(), sideLength(), RecipeGridHelper.copyItems(items, gridSize()), tier());
    }
}
