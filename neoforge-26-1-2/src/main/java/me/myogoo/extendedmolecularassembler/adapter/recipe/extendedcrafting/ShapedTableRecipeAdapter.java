package me.myogoo.extendedmolecularassembler.adapter.recipe.extendedcrafting;

import com.blakebr0.extendedcrafting.api.TableCraftingInput;
import com.blakebr0.extendedcrafting.crafting.recipe.ShapedTableRecipe;
import me.myogoo.extendedmolecularassembler.adapter.recipe.AbstractTableRecipeAdapter;
import me.myogoo.extendedmolecularassembler.adapter.recipe.RecipeGridHelper;
import me.myogoo.myotus.api.recipe.IMyotusTableRecipe;
import me.myogoo.myotus.api.recipe.IMyotusShapedTableRecipe;
import me.myogoo.extendedmolecularassembler.pattern.ExtendedPatternTableTypes;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class ShapedTableRecipeAdapter extends AbstractTableRecipeAdapter<TableCraftingInput, ShapedTableRecipe>
        implements IMyotusShapedTableRecipe<TableCraftingInput> {
    public ShapedTableRecipeAdapter(ShapedTableRecipe recipe) {
        super(recipe, ExtendedPatternTableTypes.extendedCrafting(recipe.getTier()), recipe.getTier(),
                sideLengthForTier(recipe.getTier()));
    }

    @Override
    public TableCraftingInput createInput(List<ItemStack> items) {
        return TableCraftingInput.of(sideLength(), sideLength(), RecipeGridHelper.copyItems(items, gridSize()), tier());
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
