package me.myogoo.extendedmolecularassembler.block.blockentity;

import appeng.blockentity.grid.AENetworkBlockEntity;
import appeng.capabilities.Capabilities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class EMABaseProvider extends AENetworkBlockEntity {
    protected EMABaseProvider(BlockEntityType<?> blockEntityType, BlockPos pos, BlockState blockState) {
        super(blockEntityType, pos, blockState);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction facing) {
        if (capability == Capabilities.IN_WORLD_GRID_NODE_HOST) {
            return LazyOptional.of(() -> this).cast();
        }
        return super.getCapability(capability, facing);
    }
}
