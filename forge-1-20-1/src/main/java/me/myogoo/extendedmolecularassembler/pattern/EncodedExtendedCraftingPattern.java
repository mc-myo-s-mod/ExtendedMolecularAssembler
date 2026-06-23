package me.myogoo.extendedmolecularassembler.pattern;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public record EncodedExtendedCraftingPattern(
        List<ItemStack> inputs,
        ItemStack result,
        ResourceLocation recipeId,
        ResourceLocation tableType,
        int tableTier,
        int tableSideLength,
        boolean canSubstitute) {
    public static final String TAG_KEY = "extendedmolecularassembler:extended_crafting_pattern";

    public EncodedExtendedCraftingPattern {
        tableType = Objects.requireNonNullElse(tableType, ExtendedPatternTableTypes.UNKNOWN);
        tableTier = Math.max(0, tableTier);
        tableSideLength = Math.max(0, tableSideLength);
        inputs = List.copyOf(inputs.stream().map(ItemStack::copy).toList());
        result = result.copy();
    }

    public boolean containsMissingContent() {
        return false;
    }

    public boolean hasTableMetadata() {
        return !tableType.equals(ExtendedPatternTableTypes.UNKNOWN) && tableTier > 0 && tableSideLength > 0;
    }

    public CompoundTag save() {
        var tag = new CompoundTag();
        var inputList = new ListTag();
        for (var input : inputs) {
            inputList.add(input.save(new CompoundTag()));
        }
        tag.put("inputs", inputList);
        tag.put("result", result.save(new CompoundTag()));
        tag.putString("recipeId", recipeId.toString());
        tag.putString("tableType", tableType.toString());
        tag.putInt("tableTier", tableTier);
        tag.putInt("tableSideLength", tableSideLength);
        tag.putBoolean("canSubstitute", canSubstitute);
        return tag;
    }

    public static EncodedExtendedCraftingPattern load(CompoundTag tag) {
        var inputList = tag.getList("inputs", Tag.TAG_COMPOUND);
        var inputs = new ArrayList<ItemStack>(inputList.size());
        for (int i = 0; i < inputList.size(); i++) {
            inputs.add(ItemStack.of(inputList.getCompound(i)));
        }
        return new EncodedExtendedCraftingPattern(
                inputs,
                ItemStack.of(tag.getCompound("result")),
                new ResourceLocation(tag.getString("recipeId")),
                tag.contains("tableType") ? new ResourceLocation(tag.getString("tableType")) : ExtendedPatternTableTypes.UNKNOWN,
                tag.getInt("tableTier"),
                tag.getInt("tableSideLength"),
                tag.getBoolean("canSubstitute"));
    }

    public static EncodedExtendedCraftingPattern get(ItemStack stack) {
        var root = stack.getTag();
        if (root == null || !root.contains(TAG_KEY, Tag.TAG_COMPOUND)) {
            return null;
        }
        return load(root.getCompound(TAG_KEY));
    }

    public static void set(ItemStack stack, EncodedExtendedCraftingPattern pattern) {
        stack.getOrCreateTag().put(TAG_KEY, pattern.save());
    }
}
