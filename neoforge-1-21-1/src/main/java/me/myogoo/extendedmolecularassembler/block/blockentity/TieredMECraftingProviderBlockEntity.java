package me.myogoo.extendedmolecularassembler.block.blockentity;

import appeng.api.implementations.IPowerChannelState;
import appeng.api.networking.GridFlags;
import appeng.api.networking.IGridNodeListener;
import me.myogoo.extendedmolecularassembler.block.TieredMECraftingProviderTier;
import me.myogoo.extendedmolecularassembler.config.EMAConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class TieredMECraftingProviderBlockEntity extends EMABaseProvider implements IPowerChannelState {
    private final TieredMECraftingProviderTier tier;
    private boolean clientSideOnline;

    public TieredMECraftingProviderBlockEntity(BlockEntityType<?> blockEntityType, BlockPos pos, BlockState blockState,
            TieredMECraftingProviderTier tier) {
        super(blockEntityType, pos, blockState);
        this.tier = tier;
        getMainNode()
                .setIdlePowerUsage(EMAConfig.tieredMECraftingProviderPassivePowerUsage(tier))
                .setFlags(GridFlags.REQUIRE_CHANNEL);
    }

    public TieredMECraftingProviderTier getTier() {
        return tier;
    }

    public int getProviderTier() {
        return tier.tier();
    }

    @Override
    public void onMainNodeStateChanged(IGridNodeListener.State reason) {
        updateVisualStateIfNeeded();
    }

    private void updateVisualStateIfNeeded() {
        if (updateClientSideState()) {
            this.markForUpdate();
        }
    }

    private boolean updateClientSideState() {
        if (this.isClientSide()) {
            return false;
        }

        var newOnline = getMainNode().isOnline();
        if (this.clientSideOnline == newOnline) {
            return false;
        }

        this.clientSideOnline = newOnline;
        return true;
    }

    @Override
    protected void saveVisualState(CompoundTag data) {
        super.saveVisualState(data);
        data.putBoolean("online", this.isOnline());
    }

    @Override
    protected void loadVisualState(CompoundTag data) {
        super.loadVisualState(data);
        this.clientSideOnline = data.getBoolean("online");
    }

    @Override
    protected boolean readFromStream(RegistryFriendlyByteBuf data) {
        var changed = super.readFromStream(data);
        var oldOnline = this.clientSideOnline;
        this.clientSideOnline = data.readBoolean();
        return changed || oldOnline != this.clientSideOnline;
    }

    @Override
    protected void writeToStream(RegistryFriendlyByteBuf data) {
        super.writeToStream(data);
        updateClientSideState();
        data.writeBoolean(this.clientSideOnline);
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
}
