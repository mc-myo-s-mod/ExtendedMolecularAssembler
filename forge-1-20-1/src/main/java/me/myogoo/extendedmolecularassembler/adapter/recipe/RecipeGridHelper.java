package me.myogoo.extendedmolecularassembler.adapter.recipe;

import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.StackedContentsCompatible;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.StackedContents;

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

    public static CraftingContainer craftingContainer(int width, int height, List<ItemStack> items) {
        return new SimpleCraftingContainer(width, height, copyItems(items, width * height));
    }

    private record SimpleCraftingContainer(int width, int height, NonNullList<ItemStack> items)
            implements CraftingContainer, StackedContentsCompatible {
        @Override public int getWidth() { return width; }
        @Override public int getHeight() { return height; }
        @Override public List<ItemStack> getItems() { return items; }
        @Override public int getContainerSize() { return items.size(); }
        @Override public boolean isEmpty() { return items.stream().allMatch(ItemStack::isEmpty); }
        @Override public ItemStack getItem(int slot) { return items.get(slot); }
        @Override public ItemStack removeItem(int slot, int amount) { return ItemStack.EMPTY; }
        @Override public ItemStack removeItemNoUpdate(int slot) { return ItemStack.EMPTY; }
        @Override public void setItem(int slot, ItemStack stack) { items.set(slot, stack); }
        @Override public void setChanged() { }
        @Override public boolean stillValid(Player player) { return true; }
        @Override public void clearContent() { items.clear(); }
        @Override public void fillStackedContents(StackedContents contents) {
            for (var stack : items) {
                contents.accountSimpleStack(stack);
            }
        }
    }
}
