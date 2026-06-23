package me.myogoo.extendedmolecularassembler.adapter.recipe.avaritianeo;

import me.myogoo.extendedmolecularassembler.adapter.recipe.AbstractTableRecipeAdapter;
import me.myogoo.extendedmolecularassembler.adapter.recipe.RecipeGridHelper;
import me.myogoo.myotus.api.recipe.IMyotusTableRecipe;
import me.myogoo.myotus.api.recipe.IMyotusShapedTableRecipe;
import me.myogoo.extendedmolecularassembler.pattern.ExtendedPatternTableTypes;
import net.byAqua3.avaritia.recipe.RecipeExtremeShaped;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;

import java.util.List;

public class ShapedExtremeRecipeAdapter extends AbstractTableRecipeAdapter<CraftingInput, RecipeExtremeShaped>
        implements IMyotusShapedTableRecipe<CraftingInput> {
    public ShapedExtremeRecipeAdapter(RecipeExtremeShaped recipe) {
        super(recipe, ExtendedPatternTableTypes.AVARITIA_NEO_EXTREME, 4, 9);
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
