package me.myogoo.extendedmolecularassembler.block;

import me.myogoo.extendedmolecularassembler.pattern.ExtendedPatternTableTypes;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.Locale;

public enum TieredMECraftingProviderTier {
    BASIC(1, "basic", ChatFormatting.WHITE, ExtendedPatternTableTypes.EXTENDED_CRAFTING_BASIC),
    ADVANCED(2, "advanced", ChatFormatting.AQUA, ExtendedPatternTableTypes.EXTENDED_CRAFTING_ADVANCED),
    ELITE(3, "elite", ChatFormatting.LIGHT_PURPLE, ExtendedPatternTableTypes.EXTENDED_CRAFTING_ELITE),
    ULTIMATE(4, "ultimate", ChatFormatting.GOLD, ExtendedPatternTableTypes.EXTENDED_CRAFTING_ULTIMATE),
    RE_AVARITIA_SCULK(1, "re_avaritia_sculk", ChatFormatting.DARK_AQUA, ExtendedPatternTableTypes.RE_AVARITIA_SCULK),
    RE_AVARITIA_NETHER(2, "re_avaritia_nether", ChatFormatting.RED, ExtendedPatternTableTypes.RE_AVARITIA_NETHER),
    RE_AVARITIA_END(3, "re_avaritia_end", ChatFormatting.LIGHT_PURPLE, ExtendedPatternTableTypes.RE_AVARITIA_END),
    RE_AVARITIA_XTREME(4, "re_avaritia_xtreme", ChatFormatting.GOLD, ExtendedPatternTableTypes.RE_AVARITIA_EXTREME),
    AVARITIA_NEO_XTREME(4, "avaritia_neo_xtreme", ChatFormatting.YELLOW, ExtendedPatternTableTypes.AVARITIA_NEO_EXTREME);

    private final int tier;
    private final String id;
    private final ChatFormatting color;
    private final ResourceLocation tableType;

    TieredMECraftingProviderTier(int tier, String id, ChatFormatting color, ResourceLocation tableType) {
        this.tier = tier;
        this.id = id;
        this.color = color;
        this.tableType = tableType;
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

    public Component providedTable() {
        return ExtendedPatternTableTypes.displayName(tableType, tier, 2 * tier + 1);
    }

    public ResourceLocation tableType() {
        return tableType;
    }

    public boolean provides(ResourceLocation tableType, int tableTier) {
        return this.tableType.equals(tableType) && this.tier == tableTier
                || this == BASIC && tableType.equals(ExtendedPatternTableTypes.VANILLA_CRAFTING) && tableTier == 1;
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
