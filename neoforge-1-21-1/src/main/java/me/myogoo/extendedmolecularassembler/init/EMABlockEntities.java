package me.myogoo.extendedmolecularassembler.init;

import appeng.blockentity.AEBaseBlockEntity;
import me.myogoo.extendedmolecularassembler.ExtendedMolecularAssembler;
import me.myogoo.extendedmolecularassembler.block.ExtendedMolecularAssemblerBlock;
import me.myogoo.extendedmolecularassembler.block.blockentity.ExtendedMolecularAssemblerBlockEntity;
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

    public static void registerBlockEntityItems() {
        AEBaseBlockEntity.registerBlockEntityItem(
                EXTENDED_MOLECULAR_ASSEMBLER.get(),
                EMAItems.EXTENDED_MOLECULAR_ASSEMBLER.get());
        AEBaseBlockEntity.registerBlockEntityItem(
                EX_EXTENDED_MOLECULAR_ASSEMBLER.get(),
                EMAItems.EX_EXTENDED_MOLECULAR_ASSEMBLER.get());
    }

    private EMABlockEntities() {
    }
}
