package me.myogoo.extendedmolecularassembler.menu.pattern;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;

public record ExtendedPatternRecipeMatch(Recipe<?> recipe, ItemStack[] inputs, ItemStack result) {
}
