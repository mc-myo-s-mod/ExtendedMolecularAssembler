package me.myogoo.extendedmolecularassembler.menu.pattern;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
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

    @Nullable
    public static ExtendedPatternRecipeType readFromNBT(
            CompoundTag data,
            String providerKey,
            String tierKey,
            String sideKey) {
        if (!data.contains(providerKey, Tag.TAG_STRING)) {
            return null;
        }

        try {
            var provider = ExtendedPatternEncodingTermMenu.RecipeProvider.valueOf(data.getString(providerKey));
            var tableTier = data.getInt(tierKey);
            var tableSide = data.getInt(sideKey);
            if (tableTier <= 0 || tableSide <= 0) {
                return null;
            }
            return new ExtendedPatternRecipeType(provider, tableTier, tableSide);
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }
}
