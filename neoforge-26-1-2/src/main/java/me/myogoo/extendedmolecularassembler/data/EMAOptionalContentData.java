package me.myogoo.extendedmolecularassembler.data;

import me.myogoo.extendedmolecularassembler.ExtendedMolecularAssembler;
import net.minecraft.resources.Identifier;

import java.util.List;

final class EMAOptionalContentData {
    static final List<OptionalBlock> BLOCKS = List.of(
            block("ex_extended_molecular_assembler", "extendedae"),
            block("extended_assembler_matrix_pattern_core", "extendedae"),
            block("extended_assembler_matrix_crafting_core", "extendedae"),
            block("extended_assembler_matrix_pattern_uploader", "extendedae"),
            block("extended_assembler_matrix_pattern_core_plus", "extendedae"),
            block("extended_assembler_matrix_crafting_core_plus", "extendedae"),
            block("extended_quantum_crafter", "advanced_ae"));

    private EMAOptionalContentData() {
    }

    static boolean isOptionalBlock(Identifier id) {
        return BLOCKS.stream().anyMatch(block -> block.id().equals(id));
    }

    private static OptionalBlock block(String path, String... activeIntegrations) {
        return new OptionalBlock(
                ExtendedMolecularAssembler.makeId(path),
                List.of(activeIntegrations));
    }

    record OptionalBlock(Identifier id, List<String> activeIntegrations) {
    }
}
