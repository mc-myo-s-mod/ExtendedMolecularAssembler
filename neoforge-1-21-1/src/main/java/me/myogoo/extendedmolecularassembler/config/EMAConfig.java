package me.myogoo.extendedmolecularassembler.config;

import me.myogoo.extendedmolecularassembler.block.TieredMECraftingProviderTier;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.EnumMap;
import java.util.Map;

public final class EMAConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec COMMON;

    private static final PowerSettings EXTENDED_MOLECULAR_ASSEMBLER;
    private static final PowerSettings EX_EXTENDED_MOLECULAR_ASSEMBLER;
    private static final Map<TieredMECraftingProviderTier, PowerSettings> TIERED_ME_CRAFTING_PROVIDERS;
    private static final PowerSettings EXTENDED_ASSEMBLER_MATRIX_PATTERN_CORE;
    private static final PowerSettings EXTENDED_ASSEMBLER_MATRIX_CRAFTING_CORE;
    private static final PowerSettings EXTENDED_ASSEMBLER_MATRIX_PATTERN_UPLOADER;
    private static final PowerSettings EXTENDED_ASSEMBLER_MATRIX_PATTERN_CORE_PLUS;
    private static final PowerSettings EXTENDED_ASSEMBLER_MATRIX_CRAFTING_CORE_PLUS;
    private static final PowerSettings EXTENDED_QUANTUM_CRAFTER;
    private static final ModConfigSpec.BooleanValue TIERED_MODE;

    static {
        BUILDER.comment("Per-block AE power settings").push("blocks");

        EXTENDED_MOLECULAR_ASSEMBLER = defineBlock("extended_molecular_assembler",
                "Extended Molecular Assembler");
        EX_EXTENDED_MOLECULAR_ASSEMBLER = defineBlock("ex_extended_molecular_assembler",
                "EX Extended Molecular Assembler");

        var providerSettings = new EnumMap<TieredMECraftingProviderTier, PowerSettings>(
                TieredMECraftingProviderTier.class);
        for (var tier : TieredMECraftingProviderTier.values()) {
            providerSettings.put(tier, defineBlock(tier.blockId(), tier.displayName().getString()));
        }
        TIERED_ME_CRAFTING_PROVIDERS = Map.copyOf(providerSettings);

        EXTENDED_ASSEMBLER_MATRIX_PATTERN_CORE = defineBlock("extended_assembler_matrix_pattern_core",
                "Extended Assembler Matrix Pattern Core");
        EXTENDED_ASSEMBLER_MATRIX_CRAFTING_CORE = defineBlock("extended_assembler_matrix_crafting_core",
                "Extended Assembler Matrix Crafting Core");
        EXTENDED_ASSEMBLER_MATRIX_PATTERN_UPLOADER = defineBlock("extended_assembler_matrix_pattern_uploader",
                "Extended Assembler Matrix Pattern Uploader");
        EXTENDED_ASSEMBLER_MATRIX_PATTERN_CORE_PLUS = defineBlock("extended_assembler_matrix_pattern_core_plus",
                "Extended Assembler Matrix Pattern Core Plus");
        EXTENDED_ASSEMBLER_MATRIX_CRAFTING_CORE_PLUS = defineBlock("extended_assembler_matrix_crafting_core_plus",
                "Extended Assembler Matrix Crafting Core Plus");
        EXTENDED_QUANTUM_CRAFTER = defineBlock("extended_quantum_crafter", "Extended Quantum Crafter");

        BUILDER.pop();

        BUILDER.comment("Extended Molecular Assembler Settings").push("general");
        TIERED_MODE = BUILDER
                .comment(
                        "When enabled, extended table auto-crafting is accepted only if an online ME crafting provider for the exact encoded table exists in the same ME network.",
                        "Use the Extended Crafting providers for Extended Crafting tables, Re:Avaritia providers for Re:Avaritia Sculk/Nether/End tables, and the shared Xtreme provider for both Re:Avaritia Xtreme and AvaritiaNeo Xtreme recipes.")
                .define("TieredMode", false);
        BUILDER.pop();

        COMMON = BUILDER.build();
    }

    private EMAConfig() {
    }

    private static PowerSettings defineBlock(String blockId, String displayName) {
        BUILDER.comment(displayName + " power settings").push(blockId);
        var craftingPowerMultiplier = BUILDER
                .comment("Multiplier for AE power consumed while this block performs EMA-managed crafting. 1.0 keeps the default cost. Blocks without crafting work keep this setting for consistency.")
                .defineInRange("craftingPowerMultiplier", 1.0, 0.0, Double.MAX_VALUE);
        var passivePowerUsage = BUILDER
                .comment("Passive AE/t idle drain for this block's ME network node.")
                .defineInRange("passivePowerUsage", 0.0, 0.0, Double.MAX_VALUE);
        BUILDER.pop();
        return new PowerSettings(craftingPowerMultiplier, passivePowerUsage);
    }

    public static double extendedMolecularAssemblerCraftingPowerMultiplier() {
        return extendedMolecularAssemblerCraftingPowerMultiplier(false);
    }

    public static double extendedMolecularAssemblerCraftingPowerMultiplier(boolean exAssembler) {
        return (exAssembler ? EX_EXTENDED_MOLECULAR_ASSEMBLER : EXTENDED_MOLECULAR_ASSEMBLER)
                .craftingPowerMultiplier().get();
    }

    public static double extendedMolecularAssemblerPassivePowerUsage() {
        return extendedMolecularAssemblerPassivePowerUsage(false);
    }

    public static double extendedMolecularAssemblerPassivePowerUsage(boolean exAssembler) {
        return (exAssembler ? EX_EXTENDED_MOLECULAR_ASSEMBLER : EXTENDED_MOLECULAR_ASSEMBLER)
                .passivePowerUsage().get();
    }

    public static double tieredMECraftingProviderCraftingPowerMultiplier(TieredMECraftingProviderTier tier) {
        return TIERED_ME_CRAFTING_PROVIDERS.get(tier).craftingPowerMultiplier().get();
    }

    public static double tieredMECraftingProviderPassivePowerUsage(TieredMECraftingProviderTier tier) {
        return TIERED_ME_CRAFTING_PROVIDERS.get(tier).passivePowerUsage().get();
    }

    public static boolean tieredMode() {
        return TIERED_MODE.get();
    }

    public static double extendedAssemblerMatrixPatternCoreCraftingPowerMultiplier() {
        return extendedAssemblerMatrixPatternCoreCraftingPowerMultiplier(false);
    }

    public static double extendedAssemblerMatrixPatternCoreCraftingPowerMultiplier(boolean plus) {
        return (plus ? EXTENDED_ASSEMBLER_MATRIX_PATTERN_CORE_PLUS : EXTENDED_ASSEMBLER_MATRIX_PATTERN_CORE)
                .craftingPowerMultiplier().get();
    }

    public static double extendedAssemblerMatrixPatternCorePassivePowerUsage() {
        return extendedAssemblerMatrixPatternCorePassivePowerUsage(false);
    }

    public static double extendedAssemblerMatrixPatternCorePassivePowerUsage(boolean plus) {
        return (plus ? EXTENDED_ASSEMBLER_MATRIX_PATTERN_CORE_PLUS : EXTENDED_ASSEMBLER_MATRIX_PATTERN_CORE)
                .passivePowerUsage().get();
    }

    public static double extendedAssemblerMatrixCraftingCoreCraftingPowerMultiplier() {
        return extendedAssemblerMatrixCraftingCoreCraftingPowerMultiplier(false);
    }

    public static double extendedAssemblerMatrixCraftingCoreCraftingPowerMultiplier(boolean plus) {
        return (plus ? EXTENDED_ASSEMBLER_MATRIX_CRAFTING_CORE_PLUS : EXTENDED_ASSEMBLER_MATRIX_CRAFTING_CORE)
                .craftingPowerMultiplier().get();
    }

    public static double extendedAssemblerMatrixCraftingCorePassivePowerUsage() {
        return extendedAssemblerMatrixCraftingCorePassivePowerUsage(false);
    }

    public static double extendedAssemblerMatrixCraftingCorePassivePowerUsage(boolean plus) {
        return (plus ? EXTENDED_ASSEMBLER_MATRIX_CRAFTING_CORE_PLUS : EXTENDED_ASSEMBLER_MATRIX_CRAFTING_CORE)
                .passivePowerUsage().get();
    }

    public static double extendedAssemblerMatrixPatternUploaderCraftingPowerMultiplier() {
        return EXTENDED_ASSEMBLER_MATRIX_PATTERN_UPLOADER.craftingPowerMultiplier().get();
    }

    public static double extendedAssemblerMatrixPatternUploaderPassivePowerUsage() {
        return EXTENDED_ASSEMBLER_MATRIX_PATTERN_UPLOADER.passivePowerUsage().get();
    }

    public static double extendedQuantumCrafterCraftingPowerMultiplier() {
        return EXTENDED_QUANTUM_CRAFTER.craftingPowerMultiplier().get();
    }

    public static double extendedQuantumCrafterPassivePowerUsage() {
        return EXTENDED_QUANTUM_CRAFTER.passivePowerUsage().get();
    }

    private record PowerSettings(ModConfigSpec.DoubleValue craftingPowerMultiplier,
            ModConfigSpec.DoubleValue passivePowerUsage) {
    }
}
