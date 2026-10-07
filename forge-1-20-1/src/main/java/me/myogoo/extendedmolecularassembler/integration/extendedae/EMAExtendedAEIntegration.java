package me.myogoo.extendedmolecularassembler.integration.extendedae;

import appeng.blockentity.AEBaseBlockEntity;
import com.glodblock.github.extendedae.container.pattern.PatternGuiHandler;
import me.myogoo.extendedmolecularassembler.init.EMABlockEntities;
import me.myogoo.extendedmolecularassembler.init.EMABlocks;
import me.myogoo.extendedmolecularassembler.init.EMAItems;
import me.myogoo.extendedmolecularassembler.init.EMAMenus;
import me.myogoo.extendedmolecularassembler.integration.extendedae.menu.ExtendedAssemblerMatrixPatternCoreMenu;
import me.myogoo.extendedmolecularassembler.integration.extendedae.network.EMAMatrixPatternCoreUpdatePacket;
import me.myogoo.extendedmolecularassembler.integration.extendedae.network.EMAOpenExtendedAEAssemblerMatrixScreenPacket;
import me.myogoo.extendedmolecularassembler.integration.extendedae.menu.ExtendedCraftingPatternViewMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import me.myogoo.extendedmolecularassembler.init.EMANetwork;
import net.minecraftforge.network.NetworkDirection;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.registries.RegistryObject;

import java.util.concurrent.atomic.AtomicReference;

public final class EMAExtendedAEIntegration {
    public static RegistryObject<ExtendedAssemblerMatrixPatternCoreBlock> EXTENDED_ASSEMBLER_MATRIX_PATTERN_CORE;
    public static RegistryObject<ExtendedAssemblerMatrixCraftingCoreBlock> EXTENDED_ASSEMBLER_MATRIX_CRAFTING_CORE;
    public static RegistryObject<ExtendedAssemblerMatrixPatternUploaderBlock> EXTENDED_ASSEMBLER_MATRIX_PATTERN_UPLOADER;
    public static RegistryObject<ExtendedAssemblerMatrixPatternCoreBlock> EXTENDED_ASSEMBLER_MATRIX_PATTERN_CORE_PLUS;
    public static RegistryObject<ExtendedAssemblerMatrixCraftingCoreBlock> EXTENDED_ASSEMBLER_MATRIX_CRAFTING_CORE_PLUS;
    public static RegistryObject<BlockItem> EXTENDED_ASSEMBLER_MATRIX_PATTERN_CORE_ITEM;
    public static RegistryObject<BlockItem> EXTENDED_ASSEMBLER_MATRIX_CRAFTING_CORE_ITEM;
    public static RegistryObject<BlockItem> EXTENDED_ASSEMBLER_MATRIX_PATTERN_UPLOADER_ITEM;
    public static RegistryObject<BlockItem> EXTENDED_ASSEMBLER_MATRIX_PATTERN_CORE_PLUS_ITEM;
    public static RegistryObject<BlockItem> EXTENDED_ASSEMBLER_MATRIX_CRAFTING_CORE_PLUS_ITEM;
    public static RegistryObject<ExtendedAssemblerMatrixCraftingCoreBlock> EPIC_ASSEMBLER_MATRIX_CRAFTING_CORE;
    public static RegistryObject<ExtendedAssemblerMatrixCraftingCoreBlock> LEGENDARY_ASSEMBLER_MATRIX_CRAFTING_CORE;
    public static RegistryObject<BlockItem> EPIC_ASSEMBLER_MATRIX_CRAFTING_CORE_ITEM;
    public static RegistryObject<BlockItem> LEGENDARY_ASSEMBLER_MATRIX_CRAFTING_CORE_ITEM;
    public static RegistryObject<ExtendedAssemblerMatrixCraftingCoreBlock> EPIC_ASSEMBLER_MATRIX_CRAFTING_CORE_PLUS;
    public static RegistryObject<ExtendedAssemblerMatrixCraftingCoreBlock> LEGENDARY_ASSEMBLER_MATRIX_CRAFTING_CORE_PLUS;
    public static RegistryObject<BlockItem> EPIC_ASSEMBLER_MATRIX_CRAFTING_CORE_PLUS_ITEM;
    public static RegistryObject<BlockItem> LEGENDARY_ASSEMBLER_MATRIX_CRAFTING_CORE_PLUS_ITEM;
    public static RegistryObject<BlockEntityType<ExtendedAssemblerMatrixPatternCoreBlockEntity>>
            EXTENDED_ASSEMBLER_MATRIX_PATTERN_CORE_BE;
    public static RegistryObject<BlockEntityType<ExtendedAssemblerMatrixCraftingCoreBlockEntity>>
            EXTENDED_ASSEMBLER_MATRIX_CRAFTING_CORE_BE;
    public static RegistryObject<BlockEntityType<ExtendedAssemblerMatrixPatternUploaderBlockEntity>>
            EXTENDED_ASSEMBLER_MATRIX_PATTERN_UPLOADER_BE;
    public static RegistryObject<BlockEntityType<ExtendedAssemblerMatrixPatternCoreBlockEntity>>
            EXTENDED_ASSEMBLER_MATRIX_PATTERN_CORE_PLUS_BE;
    public static RegistryObject<BlockEntityType<ExtendedAssemblerMatrixCraftingCoreBlockEntity>>
            EXTENDED_ASSEMBLER_MATRIX_CRAFTING_CORE_PLUS_BE;
    public static RegistryObject<BlockEntityType<ExtendedAssemblerMatrixCraftingCoreBlockEntity>>
            EPIC_ASSEMBLER_MATRIX_CRAFTING_CORE_BE;
    public static RegistryObject<BlockEntityType<ExtendedAssemblerMatrixCraftingCoreBlockEntity>>
            LEGENDARY_ASSEMBLER_MATRIX_CRAFTING_CORE_BE;
    public static RegistryObject<BlockEntityType<ExtendedAssemblerMatrixCraftingCoreBlockEntity>>
            EPIC_ASSEMBLER_MATRIX_CRAFTING_CORE_PLUS_BE;
    public static RegistryObject<BlockEntityType<ExtendedAssemblerMatrixCraftingCoreBlockEntity>>
            LEGENDARY_ASSEMBLER_MATRIX_CRAFTING_CORE_PLUS_BE;
    public static RegistryObject<MenuType<ExtendedAssemblerMatrixPatternCoreMenu>>
            EXTENDED_ASSEMBLER_MATRIX_PATTERN_CORE_MENU;
    public static RegistryObject<MenuType<ExtendedCraftingPatternViewMenu>>
            EXTENDED_CRAFTING_PATTERN_VIEW_MENU;


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
        EXTENDED_CRAFTING_PATTERN_VIEW_MENU = EMAMenus.REGISTER.register(
                "extended_crafting_pattern_view",
                () -> ExtendedCraftingPatternViewMenu.TYPE);

        registerExtendedAEPlusDeferred();
        registerEpicAndLegendaryDeferred();
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

    private static void registerEpicAndLegendaryDeferred() {
        EPIC_ASSEMBLER_MATRIX_CRAFTING_CORE = EMABlocks.BLOCKS.register(
                "epic_assembler_matrix_crafting_core",
                () -> new ExtendedAssemblerMatrixCraftingCoreBlock(
                        () -> EPIC_ASSEMBLER_MATRIX_CRAFTING_CORE_ITEM.get()));
        LEGENDARY_ASSEMBLER_MATRIX_CRAFTING_CORE = EMABlocks.BLOCKS.register(
                "legendary_assembler_matrix_crafting_core",
                () -> new ExtendedAssemblerMatrixCraftingCoreBlock(
                        () -> LEGENDARY_ASSEMBLER_MATRIX_CRAFTING_CORE_ITEM.get()));

        EPIC_ASSEMBLER_MATRIX_CRAFTING_CORE_ITEM = EMAItems.ITEMS.register(
                "epic_assembler_matrix_crafting_core",
                () -> new BlockItem(EPIC_ASSEMBLER_MATRIX_CRAFTING_CORE.get(), new net.minecraft.world.item.Item.Properties()));
        LEGENDARY_ASSEMBLER_MATRIX_CRAFTING_CORE_ITEM = EMAItems.ITEMS.register(
                "legendary_assembler_matrix_crafting_core",
                () -> new BlockItem(LEGENDARY_ASSEMBLER_MATRIX_CRAFTING_CORE.get(), new net.minecraft.world.item.Item.Properties()));

        EPIC_ASSEMBLER_MATRIX_CRAFTING_CORE_BE = EMABlockEntities.BLOCK_ENTITIES.register(
                "epic_assembler_matrix_crafting_core",
                () -> createCraftingCoreBlockEntityType(EPIC_ASSEMBLER_MATRIX_CRAFTING_CORE.get(), 8, 11));
        LEGENDARY_ASSEMBLER_MATRIX_CRAFTING_CORE_BE = EMABlockEntities.BLOCK_ENTITIES.register(
                "legendary_assembler_matrix_crafting_core",
                () -> createCraftingCoreBlockEntityType(LEGENDARY_ASSEMBLER_MATRIX_CRAFTING_CORE.get(), 8, 13));

        EPIC_ASSEMBLER_MATRIX_CRAFTING_CORE_PLUS = EMABlocks.BLOCKS.register(
                "epic_assembler_matrix_crafting_core_plus",
                () -> new ExtendedAssemblerMatrixCraftingCoreBlock(
                        () -> EPIC_ASSEMBLER_MATRIX_CRAFTING_CORE_PLUS_ITEM.get()));
        LEGENDARY_ASSEMBLER_MATRIX_CRAFTING_CORE_PLUS = EMABlocks.BLOCKS.register(
                "legendary_assembler_matrix_crafting_core_plus",
                () -> new ExtendedAssemblerMatrixCraftingCoreBlock(
                        () -> LEGENDARY_ASSEMBLER_MATRIX_CRAFTING_CORE_PLUS_ITEM.get()));

        EPIC_ASSEMBLER_MATRIX_CRAFTING_CORE_PLUS_ITEM = EMAItems.ITEMS.register(
                "epic_assembler_matrix_crafting_core_plus",
                () -> new BlockItem(EPIC_ASSEMBLER_MATRIX_CRAFTING_CORE_PLUS.get(), new net.minecraft.world.item.Item.Properties()));
        LEGENDARY_ASSEMBLER_MATRIX_CRAFTING_CORE_PLUS_ITEM = EMAItems.ITEMS.register(
                "legendary_assembler_matrix_crafting_core_plus",
                () -> new BlockItem(LEGENDARY_ASSEMBLER_MATRIX_CRAFTING_CORE_PLUS.get(), new net.minecraft.world.item.Item.Properties()));

        EPIC_ASSEMBLER_MATRIX_CRAFTING_CORE_PLUS_BE = EMABlockEntities.BLOCK_ENTITIES.register(
                "epic_assembler_matrix_crafting_core_plus",
                () -> createCraftingCoreBlockEntityType(EPIC_ASSEMBLER_MATRIX_CRAFTING_CORE_PLUS.get(),
                        ExtendedAssemblerMatrixCraftingCoreBlockEntity.PLUS_THREAD_COUNT, 11));
        LEGENDARY_ASSEMBLER_MATRIX_CRAFTING_CORE_PLUS_BE = EMABlockEntities.BLOCK_ENTITIES.register(
                "legendary_assembler_matrix_crafting_core_plus",
                () -> createCraftingCoreBlockEntityType(LEGENDARY_ASSEMBLER_MATRIX_CRAFTING_CORE_PLUS.get(),
                        ExtendedAssemblerMatrixCraftingCoreBlockEntity.PLUS_THREAD_COUNT, 13));
    }

    public static void addCreativeTabItems(CreativeModeTab.Output output, boolean includePlusStyleContent) {
        output.accept(EXTENDED_ASSEMBLER_MATRIX_PATTERN_CORE_ITEM.get());
        output.accept(EXTENDED_ASSEMBLER_MATRIX_CRAFTING_CORE_ITEM.get());
        output.accept(EPIC_ASSEMBLER_MATRIX_CRAFTING_CORE_ITEM.get());
        output.accept(LEGENDARY_ASSEMBLER_MATRIX_CRAFTING_CORE_ITEM.get());
        if (includePlusStyleContent) {
            output.accept(EXTENDED_ASSEMBLER_MATRIX_PATTERN_UPLOADER_ITEM.get());
            output.accept(EXTENDED_ASSEMBLER_MATRIX_PATTERN_CORE_PLUS_ITEM.get());
            output.accept(EXTENDED_ASSEMBLER_MATRIX_CRAFTING_CORE_PLUS_ITEM.get());
            output.accept(EPIC_ASSEMBLER_MATRIX_CRAFTING_CORE_PLUS_ITEM.get());
            output.accept(LEGENDARY_ASSEMBLER_MATRIX_CRAFTING_CORE_PLUS_ITEM.get());
        }
    }

    public static void registerNetwork() {
        EMANetwork.CHANNEL.messageBuilder(EMAOpenExtendedAEAssemblerMatrixScreenPacket.class, 10,
                        NetworkDirection.PLAY_TO_SERVER)
                .encoder(EMAOpenExtendedAEAssemblerMatrixScreenPacket::encode)
                .decoder(EMAOpenExtendedAEAssemblerMatrixScreenPacket::decode)
                .consumerNetworkThread(EMAOpenExtendedAEAssemblerMatrixScreenPacket::handle)
                .add();
        EMANetwork.CHANNEL.messageBuilder(EMAMatrixPatternCoreUpdatePacket.class, 11,
                        NetworkDirection.PLAY_TO_CLIENT)
                .encoder(EMAMatrixPatternCoreUpdatePacket::encode)
                .decoder(EMAMatrixPatternCoreUpdatePacket::decode)
                .consumerNetworkThread(EMAMatrixPatternCoreUpdatePacket::handle)
                .add();
    }

    public static boolean openPatternView(Player player, ItemStack stack) {
        if (!player.level().isClientSide()) {
            PatternGuiHandler.open(player, ExtendedCraftingPatternViewMenu.ID, stack);
        }
        return true;
    }

    public static ItemStack tryInsertIntoAssemblerMatrix(Level level, BlockPos pos, ItemStack stack) {
        return ExtendedAEAssemblerMatrixBridge.insertOutput(level, pos, stack);
    }

    public static boolean hasEligibleMatrixUploader(Object menu) {
        return ExtendedAssemblerMatrixPatternUploadUtil.hasEligibleMatrixUploader(menu);
    }

    public static boolean canUploadToAssemblerMatrix(ServerPlayer player, Object menu, ItemStack stack) {
        return ExtendedAssemblerMatrixPatternUploadUtil.canUploadFromEncodingMenuToMatrix(player, menu, stack);
    }

    public static boolean assemblerMatrixContainsPattern(ServerPlayer player, Object menu, ItemStack stack) {
        return ExtendedAssemblerMatrixPatternUploadUtil.matrixAlreadyContainsPatternFromEncodingMenu(
                player,
                menu,
                stack);
    }

    public static ItemStack uploadToAssemblerMatrix(ServerPlayer player, Object menu, ItemStack stack) {
        return ExtendedAssemblerMatrixPatternUploadUtil.uploadFromEncodingMenuToMatrix(player, menu, stack);
    }

    private static BlockEntityType<ExtendedAssemblerMatrixPatternCoreBlockEntity> createBlockEntityType() {
        return createPatternCoreBlockEntityType(EXTENDED_ASSEMBLER_MATRIX_PATTERN_CORE.get(),
                ExtendedAssemblerMatrixPatternCoreBlockEntity.DEFAULT_INV_SIZE);
    }

    private static BlockEntityType<ExtendedAssemblerMatrixCraftingCoreBlockEntity> createCraftingCoreBlockEntityType() {
        return createCraftingCoreBlockEntityType(EXTENDED_ASSEMBLER_MATRIX_CRAFTING_CORE.get(),
                ExtendedAssemblerMatrixCraftingCoreBlockEntity.DEFAULT_THREAD_COUNT, 9);
    }

    private static BlockEntityType<ExtendedAssemblerMatrixPatternCoreBlockEntity> createPatternCoreBlockEntityType(
            ExtendedAssemblerMatrixPatternCoreBlock block, int patternSlotCount) {
        var typeHolder = new AtomicReference<BlockEntityType<ExtendedAssemblerMatrixPatternCoreBlockEntity>>();
        BlockEntityType.BlockEntitySupplier<ExtendedAssemblerMatrixPatternCoreBlockEntity> supplier =
                (pos, state) -> new ExtendedAssemblerMatrixPatternCoreBlockEntity(typeHolder.get(), pos, state,
                        patternSlotCount);
        var type = BlockEntityType.Builder.of(supplier, block).build(null);
        typeHolder.setPlain(type);
        block.setBlockEntity(ExtendedAssemblerMatrixPatternCoreBlockEntity.class, type, null, null);
        return type;
    }

    private static BlockEntityType<ExtendedAssemblerMatrixCraftingCoreBlockEntity> createCraftingCoreBlockEntityType(
            ExtendedAssemblerMatrixCraftingCoreBlock block, int threadCount, int gridSide) {
        var typeHolder = new AtomicReference<BlockEntityType<ExtendedAssemblerMatrixCraftingCoreBlockEntity>>();
        BlockEntityType.BlockEntitySupplier<ExtendedAssemblerMatrixCraftingCoreBlockEntity> supplier =
                (pos, state) -> new ExtendedAssemblerMatrixCraftingCoreBlockEntity(typeHolder.get(), pos, state,
                        threadCount, gridSide);
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
        return createPatternCoreBlockEntityType(EXTENDED_ASSEMBLER_MATRIX_PATTERN_CORE_PLUS.get(),
                ExtendedAssemblerMatrixPatternCoreBlockEntity.PLUS_INV_SIZE);
    }

    private static BlockEntityType<ExtendedAssemblerMatrixCraftingCoreBlockEntity> createCraftingCorePlusBlockEntityType() {
        return createCraftingCoreBlockEntityType(EXTENDED_ASSEMBLER_MATRIX_CRAFTING_CORE_PLUS.get(),
                ExtendedAssemblerMatrixCraftingCoreBlockEntity.PLUS_THREAD_COUNT, 9);
    }

    public static void registerBlockEntityItems() {
        AEBaseBlockEntity.registerBlockEntityItem(
                EXTENDED_ASSEMBLER_MATRIX_PATTERN_CORE_BE.get(),
                EXTENDED_ASSEMBLER_MATRIX_PATTERN_CORE_ITEM.get());
        AEBaseBlockEntity.registerBlockEntityItem(
                EXTENDED_ASSEMBLER_MATRIX_CRAFTING_CORE_BE.get(),
                EXTENDED_ASSEMBLER_MATRIX_CRAFTING_CORE_ITEM.get());
        AEBaseBlockEntity.registerBlockEntityItem(
                EPIC_ASSEMBLER_MATRIX_CRAFTING_CORE_BE.get(), EPIC_ASSEMBLER_MATRIX_CRAFTING_CORE_ITEM.get());
        AEBaseBlockEntity.registerBlockEntityItem(
                LEGENDARY_ASSEMBLER_MATRIX_CRAFTING_CORE_BE.get(), LEGENDARY_ASSEMBLER_MATRIX_CRAFTING_CORE_ITEM.get());
        AEBaseBlockEntity.registerBlockEntityItem(
                EPIC_ASSEMBLER_MATRIX_CRAFTING_CORE_PLUS_BE.get(), EPIC_ASSEMBLER_MATRIX_CRAFTING_CORE_PLUS_ITEM.get());
        AEBaseBlockEntity.registerBlockEntityItem(
                LEGENDARY_ASSEMBLER_MATRIX_CRAFTING_CORE_PLUS_BE.get(), LEGENDARY_ASSEMBLER_MATRIX_CRAFTING_CORE_PLUS_ITEM.get());

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
