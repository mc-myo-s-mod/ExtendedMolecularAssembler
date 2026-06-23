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

    public static final RegistryObject<Item> EXTENDED_CRAFTING_PATTERN =
            ITEMS.register("extended_crafting_pattern", () -> new Item(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<WirelessExtendedPatternEncodingTerminalItem>
            WIRELESS_EXTENDED_PATTERN_ENCODING_TERMINAL = ITEMS.register(
                    "wireless_extended_pattern_encoding_terminal",
                    WirelessExtendedPatternEncodingTerminalItem::new);

    private EMAItems() {
    }
}
