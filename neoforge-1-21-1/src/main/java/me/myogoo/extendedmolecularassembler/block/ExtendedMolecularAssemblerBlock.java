package me.myogoo.extendedmolecularassembler.block;

import appeng.block.AEBaseEntityBlock;
import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuLocators;
import me.myogoo.extendedmolecularassembler.block.blockentity.ExtendedMolecularAssemblerBlockEntity;
import me.myogoo.extendedmolecularassembler.menu.ExtendedMolecularAssemblerMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;

public class ExtendedMolecularAssemblerBlock extends AEBaseEntityBlock<ExtendedMolecularAssemblerBlockEntity> {
    public static final BooleanProperty POWERED = BooleanProperty.create("powered");

    public ExtendedMolecularAssemblerBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(POWERED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(POWERED);
    }

    @Override
    protected BlockState updateBlockStateFromBlockEntity(BlockState currentState,
            ExtendedMolecularAssemblerBlockEntity blockEntity) {
        return currentState.setValue(POWERED, blockEntity.isPowered());
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player,
            BlockHitResult hitResult) {
        var blockEntity = this.getBlockEntity(level, pos);
        if (blockEntity != null) {
            if (!level.isClientSide()) {
                MenuOpener.open(ExtendedMolecularAssemblerMenu.TYPE, player,
                        MenuLocators.forBlockEntity(blockEntity));
            }
            return InteractionResult.sidedSuccess(level.isClientSide());
        }

        return InteractionResult.PASS;
    }
}
