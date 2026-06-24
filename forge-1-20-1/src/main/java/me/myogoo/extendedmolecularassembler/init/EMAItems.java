package me.myogoo.extendedmolecularassembler.init;

import me.myogoo.extendedmolecularassembler.ExtendedMolecularAssembler;
import me.myogoo.extendedmolecularassembler.item.WirelessExtendedPatternEncodingTerminalItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class EMAItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, ExtendedMolecularAssembler.MODID);

    public static final RegistryObject<BlockItem> EXTENDED_MOLECULAR_ASSEMBLER =
            ITEMS.register("extended_molecular_assembler",
                    () -> new BlockItem(EMABlocks.EXTENDED_MOLECULAR_ASSEMBLER.get(), new Item.Properties()));

    public static final RegistryObject<BlockItem> EX_EXTENDED_MOLECULAR_ASSEMBLER =
            ITEMS.register("ex_extended_molecular_assembler",
                    () -> new BlockItem(EMABlocks.EX_EXTENDED_MOLECULAR_ASSEMBLER.get(), new Item.Properties()));

    public static final RegistryObject<BlockItem> BASIC_ME_CRAFTING_PROVIDER =
            ITEMS.register("basic_me_crafting_provider",
                    () -> new BlockItem(EMABlocks.BASIC_ME_CRAFTING_PROVIDER.get(), new Item.Properties()));
    public static final RegistryObject<BlockItem> ADVANCED_ME_CRAFTING_PROVIDER =
            ITEMS.register("advanced_me_crafting_provider",
                    () -> new BlockItem(EMABlocks.ADVANCED_ME_CRAFTING_PROVIDER.get(), new Item.Properties()));
    public static final RegistryObject<BlockItem> ELITE_ME_CRAFTING_PROVIDER =
            ITEMS.register("elite_me_crafting_provider",
                    () -> new BlockItem(EMABlocks.ELITE_ME_CRAFTING_PROVIDER.get(), new Item.Properties()));
    public static final RegistryObject<BlockItem> ULTIMATE_ME_CRAFTING_PROVIDER =
            ITEMS.register("ultimate_me_crafting_provider",
                    () -> new BlockItem(EMABlocks.ULTIMATE_ME_CRAFTING_PROVIDER.get(), new Item.Properties()));
    public static final RegistryObject<BlockItem> RE_AVARITIA_SCULK_ME_CRAFTING_PROVIDER =
            ITEMS.register("re_avaritia_sculk_me_crafting_provider",
                    () -> new BlockItem(EMABlocks.RE_AVARITIA_SCULK_ME_CRAFTING_PROVIDER.get(), new Item.Properties()));
    public static final RegistryObject<BlockItem> RE_AVARITIA_NETHER_ME_CRAFTING_PROVIDER =
            ITEMS.register("re_avaritia_nether_me_crafting_provider",
                    () -> new BlockItem(EMABlocks.RE_AVARITIA_NETHER_ME_CRAFTING_PROVIDER.get(), new Item.Properties()));
    public static final RegistryObject<BlockItem> RE_AVARITIA_END_ME_CRAFTING_PROVIDER =
            ITEMS.register("re_avaritia_end_me_crafting_provider",
                    () -> new BlockItem(EMABlocks.RE_AVARITIA_END_ME_CRAFTING_PROVIDER.get(), new Item.Properties()));
    public static final RegistryObject<BlockItem> RE_AVARITIA_XTREME_ME_CRAFTING_PROVIDER =
            ITEMS.register("re_avaritia_xtreme_me_crafting_provider",
                    () -> new BlockItem(EMABlocks.RE_AVARITIA_XTREME_ME_CRAFTING_PROVIDER.get(), new Item.Properties()));
    public static final RegistryObject<BlockItem> AVARITIA_NEO_XTREME_ME_CRAFTING_PROVIDER =
            ITEMS.register("avaritia_neo_xtreme_me_crafting_provider",
                    () -> new BlockItem(EMABlocks.AVARITIA_NEO_XTREME_ME_CRAFTING_PROVIDER.get(), new Item.Properties()));

    public static final RegistryObject<Item> EXTENDED_CRAFTING_PATTERN =
            ITEMS.register("extended_crafting_pattern", () -> new Item(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<WirelessExtendedPatternEncodingTerminalItem>
            WIRELESS_EXTENDED_PATTERN_ENCODING_TERMINAL = ITEMS.register(
                    "wireless_extended_pattern_encoding_terminal",
                    WirelessExtendedPatternEncodingTerminalItem::new);

    private EMAItems() {
    }
}
