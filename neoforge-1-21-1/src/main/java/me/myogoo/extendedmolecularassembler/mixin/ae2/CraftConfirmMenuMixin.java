package me.myogoo.extendedmolecularassembler.mixin.ae2;

import java.util.Map;
import java.util.Objects;

import appeng.api.networking.IGrid;
import appeng.api.networking.security.IActionHost;
import appeng.api.networking.crafting.ICraftingPlan;
import appeng.api.stacks.AEKey;
import appeng.menu.me.crafting.CraftConfirmMenu;
import me.myogoo.extendedmolecularassembler.block.ExportMECraftingProviderTier;
import me.myogoo.extendedmolecularassembler.config.EMAConfig;
import me.myogoo.extendedmolecularassembler.crafting.ExportCraftingPlanGuard;
import me.myogoo.extendedmolecularassembler.crafting.ExportPlanEntryHighlight;
import me.myogoo.extendedmolecularassembler.menu.crafting.CraftConfirmExportPlanGate;
import me.myogoo.extendedmolecularassembler.network.clientbound.EMACraftConfirmPlanBlockPacket;
import me.myogoo.extendedmolecularassembler.network.clientbound.EMACraftConfirmPlanHighlightsPacket;
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
    @Unique
    private Map<AEKey, ExportPlanEntryHighlight> ema$exportPlanEntryHighlights = Map.of();
    @Unique
    private Map<AEKey, ExportPlanEntryHighlight> ema$lastSyncedExportPlanEntryHighlights = Map.of();
    @Unique
    @Nullable
    private ICraftingPlan ema$lastExportHighlightPlan;
    @Unique
    private Map<AEKey, ExportMECraftingProviderTier> ema$requiredExportPlanProviders = Map.of();
    @Unique
    private int ema$lastOnlineExportProviderMask = Integer.MIN_VALUE;
    @Unique
    private boolean ema$lastExportMode;

    @Override
    @Nullable
    public Component ema$getExportPlanBlockReason() {
        return ema$exportPlanBlockReason;
    }

    @Override
    public void ema$setExportPlanBlockReason(@Nullable Component reason) {
        this.ema$exportPlanBlockReason = reason;
    }

    @Override
    public Map<AEKey, ExportPlanEntryHighlight> ema$getExportPlanEntryHighlights() {
        return ema$exportPlanEntryHighlights;
    }

    @Override
    public void ema$setExportPlanEntryHighlights(Map<AEKey, ExportPlanEntryHighlight> highlights) {
        this.ema$exportPlanEntryHighlights = Map.copyOf(highlights);
    }

    @Inject(method = "broadcastChanges", at = @At("TAIL"), require = 1)
    private void ema$syncExportPlanState(CallbackInfo ci) {
        var menu = (CraftConfirmMenu) (Object) this;
        if (menu.isClientSide()) {
            return;
        }

        var grid = ema$getGrid(menu);
        var exportMode = EMAConfig.exportMode();
        var onlineProviderMask = exportMode ? ExportCraftingPlanGuard.getOnlineProviderMask(grid) : 0;
        var reason = ExportCraftingPlanGuard.getBlockReason(grid, this.result, onlineProviderMask, exportMode);
        ema$setExportPlanBlockReason(reason);
        ema$syncExportPlanBlockReason(menu);

        ema$refreshExportPlanEntryHighlights(menu, onlineProviderMask, exportMode);
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

        var grid = ema$getGrid(menu);
        var exportMode = EMAConfig.exportMode();
        var onlineProviderMask = exportMode ? ExportCraftingPlanGuard.getOnlineProviderMask(grid) : 0;
        var reason = ExportCraftingPlanGuard.getBlockReason(grid, this.result, onlineProviderMask, exportMode);
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
    private void ema$refreshExportPlanEntryHighlights(CraftConfirmMenu menu, int onlineProviderMask,
            boolean exportMode) {
        if (this.result != this.ema$lastExportHighlightPlan) {
            this.ema$lastExportHighlightPlan = this.result;
            this.ema$requiredExportPlanProviders = ExportCraftingPlanGuard.getRequiredProviders(this.result);
            this.ema$lastOnlineExportProviderMask = Integer.MIN_VALUE;
        }

        if (onlineProviderMask == this.ema$lastOnlineExportProviderMask
                && exportMode == this.ema$lastExportMode) {
            return;
        }
        this.ema$lastOnlineExportProviderMask = onlineProviderMask;
        this.ema$lastExportMode = exportMode;
        ema$setExportPlanEntryHighlights(ExportCraftingPlanGuard.getEntryHighlights(
                this.ema$requiredExportPlanProviders,
                onlineProviderMask,
                exportMode));
        ema$syncExportPlanEntryHighlights(menu);
    }

    @Unique
    private void ema$syncExportPlanEntryHighlights(CraftConfirmMenu menu) {
        if (Objects.equals(this.ema$exportPlanEntryHighlights,
                this.ema$lastSyncedExportPlanEntryHighlights)) {
            return;
        }
        this.ema$lastSyncedExportPlanEntryHighlights =
                this.ema$exportPlanEntryHighlights;

        if (menu.getPlayer() instanceof ServerPlayer serverPlayer) {
            PacketDistributor.sendToPlayer(serverPlayer,
                    new EMACraftConfirmPlanHighlightsPacket(
                            menu.containerId,
                            this.ema$exportPlanEntryHighlights));
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
