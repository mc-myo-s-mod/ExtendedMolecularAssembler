package me.myogoo.extendedmolecularassembler;

import java.util.List;

import me.myogoo.extendedmolecularassembler.crafting.AssemblerSpeedProfile;
import me.myogoo.extendedmolecularassembler.integration.advancedae.QuantumCraftingBatch;
import me.myogoo.extendedmolecularassembler.integration.advancedae.QuantumCraftingBatch.Extraction;
import me.myogoo.extendedmolecularassembler.integration.itemlist.RecipeBridgePathResolver;

public final class CommonCraftingChecks {
    public static void main(String[] args) {
        int[] speeds = {10, 13, 17, 20, 25, 50};
        for (int cards = -1; cards <= 6; cards++) {
            var normal = AssemblerSpeedProfile.forUpgrades(false, cards);
            var extended = AssemblerSpeedProfile.forUpgrades(true, cards);
            assert normal.speed() == speeds[Math.max(0, Math.min(cards, 5))];
            assert extended.speed() == normal.speed() * 2;
            assert extended.acceleratorTax() == normal.acceleratorTax();
        }

        for (int bound = 0; bound <= 128; bound++) {
            for (int capacity = 0; capacity <= 128; capacity++) {
                int limit = capacity;
                assert QuantumCraftingBatch.maximumCrafts(bound, count -> count <= limit)
                        == Math.min(bound, limit);
            }
        }
        assert QuantumCraftingBatch.maximumCrafts(-1, count -> true) == 0;
        assert QuantumCraftingBatch.maximumCrafts(Integer.MAX_VALUE, count -> count <= 42) == 42;
        assert QuantumCraftingBatch.completedCrafts(3, 2,
                List.of(new Extraction(6, 6), new Extraction(9, 9))) == 3;
        assert QuantumCraftingBatch.completedCrafts(3, 2,
                List.of(new Extraction(6, 6), new Extraction(9, 8))) == 0;
        assert QuantumCraftingBatch.completedCrafts(3, 2, List.of(new Extraction(6, 6))) == 0;
        assert QuantumCraftingBatch.completedCrafts(3, 1, List.of(new Extraction(0, 0))) == 0;

        var unwrapped = RecipeBridgePathResolver.unwrap("jei", "/extendedcrafting/test/recipe");
        assert unwrapped.namespace().equals("extendedcrafting");
        assert unwrapped.path().equals("test/recipe");
        assert RecipeBridgePathResolver.unwrap("minecraft", "iron_ingot").path().equals("iron_ingot");
        assert RecipeBridgePathResolver.unwrap("jei", "/missing/").namespace().equals("jei");
        System.out.println("Shared crafting checks passed.");
    }
}
