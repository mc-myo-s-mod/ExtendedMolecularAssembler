package me.myogoo.extendedmolecularassembler.crafting;

import java.util.Objects;

import me.myogoo.extendedmolecularassembler.block.ExportMECraftingProviderTier;

public record ExportPlanEntryHighlight(ExportMECraftingProviderTier provider, boolean missingProvider) {
    public ExportPlanEntryHighlight {
        Objects.requireNonNull(provider, "provider");
    }
}
