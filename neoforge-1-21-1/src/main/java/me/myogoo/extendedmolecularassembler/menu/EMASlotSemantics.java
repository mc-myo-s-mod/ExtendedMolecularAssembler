package me.myogoo.extendedmolecularassembler.menu;

import appeng.menu.SlotSemantic;
import appeng.menu.SlotSemantics;

public final class EMASlotSemantics {
    public static final SlotSemantic EXTENDED_PATTERN_CRAFTING_GRID =
            SlotSemantics.register("EXTENDED_PATTERN_CRAFTING_GRID", true);
    public static final SlotSemantic EXTENDED_PATTERN_CRAFTING_RESULT =
            SlotSemantics.register("EXTENDED_PATTERN_CRAFTING_RESULT", false);
    public static final SlotSemantic[] EXTENDED_MOLECULAR_ASSEMBLER_GRID = registerSlots(
            "EXTENDED_MOLECULAR_ASSEMBLER_GRID_", 8);
    public static final SlotSemantic[] EXTENDED_MOLECULAR_ASSEMBLER_OUTPUT = registerSlots(
            "EXTENDED_MOLECULAR_ASSEMBLER_OUTPUT_", 8);

    private EMASlotSemantics() {
    }

    private static SlotSemantic[] registerSlots(String prefix, int size) {
        var slots = new SlotSemantic[size];
        for (int i = 0; i < size; i++) {
            slots[i] = SlotSemantics.register(prefix + (i + 1), false);
        }
        return slots;
    }
}
