package me.myogoo.extendedmolecularassembler.integration.extendedae;

import appeng.api.crafting.PatternDetailsHelper;
import appeng.crafting.pattern.EncodedPatternItem;
import com.glodblock.github.extendedae.common.me.matrix.ClusterAssemblerMatrix;
import com.glodblock.github.extendedae.common.tileentities.matrix.TileAssemblerMatrixFunction;
import me.myogoo.extendedmolecularassembler.pattern.ExtendedTableCraftingPattern;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ExtendedAssemblerMatrixPatternUploaderBlockEntity extends TileAssemblerMatrixFunction {
    private final IItemHandler uploadHandler = new UploadHandler();

    public ExtendedAssemblerMatrixPatternUploaderBlockEntity(BlockEntityType<?> type, BlockPos pos,
            BlockState blockState) {
        super(type, pos, blockState);
    }

    @Override
    public void add(ClusterAssemblerMatrix cluster) {
        // Function block only. Uploaded patterns are routed directly to Extended Pattern Core inventories.
    }

    @Nullable
    @Override
    public IItemHandler getPatternInv(Direction ignored) {
        return this.uploadHandler;
    }

    private boolean isExtendedEncodedPattern(ItemStack stack) {
        return !stack.isEmpty()
                && stack.getItem() instanceof EncodedPatternItem<?>
                && PatternDetailsHelper.decodePattern(stack, this.getLevel()) instanceof ExtendedTableCraftingPattern;
    }

    private ItemStack upload(ItemStack stack, boolean simulate) {
        if (!isExtendedEncodedPattern(stack)) {
            return stack;
        }

        var remainder = stack.copy();
        for (var core : this.findTargets()) {
            var handler = core.getPatternInv(null);
            if (handler == null) {
                continue;
            }
            if (ExtendedAssemblerMatrixPatternUploadUtil.itemHandlerContainsPattern(handler, stack)) {
                return stack;
            }
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
            return slot == 0 && ExtendedAssemblerMatrixPatternUploaderBlockEntity.this.isExtendedEncodedPattern(stack);
        }
    }
}
