package me.myogoo.extendedmolecularassembler.block;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import java.util.Locale;

public enum TieredMECraftingProviderTier {
    BASIC(1, "basic", ChatFormatting.WHITE),
    ADVANCED(2, "advanced", ChatFormatting.AQUA),
    ELITE(3, "elite", ChatFormatting.LIGHT_PURPLE),
    ULTIMATE(4, "ultimate", ChatFormatting.GOLD);

    private final int tier;
    private final String id;
    private final ChatFormatting color;

    TieredMECraftingProviderTier(int tier, String id, ChatFormatting color) {
        this.tier = tier;
        this.id = id;
        this.color = color;
    }

    public int tier() {
        return tier;
    }

    public String id() {
        return id;
    }

    public ChatFormatting color() {
        return color;
    }

    public String blockId() {
        return id + "_me_crafting_provider";
    }

    public Component displayName() {
        return Component.translatable("tier.extendedmolecularassembler." + id);
    }

    public static TieredMECraftingProviderTier byTier(int tier) {
        for (var value : values()) {
            if (value.tier == tier) {
                return value;
            }
        }
        throw new IllegalArgumentException("Unsupported ME crafting provider tier " + tier);
    }

    public static String tierName(int tier) {
        try {
            return byTier(tier).id.toUpperCase(Locale.ROOT);
        } catch (IllegalArgumentException ignored) {
            return "TIER_" + tier;
        }
    }
}
