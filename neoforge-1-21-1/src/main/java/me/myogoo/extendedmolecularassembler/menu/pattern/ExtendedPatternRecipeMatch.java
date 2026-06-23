package me.myogoo.extendedmolecularassembler.menu.pattern;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

public record ExtendedPatternRecipeMatch(RecipeHolder<?> recipe, ItemStack[] inputs, ItemStack result) {
}
