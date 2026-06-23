package me.myogoo.extendedmolecularassembler.init;

import appeng.api.crafting.IPatternDetails;
import appeng.api.crafting.IPatternDetailsDecoder;
import appeng.api.crafting.PatternDetailsHelper;
import appeng.api.stacks.AEItemKey;
import me.myogoo.extendedmolecularassembler.pattern.EncodedExtendedCraftingPattern;
import me.myogoo.extendedmolecularassembler.pattern.ExtendedTableCraftingPattern;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public final class EMAModIntegration {
    private EMAModIntegration() {
    }

    public static void initialize() {
        PatternDetailsHelper.registerDecoder(new IPatternDetailsDecoder() {
            @Override
            public boolean isEncodedPattern(ItemStack stack) {
                return EncodedExtendedCraftingPattern.get(stack) != null;
            }

            @Override
            public IPatternDetails decodePattern(AEItemKey what, Level level) {
                return new ExtendedTableCraftingPattern(what, level);
            }

            @Override
            public IPatternDetails decodePattern(ItemStack stack, Level level, boolean tryRecovery) {
                return new ExtendedTableCraftingPattern(AEItemKey.of(stack), level);
            }
        });
    }
}
