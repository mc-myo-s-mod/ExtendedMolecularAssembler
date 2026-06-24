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
                        "When enabled, extended table auto-crafting is accepted only if an online ME crafting provider for the exact encoded table exists in the same ME network.",
                        "Use the Extended Crafting providers for Extended Crafting tables, Re:Avaritia providers for Re:Avaritia Sculk/Nether/End tables, and the shared Xtreme provider for both Re:Avaritia Xtreme and AvaritiaNeo Xtreme recipes.")
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
