package me.myogoo.extendedmolecularassembler.api;

import me.myogoo.extendedmolecularassembler.init.EMAItems;
import me.myogoo.extendedmolecularassembler.pattern.ExtendedTableCraftingPattern;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;

public final class ExtendedPatternDetailsHelper {
    private ExtendedPatternDetailsHelper() {
    }

    public static ItemStack encodeExtendedCraftingPattern(Recipe<?> recipe, ItemStack[] inputs, ItemStack output,
            boolean allowSubstitutes) {
        var stack = new ItemStack(EMAItems.EXTENDED_CRAFTING_PATTERN.get());
        ExtendedTableCraftingPattern.encode(stack, recipe, inputs, output, allowSubstitutes);
        return stack;
    }
}
