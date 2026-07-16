package me.myogoo.extendedmolecularassembler.pattern;

import me.myogoo.extendedmolecularassembler.ExtendedMolecularAssembler;
import me.myogoo.extendedmolecularassembler.lang.EMATranslationKey;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public final class ExtendedPatternTableTypes {
    public static final ResourceLocation UNKNOWN = ExtendedMolecularAssembler.makeId("unknown");
    public static final ResourceLocation VANILLA_CRAFTING = ResourceLocation.withDefaultNamespace("crafting_table");
    public static final ResourceLocation EXTENDED_CRAFTING_BASIC =
            ResourceLocation.fromNamespaceAndPath("extendedcrafting", "basic_table");
    public static final ResourceLocation EXTENDED_CRAFTING_ADVANCED =
            ResourceLocation.fromNamespaceAndPath("extendedcrafting", "advanced_table");
    public static final ResourceLocation EXTENDED_CRAFTING_ELITE =
            ResourceLocation.fromNamespaceAndPath("extendedcrafting", "elite_table");
    public static final ResourceLocation EXTENDED_CRAFTING_ULTIMATE =
            ResourceLocation.fromNamespaceAndPath("extendedcrafting", "ultimate_table");
    public static final ResourceLocation RE_AVARITIA_SCULK =
            ResourceLocation.fromNamespaceAndPath("reavaritia", "sculk_table");
    public static final ResourceLocation RE_AVARITIA_NETHER =
            ResourceLocation.fromNamespaceAndPath("reavaritia", "nether_table");
    public static final ResourceLocation RE_AVARITIA_END =
            ResourceLocation.fromNamespaceAndPath("reavaritia", "end_table");
    public static final ResourceLocation RE_AVARITIA_EXTREME =
            ResourceLocation.fromNamespaceAndPath("reavaritia", "extreme_table");
    public static final ResourceLocation AVARITIA_NEO_EXTREME =
            ResourceLocation.fromNamespaceAndPath("avaritianeo", "extreme_table");

    private ExtendedPatternTableTypes() {
    }

    public static ResourceLocation extendedCrafting(int tier) {
        return switch (tier) {
            case 1 -> EXTENDED_CRAFTING_BASIC;
            case 2 -> EXTENDED_CRAFTING_ADVANCED;
            case 3 -> EXTENDED_CRAFTING_ELITE;
            case 4 -> EXTENDED_CRAFTING_ULTIMATE;
            default -> ResourceLocation.fromNamespaceAndPath("extendedcrafting", "tier_" + tier + "_table");
        };
    }

    public static ResourceLocation reAvaritia(int tier) {
        return switch (tier) {
            case 1 -> RE_AVARITIA_SCULK;
            case 2 -> RE_AVARITIA_NETHER;
            case 3 -> RE_AVARITIA_END;
            case 4 -> RE_AVARITIA_EXTREME;
            default -> ResourceLocation.fromNamespaceAndPath("reavaritia", "tier_" + tier + "_table");
        };
    }

    public static Component displayName(ResourceLocation tableType, int tier, int sideLength) {
        var key = displayNameKey(tableType);
        if (key == EMATranslationKey.TABLE.UNKNOWN) {
            return Component.translatable(key.key(), tier, sideLength);
        }
        return Component.translatable(key.key());
    }

    private static EMATranslationKey.TABLE displayNameKey(ResourceLocation tableType) {
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
