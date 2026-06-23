package me.myogoo.extendedmolecularassembler.integration.extendedae;

import com.glodblock.github.extendedae.common.blocks.matrix.BlockAssemblerMatrixBase;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

public class ExtendedAssemblerMatrixPatternUploaderBlock
        extends BlockAssemblerMatrixBase<ExtendedAssemblerMatrixPatternUploaderBlockEntity> {
    public ExtendedAssemblerMatrixPatternUploaderBlock() {
        super(BlockBehaviour.Properties.of()
                .mapColor(MapColor.METAL)
                .strength(3.5F)
                .sound(SoundType.METAL)
                .requiresCorrectToolForDrops()
                .noOcclusion());
    }

    @Override
    public void openGui(ExtendedAssemblerMatrixPatternUploaderBlockEntity tile, Player player) {
        // Automation-facing matrix function block. Item insertion uploads extended encoded patterns.
    }

    @Override
    public Item getPresentItem() {
        return EMAExtendedAEIntegration.EXTENDED_ASSEMBLER_MATRIX_PATTERN_UPLOADER_ITEM.get();
    }
}
