package me.myogoo.extendedmolecularassembler.adapter.recipe.avaritianeo;

import me.myogoo.extendedmolecularassembler.adapter.recipe.AbstractTableRecipeAdapter;
import me.myogoo.extendedmolecularassembler.adapter.recipe.RecipeGridHelper;
import me.myogoo.extendedmolecularassembler.pattern.ExtendedPatternTableTypes;
import me.myogoo.myotus.api.recipe.IMyotusShapedTableRecipe;
import net.byAqua3.avaritia.recipe.RecipeExtremeShaped;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class ShapedExtremeRecipeAdapter extends AbstractTableRecipeAdapter<CraftingContainer, RecipeExtremeShaped>
        implements IMyotusShapedTableRecipe<CraftingContainer> {
    public ShapedExtremeRecipeAdapter(RecipeExtremeShaped recipe) {
        super(recipe, ExtendedPatternTableTypes.AVARITIA_NEO_EXTREME, 4, 9);
    }

    @Override
    public CraftingContainer createInput(List<ItemStack> items) {
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
