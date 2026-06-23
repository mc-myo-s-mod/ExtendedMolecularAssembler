package me.myogoo.extendedmolecularassembler.integration.extendedae;

import appeng.api.AECapabilities;
import appeng.blockentity.AEBaseBlockEntity;
import me.myogoo.extendedmolecularassembler.init.EMABlockEntities;
import me.myogoo.extendedmolecularassembler.init.EMABlocks;
import me.myogoo.extendedmolecularassembler.init.EMAItems;
import me.myogoo.extendedmolecularassembler.init.EMAMenus;
import me.myogoo.extendedmolecularassembler.init.EMAModPresence;
import me.myogoo.extendedmolecularassembler.integration.AssemblerMatrixJobContext;
import me.myogoo.extendedmolecularassembler.integration.extendedae.menu.ExtendedAssemblerMatrixPatternCoreMenu;
import me.myogoo.extendedmolecularassembler.integration.extendedae.network.EMAMatrixPatternCoreUpdatePacket;
import me.myogoo.extendedmolecularassembler.integration.extendedae.network.EMAOpenExtendedAEAssemblerMatrixScreenPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicReference;

public final class EMAExtendedAEIntegration {
    public static DeferredBlock<ExtendedAssemblerMatrixPatternCoreBlock> EXTENDED_ASSEMBLER_MATRIX_PATTERN_CORE;
    public static DeferredBlock<ExtendedAssemblerMatrixCraftingCoreBlock> EXTENDED_ASSEMBLER_MATRIX_CRAFTING_CORE;
    public static DeferredBlock<ExtendedAssemblerMatrixPatternUploaderBlock> EXTENDED_ASSEMBLER_MATRIX_PATTERN_UPLOADER;
    public static DeferredBlock<ExtendedAssemblerMatrixPatternCoreBlock> EXTENDED_ASSEMBLER_MATRIX_PATTERN_CORE_PLUS;
    public static DeferredBlock<ExtendedAssemblerMatrixCraftingCoreBlock> EXTENDED_ASSEMBLER_MATRIX_CRAFTING_CORE_PLUS;
    public static DeferredItem<BlockItem> EXTENDED_ASSEMBLER_MATRIX_PATTERN_CORE_ITEM;
    public static DeferredItem<BlockItem> EXTENDED_ASSEMBLER_MATRIX_CRAFTING_CORE_ITEM;
    public static DeferredItem<BlockItem> EXTENDED_ASSEMBLER_MATRIX_PATTERN_UPLOADER_ITEM;
    public static DeferredItem<BlockItem> EXTENDED_ASSEMBLER_MATRIX_PATTERN_CORE_PLUS_ITEM;
    public static DeferredItem<BlockItem> EXTENDED_ASSEMBLER_MATRIX_CRAFTING_CORE_PLUS_ITEM;
    public static DeferredHolder<BlockEntityType<?>, BlockEntityType<ExtendedAssemblerMatrixPatternCoreBlockEntity>>
            EXTENDED_ASSEMBLER_MATRIX_PATTERN_CORE_BE;
    public static DeferredHolder<BlockEntityType<?>, BlockEntityType<ExtendedAssemblerMatrixCraftingCoreBlockEntity>>
            EXTENDED_ASSEMBLER_MATRIX_CRAFTING_CORE_BE;
    public static DeferredHolder<BlockEntityType<?>, BlockEntityType<ExtendedAssemblerMatrixPatternUploaderBlockEntity>>
            EXTENDED_ASSEMBLER_MATRIX_PATTERN_UPLOADER_BE;
    public static DeferredHolder<BlockEntityType<?>, BlockEntityType<ExtendedAssemblerMatrixPatternCoreBlockEntity>>
            EXTENDED_ASSEMBLER_MATRIX_PATTERN_CORE_PLUS_BE;
    public static DeferredHolder<BlockEntityType<?>, BlockEntityType<ExtendedAssemblerMatrixCraftingCoreBlockEntity>>
            EXTENDED_ASSEMBLER_MATRIX_CRAFTING_CORE_PLUS_BE;
    public static DeferredHolder<MenuType<?>, MenuType<ExtendedAssemblerMatrixPatternCoreMenu>>
            EXTENDED_ASSEMBLER_MATRIX_PATTERN_CORE_MENU;


    private EMAExtendedAEIntegration() {
    }

    public static void registerDeferred() {
        if (EXTENDED_ASSEMBLER_MATRIX_PATTERN_CORE != null) {
            return;
        }

        EXTENDED_ASSEMBLER_MATRIX_PATTERN_CORE = EMABlocks.BLOCKS.register(
                "extended_assembler_matrix_pattern_core",
                () -> new ExtendedAssemblerMatrixPatternCoreBlock());
        EXTENDED_ASSEMBLER_MATRIX_CRAFTING_CORE = EMABlocks.BLOCKS.register(
                "extended_assembler_matrix_crafting_core",
                () -> new ExtendedAssemblerMatrixCraftingCoreBlock());
        EXTENDED_ASSEMBLER_MATRIX_PATTERN_CORE_ITEM = EMAItems.ITEMS.register(
                "extended_assembler_matrix_pattern_core",
                () -> new BlockItem(EXTENDED_ASSEMBLER_MATRIX_PATTERN_CORE.get(), new net.minecraft.world.item.Item.Properties()));
        EXTENDED_ASSEMBLER_MATRIX_CRAFTING_CORE_ITEM = EMAItems.ITEMS.register(
                "extended_assembler_matrix_crafting_core",
                () -> new BlockItem(EXTENDED_ASSEMBLER_MATRIX_CRAFTING_CORE.get(), new net.minecraft.world.item.Item.Properties()));
        EXTENDED_ASSEMBLER_MATRIX_PATTERN_CORE_BE = EMABlockEntities.BLOCK_ENTITIES.register(
                "extended_assembler_matrix_pattern_core",
                EMAExtendedAEIntegration::createBlockEntityType);
        EXTENDED_ASSEMBLER_MATRIX_CRAFTING_CORE_BE = EMABlockEntities.BLOCK_ENTITIES.register(
                "extended_assembler_matrix_crafting_core",
                EMAExtendedAEIntegration::createCraftingCoreBlockEntityType);
        EXTENDED_ASSEMBLER_MATRIX_PATTERN_CORE_MENU = EMAMenus.REGISTER.register(
                "extended_assembler_matrix_pattern_core",
                () -> ExtendedAssemblerMatrixPatternCoreMenu.TYPE);

        if (EMAModPresence.isExtendedAEPlusLoaded()) {
            registerExtendedAEPlusDeferred();
        }
    }

    private static void registerExtendedAEPlusDeferred() {
        EXTENDED_ASSEMBLER_MATRIX_PATTERN_UPLOADER = EMABlocks.BLOCKS.register(
                "extended_assembler_matrix_pattern_uploader",
                () -> new ExtendedAssemblerMatrixPatternUploaderBlock());
        EXTENDED_ASSEMBLER_MATRIX_PATTERN_CORE_PLUS = EMABlocks.BLOCKS.register(
                "extended_assembler_matrix_pattern_core_plus",
                () -> new ExtendedAssemblerMatrixPatternCoreBlock(() -> EXTENDED_ASSEMBLER_MATRIX_PATTERN_CORE_PLUS_ITEM.get()));
        EXTENDED_ASSEMBLER_MATRIX_CRAFTING_CORE_PLUS = EMABlocks.BLOCKS.register(
                "extended_assembler_matrix_crafting_core_plus",
                () -> new ExtendedAssemblerMatrixCraftingCoreBlock(
                        () -> EXTENDED_ASSEMBLER_MATRIX_CRAFTING_CORE_PLUS_ITEM.get()));

        EXTENDED_ASSEMBLER_MATRIX_PATTERN_UPLOADER_ITEM = EMAItems.ITEMS.register(
                "extended_assembler_matrix_pattern_uploader",
                () -> new BlockItem(EXTENDED_ASSEMBLER_MATRIX_PATTERN_UPLOADER.get(),
                        new net.minecraft.world.item.Item.Properties()));
        EXTENDED_ASSEMBLER_MATRIX_PATTERN_CORE_PLUS_ITEM = EMAItems.ITEMS.register(
                "extended_assembler_matrix_pattern_core_plus",
                () -> new BlockItem(EXTENDED_ASSEMBLER_MATRIX_PATTERN_CORE_PLUS.get(),
                        new net.minecraft.world.item.Item.Properties()));
        EXTENDED_ASSEMBLER_MATRIX_CRAFTING_CORE_PLUS_ITEM = EMAItems.ITEMS.register(
                "extended_assembler_matrix_crafting_core_plus",
                () -> new BlockItem(EXTENDED_ASSEMBLER_MATRIX_CRAFTING_CORE_PLUS.get(),
                        new net.minecraft.world.item.Item.Properties()));

        EXTENDED_ASSEMBLER_MATRIX_PATTERN_UPLOADER_BE = EMABlockEntities.BLOCK_ENTITIES.register(
                "extended_assembler_matrix_pattern_uploader",
                EMAExtendedAEIntegration::createPatternUploaderBlockEntityType);
        EXTENDED_ASSEMBLER_MATRIX_PATTERN_CORE_PLUS_BE = EMABlockEntities.BLOCK_ENTITIES.register(
                "extended_assembler_matrix_pattern_core_plus",
                EMAExtendedAEIntegration::createPatternCorePlusBlockEntityType);
        EXTENDED_ASSEMBLER_MATRIX_CRAFTING_CORE_PLUS_BE = EMABlockEntities.BLOCK_ENTITIES.register(
                "extended_assembler_matrix_crafting_core_plus",
                EMAExtendedAEIntegration::createCraftingCorePlusBlockEntityType);
    }

    public static void addCreativeTabItems(CreativeModeTab.Output output) {
        output.accept(EXTENDED_ASSEMBLER_MATRIX_PATTERN_CORE_ITEM.get());
        output.accept(EXTENDED_ASSEMBLER_MATRIX_CRAFTING_CORE_ITEM.get());
        if (EXTENDED_ASSEMBLER_MATRIX_PATTERN_UPLOADER_ITEM != null) {
            output.accept(EXTENDED_ASSEMBLER_MATRIX_PATTERN_UPLOADER_ITEM.get());
        }
        if (EXTENDED_ASSEMBLER_MATRIX_PATTERN_CORE_PLUS_ITEM != null) {
            output.accept(EXTENDED_ASSEMBLER_MATRIX_PATTERN_CORE_PLUS_ITEM.get());
            output.accept(EXTENDED_ASSEMBLER_MATRIX_CRAFTING_CORE_PLUS_ITEM.get());
        }
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        var type = EXTENDED_ASSEMBLER_MATRIX_PATTERN_CORE_BE.get();
        event.registerBlockEntity(
                AECapabilities.IN_WORLD_GRID_NODE_HOST,
                type,
                (cell, context) -> cell);
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                type,
                ExtendedAssemblerMatrixPatternCoreBlockEntity::getPatternInv);

        event.registerBlockEntity(
                AECapabilities.IN_WORLD_GRID_NODE_HOST,
                EXTENDED_ASSEMBLER_MATRIX_CRAFTING_CORE_BE.get(),
                (cell, context) -> cell);

        if (EXTENDED_ASSEMBLER_MATRIX_PATTERN_UPLOADER_BE != null) {
            var uploaderType = EXTENDED_ASSEMBLER_MATRIX_PATTERN_UPLOADER_BE.get();
            event.registerBlockEntity(
                    AECapabilities.IN_WORLD_GRID_NODE_HOST,
                    uploaderType,
                    (cell, context) -> cell);
            event.registerBlockEntity(
                    Capabilities.ItemHandler.BLOCK,
                    uploaderType,
                    ExtendedAssemblerMatrixPatternUploaderBlockEntity::getPatternInv);
        }

        if (EXTENDED_ASSEMBLER_MATRIX_PATTERN_CORE_PLUS_BE != null) {
            var plusPatternType = EXTENDED_ASSEMBLER_MATRIX_PATTERN_CORE_PLUS_BE.get();
            event.registerBlockEntity(
                    AECapabilities.IN_WORLD_GRID_NODE_HOST,
                    plusPatternType,
                    (cell, context) -> cell);
            event.registerBlockEntity(
                    Capabilities.ItemHandler.BLOCK,
                    plusPatternType,
                    ExtendedAssemblerMatrixPatternCoreBlockEntity::getPatternInv);

            event.registerBlockEntity(
                    AECapabilities.IN_WORLD_GRID_NODE_HOST,
                    EXTENDED_ASSEMBLER_MATRIX_CRAFTING_CORE_PLUS_BE.get(),
                    (cell, context) -> cell);
        }
    }

    public static void registerNetwork(PayloadRegistrar registrar) {
        registrar.playToServer(
                EMAOpenExtendedAEAssemblerMatrixScreenPacket.TYPE,
                EMAOpenExtendedAEAssemblerMatrixScreenPacket.STREAM_CODEC,
                EMAOpenExtendedAEAssemblerMatrixScreenPacket::handle);
        registrar.playToClient(
                EMAMatrixPatternCoreUpdatePacket.TYPE,
                EMAMatrixPatternCoreUpdatePacket.STREAM_CODEC,
                EMAMatrixPatternCoreUpdatePacket::handle);
    }

    public static ItemStack tryInsertIntoAssemblerMatrix(Level level, BlockPos pos, ItemStack stack) {
        return ExtendedAEAssemblerMatrixBridge.insertIntoMatrixNetwork(level, pos, stack);
    }

    @Nullable
    public static AssemblerMatrixJobContext claimAssemblerMatrixJobContext() {
        return ExtendedAEAssemblerMatrixBridge.claimCurrentJobContext();
    }

    private static BlockEntityType<ExtendedAssemblerMatrixPatternCoreBlockEntity> createBlockEntityType() {
        var typeHolder = new AtomicReference<BlockEntityType<ExtendedAssemblerMatrixPatternCoreBlockEntity>>();
        var block = EXTENDED_ASSEMBLER_MATRIX_PATTERN_CORE.get();
        BlockEntityType.BlockEntitySupplier<ExtendedAssemblerMatrixPatternCoreBlockEntity> supplier =
                (pos, state) -> new ExtendedAssemblerMatrixPatternCoreBlockEntity(typeHolder.get(), pos, state,
                        ExtendedAssemblerMatrixPatternCoreBlockEntity.DEFAULT_INV_SIZE);
        var type = BlockEntityType.Builder.of(supplier, block).build(null);
        typeHolder.setPlain(type);
        block.setBlockEntity(ExtendedAssemblerMatrixPatternCoreBlockEntity.class, type, null, null);
        return type;
    }

    private static BlockEntityType<ExtendedAssemblerMatrixCraftingCoreBlockEntity> createCraftingCoreBlockEntityType() {
        var typeHolder = new AtomicReference<BlockEntityType<ExtendedAssemblerMatrixCraftingCoreBlockEntity>>();
        var block = EXTENDED_ASSEMBLER_MATRIX_CRAFTING_CORE.get();
        BlockEntityType.BlockEntitySupplier<ExtendedAssemblerMatrixCraftingCoreBlockEntity> supplier =
                (pos, state) -> new ExtendedAssemblerMatrixCraftingCoreBlockEntity(typeHolder.get(), pos, state);
        var type = BlockEntityType.Builder.of(supplier, block).build(null);
        typeHolder.setPlain(type);
        block.setBlockEntity(ExtendedAssemblerMatrixCraftingCoreBlockEntity.class, type, null, null);
        return type;
    }

    private static BlockEntityType<ExtendedAssemblerMatrixPatternUploaderBlockEntity> createPatternUploaderBlockEntityType() {
        var typeHolder = new AtomicReference<BlockEntityType<ExtendedAssemblerMatrixPatternUploaderBlockEntity>>();
        var block = EXTENDED_ASSEMBLER_MATRIX_PATTERN_UPLOADER.get();
        BlockEntityType.BlockEntitySupplier<ExtendedAssemblerMatrixPatternUploaderBlockEntity> supplier =
                (pos, state) -> new ExtendedAssemblerMatrixPatternUploaderBlockEntity(typeHolder.get(), pos, state);
        var type = BlockEntityType.Builder.of(supplier, block).build(null);
        typeHolder.setPlain(type);
        block.setBlockEntity(ExtendedAssemblerMatrixPatternUploaderBlockEntity.class, type, null, null);
        return type;
    }

    private static BlockEntityType<ExtendedAssemblerMatrixPatternCoreBlockEntity> createPatternCorePlusBlockEntityType() {
        var typeHolder = new AtomicReference<BlockEntityType<ExtendedAssemblerMatrixPatternCoreBlockEntity>>();
        var block = EXTENDED_ASSEMBLER_MATRIX_PATTERN_CORE_PLUS.get();
        BlockEntityType.BlockEntitySupplier<ExtendedAssemblerMatrixPatternCoreBlockEntity> supplier =
                (pos, state) -> new ExtendedAssemblerMatrixPatternCoreBlockEntity(typeHolder.get(), pos, state,
                        ExtendedAssemblerMatrixPatternCoreBlockEntity.PLUS_INV_SIZE);
        var type = BlockEntityType.Builder.of(supplier, block).build(null);
        typeHolder.setPlain(type);
        block.setBlockEntity(ExtendedAssemblerMatrixPatternCoreBlockEntity.class, type, null, null);
        return type;
    }

    private static BlockEntityType<ExtendedAssemblerMatrixCraftingCoreBlockEntity> createCraftingCorePlusBlockEntityType() {
        var typeHolder = new AtomicReference<BlockEntityType<ExtendedAssemblerMatrixCraftingCoreBlockEntity>>();
        var block = EXTENDED_ASSEMBLER_MATRIX_CRAFTING_CORE_PLUS.get();
        BlockEntityType.BlockEntitySupplier<ExtendedAssemblerMatrixCraftingCoreBlockEntity> supplier =
                (pos, state) -> new ExtendedAssemblerMatrixCraftingCoreBlockEntity(typeHolder.get(), pos, state,
                        ExtendedAssemblerMatrixCraftingCoreBlockEntity.PLUS_THREAD_COUNT);
        var type = BlockEntityType.Builder.of(supplier, block).build(null);
        typeHolder.setPlain(type);
        block.setBlockEntity(ExtendedAssemblerMatrixCraftingCoreBlockEntity.class, type, null, null);
        return type;
    }

    public static void registerBlockEntityItems() {
        AEBaseBlockEntity.registerBlockEntityItem(
                EXTENDED_ASSEMBLER_MATRIX_PATTERN_CORE_BE.get(),
                EXTENDED_ASSEMBLER_MATRIX_PATTERN_CORE_ITEM.get());
        AEBaseBlockEntity.registerBlockEntityItem(
                EXTENDED_ASSEMBLER_MATRIX_CRAFTING_CORE_BE.get(),
                EXTENDED_ASSEMBLER_MATRIX_CRAFTING_CORE_ITEM.get());

        if (EXTENDED_ASSEMBLER_MATRIX_PATTERN_UPLOADER_BE != null) {
            AEBaseBlockEntity.registerBlockEntityItem(
                    EXTENDED_ASSEMBLER_MATRIX_PATTERN_UPLOADER_BE.get(),
                    EXTENDED_ASSEMBLER_MATRIX_PATTERN_UPLOADER_ITEM.get());
        }
        if (EXTENDED_ASSEMBLER_MATRIX_PATTERN_CORE_PLUS_BE != null) {
            AEBaseBlockEntity.registerBlockEntityItem(
                    EXTENDED_ASSEMBLER_MATRIX_PATTERN_CORE_PLUS_BE.get(),
                    EXTENDED_ASSEMBLER_MATRIX_PATTERN_CORE_PLUS_ITEM.get());
        }
        if (EXTENDED_ASSEMBLER_MATRIX_CRAFTING_CORE_PLUS_BE != null) {
            AEBaseBlockEntity.registerBlockEntityItem(
                    EXTENDED_ASSEMBLER_MATRIX_CRAFTING_CORE_PLUS_BE.get(),
                    EXTENDED_ASSEMBLER_MATRIX_CRAFTING_CORE_PLUS_ITEM.get());
        }
    }

}
