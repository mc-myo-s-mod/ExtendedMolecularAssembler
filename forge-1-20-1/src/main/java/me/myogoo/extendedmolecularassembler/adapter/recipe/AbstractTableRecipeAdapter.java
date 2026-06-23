package me.myogoo.extendedmolecularassembler.adapter.recipe;

import me.myogoo.myotus.api.recipe.IMyotusShapedTableRecipe;
import me.myogoo.myotus.api.recipe.IMyotusTableRecipe;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;

public abstract class AbstractTableRecipeAdapter<I extends Container, R extends Recipe<I>>
        implements IMyotusTableRecipe<I> {
    public static final int MAX_SIDE_LENGTH = 9;

    private final R recipe;
    private final ResourceLocation tableType;
    private final int tier;
    private final int sideLength;

    protected AbstractTableRecipeAdapter(R recipe, ResourceLocation tableType, int tier, int sideLength) {
        if (sideLength > MAX_SIDE_LENGTH) {
            throw new IllegalArgumentException("Recipe grid " + sideLength + "x" + sideLength
                    + " is larger than the extended molecular assembler limit");
        }
        this.recipe = recipe;
        this.tableType = tableType;
        this.tier = tier;
        this.sideLength = sideLength;
    }

    @Override public ResourceLocation tableType() { return tableType; }
    @Override public int tier() { return tier; }
    @Override public int sideLength() { return sideLength; }
    @Override public R recipe() { return recipe; }

    @Override
    public NonNullList<Ingredient> slotIngredients() {
        var ingredients = recipe.getIngredients();
        var result = NonNullList.withSize(gridSize(), Ingredient.EMPTY);
        if (this instanceof IMyotusShapedTableRecipe<?> shaped) {
            var offsetX = Math.floorDiv(sideLength - shaped.width(), 2);
            var offsetY = Math.floorDiv(sideLength - shaped.height(), 2);
            for (int y = 0; y < shaped.height(); y++) {
                for (int x = 0; x < shaped.width(); x++) {
                    var source = x + y * shaped.width();
                    if (source < ingredients.size()) {
                        result.set((x + offsetX) + (y + offsetY) * sideLength, ingredients.get(source));
                    }
                }
            }
        } else {
            var count = Math.min(ingredients.size(), result.size());
            for (int i = 0; i < count; i++) {
                result.set(i, ingredients.get(i));
            }
        }
        return result;
    }

    protected static int sideLengthForTier(int tier) {
        return tier * 2 + 1;
    }
}
