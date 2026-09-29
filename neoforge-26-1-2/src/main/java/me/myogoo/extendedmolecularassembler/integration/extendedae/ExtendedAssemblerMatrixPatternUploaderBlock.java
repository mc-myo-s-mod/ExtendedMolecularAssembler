package me.myogoo.extendedmolecularassembler.integration.extendedae;

import com.glodblock.github.extendedae.common.blocks.matrix.BlockAssemblerMatrixBase;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;

import java.util.function.Supplier;

public class ExtendedAssemblerMatrixPatternUploaderBlock
        extends BlockAssemblerMatrixBase<ExtendedAssemblerMatrixPatternUploaderBlockEntity> {
    private final Supplier<Item> presentItem;

    public ExtendedAssemblerMatrixPatternUploaderBlock(Properties properties) {
        this(properties, () -> EMAExtendedAEIntegration.EXTENDED_ASSEMBLER_MATRIX_PATTERN_UPLOADER_ITEM.get());
    }

    public ExtendedAssemblerMatrixPatternUploaderBlock(Properties properties, Supplier<Item> presentItem) {
        super(properties
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
    public InteractionResult check(ExtendedAssemblerMatrixPatternUploaderBlockEntity tile, ItemStack stack,
            Level level, BlockPos pos, BlockHitResult hit, Player player) {
        if (stack.isEmpty()) {
            return InteractionResult.PASS;
        }

        var handler = tile.getPatternInv(hit.getDirection());
        if (handler == null || !handler.isValid(0, ItemResource.of(stack))) {
            return InteractionResult.PASS;
        }

        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        try (var transaction = Transaction.openRoot()) {
            int inserted = handler.insert(0, ItemResource.of(stack), stack.getCount(), transaction);
            if (inserted == 0) {
                return InteractionResult.PASS;
            }
            transaction.commit();
            if (!player.getAbilities().instabuild) {
                stack.shrink(inserted);
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public Item getPresentItem() {
        return this.presentItem.get();
    }
}
