package me.myogoo.extendedmolecularassembler.data;

import me.myogoo.extendedmolecularassembler.ExtendedMolecularAssembler;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

final class EMAOptionalContentData {
    static final List<ResourceLocation> BLOCKS = List.of(
            "ex_extended_molecular_assembler",
            "ex_epic_molecular_assembler",
            "ex_legendary_molecular_assembler",
            "extended_assembler_matrix_pattern_core",
            "extended_assembler_matrix_crafting_core",
            "extended_assembler_matrix_pattern_uploader",
            "extended_assembler_matrix_pattern_core_plus",
            "extended_assembler_matrix_crafting_core_plus",
            "epic_assembler_matrix_crafting_core",
            "epic_assembler_matrix_pattern_core",
            "legendary_assembler_matrix_crafting_core",
            "legendary_assembler_matrix_pattern_core",
            "epic_assembler_matrix_crafting_core_plus",
            "epic_assembler_matrix_pattern_core_plus",
            "legendary_assembler_matrix_crafting_core_plus",
            "legendary_assembler_matrix_pattern_core_plus",
            "extended_quantum_crafter").stream().map(ExtendedMolecularAssembler::makeId).toList();

    private EMAOptionalContentData() {
    }

    static boolean isOptionalBlock(ResourceLocation id) {
        return BLOCKS.contains(id);
    }
}
