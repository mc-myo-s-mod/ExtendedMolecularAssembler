package me.myogoo.extendedmolecularassembler.menu.pattern;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;

public record ExtendedPatternRecipeType(
        ExtendedPatternEncodingTermMenu.RecipeProvider provider,
        int tableTier,
        int tableSide) {
    public boolean isActive() {
        return provider != null && provider.isActive() && tableTier > 0 && tableSide > 0;
    }

    public void writeToNBT(CompoundTag data, String providerKey, String tierKey, String sideKey) {
        data.putString(providerKey, provider.name());
        data.putInt(tierKey, tableTier);
        data.putInt(sideKey, tableSide);
    }

    public void writeToNBT(ValueOutput output, String providerKey, String tierKey, String sideKey) {
        output.putString(providerKey, provider.name());
        output.putInt(tierKey, tableTier);
        output.putInt(sideKey, tableSide);
    }

    @Nullable
    public static ExtendedPatternRecipeType readFromNBT(
            CompoundTag data,
            String providerKey,
            String tierKey,
            String sideKey) {
        var providerName = data.getStringOr(providerKey, "");
        if (providerName.isEmpty()) {
            return null;
        }

        try {
            var provider = ExtendedPatternEncodingTermMenu.RecipeProvider.valueOf(providerName);
            var tableTier = data.getIntOr(tierKey, 0);
            var tableSide = data.getIntOr(sideKey, 0);
            if (tableTier <= 0 || tableSide <= 0) {
                return null;
            }
            return new ExtendedPatternRecipeType(provider, tableTier, tableSide);
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }

    @Nullable
    public static ExtendedPatternRecipeType readFromNBT(
            ValueInput input,
            String providerKey,
            String tierKey,
            String sideKey) {
        try {
            var provider = ExtendedPatternEncodingTermMenu.RecipeProvider.valueOf(input.getStringOr(providerKey, ""));
            var tableTier = input.getIntOr(tierKey, 0);
            var tableSide = input.getIntOr(sideKey, 0);
            if (tableTier <= 0 || tableSide <= 0) {
                return null;
            }
            return new ExtendedPatternRecipeType(provider, tableTier, tableSide);
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }
}
