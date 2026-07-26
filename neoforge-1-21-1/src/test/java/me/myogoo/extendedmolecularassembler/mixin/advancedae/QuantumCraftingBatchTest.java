package me.myogoo.extendedmolecularassembler.mixin.advancedae;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class QuantumCraftingBatchTest {
    @Test
    void partialExtractionCompletesNoCrafts() {
        var extractions = List.of(new QuantumCraftingBatch.Extraction(8, 3));

        assertEquals(0, QuantumCraftingBatch.completedCrafts(4, 1, extractions));
    }

    @Test
    void missingExtractionCompletesNoCrafts() {
        assertEquals(0, QuantumCraftingBatch.completedCrafts(4, 2,
                List.of(new QuantumCraftingBatch.Extraction(8, 8))));
    }

    @Test
    void exactExtractionCompletesRequestedCrafts() {
        var extractions = List.of(
                new QuantumCraftingBatch.Extraction(8, 8),
                new QuantumCraftingBatch.Extraction(1, 1));

        assertEquals(4, QuantumCraftingBatch.completedCrafts(4, 2, extractions));
    }

    @Test
    void outputCapacityLimitsBatch() {
        assertEquals(3, QuantumCraftingBatch.maximumCrafts(8, crafts -> crafts <= 3));
    }
}
