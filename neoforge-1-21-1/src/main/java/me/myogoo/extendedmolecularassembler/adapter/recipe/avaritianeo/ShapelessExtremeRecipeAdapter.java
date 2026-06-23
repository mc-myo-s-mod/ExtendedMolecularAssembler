package me.myogoo.extendedmolecularassembler.adapter.recipe.avaritianeo;

import me.myogoo.extendedmolecularassembler.adapter.recipe.AbstractTableRecipeAdapter;
import me.myogoo.extendedmolecularassembler.adapter.recipe.RecipeGridHelper;
import me.myogoo.myotus.api.recipe.IMyotusTableRecipe;
import me.myogoo.myotus.api.recipe.IMyotusShapelessTableRecipe;
import me.myogoo.extendedmolecularassembler.pattern.ExtendedPatternTableTypes;
import net.byAqua3.avaritia.recipe.RecipeExtremeCrafting;
import net.byAqua3.avaritia.recipe.RecipeExtremeShapeless;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;

import java.util.List;

public class ShapelessExtremeRecipeAdapter extends AbstractTableRecipeAdapter<CraftingInput, RecipeExtremeCrafting>
        implements IMyotusShapelessTableRecipe<CraftingInput> {
    public ShapelessExtremeRecipeAdapter(RecipeExtremeShapeless recipe) {
        super(recipe, ExtendedPatternTableTypes.AVARITIA_NEO_EXTREME, 4, 9);
    }

    @Override
    public CraftingInput createInput(List<ItemStack> items) {
        return CraftingInput.of(sideLength(), sideLength(), RecipeGridHelper.copyItems(items, gridSize()));
    }
}
