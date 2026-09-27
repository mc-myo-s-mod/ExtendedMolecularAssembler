package me.myogoo.extendedmolecularassembler.block;

import me.myogoo.extendedmolecularassembler.lang.EMATranslationKey;
import me.myogoo.extendedmolecularassembler.pattern.ExtendedPatternTableTypes;
import me.myogoo.myotus.client.MyoTranslateKey;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.Locale;

public enum ExportMECraftingProviderTier {
    BASIC(1, "basic", 0xE6E6E6, EMATranslationKey.TIER.BASIC,
            ExtendedPatternTableTypes.EXTENDED_CRAFTING_BASIC),
    ADVANCED(2, "advanced", 0xFFFF6D, EMATranslationKey.TIER.ADVANCED,
            ExtendedPatternTableTypes.EXTENDED_CRAFTING_ADVANCED),
    ELITE(3, "elite", 0x4CFFFF, EMATranslationKey.TIER.ELITE,
            ExtendedPatternTableTypes.EXTENDED_CRAFTING_ELITE),
    ULTIMATE(4, "ultimate", 0x6FEC86, EMATranslationKey.TIER.ULTIMATE,
            ExtendedPatternTableTypes.EXTENDED_CRAFTING_ULTIMATE),
    RE_AVARITIA_SCULK(1, "re_avaritia_sculk", 0xAFB991,
            EMATranslationKey.TIER.RE_AVARITIA_SCULK, ExtendedPatternTableTypes.RE_AVARITIA_SCULK),
    RE_AVARITIA_NETHER(2, "re_avaritia_nether", 0x61343A,
            EMATranslationKey.TIER.RE_AVARITIA_NETHER, ExtendedPatternTableTypes.RE_AVARITIA_NETHER),
    RE_AVARITIA_END(3, "re_avaritia_end", 0x2F584E,
            EMATranslationKey.TIER.RE_AVARITIA_END, ExtendedPatternTableTypes.RE_AVARITIA_END),
    XTREME(4, "xtreme", 0xC2FFFF, EMATranslationKey.TIER.XTREME,
            ExtendedPatternTableTypes.RE_AVARITIA_EXTREME,
            ExtendedPatternTableTypes.AVARITIA_NEO_EXTREME),
    EPIC(5, "epic", 0xBC80EA, EMATranslationKey.TIER.EPIC,
            ExtendedPatternTableTypes.EXTENDED_CRAFTING_EPIC),
    LEGENDARY(6, "legendary", 0xF3BC4C, EMATranslationKey.TIER.LEGENDARY,
            ExtendedPatternTableTypes.EXTENDED_CRAFTING_LEGENDARY);

    private static final int NORMAL_PLAN_HIGHLIGHT_ALPHA = 0x22;
    private static final int MISSING_PROVIDER_HIGHLIGHT_ALPHA = 0x66;

    private final int tier;
    private final String id;
    private final int colorRgb;
    private final MyoTranslateKey translationKey;
    private final ResourceLocation[] tableTypes;

    ExportMECraftingProviderTier(int tier, String id, int colorRgb,
            MyoTranslateKey translationKey, ResourceLocation... tableTypes) {
        this.tier = tier;
        this.id = id;
        this.colorRgb = colorRgb;
        this.translationKey = translationKey;
        this.tableTypes = tableTypes;
    }

    public int tier() {
        return tier;
    }

    public String id() {
        return id;
    }

    public int colorRgb() {
        return colorRgb;
    }

    /**
     * Translucent tint used behind this provider's outputs in the AE2 crafting plan.
     * The RGB is the average of pixels (0, 0) and (0, 1) in the provider's base texture.
     */
    public int planHighlightColor(boolean missingProvider) {
        var alpha = missingProvider ? MISSING_PROVIDER_HIGHLIGHT_ALPHA : NORMAL_PLAN_HIGHLIGHT_ALPHA;
        return alpha << 24 | colorRgb;
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

    public static ExportMECraftingProviderTier requiredFor(ResourceLocation tableType, int tableTier) {
        for (var value : values()) {
            if (value.provides(tableType, tableTier)) {
                return value;
            }
        }
        throw new IllegalArgumentException("Unsupported ME crafting provider table " + tableType
                + " tier " + tableTier);
    }

    public static ExportMECraftingProviderTier byTier(int tier) {
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
