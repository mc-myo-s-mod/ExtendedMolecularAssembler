package me.myogoo.extendedmolecularassembler.api;

import me.myogoo.extendedmolecularassembler.init.EMAItems;
import me.myogoo.extendedmolecularassembler.pattern.ExtendedTableCraftingPattern;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

public final class ExtendedPatternDetailsHelper {
    private ExtendedPatternDetailsHelper() {
    }

    public static ItemStack encodeExtendedCraftingPattern(RecipeHolder<?> recipe, ItemStack[] inputs, ItemStack output,
            boolean allowSubstitutes, boolean allowFluidSubstitutes) {
        var stack = EMAItems.EXTENDED_CRAFTING_PATTERN.toStack();
        ExtendedTableCraftingPattern.encode(stack, recipe, inputs, output, allowSubstitutes, allowFluidSubstitutes);
        return stack;
    }
}
