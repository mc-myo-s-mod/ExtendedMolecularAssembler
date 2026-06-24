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
                        "When enabled, extended table auto-crafting is accepted only if an online ME tiered crafting provider of the exact table tier exists in the same ME network.",
                        "Basic provider = tier 1, Advanced = tier 2, Elite = tier 3, Ultimate = tier 4.")
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
