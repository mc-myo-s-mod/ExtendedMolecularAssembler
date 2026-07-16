package me.myogoo.extendedmolecularassembler.block;

import me.myogoo.extendedmolecularassembler.pattern.ExtendedPatternTableTypes;
import me.myogoo.extendedmolecularassembler.lang.EMATranslationKey;
import me.myogoo.myotus.client.MyoTranslateKey;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.Locale;

public enum TieredMECraftingProviderTier {
    BASIC(1, "basic", ChatFormatting.WHITE, EMATranslationKey.TIER.BASIC,
            ExtendedPatternTableTypes.EXTENDED_CRAFTING_BASIC),
    ADVANCED(2, "advanced", ChatFormatting.AQUA, EMATranslationKey.TIER.ADVANCED,
            ExtendedPatternTableTypes.EXTENDED_CRAFTING_ADVANCED),
    ELITE(3, "elite", ChatFormatting.LIGHT_PURPLE, EMATranslationKey.TIER.ELITE,
            ExtendedPatternTableTypes.EXTENDED_CRAFTING_ELITE),
    ULTIMATE(4, "ultimate", ChatFormatting.GOLD, EMATranslationKey.TIER.ULTIMATE,
            ExtendedPatternTableTypes.EXTENDED_CRAFTING_ULTIMATE),
    RE_AVARITIA_SCULK(1, "re_avaritia_sculk", ChatFormatting.DARK_AQUA,
            EMATranslationKey.TIER.RE_AVARITIA_SCULK, ExtendedPatternTableTypes.RE_AVARITIA_SCULK),
    RE_AVARITIA_NETHER(2, "re_avaritia_nether", ChatFormatting.RED,
            EMATranslationKey.TIER.RE_AVARITIA_NETHER, ExtendedPatternTableTypes.RE_AVARITIA_NETHER),
    RE_AVARITIA_END(3, "re_avaritia_end", ChatFormatting.LIGHT_PURPLE,
            EMATranslationKey.TIER.RE_AVARITIA_END, ExtendedPatternTableTypes.RE_AVARITIA_END),
    XTREME(4, "xtreme", ChatFormatting.GOLD, EMATranslationKey.TIER.XTREME,
            ExtendedPatternTableTypes.RE_AVARITIA_EXTREME,
            ExtendedPatternTableTypes.AVARITIA_NEO_EXTREME);

    private final int tier;
    private final String id;
    private final ChatFormatting color;
    private final MyoTranslateKey translationKey;
    private final ResourceLocation[] tableTypes;

    TieredMECraftingProviderTier(int tier, String id, ChatFormatting color,
            MyoTranslateKey translationKey, ResourceLocation... tableTypes) {
        this.tier = tier;
        this.id = id;
        this.color = color;
        this.translationKey = translationKey;
        this.tableTypes = tableTypes;
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
        return Component.translatable(translationKey.key());
    }

    public Component providedTable() {
        if (this == XTREME) {
            return Component.translatable(translationKey.key());
        }
        return ExtendedPatternTableTypes.displayName(tableTypes[0], tier, 2 * tier + 1);
    }

    public boolean provides(ResourceLocation tableType, int tableTier) {
        if (this == BASIC && tableType.equals(ExtendedPatternTableTypes.VANILLA_CRAFTING) && tableTier == 1) {
            return true;
        }
        if (this.tier != tableTier) {
            return false;
        }
        for (var supportedTableType : tableTypes) {
            if (supportedTableType.equals(tableType)) {
                return true;
            }
        }
        return false;
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
