package me.myogoo.extendedmolecularassembler.init;

import appeng.api.crafting.PatternDetailsHelper;
import appeng.block.AEBaseBlockItem;
import me.myogoo.extendedmolecularassembler.ExtendedMolecularAssembler;
import me.myogoo.extendedmolecularassembler.pattern.ExtendedTableCraftingPattern;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.Nullable;

public final class EMAItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ExtendedMolecularAssembler.MODID);

    public static final DeferredItem<BlockItem> EXTENDED_MOLECULAR_ASSEMBLER =
            ITEMS.registerItem("extended_molecular_assembler",
                    properties -> new AEBaseBlockItem(EMABlocks.EXTENDED_MOLECULAR_ASSEMBLER.get(), properties.useBlockDescriptionPrefix()));
    @Nullable
    public static DeferredItem<BlockItem> EX_EXTENDED_MOLECULAR_ASSEMBLER;

    public static final DeferredItem<BlockItem> BASIC_ME_CRAFTING_PROVIDER =
            ITEMS.registerItem("basic_me_crafting_provider",
                    properties -> new AEBaseBlockItem(EMABlocks.BASIC_ME_CRAFTING_PROVIDER.get(), properties.useBlockDescriptionPrefix()));
    public static final DeferredItem<BlockItem> ADVANCED_ME_CRAFTING_PROVIDER =
            ITEMS.registerItem("advanced_me_crafting_provider",
                    properties -> new AEBaseBlockItem(EMABlocks.ADVANCED_ME_CRAFTING_PROVIDER.get(), properties.useBlockDescriptionPrefix()));
    public static final DeferredItem<BlockItem> ELITE_ME_CRAFTING_PROVIDER =
            ITEMS.registerItem("elite_me_crafting_provider",
                    properties -> new AEBaseBlockItem(EMABlocks.ELITE_ME_CRAFTING_PROVIDER.get(), properties.useBlockDescriptionPrefix()));
    public static final DeferredItem<BlockItem> ULTIMATE_ME_CRAFTING_PROVIDER =
            ITEMS.registerItem("ultimate_me_crafting_provider",
                    properties -> new AEBaseBlockItem(EMABlocks.ULTIMATE_ME_CRAFTING_PROVIDER.get(), properties.useBlockDescriptionPrefix()));
    public static final DeferredItem<BlockItem> RE_AVARITIA_SCULK_ME_CRAFTING_PROVIDER =
            ITEMS.registerItem("re_avaritia_sculk_me_crafting_provider",
                    properties -> new AEBaseBlockItem(EMABlocks.RE_AVARITIA_SCULK_ME_CRAFTING_PROVIDER.get(), properties.useBlockDescriptionPrefix()));
    public static final DeferredItem<BlockItem> RE_AVARITIA_NETHER_ME_CRAFTING_PROVIDER =
            ITEMS.registerItem("re_avaritia_nether_me_crafting_provider",
                    properties -> new AEBaseBlockItem(EMABlocks.RE_AVARITIA_NETHER_ME_CRAFTING_PROVIDER.get(), properties.useBlockDescriptionPrefix()));
    public static final DeferredItem<BlockItem> RE_AVARITIA_END_ME_CRAFTING_PROVIDER =
            ITEMS.registerItem("re_avaritia_end_me_crafting_provider",
                    properties -> new AEBaseBlockItem(EMABlocks.RE_AVARITIA_END_ME_CRAFTING_PROVIDER.get(), properties.useBlockDescriptionPrefix()));
    public static final DeferredItem<BlockItem> XTREME_ME_CRAFTING_PROVIDER =
            ITEMS.registerItem("xtreme_me_crafting_provider",
                    properties -> new AEBaseBlockItem(EMABlocks.XTREME_ME_CRAFTING_PROVIDER.get(), properties.useBlockDescriptionPrefix()));

    public static final DeferredItem<Item> EXTENDED_CRAFTING_PATTERN =
            ITEMS.registerItem("extended_crafting_pattern", properties -> PatternDetailsHelper
                    .encodedPatternItemBuilder(ExtendedTableCraftingPattern::new)
                    .invalidPatternTooltip(ExtendedTableCraftingPattern::getInvalidPatternTooltip)
                    .build(properties));

    private EMAItems() {
    }

    public static void registerExtendedAEDeferred() {
        if (EX_EXTENDED_MOLECULAR_ASSEMBLER == null) {
            EX_EXTENDED_MOLECULAR_ASSEMBLER = ITEMS.registerItem(
                    "ex_extended_molecular_assembler",
                    properties -> new AEBaseBlockItem(EMABlocks.EX_EXTENDED_MOLECULAR_ASSEMBLER.get(), properties.useBlockDescriptionPrefix()));
        }
    }

}
