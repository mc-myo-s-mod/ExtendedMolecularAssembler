package me.myogoo.extendedmolecularassembler.mixin.ae2;

import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.me.crafting.CraftConfirmScreen;
import appeng.client.gui.style.ScreenStyle;
import appeng.menu.me.crafting.CraftConfirmMenu;
import me.myogoo.extendedmolecularassembler.menu.crafting.CraftConfirmExportPlanGate;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CraftConfirmScreen.class)
public abstract class CraftConfirmScreenMixin extends AEBaseScreen<CraftConfirmMenu> {
    @Shadow
    @Final
    private Button start;

    protected CraftConfirmScreenMixin(CraftConfirmMenu menu, Inventory playerInventory, Component title,
            ScreenStyle style) {
        super(menu, playerInventory, title, style);
    }

    @Inject(method = "updateBeforeRender", at = @At("TAIL"), require = 1)
    private void ema$disableStartForExportPlan(CallbackInfo ci) {
        var screen = (CraftConfirmScreen) (Object) this;
        if (screen.getMenu() instanceof CraftConfirmExportPlanGate gate) {
            var reason = gate.ema$getExportPlanBlockReason();
            if (reason != null) {
                this.start.active = false;
                this.setTextContent("cpu_status", reason.copy().withStyle(ChatFormatting.RED));
            }
        }
    }

    @Inject(method = "start", at = @At("HEAD"), cancellable = true, require = 1)
    private void ema$blockStartForExportPlan(CallbackInfo ci) {
        var screen = (CraftConfirmScreen) (Object) this;
        if (screen.getMenu() instanceof CraftConfirmExportPlanGate gate
                && gate.ema$isExportPlanBlocked()) {
            ci.cancel();
        }
    }
}
