package me.myogoo.extendedmolecularassembler.mixin.ae2;

import java.util.Objects;

import appeng.api.networking.IGrid;
import appeng.api.networking.security.IActionHost;
import appeng.api.networking.crafting.ICraftingPlan;
import appeng.menu.me.crafting.CraftConfirmMenu;
import me.myogoo.extendedmolecularassembler.crafting.ExportCraftingPlanGuard;
import me.myogoo.extendedmolecularassembler.menu.crafting.CraftConfirmExportPlanGate;
import me.myogoo.extendedmolecularassembler.network.clientbound.EMACraftConfirmPlanBlockPacket;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CraftConfirmMenu.class)
public abstract class CraftConfirmMenuMixin implements CraftConfirmExportPlanGate {
    @Shadow
    @Nullable
    private ICraftingPlan result;

    @Unique
    @Nullable
    private Component ema$exportPlanBlockReason;
    @Unique
    @Nullable
    private Component ema$lastSyncedExportPlanBlockReason;

    @Override
    @Nullable
    public Component ema$getExportPlanBlockReason() {
        return ema$exportPlanBlockReason;
    }

    @Override
    public void ema$setExportPlanBlockReason(@Nullable Component reason) {
        this.ema$exportPlanBlockReason = reason;
    }

    @Inject(method = "broadcastChanges", at = @At("TAIL"), require = 1)
    private void ema$syncExportPlanBlockReason(CallbackInfo ci) {
        var menu = (CraftConfirmMenu) (Object) this;
        if (menu.isClientSide()) {
            return;
        }

        var reason = ExportCraftingPlanGuard.getBlockReason(ema$getGrid(menu), this.result);
        ema$setExportPlanBlockReason(reason);
        ema$syncExportPlanBlockReason(menu);
    }

    @Inject(
            method = "startJob",
            at = @At(
                    value = "INVOKE",
                    target = "Lappeng/api/networking/crafting/ICraftingService;submitJob(Lappeng/api/networking/crafting/ICraftingPlan;Lappeng/api/networking/crafting/ICraftingRequester;Lappeng/api/networking/crafting/ICraftingCPU;ZLappeng/api/networking/security/IActionSource;)Lappeng/api/networking/crafting/ICraftingSubmitResult;"),
            cancellable = true,
            require = 1)
    private void ema$blockExportPlanStart(CallbackInfo ci) {
        var menu = (CraftConfirmMenu) (Object) this;
        if (menu.isClientSide()) {
            return;
        }

        var reason = ExportCraftingPlanGuard.getBlockReason(ema$getGrid(menu), this.result);
        if (reason == null) {
            ema$setExportPlanBlockReason(null);
            ema$syncExportPlanBlockReason(menu);
            return;
        }

        ema$setExportPlanBlockReason(reason);
        ema$syncExportPlanBlockReason(menu);
        menu.setAutoStart(false);
        menu.getPlayer().sendSystemMessage(reason);
        ci.cancel();
    }

    @Unique
    private void ema$syncExportPlanBlockReason(CraftConfirmMenu menu) {
        if (Objects.equals(this.ema$exportPlanBlockReason,
                this.ema$lastSyncedExportPlanBlockReason)) {
            return;
        }
        this.ema$lastSyncedExportPlanBlockReason =
                this.ema$exportPlanBlockReason;

        if (menu.getPlayer() instanceof ServerPlayer serverPlayer) {
            PacketDistributor.sendToPlayer(serverPlayer,
                    new EMACraftConfirmPlanBlockPacket(
                            menu.containerId,
                            this.ema$exportPlanBlockReason));
        }
    }

    @Unique
    @Nullable
    private static IGrid ema$getGrid(CraftConfirmMenu menu) {
        if (!(menu.getTarget() instanceof IActionHost host)) {
            return null;
        }
        var node = host.getActionableNode();
        return node == null ? null : node.getGrid();
    }
}
