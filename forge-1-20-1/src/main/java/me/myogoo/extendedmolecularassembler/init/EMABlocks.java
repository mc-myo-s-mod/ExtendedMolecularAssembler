package me.myogoo.extendedmolecularassembler.init;

import me.myogoo.extendedmolecularassembler.ExtendedMolecularAssembler;
import me.myogoo.extendedmolecularassembler.block.ExtendedMolecularAssemblerBlock;
import me.myogoo.extendedmolecularassembler.block.ExportMECraftingProviderBlock;
import me.myogoo.extendedmolecularassembler.block.ExportMECraftingProviderTier;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import org.jetbrains.annotations.Nullable;

public final class EMABlocks {
    public static final DeferredRegister<net.minecraft.world.level.block.Block> BLOCKS = DeferredRegister.create(net.minecraft.core.registries.Registries.BLOCK, ExtendedMolecularAssembler.MODID);

    public static final RegistryObject<ExtendedMolecularAssemblerBlock> EXTENDED_MOLECULAR_ASSEMBLER =
            BLOCKS.register("extended_molecular_assembler", () -> new ExtendedMolecularAssemblerBlock(
                    assemblerProperties()));
    @Nullable
    public static RegistryObject<ExtendedMolecularAssemblerBlock> EX_EXTENDED_MOLECULAR_ASSEMBLER;
    @Nullable
    public static RegistryObject<ExtendedMolecularAssemblerBlock> EX_EPIC_MOLECULAR_ASSEMBLER;
    @Nullable
    public static RegistryObject<ExtendedMolecularAssemblerBlock> EX_LEGENDARY_MOLECULAR_ASSEMBLER;

    public static final RegistryObject<ExtendedMolecularAssemblerBlock> EPIC_MOLECULAR_ASSEMBLER =
            BLOCKS.register("epic_molecular_assembler", () -> new ExtendedMolecularAssemblerBlock(
                    assemblerProperties(), 11));
    public static final RegistryObject<ExtendedMolecularAssemblerBlock> LEGENDARY_MOLECULAR_ASSEMBLER =
            BLOCKS.register("legendary_molecular_assembler", () -> new ExtendedMolecularAssemblerBlock(
                    assemblerProperties(), 13));

    public static final RegistryObject<ExportMECraftingProviderBlock> BASIC_ME_CRAFTING_PROVIDER =
            registerProvider(ExportMECraftingProviderTier.BASIC);
    public static final RegistryObject<ExportMECraftingProviderBlock> ADVANCED_ME_CRAFTING_PROVIDER =
            registerProvider(ExportMECraftingProviderTier.ADVANCED);
    public static final RegistryObject<ExportMECraftingProviderBlock> ELITE_ME_CRAFTING_PROVIDER =
            registerProvider(ExportMECraftingProviderTier.ELITE);
    public static final RegistryObject<ExportMECraftingProviderBlock> ULTIMATE_ME_CRAFTING_PROVIDER =
            registerProvider(ExportMECraftingProviderTier.ULTIMATE);
    public static final RegistryObject<ExportMECraftingProviderBlock> RE_AVARITIA_SCULK_ME_CRAFTING_PROVIDER =
            registerProvider(ExportMECraftingProviderTier.RE_AVARITIA_SCULK);
    public static final RegistryObject<ExportMECraftingProviderBlock> RE_AVARITIA_NETHER_ME_CRAFTING_PROVIDER =
            registerProvider(ExportMECraftingProviderTier.RE_AVARITIA_NETHER);
    public static final RegistryObject<ExportMECraftingProviderBlock> RE_AVARITIA_END_ME_CRAFTING_PROVIDER =
            registerProvider(ExportMECraftingProviderTier.RE_AVARITIA_END);
    public static final RegistryObject<ExportMECraftingProviderBlock> XTREME_ME_CRAFTING_PROVIDER =
            registerProvider(ExportMECraftingProviderTier.XTREME);
    public static final RegistryObject<ExportMECraftingProviderBlock> EPIC_ME_CRAFTING_PROVIDER =
            registerProvider(ExportMECraftingProviderTier.EPIC);
    public static final RegistryObject<ExportMECraftingProviderBlock> LEGENDARY_ME_CRAFTING_PROVIDER =
            registerProvider(ExportMECraftingProviderTier.LEGENDARY);

    private static RegistryObject<ExportMECraftingProviderBlock> registerProvider(ExportMECraftingProviderTier tier) {
        return BLOCKS.register(tier.blockId(), () -> new ExportMECraftingProviderBlock(tier, providerProperties()));
    }

    public static void registerExtendedAEDeferred() {
        if (EX_EXTENDED_MOLECULAR_ASSEMBLER == null) {
            EX_EXTENDED_MOLECULAR_ASSEMBLER = BLOCKS.register(
                    "ex_extended_molecular_assembler",
                    () -> new ExtendedMolecularAssemblerBlock(assemblerProperties(), 9, true));
            EX_EPIC_MOLECULAR_ASSEMBLER = BLOCKS.register(
                    "ex_epic_molecular_assembler",
                    () -> new ExtendedMolecularAssemblerBlock(assemblerProperties(), 11, true));
            EX_LEGENDARY_MOLECULAR_ASSEMBLER = BLOCKS.register(
                    "ex_legendary_molecular_assembler",
                    () -> new ExtendedMolecularAssemblerBlock(assemblerProperties(), 13, true));
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
