package me.myogoo.extendedmolecularassembler.integration.extendedae;

import com.glodblock.github.extendedae.common.blocks.matrix.BlockAssemblerMatrixBase;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

import java.util.function.Supplier;

public class ExtendedAssemblerMatrixCraftingCoreBlock
        extends BlockAssemblerMatrixBase<ExtendedAssemblerMatrixCraftingCoreBlockEntity> {
    private final Supplier<Item> presentItem;

    public ExtendedAssemblerMatrixCraftingCoreBlock() {
        this(() -> EMAExtendedAEIntegration.EXTENDED_ASSEMBLER_MATRIX_CRAFTING_CORE_ITEM.get());
    }

    public ExtendedAssemblerMatrixCraftingCoreBlock(Supplier<Item> presentItem) {
        super(BlockBehaviour.Properties.of()
                .mapColor(MapColor.METAL)
                .strength(3.5F)
                .sound(SoundType.METAL)
                .requiresCorrectToolForDrops()
                .noOcclusion());
        this.presentItem = presentItem;
    }

    @Override
    public void openGui(ExtendedAssemblerMatrixCraftingCoreBlockEntity tile, Player player) {
        // EMA crafting cores have no separate UI yet; the Matrix/Pattern Core UI remains the control surface.
    }

    @Override
    public Item getPresentItem() {
        return this.presentItem.get();
    }
}
