package me.myogoo.extendedmolecularassembler.init;

import appeng.blockentity.AEBaseBlockEntity;
import me.myogoo.extendedmolecularassembler.ExtendedMolecularAssembler;
import me.myogoo.extendedmolecularassembler.block.ExtendedMolecularAssemblerBlock;
import me.myogoo.extendedmolecularassembler.block.TieredMECraftingProviderBlock;
import me.myogoo.extendedmolecularassembler.block.TieredMECraftingProviderTier;
import me.myogoo.extendedmolecularassembler.block.blockentity.ExtendedMolecularAssemblerBlockEntity;
import me.myogoo.extendedmolecularassembler.block.blockentity.TieredMECraftingProviderBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.concurrent.atomic.AtomicReference;

public final class EMABlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, ExtendedMolecularAssembler.MODID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ExtendedMolecularAssemblerBlockEntity>>
            EXTENDED_MOLECULAR_ASSEMBLER = BLOCK_ENTITIES.register("extended_molecular_assembler",
                    () -> createAssemblerType(EMABlocks.EXTENDED_MOLECULAR_ASSEMBLER.get()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ExtendedMolecularAssemblerBlockEntity>>
            EX_EXTENDED_MOLECULAR_ASSEMBLER = BLOCK_ENTITIES.register("ex_extended_molecular_assembler",
                    () -> createAssemblerType(EMABlocks.EX_EXTENDED_MOLECULAR_ASSEMBLER.get()));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TieredMECraftingProviderBlockEntity>>
            BASIC_ME_CRAFTING_PROVIDER = BLOCK_ENTITIES.register("basic_me_crafting_provider",
                    () -> createProviderType(EMABlocks.BASIC_ME_CRAFTING_PROVIDER.get(),
                            TieredMECraftingProviderTier.BASIC));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TieredMECraftingProviderBlockEntity>>
            ADVANCED_ME_CRAFTING_PROVIDER = BLOCK_ENTITIES.register("advanced_me_crafting_provider",
                    () -> createProviderType(EMABlocks.ADVANCED_ME_CRAFTING_PROVIDER.get(),
                            TieredMECraftingProviderTier.ADVANCED));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TieredMECraftingProviderBlockEntity>>
            ELITE_ME_CRAFTING_PROVIDER = BLOCK_ENTITIES.register("elite_me_crafting_provider",
                    () -> createProviderType(EMABlocks.ELITE_ME_CRAFTING_PROVIDER.get(),
                            TieredMECraftingProviderTier.ELITE));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TieredMECraftingProviderBlockEntity>>
            ULTIMATE_ME_CRAFTING_PROVIDER = BLOCK_ENTITIES.register("ultimate_me_crafting_provider",
                    () -> createProviderType(EMABlocks.ULTIMATE_ME_CRAFTING_PROVIDER.get(),
                            TieredMECraftingProviderTier.ULTIMATE));

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

    private static BlockEntityType<TieredMECraftingProviderBlockEntity> createProviderType(
            TieredMECraftingProviderBlock block, TieredMECraftingProviderTier tier) {
        var typeHolder = new AtomicReference<BlockEntityType<TieredMECraftingProviderBlockEntity>>();
        BlockEntityType.BlockEntitySupplier<TieredMECraftingProviderBlockEntity> supplier =
                (pos, state) -> new TieredMECraftingProviderBlockEntity(typeHolder.get(), pos, state, tier);
        var type = BlockEntityType.Builder.of(supplier, block).build(null);
        typeHolder.setPlain(type);
        block.setBlockEntity(TieredMECraftingProviderBlockEntity.class, type, null, null);
        return type;
    }

    public static void registerBlockEntityItems() {
        AEBaseBlockEntity.registerBlockEntityItem(
                EXTENDED_MOLECULAR_ASSEMBLER.get(),
                EMAItems.EXTENDED_MOLECULAR_ASSEMBLER.get());
        AEBaseBlockEntity.registerBlockEntityItem(
                EX_EXTENDED_MOLECULAR_ASSEMBLER.get(),
                EMAItems.EX_EXTENDED_MOLECULAR_ASSEMBLER.get());
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
    }

    private EMABlockEntities() {
    }
}
