package me.myogoo.extendedmolecularassembler.init;

import me.myogoo.extendedmolecularassembler.ExtendedMolecularAssembler;
import me.myogoo.extendedmolecularassembler.block.ExtendedMolecularAssemblerBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class EMABlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(ExtendedMolecularAssembler.MODID);

    public static final DeferredBlock<ExtendedMolecularAssemblerBlock> EXTENDED_MOLECULAR_ASSEMBLER =
            BLOCKS.register("extended_molecular_assembler", () -> new ExtendedMolecularAssemblerBlock(
                    assemblerProperties()));
    public static final DeferredBlock<ExtendedMolecularAssemblerBlock> EX_EXTENDED_MOLECULAR_ASSEMBLER =
            BLOCKS.register("ex_extended_molecular_assembler", () -> new ExtendedMolecularAssemblerBlock(
                    assemblerProperties()));

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
