package me.myogoo.extendedmolecularassembler.block.blockentity;

import appeng.api.implementations.IPowerChannelState;
import appeng.api.networking.GridFlags;
import appeng.api.networking.IGridNodeListener;
import appeng.api.util.AEColor;
import appeng.blockentity.networking.CableBusBlockEntity;
import me.myogoo.extendedmolecularassembler.block.ExportMECraftingProviderTier;
import me.myogoo.extendedmolecularassembler.config.EMAConfig;
import me.myogoo.extendedmolecularassembler.crafting.ExportCraftingPlanGuard;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class ExportMECraftingProviderBlockEntity extends EMABaseProvider implements IPowerChannelState {
    private final ExportMECraftingProviderTier tier;
    private boolean clientSideOnline;
    private AEColor clientSideCableColor = AEColor.TRANSPARENT;

    public ExportMECraftingProviderBlockEntity(BlockEntityType<?> blockEntityType, BlockPos pos, BlockState blockState,
            ExportMECraftingProviderTier tier) {
        super(blockEntityType, pos, blockState);
        this.tier = tier;
        getMainNode()
                .setIdlePowerUsage(EMAConfig.exportMECraftingProviderIdlePowerUsage(tier))
                .setFlags(GridFlags.REQUIRE_CHANNEL);
    }

    public ExportMECraftingProviderTier getTier() {
        return tier;
    }

    public int getProviderTier() {
        return tier.tier();
    }

    @Override
    public void onMainNodeStateChanged(IGridNodeListener.State reason) {
        if (!this.isClientSide()) {
            ExportCraftingPlanGuard.providersChanged();
        }
        updateVisualStateIfNeeded();
    }

    @Override
    public void setRemoved() {
        if (!this.isClientSide()) {
            ExportCraftingPlanGuard.providersChanged();
        }
        super.setRemoved();
    }

    public void updateVisualStateIfNeeded() {
        if (updateClientSideState()) {
            this.markForUpdate();
        }
    }

    private boolean updateClientSideState() {
        if (this.isClientSide()) {
            return false;
        }

        var newOnline = getMainNode().isOnline();
        var newCableColor = findAdjacentCableColor();
        if (this.clientSideOnline == newOnline && this.clientSideCableColor == newCableColor) {
            return false;
        }

        this.clientSideOnline = newOnline;
        this.clientSideCableColor = newCableColor;
        return true;
    }

    public AEColor getCableColor() {
        return this.isClientSide() ? this.clientSideCableColor : findAdjacentCableColor();
    }

    private AEColor findAdjacentCableColor() {
        var level = getLevel();
        if (level == null) {
            return AEColor.TRANSPARENT;
        }

        for (var direction : Direction.values()) {
            if (level.getBlockEntity(this.worldPosition.relative(direction)) instanceof CableBusBlockEntity cableBus) {
                return cableBus.getColor();
            }
        }

        return AEColor.TRANSPARENT;
    }

    @Override
    protected void saveVisualState(CompoundTag data) {
        super.saveVisualState(data);
        data.putBoolean("online", this.isOnline());
        data.putString("cableColor", this.getCableColor().name());
    }

    @Override
    protected void loadVisualState(CompoundTag data) {
        super.loadVisualState(data);
        this.clientSideOnline = data.getBoolean("online");
        this.clientSideCableColor = parseCableColor(data.getString("cableColor"));
    }

    @Override
    protected boolean readFromStream(FriendlyByteBuf data) {
        var changed = super.readFromStream(data);
        var oldOnline = this.clientSideOnline;
        var oldCableColor = this.clientSideCableColor;
        this.clientSideOnline = data.readBoolean();
        this.clientSideCableColor = data.readEnum(AEColor.class);
        return changed || oldOnline != this.clientSideOnline || oldCableColor != this.clientSideCableColor;
    }

    @Override
    protected void writeToStream(FriendlyByteBuf data) {
        super.writeToStream(data);
        updateClientSideState();
        data.writeBoolean(this.clientSideOnline);
        data.writeEnum(this.clientSideCableColor);
    }

    @Override
    public boolean isPowered() {
        return this.isOnline();
    }

    @Override
    public boolean isActive() {
        return this.isOnline();
    }

    public boolean isOnline() {
        return this.isClientSide() ? this.clientSideOnline : getMainNode().isOnline();
    }

    private static AEColor parseCableColor(String name) {
        if (name == null || name.isBlank()) {
            return AEColor.TRANSPARENT;
        }

        try {
            return AEColor.valueOf(name);
        } catch (IllegalArgumentException ignored) {
            return AEColor.TRANSPARENT;
        }
    }
}
