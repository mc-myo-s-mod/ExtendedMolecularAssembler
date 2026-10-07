package me.myogoo.extendedmolecularassembler.init;

import appeng.api.crafting.PatternDetailsHelper;
import me.myogoo.extendedmolecularassembler.ExtendedMolecularAssembler;
import me.myogoo.extendedmolecularassembler.item.AssemblerUpgradeKitItem;
import me.myogoo.extendedmolecularassembler.pattern.ExtendedTableCraftingPattern;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.Nullable;

public final class EMAItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ExtendedMolecularAssembler.MODID);

    public static final DeferredItem<BlockItem> EXTENDED_MOLECULAR_ASSEMBLER =
            ITEMS.register("extended_molecular_assembler",
                    () -> new BlockItem(EMABlocks.EXTENDED_MOLECULAR_ASSEMBLER.get(), new Item.Properties()));
    @Nullable
    public static DeferredItem<BlockItem> EX_EXTENDED_MOLECULAR_ASSEMBLER;
    @Nullable
    public static DeferredItem<AssemblerUpgradeKitItem> EX_EXTENDED_ASSEMBLER_UPGRADE_KIT;

    public static final DeferredItem<BlockItem> BASIC_ME_CRAFTING_PROVIDER =
            ITEMS.register("basic_me_crafting_provider",
                    () -> new BlockItem(EMABlocks.BASIC_ME_CRAFTING_PROVIDER.get(), new Item.Properties()));
    public static final DeferredItem<BlockItem> ADVANCED_ME_CRAFTING_PROVIDER =
            ITEMS.register("advanced_me_crafting_provider",
                    () -> new BlockItem(EMABlocks.ADVANCED_ME_CRAFTING_PROVIDER.get(), new Item.Properties()));
    public static final DeferredItem<BlockItem> ELITE_ME_CRAFTING_PROVIDER =
            ITEMS.register("elite_me_crafting_provider",
                    () -> new BlockItem(EMABlocks.ELITE_ME_CRAFTING_PROVIDER.get(), new Item.Properties()));
    public static final DeferredItem<BlockItem> ULTIMATE_ME_CRAFTING_PROVIDER =
            ITEMS.register("ultimate_me_crafting_provider",
                    () -> new BlockItem(EMABlocks.ULTIMATE_ME_CRAFTING_PROVIDER.get(), new Item.Properties()));
    public static final DeferredItem<BlockItem> RE_AVARITIA_SCULK_ME_CRAFTING_PROVIDER =
            ITEMS.register("re_avaritia_sculk_me_crafting_provider",
                    () -> new BlockItem(EMABlocks.RE_AVARITIA_SCULK_ME_CRAFTING_PROVIDER.get(), new Item.Properties()));
    public static final DeferredItem<BlockItem> RE_AVARITIA_NETHER_ME_CRAFTING_PROVIDER =
            ITEMS.register("re_avaritia_nether_me_crafting_provider",
                    () -> new BlockItem(EMABlocks.RE_AVARITIA_NETHER_ME_CRAFTING_PROVIDER.get(), new Item.Properties()));
    public static final DeferredItem<BlockItem> RE_AVARITIA_END_ME_CRAFTING_PROVIDER =
            ITEMS.register("re_avaritia_end_me_crafting_provider",
                    () -> new BlockItem(EMABlocks.RE_AVARITIA_END_ME_CRAFTING_PROVIDER.get(), new Item.Properties()));
    public static final DeferredItem<BlockItem> XTREME_ME_CRAFTING_PROVIDER =
            ITEMS.register("xtreme_me_crafting_provider",
                    () -> new BlockItem(EMABlocks.XTREME_ME_CRAFTING_PROVIDER.get(), new Item.Properties()));

    public static final DeferredItem<Item> EXTENDED_CRAFTING_PATTERN =
            ITEMS.register("extended_crafting_pattern", () -> PatternDetailsHelper
                    .encodedPatternItemBuilder(ExtendedTableCraftingPattern::new)
                    .invalidPatternTooltip(ExtendedTableCraftingPattern::getInvalidPatternTooltip)
                    .itemProperties(new Item.Properties().stacksTo(1))
                    .build());

    private EMAItems() {
    }

    public static void registerExtendedAEDeferred() {
        if (EX_EXTENDED_MOLECULAR_ASSEMBLER == null) {
            EX_EXTENDED_MOLECULAR_ASSEMBLER = ITEMS.register(
                    "ex_extended_molecular_assembler",
                    () -> new BlockItem(EMABlocks.EX_EXTENDED_MOLECULAR_ASSEMBLER.get(), new Item.Properties()));
            EX_EXTENDED_ASSEMBLER_UPGRADE_KIT = ITEMS.register(
                    "ex_extended_molecular_assembler_upgrade_kit", () -> new AssemblerUpgradeKitItem(new Item.Properties()));
        }
    }

}
