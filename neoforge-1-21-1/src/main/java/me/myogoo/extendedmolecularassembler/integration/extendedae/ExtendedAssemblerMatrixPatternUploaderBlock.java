package me.myogoo.extendedmolecularassembler.integration.extendedae;

import com.glodblock.github.extendedae.common.blocks.matrix.BlockAssemblerMatrixBase;
import net.minecraft.core.BlockPos;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;

import java.util.function.Supplier;

public class ExtendedAssemblerMatrixPatternUploaderBlock
        extends BlockAssemblerMatrixBase<ExtendedAssemblerMatrixPatternUploaderBlockEntity> {
    private final Supplier<Item> presentItem;

    public ExtendedAssemblerMatrixPatternUploaderBlock() {
        this(() -> EMAExtendedAEIntegration.EXTENDED_ASSEMBLER_MATRIX_PATTERN_UPLOADER_ITEM.get());
    }

    public ExtendedAssemblerMatrixPatternUploaderBlock(Supplier<Item> presentItem) {
        super(BlockBehaviour.Properties.of()
                .mapColor(MapColor.METAL)
                .strength(3.5F)
                .sound(SoundType.METAL)
                .requiresCorrectToolForDrops()
                .noOcclusion());
        this.presentItem = presentItem;
    }

    @Override
    public void openGui(ExtendedAssemblerMatrixPatternUploaderBlockEntity tile, Player player) {
        // This is an automation-facing matrix function block. Item insertion through
        // the exposed item handler uploads extended encoded patterns into nearby or
        // same-cluster Extended Pattern Cores.
    }

    @Override
    public ItemInteractionResult check(ExtendedAssemblerMatrixPatternUploaderBlockEntity tile, ItemStack stack,
            Level level, BlockPos pos, BlockHitResult hit, Player player) {
        if (stack.isEmpty()) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        var handler = tile.getPatternInv(hit.getDirection());
        if (handler == null || !handler.isItemValid(0, stack)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        if (level.isClientSide()) {
            return ItemInteractionResult.SUCCESS;
        }

        var remainder = handler.insertItem(0, stack.copy(), false);
        if (remainder.getCount() == stack.getCount()) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        if (!player.getAbilities().instabuild) {
            stack.setCount(remainder.getCount());
        }
        return ItemInteractionResult.SUCCESS;
    }

    @Override
    public Item getPresentItem() {
        return this.presentItem.get();
    }
}
