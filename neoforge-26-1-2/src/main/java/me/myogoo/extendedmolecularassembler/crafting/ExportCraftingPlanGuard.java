package me.myogoo.extendedmolecularassembler.crafting;

import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.Map;

import appeng.api.networking.IGrid;
import appeng.api.networking.crafting.ICraftingPlan;
import appeng.api.stacks.AEKey;
import me.myogoo.extendedmolecularassembler.block.ExportMECraftingProviderTier;
import me.myogoo.extendedmolecularassembler.block.blockentity.ExportMECraftingProviderBlockEntity;
import me.myogoo.extendedmolecularassembler.lang.EMATranslationKey;
import me.myogoo.extendedmolecularassembler.pattern.ExtendedTableCraftingPattern;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

public final class ExportCraftingPlanGuard {
    private static long providerRevision;

    private ExportCraftingPlanGuard() {
    }

    public static long getProviderRevision() {
        return providerRevision;
    }

    public static void providersChanged() {
        providerRevision++;
    }

    public static Map<AEKey, ExportMECraftingProviderTier> getRequiredProviders(@Nullable ICraftingPlan plan) {
        if (plan == null) {
            return Map.of();
        }

        var providers = new LinkedHashMap<AEKey, ExportMECraftingProviderTier>();
        for (var entry : plan.patternTimes().entrySet()) {
            if (entry.getValue() <= 0 || !(entry.getKey() instanceof ExtendedTableCraftingPattern pattern)) {
                continue;
            }

            final ExportMECraftingProviderTier providerTier;
            try {
                providerTier = ExportMECraftingProviderTier.requiredFor(pattern.tableType(), pattern.tableTier());
            } catch (IllegalArgumentException ignored) {
                continue;
            }

            for (var output : pattern.getOutputs()) {
                providers.merge(output.what(), providerTier, ExportCraftingPlanGuard::higherProviderTier);
            }
        }

        return Map.copyOf(providers);
    }

    public static int getOnlineProviderMask(@Nullable IGrid grid) {
        if (grid == null) {
            return 0;
        }

        var onlineProviders = EnumSet.noneOf(ExportMECraftingProviderTier.class);
        for (var provider : grid.getActiveMachines(ExportMECraftingProviderBlockEntity.class)) {
            if (provider.isOnline()) {
                onlineProviders.add(provider.getTier());
            }
        }

        var mask = 0;
        for (var provider : onlineProviders) {
            mask |= 1 << provider.ordinal();
        }
        return mask;
    }

    public static Map<AEKey, ExportPlanEntryHighlight> getEntryHighlights(
            Map<AEKey, ExportMECraftingProviderTier> requiredProviders,
            int onlineProviderMask,
            boolean exportMode) {
        if (requiredProviders.isEmpty()) {
            return Map.of();
        }

        var highlights = new LinkedHashMap<AEKey, ExportPlanEntryHighlight>(requiredProviders.size());
        requiredProviders.forEach((key, provider) -> highlights.put(key,
                new ExportPlanEntryHighlight(provider,
                        exportMode && (onlineProviderMask & (1 << provider.ordinal())) == 0)));
        return Map.copyOf(highlights);
    }

    private static ExportMECraftingProviderTier higherProviderTier(
            ExportMECraftingProviderTier first,
            ExportMECraftingProviderTier second) {
        if (first.tier() != second.tier()) {
            return first.tier() > second.tier() ? first : second;
        }
        return first.ordinal() >= second.ordinal() ? first : second;
    }

    @Nullable
    public static Component getBlockReason(@Nullable IGrid grid, @Nullable ICraftingPlan plan,
            int onlineProviderMask, boolean exportMode) {
        if (!exportMode || grid == null || plan == null || plan.simulation()) {
            return null;
        }

        for (var patternDetails : plan.patternTimes().keySet()) {
            if (patternDetails instanceof ExtendedTableCraftingPattern pattern) {
                var reason = getBlockReason(onlineProviderMask, pattern);
                if (reason != null) {
                    return reason;
                }
            }
        }
        return null;
    }

    @Nullable
    private static Component getBlockReason(int onlineProviderMask, ExtendedTableCraftingPattern pattern) {
        final int tableTier = pattern.tableTier();
        final ExportMECraftingProviderTier providerTier;
        try {
            providerTier = ExportMECraftingProviderTier.requiredFor(pattern.tableType(), tableTier);
        } catch (IllegalArgumentException ignored) {
            return Component.translatable(
                    EMATranslationKey.TOOLTIP.EXPORT_MODE_UNSUPPORTED_TIER.key(),
                    ExportMECraftingProviderTier.tierName(tableTier), tableTier);
        }

        if ((onlineProviderMask & (1 << providerTier.ordinal())) != 0) {
            return null;
        }

        return Component.translatable(
                EMATranslationKey.TOOLTIP.EXPORT_MODE_MISSING_PROVIDER.key(),
                providerTier.displayName(), tableTier);
    }
}
