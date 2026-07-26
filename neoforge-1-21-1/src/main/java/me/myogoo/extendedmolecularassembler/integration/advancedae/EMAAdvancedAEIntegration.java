package me.myogoo.extendedmolecularassembler.integration.advancedae;

import appeng.api.AECapabilities;
import appeng.blockentity.AEBaseBlockEntity;
import me.myogoo.extendedmolecularassembler.init.EMABlockEntities;
import me.myogoo.extendedmolecularassembler.init.EMABlocks;
import me.myogoo.extendedmolecularassembler.init.EMAItems;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.pedroksl.advanced_ae.common.entities.QuantumCrafterEntity;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.concurrent.atomic.AtomicReference;

public final class EMAAdvancedAEIntegration {
    public static DeferredBlock<ExtendedQuantumCrafterBlock> EXTENDED_QUANTUM_CRAFTER;
    public static DeferredItem<BlockItem> EXTENDED_QUANTUM_CRAFTER_ITEM;
    public static DeferredHolder<BlockEntityType<?>, BlockEntityType<QuantumCrafterEntity>> EXTENDED_QUANTUM_CRAFTER_BE;

    private EMAAdvancedAEIntegration() {
    }

    public static void registerDeferred() {
        if (EXTENDED_QUANTUM_CRAFTER != null) {
            return;
        }

        EXTENDED_QUANTUM_CRAFTER = EMABlocks.BLOCKS.register(
                "extended_quantum_crafter",
                ExtendedQuantumCrafterBlock::new);
        EXTENDED_QUANTUM_CRAFTER_ITEM = EMAItems.ITEMS.register(
                "extended_quantum_crafter",
                () -> new BlockItem(EXTENDED_QUANTUM_CRAFTER.get(), new Item.Properties()));
        EXTENDED_QUANTUM_CRAFTER_BE = EMABlockEntities.BLOCK_ENTITIES.register(
                "extended_quantum_crafter",
                EMAAdvancedAEIntegration::createBlockEntityType);
    }

    public static void addCreativeTabItems(CreativeModeTab.Output output) {
        output.accept(EXTENDED_QUANTUM_CRAFTER_ITEM.get());
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                AECapabilities.IN_WORLD_GRID_NODE_HOST,
                EXTENDED_QUANTUM_CRAFTER_BE.get(),
                (cell, context) -> cell);
    }

    private static BlockEntityType<QuantumCrafterEntity> createBlockEntityType() {
        var typeHolder = new AtomicReference<BlockEntityType<QuantumCrafterEntity>>();
        var block = EXTENDED_QUANTUM_CRAFTER.get();
        BlockEntityType.BlockEntitySupplier<QuantumCrafterEntity> supplier =
                (pos, state) -> new ExtendedQuantumCrafterBlockEntity(typeHolder.get(), pos, state);
        var type = BlockEntityType.Builder.of(supplier, block).build(null);
        typeHolder.setPlain(type);
        block.setBlockEntity(QuantumCrafterEntity.class, type, null, null);
        return type;
    }

    public static void registerBlockEntityItems() {
        AEBaseBlockEntity.registerBlockEntityItem(
                EXTENDED_QUANTUM_CRAFTER_BE.get(),
                EXTENDED_QUANTUM_CRAFTER_ITEM.get());
    }
}
