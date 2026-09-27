package me.myogoo.extendedmolecularassembler.init;

import appeng.blockentity.AEBaseBlockEntity;
import me.myogoo.extendedmolecularassembler.ExtendedMolecularAssembler;
import me.myogoo.extendedmolecularassembler.block.ExtendedMolecularAssemblerBlock;
import me.myogoo.extendedmolecularassembler.block.ExportMECraftingProviderBlock;
import me.myogoo.extendedmolecularassembler.block.ExportMECraftingProviderTier;
import me.myogoo.extendedmolecularassembler.block.blockentity.ExtendedMolecularAssemblerBlockEntity;
import me.myogoo.extendedmolecularassembler.block.blockentity.ExportMECraftingProviderBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public final class EMABlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, ExtendedMolecularAssembler.MODID);

    public static final RegistryObject<BlockEntityType<ExtendedMolecularAssemblerBlockEntity>>
            EXTENDED_MOLECULAR_ASSEMBLER = BLOCK_ENTITIES.register("extended_molecular_assembler",
                    () -> createAssemblerType(EMABlocks.EXTENDED_MOLECULAR_ASSEMBLER.get()));
    @Nullable
    public static RegistryObject<BlockEntityType<ExtendedMolecularAssemblerBlockEntity>>
            EX_EXTENDED_MOLECULAR_ASSEMBLER;
    @Nullable
    public static RegistryObject<BlockEntityType<ExtendedMolecularAssemblerBlockEntity>>
            EX_EPIC_MOLECULAR_ASSEMBLER;
    @Nullable
    public static RegistryObject<BlockEntityType<ExtendedMolecularAssemblerBlockEntity>>
            EX_LEGENDARY_MOLECULAR_ASSEMBLER;

    public static final RegistryObject<BlockEntityType<ExtendedMolecularAssemblerBlockEntity>>
            EPIC_MOLECULAR_ASSEMBLER = BLOCK_ENTITIES.register("epic_molecular_assembler",
                    () -> createAssemblerType(EMABlocks.EPIC_MOLECULAR_ASSEMBLER.get()));
    public static final RegistryObject<BlockEntityType<ExtendedMolecularAssemblerBlockEntity>>
            LEGENDARY_MOLECULAR_ASSEMBLER = BLOCK_ENTITIES.register("legendary_molecular_assembler",
                    () -> createAssemblerType(EMABlocks.LEGENDARY_MOLECULAR_ASSEMBLER.get()));

    public static final RegistryObject<BlockEntityType<ExportMECraftingProviderBlockEntity>>
            BASIC_ME_CRAFTING_PROVIDER = BLOCK_ENTITIES.register("basic_me_crafting_provider",
                    () -> createProviderType(EMABlocks.BASIC_ME_CRAFTING_PROVIDER.get(),
                            ExportMECraftingProviderTier.BASIC));
    public static final RegistryObject<BlockEntityType<ExportMECraftingProviderBlockEntity>>
            ADVANCED_ME_CRAFTING_PROVIDER = BLOCK_ENTITIES.register("advanced_me_crafting_provider",
                    () -> createProviderType(EMABlocks.ADVANCED_ME_CRAFTING_PROVIDER.get(),
                            ExportMECraftingProviderTier.ADVANCED));
    public static final RegistryObject<BlockEntityType<ExportMECraftingProviderBlockEntity>>
            ELITE_ME_CRAFTING_PROVIDER = BLOCK_ENTITIES.register("elite_me_crafting_provider",
                    () -> createProviderType(EMABlocks.ELITE_ME_CRAFTING_PROVIDER.get(),
                            ExportMECraftingProviderTier.ELITE));
    public static final RegistryObject<BlockEntityType<ExportMECraftingProviderBlockEntity>>
            ULTIMATE_ME_CRAFTING_PROVIDER = BLOCK_ENTITIES.register("ultimate_me_crafting_provider",
                    () -> createProviderType(EMABlocks.ULTIMATE_ME_CRAFTING_PROVIDER.get(),
                            ExportMECraftingProviderTier.ULTIMATE));
    public static final RegistryObject<BlockEntityType<ExportMECraftingProviderBlockEntity>>
            RE_AVARITIA_SCULK_ME_CRAFTING_PROVIDER = BLOCK_ENTITIES.register("re_avaritia_sculk_me_crafting_provider",
                    () -> createProviderType(EMABlocks.RE_AVARITIA_SCULK_ME_CRAFTING_PROVIDER.get(),
                            ExportMECraftingProviderTier.RE_AVARITIA_SCULK));
    public static final RegistryObject<BlockEntityType<ExportMECraftingProviderBlockEntity>>
            RE_AVARITIA_NETHER_ME_CRAFTING_PROVIDER = BLOCK_ENTITIES.register("re_avaritia_nether_me_crafting_provider",
                    () -> createProviderType(EMABlocks.RE_AVARITIA_NETHER_ME_CRAFTING_PROVIDER.get(),
                            ExportMECraftingProviderTier.RE_AVARITIA_NETHER));
    public static final RegistryObject<BlockEntityType<ExportMECraftingProviderBlockEntity>>
            RE_AVARITIA_END_ME_CRAFTING_PROVIDER = BLOCK_ENTITIES.register("re_avaritia_end_me_crafting_provider",
                    () -> createProviderType(EMABlocks.RE_AVARITIA_END_ME_CRAFTING_PROVIDER.get(),
                            ExportMECraftingProviderTier.RE_AVARITIA_END));
    public static final RegistryObject<BlockEntityType<ExportMECraftingProviderBlockEntity>>
            XTREME_ME_CRAFTING_PROVIDER = BLOCK_ENTITIES.register("xtreme_me_crafting_provider",
                    () -> createProviderType(EMABlocks.XTREME_ME_CRAFTING_PROVIDER.get(),
                            ExportMECraftingProviderTier.XTREME));

    public static final RegistryObject<BlockEntityType<ExportMECraftingProviderBlockEntity>>
            EPIC_ME_CRAFTING_PROVIDER = BLOCK_ENTITIES.register("epic_me_crafting_provider",
                    () -> createProviderType(EMABlocks.EPIC_ME_CRAFTING_PROVIDER.get(), ExportMECraftingProviderTier.EPIC));
    public static final RegistryObject<BlockEntityType<ExportMECraftingProviderBlockEntity>>
            LEGENDARY_ME_CRAFTING_PROVIDER = BLOCK_ENTITIES.register("legendary_me_crafting_provider",
                    () -> createProviderType(EMABlocks.LEGENDARY_ME_CRAFTING_PROVIDER.get(), ExportMECraftingProviderTier.LEGENDARY));

    private static BlockEntityType<ExtendedMolecularAssemblerBlockEntity> createAssemblerType(
            ExtendedMolecularAssemblerBlock block) {
        var typeHolder = new AtomicReference<BlockEntityType<ExtendedMolecularAssemblerBlockEntity>>();
        BlockEntityType.BlockEntitySupplier<ExtendedMolecularAssemblerBlockEntity> supplier =
                (pos, state) -> new ExtendedMolecularAssemblerBlockEntity(typeHolder.get(), pos, state);
        var type = BlockEntityType.Builder.of(supplier, block).build(null);
        typeHolder.setPlain(type);
        block.setBlockEntity(ExtendedMolecularAssemblerBlockEntity.class, type, null, null);
        return type;
    }

    public static void registerExtendedAEDeferred() {
        if (EX_EXTENDED_MOLECULAR_ASSEMBLER == null) {
            EX_EXTENDED_MOLECULAR_ASSEMBLER = BLOCK_ENTITIES.register(
                    "ex_extended_molecular_assembler",
                    () -> createAssemblerType(EMABlocks.EX_EXTENDED_MOLECULAR_ASSEMBLER.get()));
            EX_EPIC_MOLECULAR_ASSEMBLER = BLOCK_ENTITIES.register(
                    "ex_epic_molecular_assembler",
                    () -> createAssemblerType(EMABlocks.EX_EPIC_MOLECULAR_ASSEMBLER.get()));
            EX_LEGENDARY_MOLECULAR_ASSEMBLER = BLOCK_ENTITIES.register(
                    "ex_legendary_molecular_assembler",
                    () -> createAssemblerType(EMABlocks.EX_LEGENDARY_MOLECULAR_ASSEMBLER.get()));
        }
    }

    private static BlockEntityType<ExportMECraftingProviderBlockEntity> createProviderType(
            ExportMECraftingProviderBlock block, ExportMECraftingProviderTier tier) {
        var typeHolder = new AtomicReference<BlockEntityType<ExportMECraftingProviderBlockEntity>>();
        BlockEntityType.BlockEntitySupplier<ExportMECraftingProviderBlockEntity> supplier =
                (pos, state) -> new ExportMECraftingProviderBlockEntity(typeHolder.get(), pos, state, tier);
        var type = BlockEntityType.Builder.of(supplier, block).build(null);
        typeHolder.setPlain(type);
        block.setBlockEntity(ExportMECraftingProviderBlockEntity.class, type, null, null);
        return type;
    }

    public static void registerBlockEntityItems() {
        AEBaseBlockEntity.registerBlockEntityItem(EPIC_ME_CRAFTING_PROVIDER.get(), EMAItems.EPIC_ME_CRAFTING_PROVIDER.get());
        AEBaseBlockEntity.registerBlockEntityItem(LEGENDARY_ME_CRAFTING_PROVIDER.get(), EMAItems.LEGENDARY_ME_CRAFTING_PROVIDER.get());
        AEBaseBlockEntity.registerBlockEntityItem(EPIC_MOLECULAR_ASSEMBLER.get(), EMAItems.EPIC_MOLECULAR_ASSEMBLER.get());
        AEBaseBlockEntity.registerBlockEntityItem(LEGENDARY_MOLECULAR_ASSEMBLER.get(), EMAItems.LEGENDARY_MOLECULAR_ASSEMBLER.get());
        AEBaseBlockEntity.registerBlockEntityItem(
                EXTENDED_MOLECULAR_ASSEMBLER.get(),
                EMAItems.EXTENDED_MOLECULAR_ASSEMBLER.get());
        if (EX_EXTENDED_MOLECULAR_ASSEMBLER != null && EMAItems.EX_EXTENDED_MOLECULAR_ASSEMBLER != null) {
            AEBaseBlockEntity.registerBlockEntityItem(
                    EX_EXTENDED_MOLECULAR_ASSEMBLER.get(),
                    EMAItems.EX_EXTENDED_MOLECULAR_ASSEMBLER.get());
        }
        if (EX_EPIC_MOLECULAR_ASSEMBLER != null && EMAItems.EX_EPIC_MOLECULAR_ASSEMBLER != null) {
            AEBaseBlockEntity.registerBlockEntityItem(
                    EX_EPIC_MOLECULAR_ASSEMBLER.get(), EMAItems.EX_EPIC_MOLECULAR_ASSEMBLER.get());
        }
        if (EX_LEGENDARY_MOLECULAR_ASSEMBLER != null && EMAItems.EX_LEGENDARY_MOLECULAR_ASSEMBLER != null) {
            AEBaseBlockEntity.registerBlockEntityItem(
                    EX_LEGENDARY_MOLECULAR_ASSEMBLER.get(), EMAItems.EX_LEGENDARY_MOLECULAR_ASSEMBLER.get());
        }
        AEBaseBlockEntity.registerBlockEntityItem(
                BASIC_ME_CRAFTING_PROVIDER.get(),
                EMAItems.BASIC_ME_CRAFTING_PROVIDER.get());
        AEBaseBlockEntity.registerBlockEntityItem(
                ADVANCED_ME_CRAFTING_PROVIDER.get(),
                EMAItems.ADVANCED_ME_CRAFTING_PROVIDER.get());
        AEBaseBlockEntity.registerBlockEntityItem(
                ELITE_ME_CRAFTING_PROVIDER.get(),
                EMAItems.ELITE_ME_CRAFTING_PROVIDER.get());
        AEBaseBlockEntity.registerBlockEntityItem(
                ULTIMATE_ME_CRAFTING_PROVIDER.get(),
                EMAItems.ULTIMATE_ME_CRAFTING_PROVIDER.get());
        AEBaseBlockEntity.registerBlockEntityItem(
                RE_AVARITIA_SCULK_ME_CRAFTING_PROVIDER.get(),
                EMAItems.RE_AVARITIA_SCULK_ME_CRAFTING_PROVIDER.get());
        AEBaseBlockEntity.registerBlockEntityItem(
                RE_AVARITIA_NETHER_ME_CRAFTING_PROVIDER.get(),
                EMAItems.RE_AVARITIA_NETHER_ME_CRAFTING_PROVIDER.get());
        AEBaseBlockEntity.registerBlockEntityItem(
                RE_AVARITIA_END_ME_CRAFTING_PROVIDER.get(),
                EMAItems.RE_AVARITIA_END_ME_CRAFTING_PROVIDER.get());
        AEBaseBlockEntity.registerBlockEntityItem(
                XTREME_ME_CRAFTING_PROVIDER.get(),
                EMAItems.XTREME_ME_CRAFTING_PROVIDER.get());
    }

    public static List<BlockEntityType<ExportMECraftingProviderBlockEntity>> providerTypes() {
        return List.of(
                BASIC_ME_CRAFTING_PROVIDER.get(),
                ADVANCED_ME_CRAFTING_PROVIDER.get(),
                ELITE_ME_CRAFTING_PROVIDER.get(),
                ULTIMATE_ME_CRAFTING_PROVIDER.get(),
                RE_AVARITIA_SCULK_ME_CRAFTING_PROVIDER.get(),
                RE_AVARITIA_NETHER_ME_CRAFTING_PROVIDER.get(),
                RE_AVARITIA_END_ME_CRAFTING_PROVIDER.get(),
                XTREME_ME_CRAFTING_PROVIDER.get(),
                EPIC_ME_CRAFTING_PROVIDER.get(),
                LEGENDARY_ME_CRAFTING_PROVIDER.get());
    }

    private EMABlockEntities() {
    }
}
