package me.myogoo.extendedmolecularassembler.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class EMAConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec COMMON;

    private static final ModConfigSpec.DoubleValue EXTENDED_MOLECULAR_ASSEMBLER_CRAFTING_POWER_MULTIPLIER;
    private static final ModConfigSpec.DoubleValue EXTENDED_MOLECULAR_ASSEMBLER_PASSIVE_POWER_USAGE;
    private static final ModConfigSpec.BooleanValue TIERED_MODE;
    private static final ModConfigSpec.DoubleValue EXTENDED_ASSEMBLER_MATRIX_CRAFTING_CORE_CRAFTING_POWER_MULTIPLIER;
    private static final ModConfigSpec.DoubleValue EXTENDED_ASSEMBLER_MATRIX_CRAFTING_CORE_PASSIVE_POWER_USAGE;

    static {
        BUILDER.comment("Extended Molecular Assembler Settings").push("extendedMolecularAssembler");

        EXTENDED_MOLECULAR_ASSEMBLER_CRAFTING_POWER_MULTIPLIER = BUILDER
                .comment("Multiplier for AE power consumed while an Extended Molecular Assembler is crafting. 1.0 keeps the default AE2-style cost.")
                .defineInRange("craftingPowerMultiplier", 1.0, 0.0, Double.MAX_VALUE);
        EXTENDED_MOLECULAR_ASSEMBLER_PASSIVE_POWER_USAGE = BUILDER
                .comment("Passive AE/t idle drain for each Extended Molecular Assembler network node.")
                .defineInRange("passivePowerUsage", 0.0, 0.0, Double.MAX_VALUE);
        TIERED_MODE = BUILDER
                .comment(
                        "When enabled, extended table auto-crafting is accepted only if an online ME tiered crafting provider of the exact table tier exists in the same ME network.",
                        "Basic provider = tier 1, Advanced = tier 2, Elite = tier 3, Ultimate = tier 4.")
                .define("TieredMode", false);

        BUILDER.pop();

        BUILDER.comment("Extended Assembler Matrix Crafting Core Settings").push("extendedAssemblerMatrixCraftingCore");

        EXTENDED_ASSEMBLER_MATRIX_CRAFTING_CORE_CRAFTING_POWER_MULTIPLIER = BUILDER
                .comment("Multiplier for AE power consumed while an Extended Assembler Matrix Crafting Core is crafting extended patterns. 1.0 keeps the default cost.")
                .defineInRange("craftingPowerMultiplier", 1.0, 0.0, Double.MAX_VALUE);
        EXTENDED_ASSEMBLER_MATRIX_CRAFTING_CORE_PASSIVE_POWER_USAGE = BUILDER
                .comment("Passive AE/t idle drain for each Extended Assembler Matrix Crafting Core network node.")
                .defineInRange("passivePowerUsage", 0.0, 0.0, Double.MAX_VALUE);

        BUILDER.pop();
        COMMON = BUILDER.build();
    }

    private EMAConfig() {
    }

    public static double extendedMolecularAssemblerCraftingPowerMultiplier() {
        return EXTENDED_MOLECULAR_ASSEMBLER_CRAFTING_POWER_MULTIPLIER.get();
    }

    public static double extendedMolecularAssemblerPassivePowerUsage() {
        return EXTENDED_MOLECULAR_ASSEMBLER_PASSIVE_POWER_USAGE.get();
    }

    public static boolean tieredMode() {
        return TIERED_MODE.get();
    }

    public static double extendedAssemblerMatrixCraftingCoreCraftingPowerMultiplier() {
        return EXTENDED_ASSEMBLER_MATRIX_CRAFTING_CORE_CRAFTING_POWER_MULTIPLIER.get();
    }

    public static double extendedAssemblerMatrixCraftingCorePassivePowerUsage() {
        return EXTENDED_ASSEMBLER_MATRIX_CRAFTING_CORE_PASSIVE_POWER_USAGE.get();
    }
}
