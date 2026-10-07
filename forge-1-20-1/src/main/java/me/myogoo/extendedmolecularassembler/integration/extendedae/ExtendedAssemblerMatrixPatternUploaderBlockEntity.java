package me.myogoo.extendedmolecularassembler.integration.extendedae;

import com.glodblock.github.extendedae.common.me.matrix.ClusterAssemblerMatrix;
import com.glodblock.github.extendedae.common.tileentities.matrix.TileAssemblerMatrixFunction;
import me.myogoo.extendedmolecularassembler.config.EMAConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ExtendedAssemblerMatrixPatternUploaderBlockEntity extends TileAssemblerMatrixFunction {
    private final IItemHandler uploadHandler = new UploadHandler();
    private LazyOptional<IItemHandler> uploadHandlerLazy = LazyOptional.of(() -> this.uploadHandler);

    public ExtendedAssemblerMatrixPatternUploaderBlockEntity(BlockEntityType<?> type, BlockPos pos,
            BlockState blockState) {
        super(type, pos, blockState);
        this.getMainNode().setIdlePowerUsage(EMAConfig.extendedAssemblerMatrixPatternUploaderIdlePowerUsage());
    }

    @Override
    public void add(ClusterAssemblerMatrix cluster) {
        // Function block only. Uploaded patterns are routed directly to Extended Pattern Core inventories.
    }

    @Nullable
    public IItemHandler getPatternInv(Direction ignored) {
        return this.uploadHandler;
    }

    private ItemStack upload(ItemStack stack, boolean simulate) {
        var decoded = ExtendedAssemblerMatrixPatternUploadUtil.decodeExtendedPattern(this.getLevel(), stack);
        if (decoded == null) {
            return stack;
        }

        var handlers = new ArrayList<IItemHandler>();
        for (var core : this.findTargets()) {
            var handler = core.getPatternInv(null);
            if (handler == null) {
                continue;
            }
            if (ExtendedAssemblerMatrixPatternUploadUtil.itemHandlerContainsPattern(handler, stack)) {
                return stack;
            }
            handlers.add(handler);
        }

        var remainder = stack.copy();
        for (var handler : handlers) {
            for (int slot = 0; slot < handler.getSlots() && !remainder.isEmpty(); slot++) {
                remainder = handler.insertItem(slot, remainder, simulate);
            }
            if (remainder.isEmpty()) {
                return ItemStack.EMPTY;
            }
        }
        return remainder;
    }

    private List<ExtendedAssemblerMatrixPatternCoreBlockEntity> findTargets() {
        var targets = new ArrayList<ExtendedAssemblerMatrixPatternCoreBlockEntity>();
        if (this.cluster != null && !this.cluster.isDestroyed()) {
            var iterator = this.cluster.getBlockEntities();
            while (iterator.hasNext()) {
                if (iterator.next() instanceof ExtendedAssemblerMatrixPatternCoreBlockEntity core) {
                    targets.add(core);
                }
            }
        }

        if (targets.isEmpty()) {
            var level = this.getLevel();
            if (level != null) {
                for (var direction : Direction.values()) {
                    BlockEntity blockEntity = level.getBlockEntity(this.worldPosition.relative(direction));
                    if (blockEntity instanceof ExtendedAssemblerMatrixPatternCoreBlockEntity core) {
                        targets.add(core);
                    }
                }
            }
        }
        return targets;
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing) {
        if (capability == ForgeCapabilities.ITEM_HANDLER) {
            return this.uploadHandlerLazy.cast();
        }
        return super.getCapability(capability, facing);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        this.uploadHandlerLazy.invalidate();
    }

    @Override
    public void reviveCaps() {
        super.reviveCaps();
        this.uploadHandlerLazy = LazyOptional.of(() -> this.uploadHandler);
    }

    private final class UploadHandler implements IItemHandler {
        @Override
        public int getSlots() {
            return 1;
        }

        @Override
        public ItemStack getStackInSlot(int slot) {
            return ItemStack.EMPTY;
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            if (slot != 0 || stack.isEmpty()) {
                return stack;
            }
            return ExtendedAssemblerMatrixPatternUploaderBlockEntity.this.upload(stack, simulate);
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            return ItemStack.EMPTY;
        }

        @Override
        public int getSlotLimit(int slot) {
            return 64;
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return slot == 0 && ExtendedAssemblerMatrixPatternUploadUtil.decodeExtendedPattern(
                    ExtendedAssemblerMatrixPatternUploaderBlockEntity.this.getLevel(), stack) != null;
        }
    }
}
