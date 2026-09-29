package me.myogoo.extendedmolecularassembler.block;

import appeng.block.AEBaseEntityBlock;
import me.myogoo.extendedmolecularassembler.block.blockentity.ExportMECraftingProviderBlockEntity;
import me.myogoo.extendedmolecularassembler.lang.EMATranslationKey;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.redstone.Orientation;

import java.util.function.Consumer;

public class ExportMECraftingProviderBlock extends AEBaseEntityBlock<ExportMECraftingProviderBlockEntity> {
    private final ExportMECraftingProviderTier tier;

    public ExportMECraftingProviderBlock(ExportMECraftingProviderTier tier, Properties properties) {
        super(properties);
        this.tier = tier;
    }

    public ExportMECraftingProviderTier getTier() {
        return tier;
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, Orientation orientation,
            boolean movedByPiston) {
        super.neighborChanged(state, level, pos, block, orientation, movedByPiston);
        if (!level.isClientSide()
                && level.getBlockEntity(pos) instanceof ExportMECraftingProviderBlockEntity provider) {
            provider.updateVisualStateIfNeeded();
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, Consumer<Component> tooltip,
            TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        tooltip.accept(Component.translatable(EMATranslationKey.TOOLTIP.ME_CRAFTING_PROVIDER_EXPORT_MODE.key()));
    }
}
