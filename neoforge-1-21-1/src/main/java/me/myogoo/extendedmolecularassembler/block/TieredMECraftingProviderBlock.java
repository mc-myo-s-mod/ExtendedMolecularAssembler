package me.myogoo.extendedmolecularassembler.block;

import appeng.block.AEBaseEntityBlock;
import me.myogoo.extendedmolecularassembler.block.blockentity.TieredMECraftingProviderBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;

import java.util.List;

public class TieredMECraftingProviderBlock extends AEBaseEntityBlock<TieredMECraftingProviderBlockEntity> {
    public static final BooleanProperty ONLINE = BooleanProperty.create("online");

    private final TieredMECraftingProviderTier tier;

    public TieredMECraftingProviderBlock(TieredMECraftingProviderTier tier, Properties properties) {
        super(properties);
        this.tier = tier;
        registerDefaultState(defaultBlockState().setValue(ONLINE, false));
    }

    public TieredMECraftingProviderTier getTier() {
        return tier;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(ONLINE);
    }

    @Override
    protected BlockState updateBlockStateFromBlockEntity(BlockState currentState,
            TieredMECraftingProviderBlockEntity blockEntity) {
        return currentState.setValue(ONLINE, blockEntity.isOnline());
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player,
            BlockHitResult hitResult) {
        if (!level.isClientSide()) {
            player.displayClientMessage(Component.translatable(
                    "message.extendedmolecularassembler.me_crafting_provider.status",
                    tier.displayName(), tier.tier()), true);
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip,
            TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        tooltip.add(Component.translatable("tooltip.extendedmolecularassembler.me_crafting_provider.provides",
                tier.displayName(), tier.providedTables()).withStyle(tier.color()));
        tooltip.add(Component.translatable("tooltip.extendedmolecularassembler.me_crafting_provider.requirement"));
    }
}
