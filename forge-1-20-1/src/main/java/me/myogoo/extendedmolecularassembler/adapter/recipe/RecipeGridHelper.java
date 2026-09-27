package me.myogoo.extendedmolecularassembler.adapter.recipe;

import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.TransientCraftingContainer;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public final class RecipeGridHelper {
    private RecipeGridHelper() {
    }

    public static CraftingContainer craftingContainer(int width, int height, List<ItemStack> items) {
        var container = new TransientCraftingContainer(new AbstractContainerMenu(null, -1) {
            @Override
            public ItemStack quickMoveStack(Player player, int slot) {
                return ItemStack.EMPTY;
            }

            @Override
            public boolean stillValid(Player player) {
                return true;
            }
        }, width, height);
        for (int slot = 0; slot < Math.min(items.size(), width * height); slot++) {
            container.setItem(slot, items.get(slot));
        }
        return container;
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
