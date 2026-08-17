package me.myogoo.extendedmolecularassembler.menu.crafting;

import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

public interface CraftConfirmExportPlanGate {
    @Nullable
    Component ema$getExportPlanBlockReason();

    void ema$setExportPlanBlockReason(@Nullable Component reason);

    default boolean ema$isExportPlanBlocked() {
        return ema$getExportPlanBlockReason() != null;
    }
}
