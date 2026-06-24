package me.myogoo.extendedmolecularassembler.block.blockentity;

import appeng.api.implementations.IPowerChannelState;
import appeng.api.networking.IGridNodeListener;
import appeng.blockentity.grid.AENetworkedBlockEntity;
import me.myogoo.extendedmolecularassembler.block.TieredMECraftingProviderTier;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class TieredMECraftingProviderBlockEntity extends AENetworkedBlockEntity implements IPowerChannelState {
    private final TieredMECraftingProviderTier tier;
    private boolean online;

    public TieredMECraftingProviderBlockEntity(BlockEntityType<?> blockEntityType, BlockPos pos, BlockState blockState,
            TieredMECraftingProviderTier tier) {
        super(blockEntityType, pos, blockState);
        this.tier = tier;
        getMainNode().setIdlePowerUsage(0.0);
    }

    public TieredMECraftingProviderTier getTier() {
        return tier;
    }

    public int getProviderTier() {
        return tier.tier();
    }

    @Override
    public void onMainNodeStateChanged(IGridNodeListener.State reason) {
        var newOnline = getMainNode().isOnline();
        if (this.online != newOnline) {
            this.online = newOnline;
            this.markForUpdate();
        }
    }

    @Override
    public boolean isPowered() {
        return this.online;
    }

    @Override
    public boolean isActive() {
        return this.online;
    }

    public boolean isOnline() {
        return this.online;
    }
}
