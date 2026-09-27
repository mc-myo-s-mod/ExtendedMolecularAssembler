package me.myogoo.extendedmolecularassembler.mixin.ae2;

import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.me.crafting.AbstractTableRenderer;
import appeng.menu.me.crafting.CraftingPlanSummaryEntry;
import me.myogoo.extendedmolecularassembler.menu.crafting.CraftConfirmExportPlanGate;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = AbstractTableRenderer.class, remap = false)
public abstract class AbstractTableRendererMixin {
    @Shadow
    @Final
    protected AEBaseScreen<?> screen;

    @Inject(method = "getEntryBackgroundColor", at = @At("RETURN"), cancellable = true, require = 1)
    private void ema$getExportPlanEntryBackgroundColor(Object entry, CallbackInfoReturnable<Integer> cir) {
        if (cir.getReturnValue() != 0
                || !(entry instanceof CraftingPlanSummaryEntry planEntry)
                || planEntry.getCraftAmount() <= 0
                || !(this.screen.getMenu() instanceof CraftConfirmExportPlanGate gate)) {
            return;
        }

        var highlight = gate.ema$getExportPlanEntryHighlights().get(planEntry.getWhat());
        if (highlight != null) {
            cir.setReturnValue(highlight.provider().planHighlightColor(highlight.missingProvider()));
        }
    }
}
