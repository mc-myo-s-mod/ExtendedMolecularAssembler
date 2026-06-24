package me.myogoo.extendedmolecularassembler.init;

import appeng.blockentity.AEBaseBlockEntity;
import me.myogoo.extendedmolecularassembler.ExtendedMolecularAssembler;
import me.myogoo.extendedmolecularassembler.block.ExtendedMolecularAssemblerBlock;
import me.myogoo.extendedmolecularassembler.block.TieredMECraftingProviderBlock;
import me.myogoo.extendedmolecularassembler.block.TieredMECraftingProviderTier;
import me.myogoo.extendedmolecularassembler.block.blockentity.ExtendedMolecularAssemblerBlockEntity;
import me.myogoo.extendedmolecularassembler.block.blockentity.TieredMECraftingProviderBlockEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.concurrent.atomic.AtomicReference;

public final class EMABlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, ExtendedMolecularAssembler.MODID);

    public static final RegistryObject<BlockEntityType<ExtendedMolecularAssemblerBlockEntity>> EXTENDED_MOLECULAR_ASSEMBLER =
            BLOCK_ENTITIES.register("extended_molecular_assembler",
                    () -> createAssemblerType(
                            EMABlocks.EXTENDED_MOLECULAR_ASSEMBLER.get(),
                            EMAItems.EXTENDED_MOLECULAR_ASSEMBLER.get()));

    public static final RegistryObject<BlockEntityType<ExtendedMolecularAssemblerBlockEntity>> EX_EXTENDED_MOLECULAR_ASSEMBLER =
            BLOCK_ENTITIES.register("ex_extended_molecular_assembler",
                    () -> createAssemblerType(
                            EMABlocks.EX_EXTENDED_MOLECULAR_ASSEMBLER.get(),
                            EMAItems.EX_EXTENDED_MOLECULAR_ASSEMBLER.get()));

    public static final RegistryObject<BlockEntityType<TieredMECraftingProviderBlockEntity>> BASIC_ME_CRAFTING_PROVIDER =
            BLOCK_ENTITIES.register("basic_me_crafting_provider",
                    () -> createProviderType(TieredMECraftingProviderTier.BASIC,
                            EMABlocks.BASIC_ME_CRAFTING_PROVIDER.get(),
                            EMAItems.BASIC_ME_CRAFTING_PROVIDER.get()));
    public static final RegistryObject<BlockEntityType<TieredMECraftingProviderBlockEntity>> ADVANCED_ME_CRAFTING_PROVIDER =
            BLOCK_ENTITIES.register("advanced_me_crafting_provider",
                    () -> createProviderType(TieredMECraftingProviderTier.ADVANCED,
                            EMABlocks.ADVANCED_ME_CRAFTING_PROVIDER.get(),
                            EMAItems.ADVANCED_ME_CRAFTING_PROVIDER.get()));
    public static final RegistryObject<BlockEntityType<TieredMECraftingProviderBlockEntity>> ELITE_ME_CRAFTING_PROVIDER =
            BLOCK_ENTITIES.register("elite_me_crafting_provider",
                    () -> createProviderType(TieredMECraftingProviderTier.ELITE,
                            EMABlocks.ELITE_ME_CRAFTING_PROVIDER.get(),
                            EMAItems.ELITE_ME_CRAFTING_PROVIDER.get()));
    public static final RegistryObject<BlockEntityType<TieredMECraftingProviderBlockEntity>> ULTIMATE_ME_CRAFTING_PROVIDER =
            BLOCK_ENTITIES.register("ultimate_me_crafting_provider",
                    () -> createProviderType(TieredMECraftingProviderTier.ULTIMATE,
                            EMABlocks.ULTIMATE_ME_CRAFTING_PROVIDER.get(),
                            EMAItems.ULTIMATE_ME_CRAFTING_PROVIDER.get()));

    private static BlockEntityType<ExtendedMolecularAssemblerBlockEntity> createAssemblerType(
            ExtendedMolecularAssemblerBlock block, Item item) {
        var typeHolder = new AtomicReference<BlockEntityType<ExtendedMolecularAssemblerBlockEntity>>();
        BlockEntityType.BlockEntitySupplier<ExtendedMolecularAssemblerBlockEntity> supplier =
                (pos, state) -> new ExtendedMolecularAssemblerBlockEntity(typeHolder.get(), pos, state);
        var type = BlockEntityType.Builder.of(supplier, block).build(null);
        typeHolder.setPlain(type);
        block.setBlockEntity(ExtendedMolecularAssemblerBlockEntity.class, type, null, null);
        AEBaseBlockEntity.registerBlockEntityItem(type, item);
        return type;
    }

    private static BlockEntityType<TieredMECraftingProviderBlockEntity> createProviderType(
            TieredMECraftingProviderTier tier, TieredMECraftingProviderBlock block, Item item) {
        var typeHolder = new AtomicReference<BlockEntityType<TieredMECraftingProviderBlockEntity>>();
        BlockEntityType.BlockEntitySupplier<TieredMECraftingProviderBlockEntity> supplier =
                (pos, state) -> new TieredMECraftingProviderBlockEntity(typeHolder.get(), pos, state, tier);
        var type = BlockEntityType.Builder.of(supplier, block).build(null);
        typeHolder.setPlain(type);
        block.setBlockEntity(TieredMECraftingProviderBlockEntity.class, type, null, null);
        AEBaseBlockEntity.registerBlockEntityItem(type, item);
        return type;
    }

    private EMABlockEntities() {
    }
}
