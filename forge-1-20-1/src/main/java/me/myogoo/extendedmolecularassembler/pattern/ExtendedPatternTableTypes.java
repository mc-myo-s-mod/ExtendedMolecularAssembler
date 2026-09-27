package me.myogoo.extendedmolecularassembler.pattern;

import me.myogoo.extendedmolecularassembler.ExtendedMolecularAssembler;
import me.myogoo.extendedmolecularassembler.lang.EMATranslationKey;
import me.myogoo.myotus.client.MyoTranslateKey;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public final class ExtendedPatternTableTypes {
    public static final ResourceLocation UNKNOWN = ExtendedMolecularAssembler.makeId("unknown");
    public static final ResourceLocation VANILLA_CRAFTING = new ResourceLocation("crafting_table");
    public static final ResourceLocation EXTENDED_CRAFTING_BASIC =
            new ResourceLocation("extendedcrafting", "basic_table");
    public static final ResourceLocation EXTENDED_CRAFTING_ADVANCED =
            new ResourceLocation("extendedcrafting", "advanced_table");
    public static final ResourceLocation EXTENDED_CRAFTING_ELITE =
            new ResourceLocation("extendedcrafting", "elite_table");
    public static final ResourceLocation EXTENDED_CRAFTING_ULTIMATE =
            new ResourceLocation("extendedcrafting", "ultimate_table");
    public static final ResourceLocation EXTENDED_CRAFTING_EPIC =
            new ResourceLocation("extendedcrafting", "epic_table");
    public static final ResourceLocation EXTENDED_CRAFTING_LEGENDARY =
            new ResourceLocation("extendedcrafting", "legendary_table");
    public static final ResourceLocation RE_AVARITIA_SCULK =
            new ResourceLocation("reavaritia", "sculk_table");
    public static final ResourceLocation RE_AVARITIA_NETHER =
            new ResourceLocation("reavaritia", "nether_table");
    public static final ResourceLocation RE_AVARITIA_END =
            new ResourceLocation("reavaritia", "end_table");
    public static final ResourceLocation RE_AVARITIA_EXTREME =
            new ResourceLocation("reavaritia", "extreme_table");
    public static final ResourceLocation AVARITIA_NEO_EXTREME =
            new ResourceLocation("avaritianeo", "extreme_table");

    private ExtendedPatternTableTypes() {
    }

    public static ResourceLocation extendedCrafting(int tier) {
        return switch (tier) {
            case 1 -> EXTENDED_CRAFTING_BASIC;
            case 2 -> EXTENDED_CRAFTING_ADVANCED;
            case 3 -> EXTENDED_CRAFTING_ELITE;
            case 4 -> EXTENDED_CRAFTING_ULTIMATE;
            case 5 -> EXTENDED_CRAFTING_EPIC;
            case 6 -> EXTENDED_CRAFTING_LEGENDARY;
            default -> new ResourceLocation("extendedcrafting", "tier_" + tier + "_table");
        };
    }

    public static ResourceLocation reAvaritia(int tier) {
        return switch (tier) {
            case 1 -> RE_AVARITIA_SCULK;
            case 2 -> RE_AVARITIA_NETHER;
            case 3 -> RE_AVARITIA_END;
            case 4 -> RE_AVARITIA_EXTREME;
            default -> new ResourceLocation("reavaritia", "tier_" + tier + "_table");
        };
    }

    public static Component displayName(ResourceLocation tableType, int tier, int sideLength) {
        var key = displayNameKey(tableType);
        if (key == EMATranslationKey.TABLE.UNKNOWN) {
            return Component.translatable(key.key(), tier, sideLength);
        }
        return Component.translatable(key.key());
    }

    private static MyoTranslateKey displayNameKey(ResourceLocation tableType) {
        if (tableType.equals(VANILLA_CRAFTING)) {
            return EMATranslationKey.TABLE.MINECRAFT_CRAFTING_TABLE;
        }
        if (tableType.equals(EXTENDED_CRAFTING_BASIC)) {
            return EMATranslationKey.TABLE.EXTENDEDCRAFTING_BASIC_TABLE;
        }
        if (tableType.equals(EXTENDED_CRAFTING_ADVANCED)) {
            return EMATranslationKey.TABLE.EXTENDEDCRAFTING_ADVANCED_TABLE;
        }
        if (tableType.equals(EXTENDED_CRAFTING_ELITE)) {
            return EMATranslationKey.TABLE.EXTENDEDCRAFTING_ELITE_TABLE;
        }
        if (tableType.equals(EXTENDED_CRAFTING_ULTIMATE)) {
            return EMATranslationKey.TABLE.EXTENDEDCRAFTING_ULTIMATE_TABLE;
        }
        if (tableType.equals(EXTENDED_CRAFTING_EPIC)) {
            return EMATranslationKey.TIER.EPIC;
        }
        if (tableType.equals(EXTENDED_CRAFTING_LEGENDARY)) {
            return EMATranslationKey.TIER.LEGENDARY;
        }
        if (tableType.equals(RE_AVARITIA_SCULK)) {
            return EMATranslationKey.TABLE.REAVARITIA_SCULK_TABLE;
        }
        if (tableType.equals(RE_AVARITIA_NETHER)) {
            return EMATranslationKey.TABLE.REAVARITIA_NETHER_TABLE;
        }
        if (tableType.equals(RE_AVARITIA_END)) {
            return EMATranslationKey.TABLE.REAVARITIA_END_TABLE;
        }
        if (tableType.equals(RE_AVARITIA_EXTREME)) {
            return EMATranslationKey.TABLE.REAVARITIA_EXTREME_TABLE;
        }
        if (tableType.equals(AVARITIA_NEO_EXTREME)) {
            return EMATranslationKey.TABLE.AVARITIANEO_EXTREME_TABLE;
        }
        return EMATranslationKey.TABLE.UNKNOWN;
    }
}
