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
import me.myogoo.extendedmolecularassembler.init.EMANetwork;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = CraftConfirmMenu.class, remap = false)
public abstract class CraftConfirmMenuMixin implements CraftConfirmExportPlanGate {
    @Shadow
    @Nullable
    private ICraftingPlan result;

    @Unique
    @Nullable
    private Component ema$exportPlanBlockReason;
    @Unique
    @Nullable
    private Component ema$sentBlockReason;
    @Unique
    private Map<AEKey, ExportPlanEntryHighlight> ema$exportPlanEntryHighlights = Map.of();
    @Unique
    private Map<AEKey, ExportPlanEntryHighlight> ema$sentHighlights = Map.of();
    @Unique
    @Nullable
    private ICraftingPlan ema$cachedPlan;
    @Unique
    private Map<AEKey, ExportMECraftingProviderTier> ema$requiredProviders = Map.of();
    @Unique
    @Nullable
    private IGrid ema$providerGrid;
    @Unique
    private long ema$providerRevision = Long.MIN_VALUE;
    @Unique
    private int ema$providerMask;
    @Unique
    private boolean ema$cachedExportMode;
    @Unique
    private boolean ema$stateCached;

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

    @Inject(method = "broadcastChanges", at = @At("TAIL"), require = 1, remap = true)
    private void ema$syncExportPlanState(CallbackInfo ci) {
        var menu = (CraftConfirmMenu) (Object) this;
        if (menu.isClientSide()) {
            return;
        }

        ema$refreshState(menu, false);
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

        var reason = ema$refreshState(menu, true);
        if (reason == null) {
            return;
        }

        menu.setAutoStart(false);
        menu.getPlayer().sendSystemMessage(reason);
        ci.cancel();
    }

    @Unique
    @Nullable
    private Component ema$refreshState(CraftConfirmMenu menu, boolean forceProviderScan) {
        var exportMode = EMAConfig.exportMode();
        var planChanged = this.result != this.ema$cachedPlan;
        if (planChanged) {
            this.ema$cachedPlan = this.result;
            this.ema$requiredProviders = ExportCraftingPlanGuard.getRequiredProviders(this.result);
        }

        IGrid grid = null;
        if (exportMode && menu.getTarget() instanceof IActionHost host) {
            var node = host.getActionableNode();
            if (node != null) {
                grid = node.getGrid();
            }
        }

        var providerRevision = ExportCraftingPlanGuard.getProviderRevision();
        var providersChanged = forceProviderScan
                || grid != this.ema$providerGrid
                || providerRevision != this.ema$providerRevision;
        if (providersChanged) {
            this.ema$providerGrid = grid;
            this.ema$providerRevision = providerRevision;
            this.ema$providerMask = exportMode ? ExportCraftingPlanGuard.getOnlineProviderMask(grid) : 0;
        }

        if (!this.ema$stateCached
                || planChanged
                || providersChanged
                || exportMode != this.ema$cachedExportMode) {
            this.ema$stateCached = true;
            this.ema$cachedExportMode = exportMode;
            ema$setExportPlanBlockReason(ExportCraftingPlanGuard.getBlockReason(
                    grid, this.result, this.ema$providerMask, exportMode));
            ema$setExportPlanEntryHighlights(ExportCraftingPlanGuard.getEntryHighlights(
                    this.ema$requiredProviders, this.ema$providerMask, exportMode));
        }

        ema$syncBlockReason(menu);
        ema$syncHighlights(menu);
        return this.ema$exportPlanBlockReason;
    }

    @Unique
    private void ema$syncBlockReason(CraftConfirmMenu menu) {
        if (Objects.equals(this.ema$exportPlanBlockReason, this.ema$sentBlockReason)) {
            return;
        }
        this.ema$sentBlockReason = this.ema$exportPlanBlockReason;

        if (menu.getPlayer() instanceof ServerPlayer serverPlayer) {
            EMANetwork.sendToClient(serverPlayer,
                    new EMACraftConfirmPlanBlockPacket(menu.containerId, this.ema$exportPlanBlockReason));
        }
    }

    @Unique
    private void ema$syncHighlights(CraftConfirmMenu menu) {
        if (Objects.equals(this.ema$exportPlanEntryHighlights, this.ema$sentHighlights)) {
            return;
        }
        this.ema$sentHighlights = this.ema$exportPlanEntryHighlights;

        if (menu.getPlayer() instanceof ServerPlayer serverPlayer) {
            EMANetwork.sendToClient(serverPlayer,
                    new EMACraftConfirmPlanHighlightsPacket(menu.containerId, this.ema$exportPlanEntryHighlights));
        }
    }
}
