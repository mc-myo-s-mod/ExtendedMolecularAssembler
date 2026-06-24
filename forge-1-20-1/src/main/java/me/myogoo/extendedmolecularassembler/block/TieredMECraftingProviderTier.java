package me.myogoo.extendedmolecularassembler.block;

import me.myogoo.extendedmolecularassembler.pattern.ExtendedPatternTableTypes;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

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

    public Component providedTables() {
        return Component.translatable("tooltip.extendedmolecularassembler.me_crafting_provider.provides." + id);
    }

    public boolean provides(ResourceLocation tableType, int tableTier) {
        if (tableType.equals(ExtendedPatternTableTypes.EXTENDED_CRAFTING_BASIC)
                || tableType.equals(ExtendedPatternTableTypes.RE_AVARITIA_SCULK)) {
            return this == BASIC;
        }
        if (tableType.equals(ExtendedPatternTableTypes.EXTENDED_CRAFTING_ADVANCED)
                || tableType.equals(ExtendedPatternTableTypes.RE_AVARITIA_NETHER)) {
            return this == ADVANCED;
        }
        if (tableType.equals(ExtendedPatternTableTypes.EXTENDED_CRAFTING_ELITE)
                || tableType.equals(ExtendedPatternTableTypes.RE_AVARITIA_END)) {
            return this == ELITE;
        }
        if (tableType.equals(ExtendedPatternTableTypes.EXTENDED_CRAFTING_ULTIMATE)
                || tableType.equals(ExtendedPatternTableTypes.RE_AVARITIA_EXTREME)
                || tableType.equals(ExtendedPatternTableTypes.AVARITIA_NEO_EXTREME)) {
            return this == ULTIMATE;
        }
        if ("reavaritia".equals(tableType.getNamespace()) || "avaritianeo".equals(tableType.getNamespace())) {
            return false;
        }
        return this.tier == tableTier;
    }

    public static TieredMECraftingProviderTier requiredFor(ResourceLocation tableType, int tableTier) {
        for (var value : values()) {
            if (value.provides(tableType, tableTier)) {
                return value;
            }
        }
        throw new IllegalArgumentException("Unsupported ME crafting provider table " + tableType
                + " tier " + tableTier);
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
