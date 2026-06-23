package me.myogoo.extendedmolecularassembler.integration.extendedae;

import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuLocators;
import com.glodblock.github.extendedae.common.blocks.matrix.BlockAssemblerMatrixBase;
import me.myogoo.extendedmolecularassembler.integration.extendedae.menu.ExtendedAssemblerMatrixPatternCoreMenu;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

import java.util.function.Supplier;

public class ExtendedAssemblerMatrixPatternCoreBlock
        extends BlockAssemblerMatrixBase<ExtendedAssemblerMatrixPatternCoreBlockEntity> {
    private final Supplier<Item> presentItem;

    public ExtendedAssemblerMatrixPatternCoreBlock() {
        this(() -> EMAExtendedAEIntegration.EXTENDED_ASSEMBLER_MATRIX_PATTERN_CORE_ITEM.get());
    }

    public ExtendedAssemblerMatrixPatternCoreBlock(Supplier<Item> presentItem) {
        super(BlockBehaviour.Properties.of()
                .mapColor(MapColor.METAL)
                .strength(3.5F)
                .sound(SoundType.METAL)
                .requiresCorrectToolForDrops()
                .noOcclusion());
        this.presentItem = presentItem;
    }

    @Override
    public void openGui(ExtendedAssemblerMatrixPatternCoreBlockEntity tile, Player player) {
        if (tile.isActive() && tile.isFormed()) {
            MenuOpener.open(ExtendedAssemblerMatrixPatternCoreMenu.TYPE, player, MenuLocators.forBlockEntity(tile));
        }
    }

    @Override
    public Item getPresentItem() {
        return this.presentItem.get();
    }
}
