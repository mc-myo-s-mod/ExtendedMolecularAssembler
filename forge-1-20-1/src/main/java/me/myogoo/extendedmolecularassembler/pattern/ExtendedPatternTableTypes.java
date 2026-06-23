package me.myogoo.extendedmolecularassembler.pattern;

import me.myogoo.extendedmolecularassembler.ExtendedMolecularAssembler;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public final class ExtendedPatternTableTypes {
    public static final ResourceLocation UNKNOWN = ExtendedMolecularAssembler.makeId("unknown");
    public static final ResourceLocation VANILLA_CRAFTING = new ResourceLocation("minecraft", "crafting_table");
    public static final ResourceLocation EXTENDED_CRAFTING_BASIC = new ResourceLocation("extendedcrafting", "basic_table");
    public static final ResourceLocation EXTENDED_CRAFTING_ADVANCED = new ResourceLocation("extendedcrafting", "advanced_table");
    public static final ResourceLocation EXTENDED_CRAFTING_ELITE = new ResourceLocation("extendedcrafting", "elite_table");
    public static final ResourceLocation EXTENDED_CRAFTING_ULTIMATE = new ResourceLocation("extendedcrafting", "ultimate_table");
    public static final ResourceLocation RE_AVARITIA_SCULK = new ResourceLocation("reavaritia", "sculk_table");
    public static final ResourceLocation RE_AVARITIA_NETHER = new ResourceLocation("reavaritia", "nether_table");
    public static final ResourceLocation RE_AVARITIA_END = new ResourceLocation("reavaritia", "end_table");
    public static final ResourceLocation RE_AVARITIA_EXTREME = new ResourceLocation("reavaritia", "extreme_table");
    public static final ResourceLocation AVARITIA_NEO_EXTREME = new ResourceLocation("avaritianeo", "extreme_table");

    private ExtendedPatternTableTypes() {
    }

    public static ResourceLocation extendedCrafting(int tier) {
        return switch (tier) {
            case 1 -> EXTENDED_CRAFTING_BASIC;
            case 2 -> EXTENDED_CRAFTING_ADVANCED;
            case 3 -> EXTENDED_CRAFTING_ELITE;
            case 4 -> EXTENDED_CRAFTING_ULTIMATE;
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
