package me.myogoo.extendedmolecularassembler.crafting;

import java.util.Arrays;

public final class PatternGridLayout {
    private PatternGridLayout() {
    }

    public static int[] centeredSlots(int side, int targetSide) {
        if (side <= 0 || targetSide < side) {
            throw new IllegalArgumentException("Pattern must fit inside the machine grid");
        }
        var result = new int[side * side];
        var offset = Math.floorDiv(targetSide - side, 2);
        for (int slot = 0; slot < result.length; slot++) {
            result[slot] = (slot % side + offset) + (slot / side + offset) * targetSide;
        }
        return result;
    }

    public static int[] inverseSlots(int[] patternSlots, int machineSize) {
        var result = new int[machineSize];
        Arrays.fill(result, -1);
        for (int slot = 0; slot < patternSlots.length; slot++) {
            result[patternSlots[slot]] = slot;
        }
        return result;
    }
}
