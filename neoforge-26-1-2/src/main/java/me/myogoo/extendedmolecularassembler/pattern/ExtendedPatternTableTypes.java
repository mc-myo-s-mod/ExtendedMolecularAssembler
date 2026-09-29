package me.myogoo.extendedmolecularassembler.pattern;

import me.myogoo.extendedmolecularassembler.ExtendedMolecularAssembler;
import me.myogoo.extendedmolecularassembler.lang.EMATranslationKey;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public final class ExtendedPatternTableTypes {
    public static final Identifier UNKNOWN = ExtendedMolecularAssembler.makeId("unknown");
    public static final Identifier VANILLA_CRAFTING = Identifier.withDefaultNamespace("crafting_table");
    public static final Identifier EXTENDED_CRAFTING_BASIC =
            Identifier.fromNamespaceAndPath("extendedcrafting", "basic_table");
    public static final Identifier EXTENDED_CRAFTING_ADVANCED =
            Identifier.fromNamespaceAndPath("extendedcrafting", "advanced_table");
    public static final Identifier EXTENDED_CRAFTING_ELITE =
            Identifier.fromNamespaceAndPath("extendedcrafting", "elite_table");
    public static final Identifier EXTENDED_CRAFTING_ULTIMATE =
            Identifier.fromNamespaceAndPath("extendedcrafting", "ultimate_table");
    public static final Identifier RE_AVARITIA_SCULK =
            Identifier.fromNamespaceAndPath("reavaritia", "sculk_table");
    public static final Identifier RE_AVARITIA_NETHER =
            Identifier.fromNamespaceAndPath("reavaritia", "nether_table");
    public static final Identifier RE_AVARITIA_END =
            Identifier.fromNamespaceAndPath("reavaritia", "end_table");
    public static final Identifier RE_AVARITIA_EXTREME =
            Identifier.fromNamespaceAndPath("reavaritia", "extreme_table");

    private ExtendedPatternTableTypes() {
    }

    public static Identifier extendedCrafting(int tier) {
        return switch (tier) {
            case 1 -> EXTENDED_CRAFTING_BASIC;
            case 2 -> EXTENDED_CRAFTING_ADVANCED;
            case 3 -> EXTENDED_CRAFTING_ELITE;
            case 4 -> EXTENDED_CRAFTING_ULTIMATE;
            default -> Identifier.fromNamespaceAndPath("extendedcrafting", "tier_" + tier + "_table");
        };
    }

    public static Identifier reAvaritia(int tier) {
        return switch (tier) {
            case 1 -> RE_AVARITIA_SCULK;
            case 2 -> RE_AVARITIA_NETHER;
            case 3 -> RE_AVARITIA_END;
            case 4 -> RE_AVARITIA_EXTREME;
            default -> Identifier.fromNamespaceAndPath("reavaritia", "tier_" + tier + "_table");
        };
    }

    public static Component displayName(Identifier tableType, int tier, int sideLength) {
        var key = displayNameKey(tableType);
        if (key == EMATranslationKey.TABLE.UNKNOWN) {
            return Component.translatable(key.key(), tier, sideLength);
        }
        return Component.translatable(key.key());
    }

    private static EMATranslationKey.TABLE displayNameKey(Identifier tableType) {
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
        return EMATranslationKey.TABLE.UNKNOWN;
    }
}
