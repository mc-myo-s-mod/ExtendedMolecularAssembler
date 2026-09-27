package me.myogoo.extendedmolecularassembler.adapter.recipe.avaritianeo;

import me.myogoo.extendedmolecularassembler.adapter.recipe.AbstractTableRecipeAdapter;
import me.myogoo.extendedmolecularassembler.adapter.recipe.RecipeGridHelper;
import me.myogoo.myotus.api.recipe.IMyotusTableRecipe;
import me.myogoo.myotus.api.recipe.IMyotusShapelessTableRecipe;
import me.myogoo.extendedmolecularassembler.pattern.ExtendedPatternTableTypes;
import net.byAqua3.avaritia.recipe.RecipeExtremeCrafting;
import net.byAqua3.avaritia.recipe.RecipeExtremeShapeless;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.inventory.CraftingContainer;

import java.util.List;

@me.myogoo.extendedmolecularassembler.api.annotation.AvaritiaNeo
public class ShapelessExtremeRecipeAdapter extends AbstractTableRecipeAdapter<CraftingContainer, RecipeExtremeCrafting>
        implements IMyotusShapelessTableRecipe<CraftingContainer> {
    public ShapelessExtremeRecipeAdapter(RecipeExtremeShapeless recipe) {
        super(recipe, ExtendedPatternTableTypes.AVARITIA_NEO_EXTREME, 4, 9);
    }

    @Override
    public CraftingContainer createInput(List<ItemStack> items) {
        return RecipeGridHelper.craftingContainer(sideLength(), sideLength(), items);
    }
}
