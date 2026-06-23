package me.myogoo.extendedmolecularassembler.pattern;

import me.myogoo.extendedmolecularassembler.ExtendedMolecularAssembler;
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
        if (tableType.equals(UNKNOWN)) {
            return Component.translatable("table.extendedmolecularassembler.unknown", tier, sideLength);
        }

        var key = "table.extendedmolecularassembler."
                + tableType.getNamespace()
                + "."
                + tableType.getPath().replace('/', '.');
        return Component.translatable(key);
    }
}
