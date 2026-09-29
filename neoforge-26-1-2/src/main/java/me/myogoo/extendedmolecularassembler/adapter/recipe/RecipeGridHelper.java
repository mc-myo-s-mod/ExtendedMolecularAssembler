package me.myogoo.extendedmolecularassembler.adapter.recipe;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public final class RecipeGridHelper {
    private RecipeGridHelper() {
    }

    public static NonNullList<ItemStack> copyItems(List<ItemStack> items, int size) {
        var result = NonNullList.withSize(size, ItemStack.EMPTY);
        var count = Math.min(items.size(), size);
        for (int i = 0; i < count; i++) {
            result.set(i, items.get(i));
        }
        return result;
    }
}
