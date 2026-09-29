package me.myogoo.extendedmolecularassembler.integration.advancedae;

import me.myogoo.extendedmolecularassembler.lang.EMATranslationKey;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.pedroksl.advanced_ae.common.blocks.QuantumCrafterBlock;

import java.util.function.Consumer;

/**
 * AdvancedAE-compatible Quantum Crafter variant for EMA extended crafting patterns.
 *
 * <p>The behavior is inherited from AdvancedAE's Quantum Crafter; EMA's AdvancedAE mixin adds support for
 * {@code ExtendedTableCraftingPattern} jobs to the shared QuantumCrafterEntity implementation.</p>
 */
public class ExtendedQuantumCrafterBlock extends QuantumCrafterBlock {
    public ExtendedQuantumCrafterBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, Consumer<Component> tooltip,
            TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        tooltip.accept(Component.translatable(EMATranslationKey.TOOLTIP.EXTENDED_QUANTUM_CRAFTER_WIP.key())
                .withStyle(ChatFormatting.GOLD));
    }
}
