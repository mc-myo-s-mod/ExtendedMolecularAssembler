package me.myogoo.extendedmolecularassembler.item;

import java.util.List;

import appeng.api.orientation.IOrientationStrategy;
import appeng.util.inv.PlayerInternalInventory;
import me.myogoo.extendedmolecularassembler.block.ExtendedMolecularAssemblerBlock;
import me.myogoo.extendedmolecularassembler.block.blockentity.ExtendedMolecularAssemblerBlockEntity;
import me.myogoo.extendedmolecularassembler.init.EMABlocks;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public final class AssemblerUpgradeKitItem extends Item {
    public AssemblerUpgradeKitItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.extendedmolecularassembler.assembler_upgrade_kit",
                EMABlocks.EXTENDED_MOLECULAR_ASSEMBLER.get().getName(),
                EMABlocks.EX_EXTENDED_MOLECULAR_ASSEMBLER.get().getName()).withStyle(ChatFormatting.GRAY));
    }

    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState oldState = level.getBlockState(pos);
        if (!oldState.is(EMABlocks.EXTENDED_MOLECULAR_ASSEMBLER.get())) {
            return InteractionResult.PASS;
        }

        var player = context.getPlayer();
        if (player == null || !level.mayInteract(player, pos)
                || !player.mayUseItemAt(pos, context.getClickedFace(), stack)) {
            return InteractionResult.FAIL;
        }
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        if (!(level.getBlockEntity(pos) instanceof ExtendedMolecularAssemblerBlockEntity oldAssembler)) {
            return InteractionResult.FAIL;
        }
        if (!oldAssembler.getMainNode().isReady() || !oldAssembler.canUpgrade()) {
            player.displayClientMessage(
                    Component.translatable("message.extendedmolecularassembler.assembler_upgrade_busy"), true);
            return InteractionResult.FAIL;
        }

        var pattern = oldAssembler.getPatternInventory().getStackInSlot(0).copy();
        var originalData = new CompoundTag();
        oldAssembler.saveAdditional(originalData, level.registryAccess());
        var data = originalData.copy();
        // Crafting/output slots are empty; return the installed pattern to the player.
        data.remove("inv");
        var target = EMABlocks.EX_EXTENDED_MOLECULAR_ASSEMBLER.get();
        var newState = target.defaultBlockState().setValue(ExtendedMolecularAssemblerBlock.POWERED,
                oldState.getValue(ExtendedMolecularAssemblerBlock.POWERED));
        newState = IOrientationStrategy.get(newState).setOrientation(newState, oldAssembler.getFront(),
                oldAssembler.getOrientation().getSpin());

        if (!(target.newBlockEntity(pos, newState) instanceof ExtendedMolecularAssemblerBlockEntity newAssembler)) {
            return InteractionResult.FAIL;
        }
        newAssembler.setLevel(level);
        newAssembler.loadTag(data, level.registryAccess());

        level.removeBlockEntity(pos);
        try {
            if (!level.setBlock(pos, newState, 3)) {
                restoreSource(level, pos, oldState, originalData);
                return InteractionResult.FAIL;
            }
            level.removeBlockEntity(pos);
            level.setBlockEntity(newAssembler);
            newAssembler.saveChanges();
            newAssembler.markForUpdate();
        } catch (RuntimeException failure) {
            level.removeBlockEntity(pos);
            restoreSource(level, pos, oldState, originalData);
            throw failure;
        }

        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }
        var remainder = new PlayerInternalInventory(player.getInventory()).addItems(pattern);
        if (!remainder.isEmpty()) {
            player.drop(remainder, false);
        }
        return InteractionResult.CONSUME;
    }

    private static void restoreSource(Level level, BlockPos pos, BlockState oldState, CompoundTag originalData) {
        level.setBlock(pos, oldState, 3);
        var restored = level.getBlockEntity(pos);
        if (!(restored instanceof ExtendedMolecularAssemblerBlockEntity)) {
            restored = EMABlocks.EXTENDED_MOLECULAR_ASSEMBLER.get().newBlockEntity(pos, oldState);
            level.setBlockEntity(restored);
        }
        var sourceAssembler = (ExtendedMolecularAssemblerBlockEntity) restored;
        sourceAssembler.loadTag(originalData, level.registryAccess());
        sourceAssembler.saveChanges();
        sourceAssembler.markForUpdate();
    }
}
