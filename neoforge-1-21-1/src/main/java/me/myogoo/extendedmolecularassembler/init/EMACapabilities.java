package me.myogoo.extendedmolecularassembler.init;

import appeng.api.AECapabilities;
import appeng.blockentity.AEBaseInvBlockEntity;
import me.myogoo.extendedmolecularassembler.block.blockentity.ExtendedMolecularAssemblerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

public final class EMACapabilities {
    private EMACapabilities() {
    }

    public static void register(RegisterCapabilitiesEvent event) {
        registerAssembler(event, EMABlockEntities.EXTENDED_MOLECULAR_ASSEMBLER.get());
        if (EMABlockEntities.EX_EXTENDED_MOLECULAR_ASSEMBLER != null) {
            registerAssembler(event, EMABlockEntities.EX_EXTENDED_MOLECULAR_ASSEMBLER.get());
        }
        EMAOptionalIntegrations.registerCapabilities(event);
    }

    private static void registerAssembler(RegisterCapabilitiesEvent event,
            BlockEntityType<ExtendedMolecularAssemblerBlockEntity> type) {
        event.registerBlockEntity(
                AECapabilities.IN_WORLD_GRID_NODE_HOST,
                type,
                (assembler, context) -> assembler);
        event.registerBlockEntity(
                AECapabilities.CRAFTING_MACHINE,
                type,
                (assembler, context) -> assembler);
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                type,
                AEBaseInvBlockEntity::getExposedItemHandler);
    }
}
