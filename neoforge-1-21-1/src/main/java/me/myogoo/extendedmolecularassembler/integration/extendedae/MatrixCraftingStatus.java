package me.myogoo.extendedmolecularassembler.integration.extendedae;

public record MatrixCraftingStatus(int availableParallelCrafters, int totalParallelCrafters,
        int totalPatternSlots, int speed, int maxSpeed) {
    public static final int MAX_SPEED = 5;
    public static final MatrixCraftingStatus EMPTY = new MatrixCraftingStatus(0, 0, 0, 0, MAX_SPEED);
}
