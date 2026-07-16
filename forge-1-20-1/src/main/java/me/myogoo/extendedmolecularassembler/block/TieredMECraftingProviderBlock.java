package me.myogoo.extendedmolecularassembler.block;

import me.myogoo.extendedmolecularassembler.block.blockentity.TieredMECraftingProviderBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import appeng.block.AEBaseEntityBlock;

public class TieredMECraftingProviderBlock extends AEBaseEntityBlock<TieredMECraftingProviderBlockEntity> {
    private final TieredMECraftingProviderTier tier;

    public TieredMECraftingProviderBlock(TieredMECraftingProviderTier tier, BlockBehaviour.Properties properties) {
        super(properties);
        this.tier = tier;
    }

    public TieredMECraftingProviderTier getTier() {
        return tier;
    }

    @Override
    public InteractionResult onActivated(Level level, BlockPos pos, Player player, InteractionHand hand,
            @Nullable ItemStack heldItem, BlockHitResult hit) {
        if (!level.isClientSide()) {
            player.displayClientMessage(Component.translatable(
                    "message.extendedmolecularassembler.me_crafting_provider.status",
                    tier.displayName(), tier.tier()), true);
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter level, List<Component> tooltip,
            TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.translatable("tooltip.extendedmolecularassembler.me_crafting_provider.provides",
                tier.displayName(), tier.providedTable()).withStyle(tier.color()));
        tooltip.add(Component.translatable("tooltip.extendedmolecularassembler.me_crafting_provider.requirement"));
    }
}
