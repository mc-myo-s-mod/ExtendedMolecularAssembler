package me.myogoo.extendedmolecularassembler.pattern;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

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
        boolean canSubstitute,
        boolean canSubstituteFluids) {
    private static final String TAG = "extendedmolecularassembler:encoded_extended_crafting_pattern";

    public EncodedExtendedCraftingPattern {
        tableType = Objects.requireNonNullElse(tableType, ExtendedPatternTableTypes.UNKNOWN);
        tableTier = Math.max(0, tableTier);
        tableSideLength = Math.max(0, tableSideLength);
    }

    @Nullable
    public static EncodedExtendedCraftingPattern get(ItemStack stack) {
        var root = stack.getTag();
        if (root == null || !root.contains(TAG, Tag.TAG_COMPOUND)) {
            return null;
        }
        var data = root.getCompound(TAG);
        var recipeId = ResourceLocation.tryParse(data.getString("recipeId"));
        var encodedInputs = data.getList("inputs", Tag.TAG_COMPOUND);
        if (recipeId == null || encodedInputs.size() > ExtendedTableCraftingPattern.MAX_GRID_SIZE) {
            return null;
        }
        var inputs = new ArrayList<ItemStack>(encodedInputs.size());
        for (int slot = 0; slot < encodedInputs.size(); slot++) {
            var input = encodedInputs.getCompound(slot);
            var itemId = ResourceLocation.tryParse(input.getString("id"));
            if (itemId == null || !BuiltInRegistries.ITEM.containsKey(itemId)) {
                return null;
            }
            inputs.add(ItemStack.of(input));
        }
        return new EncodedExtendedCraftingPattern(inputs, ItemStack.of(data.getCompound("result")), recipeId,
                ResourceLocation.tryParse(data.getString("tableType")), data.getInt("tableTier"),
                data.getInt("tableSideLength"), data.getBoolean("canSubstitute"),
                !data.contains("canSubstituteFluids") || data.getBoolean("canSubstituteFluids"));
    }

    public static void set(ItemStack stack, EncodedExtendedCraftingPattern pattern) {
        var data = new CompoundTag();
        var inputs = new ListTag();
        for (var input : pattern.inputs()) {
            inputs.add(input.save(new CompoundTag()));
        }
        data.put("inputs", inputs);
        data.put("result", pattern.result().save(new CompoundTag()));
        data.putString("recipeId", pattern.recipeId().toString());
        data.putString("tableType", pattern.tableType().toString());
        data.putInt("tableTier", pattern.tableTier());
        data.putInt("tableSideLength", pattern.tableSideLength());
        data.putBoolean("canSubstitute", pattern.canSubstitute());
        data.putBoolean("canSubstituteFluids", pattern.canSubstituteFluids());
        stack.getOrCreateTag().put(TAG, data);
    }

    public boolean containsMissingContent() {
        return result.isEmpty();
    }

    public boolean hasTableMetadata() {
        return !tableType.equals(ExtendedPatternTableTypes.UNKNOWN) && tableTier > 0 && tableSideLength > 0;
    }
}
