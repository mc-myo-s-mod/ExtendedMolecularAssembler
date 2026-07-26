package me.myogoo.extendedmolecularassembler.integration.advancedae;

import java.util.List;
import java.util.function.IntPredicate;

// Mixin methods execute on AdvancedAE's target class, so this helper must remain public.
public final class QuantumCraftingBatch {
    private QuantumCraftingBatch() {
    }

    public static int completedCrafts(int requestedCrafts, int expectedInputs, List<Extraction> extractions) {
        if (requestedCrafts <= 0 || extractions.size() != expectedInputs) {
            return 0;
        }
        for (var extraction : extractions) {
            if (extraction.requested() <= 0 || extraction.extracted() != extraction.requested()) {
                return 0;
            }
        }
        return requestedCrafts;
    }

    public static int maximumCrafts(int upperBound, IntPredicate canStore) {
        var low = 0;
        var high = Math.max(0, upperBound);
        while (low < high) {
            var middle = low + (high - low + 1) / 2;
            if (canStore.test(middle)) {
                low = middle;
            } else {
                high = middle - 1;
            }
        }
        return low;
    }

    public record Extraction(long requested, long extracted) {
    }
}
