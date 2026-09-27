package me.myogoo.extendedmolecularassembler.menu.crafting;

import java.util.Map;

import appeng.api.stacks.AEKey;
import me.myogoo.extendedmolecularassembler.crafting.ExportPlanEntryHighlight;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

public interface CraftConfirmExportPlanGate {
    @Nullable
    Component ema$getExportPlanBlockReason();

    void ema$setExportPlanBlockReason(@Nullable Component reason);

    Map<AEKey, ExportPlanEntryHighlight> ema$getExportPlanEntryHighlights();

    void ema$setExportPlanEntryHighlights(Map<AEKey, ExportPlanEntryHighlight> highlights);

    default boolean ema$isExportPlanBlocked() {
        return ema$getExportPlanBlockReason() != null;
    }
}
