package me.myogoo.extendedmolecularassembler.crafting;

import appeng.api.networking.IGrid;
import appeng.api.networking.crafting.ICraftingPlan;
import me.myogoo.extendedmolecularassembler.block.ExportMECraftingProviderTier;
import me.myogoo.extendedmolecularassembler.block.blockentity.ExportMECraftingProviderBlockEntity;
import me.myogoo.extendedmolecularassembler.config.EMAConfig;
import me.myogoo.extendedmolecularassembler.lang.EMATranslationKey;
import me.myogoo.extendedmolecularassembler.pattern.ExtendedTableCraftingPattern;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

public final class ExportCraftingPlanGuard {
    private ExportCraftingPlanGuard() {
    }

    @Nullable
    public static Component getBlockReason(@Nullable IGrid grid, @Nullable ICraftingPlan plan) {
        if (!EMAConfig.exportMode() || grid == null || plan == null || plan.simulation()) {
            return null;
        }

        for (var patternDetails : plan.patternTimes().keySet()) {
            if (patternDetails instanceof ExtendedTableCraftingPattern pattern) {
                var reason = getBlockReason(grid, pattern);
                if (reason != null) {
                    return reason;
                }
            }
        }
        return null;
    }

    @Nullable
    public static Component getBlockReason(IGrid grid, ExtendedTableCraftingPattern pattern) {
        final int tableTier = pattern.tableTier();
        final ExportMECraftingProviderTier providerTier;
        try {
            providerTier = ExportMECraftingProviderTier.requiredFor(pattern.tableType(), tableTier);
        } catch (IllegalArgumentException ignored) {
            return Component.translatable(
                    EMATranslationKey.TOOLTIP.EXPORT_MODE_UNSUPPORTED_TIER.key(),
                    ExportMECraftingProviderTier.tierName(tableTier), tableTier);
        }

        for (var provider : grid.getActiveMachines(ExportMECraftingProviderBlockEntity.class)) {
            if (provider.getTier().provides(pattern.tableType(), tableTier) && provider.isOnline()) {
                return null;
            }
        }

        return Component.translatable(
                EMATranslationKey.TOOLTIP.EXPORT_MODE_MISSING_PROVIDER.key(),
                providerTier.displayName(), tableTier);
    }
}
