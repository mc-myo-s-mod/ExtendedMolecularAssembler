package me.myogoo.extendedmolecularassembler.integration.extendedae;

import com.glodblock.github.extendedae.common.blocks.matrix.BlockAssemblerMatrixBase;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

public class ExtendedAssemblerMatrixPatternCoreBlock
        extends BlockAssemblerMatrixBase<ExtendedAssemblerMatrixPatternCoreBlockEntity> {
    public ExtendedAssemblerMatrixPatternCoreBlock() {
        super(BlockBehaviour.Properties.of()
                .mapColor(MapColor.METAL)
                .strength(3.5F)
                .sound(SoundType.METAL)
                .requiresCorrectToolForDrops()
                .noOcclusion());
    }

    @Override
    public void openGui(ExtendedAssemblerMatrixPatternCoreBlockEntity tile, Player player) {
        // Forge 1.20.1 parity shell: patterns are inserted through item capabilities.
    }

    @Override
    public Item getPresentItem() {
        return EMAExtendedAEIntegration.EXTENDED_ASSEMBLER_MATRIX_PATTERN_CORE_ITEM.get();
    }
}
