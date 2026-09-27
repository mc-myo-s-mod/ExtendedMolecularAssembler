package me.myogoo.extendedmolecularassembler.crafting;

public record AssemblerSpeedProfile(int speed, double acceleratorTax) {
    private static final AssemblerSpeedProfile[] STANDARD = {
            new AssemblerSpeedProfile(10, 1.0),
            new AssemblerSpeedProfile(13, 1.3),
            new AssemblerSpeedProfile(17, 1.7),
            new AssemblerSpeedProfile(20, 2.0),
            new AssemblerSpeedProfile(25, 2.5),
            new AssemblerSpeedProfile(50, 5.0)
    };
    private static final AssemblerSpeedProfile[] EXTENDED = {
            new AssemblerSpeedProfile(20, 1.0),
            new AssemblerSpeedProfile(26, 1.3),
            new AssemblerSpeedProfile(34, 1.7),
            new AssemblerSpeedProfile(40, 2.0),
            new AssemblerSpeedProfile(50, 2.5),
            new AssemblerSpeedProfile(100, 5.0)
    };

    public static AssemblerSpeedProfile forUpgrades(boolean extended, int cards) {
        var profiles = extended ? EXTENDED : STANDARD;
        return profiles[Math.max(0, Math.min(cards, profiles.length - 1))];
    }
}
