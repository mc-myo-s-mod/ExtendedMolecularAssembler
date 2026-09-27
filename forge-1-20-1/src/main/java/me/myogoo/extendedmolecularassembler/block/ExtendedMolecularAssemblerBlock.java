package me.myogoo.extendedmolecularassembler.block;

import appeng.block.AEBaseEntityBlock;
import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuLocators;
import appeng.util.InteractionUtil;
import me.myogoo.extendedmolecularassembler.block.blockentity.ExtendedMolecularAssemblerBlockEntity;
import me.myogoo.extendedmolecularassembler.menu.ExtendedMolecularAssemblerMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;

public class ExtendedMolecularAssemblerBlock extends AEBaseEntityBlock<ExtendedMolecularAssemblerBlockEntity> {
    public static final BooleanProperty POWERED = BooleanProperty.create("powered");

    private final int gridSide;
    private final boolean exAssembler;

    public ExtendedMolecularAssemblerBlock(Properties properties) {
        this(properties, 9, false);
    }

    public ExtendedMolecularAssemblerBlock(Properties properties, int gridSide) {
        this(properties, gridSide, false);
    }

    public ExtendedMolecularAssemblerBlock(Properties properties, int gridSide, boolean exAssembler) {
        super(properties);
        this.gridSide = gridSide;
        this.exAssembler = exAssembler;
        registerDefaultState(defaultBlockState().setValue(POWERED, false));
    }

    public int getGridSide() {
        return this.gridSide;
    }

    public boolean isExAssembler() {
        return this.exAssembler;
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
    public InteractionResult onActivated(Level level, BlockPos pos, Player player, InteractionHand hand, ItemStack heldItem,
            BlockHitResult hitResult) {
        var blockEntity = this.getBlockEntity(level, pos);
        if (blockEntity != null && !InteractionUtil.isInAlternateUseMode(player)) {
            if (!level.isClientSide()) {
                MenuOpener.open(ExtendedMolecularAssemblerMenu.TYPE, player,
                        MenuLocators.forBlockEntity(blockEntity));
            }
            return InteractionResult.sidedSuccess(level.isClientSide());
        }

        return InteractionResult.PASS;
    }
}
