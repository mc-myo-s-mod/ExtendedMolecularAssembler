package me.myogoo.extendedmolecularassembler.init;

import me.myogoo.extendedmolecularassembler.integration.AssemblerMatrixJobContext;
import me.myogoo.extendedmolecularassembler.integration.extendedae.EMAExtendedAEIntegration;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

/**
 * Forge 1.20.1 placeholder for optional integrations that are ported after the
 * base assembler runtime. Methods intentionally no-op until ExtendedAE /
 * AdvancedAE support is implemented for this loader/version.
 */
public final class EMAOptionalIntegrations {
    private static boolean extendedAERegistered = false;

    private EMAOptionalIntegrations() {
    }

    public static void registerDeferred() {
        if (EMAModPresence.isExtendedAELoaded()) {
            EMAExtendedAEIntegration.registerDeferred();
            extendedAERegistered = true;
        }
    }

    public static void registerRepresentativeItems() {
        if (extendedAERegistered) {
            EMAExtendedAEIntegration.registerRepresentativeItems();
        }
    }

    @Nullable
    public static AssemblerMatrixJobContext claimExtendedAEAssemblerMatrixJobContext() {
        return null;
    }

    public static ItemStack tryInsertIntoExtendedAEAssemblerMatrix(Level level, BlockPos pos, ItemStack stack) {
        return stack;
    }
}
