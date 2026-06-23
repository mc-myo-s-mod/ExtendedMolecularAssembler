package me.myogoo.extendedmolecularassembler.integration.extendedae;

import appeng.blockentity.AEBaseBlockEntity;
import me.myogoo.extendedmolecularassembler.init.EMABlockEntities;
import me.myogoo.extendedmolecularassembler.init.EMABlocks;
import me.myogoo.extendedmolecularassembler.init.EMAItems;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.RegistryObject;

import java.util.concurrent.atomic.AtomicReference;

public final class EMAExtendedAEIntegration {
    public static RegistryObject<ExtendedAssemblerMatrixPatternCoreBlock> EXTENDED_ASSEMBLER_MATRIX_PATTERN_CORE;
    public static RegistryObject<BlockItem> EXTENDED_ASSEMBLER_MATRIX_PATTERN_CORE_ITEM;
    public static RegistryObject<BlockEntityType<ExtendedAssemblerMatrixPatternCoreBlockEntity>>
            EXTENDED_ASSEMBLER_MATRIX_PATTERN_CORE_ENTITY;

    public static RegistryObject<ExtendedAssemblerMatrixPatternUploaderBlock> EXTENDED_ASSEMBLER_MATRIX_PATTERN_UPLOADER;
    public static RegistryObject<BlockItem> EXTENDED_ASSEMBLER_MATRIX_PATTERN_UPLOADER_ITEM;
    public static RegistryObject<BlockEntityType<ExtendedAssemblerMatrixPatternUploaderBlockEntity>>
            EXTENDED_ASSEMBLER_MATRIX_PATTERN_UPLOADER_ENTITY;

    private EMAExtendedAEIntegration() {
    }

    public static void registerDeferred() {
        EXTENDED_ASSEMBLER_MATRIX_PATTERN_CORE = EMABlocks.BLOCKS.register(
                "extended_assembler_matrix_pattern_core",
                ExtendedAssemblerMatrixPatternCoreBlock::new);
        EXTENDED_ASSEMBLER_MATRIX_PATTERN_CORE_ITEM = EMAItems.ITEMS.register(
                "extended_assembler_matrix_pattern_core",
                () -> new BlockItem(EXTENDED_ASSEMBLER_MATRIX_PATTERN_CORE.get(), new Item.Properties()));
        EXTENDED_ASSEMBLER_MATRIX_PATTERN_CORE_ENTITY = EMABlockEntities.BLOCK_ENTITIES.register(
                "extended_assembler_matrix_pattern_core",
                EMAExtendedAEIntegration::createPatternCoreBlockEntityType);

        EXTENDED_ASSEMBLER_MATRIX_PATTERN_UPLOADER = EMABlocks.BLOCKS.register(
                "extended_assembler_matrix_pattern_uploader",
                ExtendedAssemblerMatrixPatternUploaderBlock::new);
        EXTENDED_ASSEMBLER_MATRIX_PATTERN_UPLOADER_ITEM = EMAItems.ITEMS.register(
                "extended_assembler_matrix_pattern_uploader",
                () -> new BlockItem(EXTENDED_ASSEMBLER_MATRIX_PATTERN_UPLOADER.get(), new Item.Properties()));
        EXTENDED_ASSEMBLER_MATRIX_PATTERN_UPLOADER_ENTITY = EMABlockEntities.BLOCK_ENTITIES.register(
                "extended_assembler_matrix_pattern_uploader",
                EMAExtendedAEIntegration::createPatternUploaderBlockEntityType);
    }

    private static BlockEntityType<ExtendedAssemblerMatrixPatternCoreBlockEntity> createPatternCoreBlockEntityType() {
        var typeHolder = new AtomicReference<BlockEntityType<ExtendedAssemblerMatrixPatternCoreBlockEntity>>();
        BlockEntityType.BlockEntitySupplier<ExtendedAssemblerMatrixPatternCoreBlockEntity> supplier =
                (pos, state) -> new ExtendedAssemblerMatrixPatternCoreBlockEntity(typeHolder.get(), pos, state);
        var block = EXTENDED_ASSEMBLER_MATRIX_PATTERN_CORE.get();
        var type = BlockEntityType.Builder.of(supplier, block).build(null);
        typeHolder.setPlain(type);
        block.setBlockEntity(ExtendedAssemblerMatrixPatternCoreBlockEntity.class, type, null, null);
        AEBaseBlockEntity.registerBlockEntityItem(type, EXTENDED_ASSEMBLER_MATRIX_PATTERN_CORE_ITEM.get());
        return type;
    }

    private static BlockEntityType<ExtendedAssemblerMatrixPatternUploaderBlockEntity> createPatternUploaderBlockEntityType() {
        var typeHolder = new AtomicReference<BlockEntityType<ExtendedAssemblerMatrixPatternUploaderBlockEntity>>();
        BlockEntityType.BlockEntitySupplier<ExtendedAssemblerMatrixPatternUploaderBlockEntity> supplier =
                (pos, state) -> new ExtendedAssemblerMatrixPatternUploaderBlockEntity(typeHolder.get(), pos, state);
        var block = EXTENDED_ASSEMBLER_MATRIX_PATTERN_UPLOADER.get();
        var type = BlockEntityType.Builder.of(supplier, block).build(null);
        typeHolder.setPlain(type);
        block.setBlockEntity(ExtendedAssemblerMatrixPatternUploaderBlockEntity.class, type, null, null);
        AEBaseBlockEntity.registerBlockEntityItem(type, EXTENDED_ASSEMBLER_MATRIX_PATTERN_UPLOADER_ITEM.get());
        return type;
    }
}
