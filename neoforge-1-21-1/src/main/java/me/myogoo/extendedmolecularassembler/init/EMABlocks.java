package me.myogoo.extendedmolecularassembler.init;

import me.myogoo.extendedmolecularassembler.ExtendedMolecularAssembler;
import me.myogoo.extendedmolecularassembler.block.ExtendedMolecularAssemblerBlock;
import me.myogoo.extendedmolecularassembler.block.TieredMECraftingProviderBlock;
import me.myogoo.extendedmolecularassembler.block.TieredMECraftingProviderTier;
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

    public static final DeferredBlock<TieredMECraftingProviderBlock> BASIC_ME_CRAFTING_PROVIDER =
            registerProvider(TieredMECraftingProviderTier.BASIC);
    public static final DeferredBlock<TieredMECraftingProviderBlock> ADVANCED_ME_CRAFTING_PROVIDER =
            registerProvider(TieredMECraftingProviderTier.ADVANCED);
    public static final DeferredBlock<TieredMECraftingProviderBlock> ELITE_ME_CRAFTING_PROVIDER =
            registerProvider(TieredMECraftingProviderTier.ELITE);
    public static final DeferredBlock<TieredMECraftingProviderBlock> ULTIMATE_ME_CRAFTING_PROVIDER =
            registerProvider(TieredMECraftingProviderTier.ULTIMATE);
    public static final DeferredBlock<TieredMECraftingProviderBlock> RE_AVARITIA_SCULK_ME_CRAFTING_PROVIDER =
            registerProvider(TieredMECraftingProviderTier.RE_AVARITIA_SCULK);
    public static final DeferredBlock<TieredMECraftingProviderBlock> RE_AVARITIA_NETHER_ME_CRAFTING_PROVIDER =
            registerProvider(TieredMECraftingProviderTier.RE_AVARITIA_NETHER);
    public static final DeferredBlock<TieredMECraftingProviderBlock> RE_AVARITIA_END_ME_CRAFTING_PROVIDER =
            registerProvider(TieredMECraftingProviderTier.RE_AVARITIA_END);
    public static final DeferredBlock<TieredMECraftingProviderBlock> XTREME_ME_CRAFTING_PROVIDER =
            registerProvider(TieredMECraftingProviderTier.XTREME);

    private static DeferredBlock<TieredMECraftingProviderBlock> registerProvider(TieredMECraftingProviderTier tier) {
        return BLOCKS.register(tier.blockId(), () -> new TieredMECraftingProviderBlock(tier, providerProperties()));
    }

    private static BlockBehaviour.Properties assemblerProperties() {
        return BlockBehaviour.Properties.of()
                .mapColor(MapColor.METAL)
                .strength(3.5F)
                .sound(SoundType.METAL)
                .requiresCorrectToolForDrops()
                .noOcclusion();
    }

    private static BlockBehaviour.Properties providerProperties() {
        return BlockBehaviour.Properties.of()
                .mapColor(MapColor.METAL)
                .strength(2.5F)
                .sound(SoundType.METAL)
                .requiresCorrectToolForDrops();
    }

    private EMABlocks() {
    }
}
