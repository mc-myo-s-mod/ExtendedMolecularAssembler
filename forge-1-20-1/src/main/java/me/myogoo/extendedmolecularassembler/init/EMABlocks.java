package me.myogoo.extendedmolecularassembler.init;

import me.myogoo.extendedmolecularassembler.ExtendedMolecularAssembler;
import me.myogoo.extendedmolecularassembler.block.ExtendedMolecularAssemblerBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class EMABlocks {
    public static final DeferredRegister<net.minecraft.world.level.block.Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, ExtendedMolecularAssembler.MODID);

    public static final RegistryObject<ExtendedMolecularAssemblerBlock> EXTENDED_MOLECULAR_ASSEMBLER =
            BLOCKS.register("extended_molecular_assembler",
                    () -> new ExtendedMolecularAssemblerBlock(assemblerProperties()));

    public static final RegistryObject<ExtendedMolecularAssemblerBlock> EX_EXTENDED_MOLECULAR_ASSEMBLER =
            BLOCKS.register("ex_extended_molecular_assembler",
                    () -> new ExtendedMolecularAssemblerBlock(assemblerProperties()));

    private static BlockBehaviour.Properties assemblerProperties() {
        return BlockBehaviour.Properties.of()
                .mapColor(MapColor.METAL)
                .strength(3.5F)
                .sound(SoundType.METAL)
                .requiresCorrectToolForDrops()
                .noOcclusion();
    }

    private EMABlocks() {
    }
}
