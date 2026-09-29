package me.myogoo.extendedmolecularassembler.init;

import me.myogoo.extendedmolecularassembler.ExtendedMolecularAssembler;
import me.myogoo.extendedmolecularassembler.block.ExtendedMolecularAssemblerBlock;
import me.myogoo.extendedmolecularassembler.block.ExportMECraftingProviderBlock;
import me.myogoo.extendedmolecularassembler.block.ExportMECraftingProviderTier;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.Nullable;

public final class EMABlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(ExtendedMolecularAssembler.MODID);

    public static final DeferredBlock<ExtendedMolecularAssemblerBlock> EXTENDED_MOLECULAR_ASSEMBLER =
            BLOCKS.registerBlock("extended_molecular_assembler", ExtendedMolecularAssemblerBlock::new,
                    EMABlocks::assemblerProperties);
    @Nullable
    public static DeferredBlock<ExtendedMolecularAssemblerBlock> EX_EXTENDED_MOLECULAR_ASSEMBLER;

    public static final DeferredBlock<ExportMECraftingProviderBlock> BASIC_ME_CRAFTING_PROVIDER =
            registerProvider(ExportMECraftingProviderTier.BASIC);
    public static final DeferredBlock<ExportMECraftingProviderBlock> ADVANCED_ME_CRAFTING_PROVIDER =
            registerProvider(ExportMECraftingProviderTier.ADVANCED);
    public static final DeferredBlock<ExportMECraftingProviderBlock> ELITE_ME_CRAFTING_PROVIDER =
            registerProvider(ExportMECraftingProviderTier.ELITE);
    public static final DeferredBlock<ExportMECraftingProviderBlock> ULTIMATE_ME_CRAFTING_PROVIDER =
            registerProvider(ExportMECraftingProviderTier.ULTIMATE);
    public static final DeferredBlock<ExportMECraftingProviderBlock> RE_AVARITIA_SCULK_ME_CRAFTING_PROVIDER =
            registerProvider(ExportMECraftingProviderTier.RE_AVARITIA_SCULK);
    public static final DeferredBlock<ExportMECraftingProviderBlock> RE_AVARITIA_NETHER_ME_CRAFTING_PROVIDER =
            registerProvider(ExportMECraftingProviderTier.RE_AVARITIA_NETHER);
    public static final DeferredBlock<ExportMECraftingProviderBlock> RE_AVARITIA_END_ME_CRAFTING_PROVIDER =
            registerProvider(ExportMECraftingProviderTier.RE_AVARITIA_END);
    public static final DeferredBlock<ExportMECraftingProviderBlock> XTREME_ME_CRAFTING_PROVIDER =
            registerProvider(ExportMECraftingProviderTier.XTREME);

    private static DeferredBlock<ExportMECraftingProviderBlock> registerProvider(ExportMECraftingProviderTier tier) {
        return BLOCKS.registerBlock(tier.blockId(), properties -> new ExportMECraftingProviderBlock(tier, properties),
                EMABlocks::providerProperties);
    }

    public static void registerExtendedAEDeferred() {
        if (EX_EXTENDED_MOLECULAR_ASSEMBLER == null) {
            EX_EXTENDED_MOLECULAR_ASSEMBLER = BLOCKS.registerBlock("ex_extended_molecular_assembler",
                    ExtendedMolecularAssemblerBlock::new, EMABlocks::assemblerProperties);
        }
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
