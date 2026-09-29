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
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

public final class EMABlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, ExtendedMolecularAssembler.MODID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ExtendedMolecularAssemblerBlockEntity>>
            EXTENDED_MOLECULAR_ASSEMBLER = BLOCK_ENTITIES.register("extended_molecular_assembler",
                    () -> createAssemblerType(EMABlocks.EXTENDED_MOLECULAR_ASSEMBLER.get()));
    @Nullable
    public static DeferredHolder<BlockEntityType<?>, BlockEntityType<ExtendedMolecularAssemblerBlockEntity>>
            EX_EXTENDED_MOLECULAR_ASSEMBLER;

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ExportMECraftingProviderBlockEntity>>
            BASIC_ME_CRAFTING_PROVIDER = BLOCK_ENTITIES.register("basic_me_crafting_provider",
                    () -> createProviderType(EMABlocks.BASIC_ME_CRAFTING_PROVIDER.get(),
                            ExportMECraftingProviderTier.BASIC));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ExportMECraftingProviderBlockEntity>>
            ADVANCED_ME_CRAFTING_PROVIDER = BLOCK_ENTITIES.register("advanced_me_crafting_provider",
                    () -> createProviderType(EMABlocks.ADVANCED_ME_CRAFTING_PROVIDER.get(),
                            ExportMECraftingProviderTier.ADVANCED));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ExportMECraftingProviderBlockEntity>>
            ELITE_ME_CRAFTING_PROVIDER = BLOCK_ENTITIES.register("elite_me_crafting_provider",
                    () -> createProviderType(EMABlocks.ELITE_ME_CRAFTING_PROVIDER.get(),
                            ExportMECraftingProviderTier.ELITE));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ExportMECraftingProviderBlockEntity>>
            ULTIMATE_ME_CRAFTING_PROVIDER = BLOCK_ENTITIES.register("ultimate_me_crafting_provider",
                    () -> createProviderType(EMABlocks.ULTIMATE_ME_CRAFTING_PROVIDER.get(),
                            ExportMECraftingProviderTier.ULTIMATE));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ExportMECraftingProviderBlockEntity>>
            RE_AVARITIA_SCULK_ME_CRAFTING_PROVIDER = BLOCK_ENTITIES.register("re_avaritia_sculk_me_crafting_provider",
                    () -> createProviderType(EMABlocks.RE_AVARITIA_SCULK_ME_CRAFTING_PROVIDER.get(),
                            ExportMECraftingProviderTier.RE_AVARITIA_SCULK));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ExportMECraftingProviderBlockEntity>>
            RE_AVARITIA_NETHER_ME_CRAFTING_PROVIDER = BLOCK_ENTITIES.register("re_avaritia_nether_me_crafting_provider",
                    () -> createProviderType(EMABlocks.RE_AVARITIA_NETHER_ME_CRAFTING_PROVIDER.get(),
                            ExportMECraftingProviderTier.RE_AVARITIA_NETHER));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ExportMECraftingProviderBlockEntity>>
            RE_AVARITIA_END_ME_CRAFTING_PROVIDER = BLOCK_ENTITIES.register("re_avaritia_end_me_crafting_provider",
                    () -> createProviderType(EMABlocks.RE_AVARITIA_END_ME_CRAFTING_PROVIDER.get(),
                            ExportMECraftingProviderTier.RE_AVARITIA_END));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ExportMECraftingProviderBlockEntity>>
            XTREME_ME_CRAFTING_PROVIDER = BLOCK_ENTITIES.register("xtreme_me_crafting_provider",
                    () -> createProviderType(EMABlocks.XTREME_ME_CRAFTING_PROVIDER.get(),
                            ExportMECraftingProviderTier.XTREME));

    private static BlockEntityType<ExtendedMolecularAssemblerBlockEntity> createAssemblerType(
            ExtendedMolecularAssemblerBlock block) {
        var typeHolder = new AtomicReference<BlockEntityType<ExtendedMolecularAssemblerBlockEntity>>();
        BlockEntityType.BlockEntitySupplier<ExtendedMolecularAssemblerBlockEntity> supplier =
                (pos, state) -> new ExtendedMolecularAssemblerBlockEntity(typeHolder.get(), pos, state);
        var type = new BlockEntityType<>(supplier, Set.of(block));
        typeHolder.setPlain(type);
        block.setBlockEntity(ExtendedMolecularAssemblerBlockEntity.class, type, null, null);
        return type;
    }

    public static void registerExtendedAEDeferred() {
        if (EX_EXTENDED_MOLECULAR_ASSEMBLER == null) {
            EX_EXTENDED_MOLECULAR_ASSEMBLER = BLOCK_ENTITIES.register(
                    "ex_extended_molecular_assembler",
                    () -> createAssemblerType(EMABlocks.EX_EXTENDED_MOLECULAR_ASSEMBLER.get()));
        }
    }

    private static BlockEntityType<ExportMECraftingProviderBlockEntity> createProviderType(
            ExportMECraftingProviderBlock block, ExportMECraftingProviderTier tier) {
        var typeHolder = new AtomicReference<BlockEntityType<ExportMECraftingProviderBlockEntity>>();
        BlockEntityType.BlockEntitySupplier<ExportMECraftingProviderBlockEntity> supplier =
                (pos, state) -> new ExportMECraftingProviderBlockEntity(typeHolder.get(), pos, state, tier);
        var type = new BlockEntityType<>(supplier, Set.of(block));
        typeHolder.setPlain(type);
        block.setBlockEntity(ExportMECraftingProviderBlockEntity.class, type, null, null);
        return type;
    }

    public static void registerBlockEntityItems() {
        AEBaseBlockEntity.registerBlockEntityItem(
                EXTENDED_MOLECULAR_ASSEMBLER.get(),
                EMAItems.EXTENDED_MOLECULAR_ASSEMBLER.get());
        if (EX_EXTENDED_MOLECULAR_ASSEMBLER != null && EMAItems.EX_EXTENDED_MOLECULAR_ASSEMBLER != null) {
            AEBaseBlockEntity.registerBlockEntityItem(
                    EX_EXTENDED_MOLECULAR_ASSEMBLER.get(),
                    EMAItems.EX_EXTENDED_MOLECULAR_ASSEMBLER.get());
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
                XTREME_ME_CRAFTING_PROVIDER.get());
    }

    private EMABlockEntities() {
    }
}
