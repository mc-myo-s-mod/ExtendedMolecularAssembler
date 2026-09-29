package me.myogoo.extendedmolecularassembler.integration.extendedae;

import appeng.api.crafting.PatternDetailsHelper;
import appeng.crafting.pattern.EncodedPatternItem;
import com.glodblock.github.extendedae.common.me.matrix.ClusterAssemblerMatrix;
import com.glodblock.github.extendedae.common.tileentities.matrix.TileAssemblerMatrixFunction;
import me.myogoo.extendedmolecularassembler.config.EMAConfig;
import me.myogoo.extendedmolecularassembler.pattern.ExtendedTableCraftingPattern;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ExtendedAssemblerMatrixPatternUploaderBlockEntity extends TileAssemblerMatrixFunction {
    private final ResourceHandler<ItemResource> uploadHandler = new UploadHandler();

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
    @Override
    public ResourceHandler<ItemResource> getPatternInv(Direction ignored) {
        return this.uploadHandler;
    }

    private boolean isExtendedEncodedPattern(ItemStack stack) {
        return !stack.isEmpty()
                && stack.getItem() instanceof EncodedPatternItem<?>
                && PatternDetailsHelper.decodePattern(stack, this.getLevel()) instanceof ExtendedTableCraftingPattern;
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

    private final class UploadHandler implements ResourceHandler<ItemResource> {
        @Override
        public int size() {
            return 1;
        }

        @Override
        public ItemResource getResource(int slot) {
            return ItemResource.EMPTY;
        }

        @Override
        public long getAmountAsLong(int slot) {
            return 0;
        }

        @Override
        public int insert(int slot, ItemResource resource, int amount, TransactionContext transaction) {
            if (amount <= 0 || !isValid(slot, resource)) {
                return 0;
            }
            var targets = findTargets();
            var pattern = resource.toStack(1);
            // Check every core before making any transactional mutation.
            for (var core : targets) {
                if (ExtendedAssemblerMatrixPatternUploadUtil.resourceHandlerContainsPattern(
                        core.getPatternInv(null), pattern)) {
                    return 0;
                }
            }
            int remaining = Math.min(amount, resource.getMaxStackSize());
            int offered = remaining;
            for (var core : targets) {
                var handler = core.getPatternInv(null);
                if (handler != null) {
                    remaining -= handler.insert(resource, remaining, transaction);
                    if (remaining == 0) {
                        break;
                    }
                }
            }
            return offered - remaining;
        }

        @Override
        public int extract(int slot, ItemResource resource, int amount, TransactionContext transaction) {
            return 0;
        }

        @Override
        public long getCapacityAsLong(int slot, ItemResource resource) {
            return isValid(slot, resource) ? resource.getMaxStackSize() : 0;
        }

        @Override
        public boolean isValid(int slot, ItemResource resource) {
            return slot == 0 && !resource.isEmpty()
                    && ExtendedAssemblerMatrixPatternUploaderBlockEntity.this.isExtendedEncodedPattern(resource.toStack(1));
        }
    }
}
