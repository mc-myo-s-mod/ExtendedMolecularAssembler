package me.myogoo.extendedmolecularassembler.config;

import net.minecraftforge.common.ForgeConfigSpec;

public final class EMAConfig {
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.BooleanValue TIERED_MODE;

    static {
        var builder = new ForgeConfigSpec.Builder();
        builder.push("general");
        TIERED_MODE = builder
                .comment(
                        "When enabled, extended table auto-crafting is accepted only if an online ME tiered crafting provider for the encoded table type exists in the same ME network.",
                        "Extended Crafting maps Basic/Advanced/Elite/Ultimate normally; Re:Avaritia maps Sculk/Nether/End/Xtreme; AvaritiaNeo maps Xtreme only.")
                .define("TieredMode", false);
        builder.pop();
        SPEC = builder.build();
    }

    private EMAConfig() {
    }

    public static boolean tieredMode() {
        return TIERED_MODE.get();
    }
}
