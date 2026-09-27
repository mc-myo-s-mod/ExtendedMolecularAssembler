package me.myogoo.extendedmolecularassembler.init;

import appeng.api.parts.IPart;
import appeng.api.parts.IPartItem;
import appeng.api.parts.PartModels;
import appeng.core.definitions.ItemDefinition;
import appeng.items.parts.PartItem;
import appeng.items.parts.PartModelsHelper;
import me.myogoo.extendedmolecularassembler.ExtendedMolecularAssembler;
import me.myogoo.extendedmolecularassembler.part.ExtendedPatternEncodingTerminalPart;
import me.myogoo.extendedmolecularassembler.part.EpicPatternEncodingTerminalPart;
import me.myogoo.extendedmolecularassembler.part.LegendaryPatternEncodingTerminalPart;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public final class EMAParts {
    public static final DeferredRegister<Item> REGISTER =
            DeferredRegister.create(ForgeRegistries.ITEMS, ExtendedMolecularAssembler.MODID);

    public static final List<ItemDefinition<? extends PartItem<?>>> TERMINAL_PARTS = new ArrayList<>();

    public static final ItemDefinition<PartItem<ExtendedPatternEncodingTerminalPart>>
            EXTENDED_PATTERN_ENCODING_TERMINAL = createTerminalPart(
                    "extended pattern encoding terminal",
                    "extended_pattern_encoding_terminal",
                    ExtendedPatternEncodingTerminalPart.class,
                    ExtendedPatternEncodingTerminalPart::new);

    public static final ItemDefinition<PartItem<EpicPatternEncodingTerminalPart>> EPIC_PATTERN_ENCODING_TERMINAL =
            createTerminalPart("Epic Pattern Encoding Terminal", "epic_pattern_encoding_terminal",
                    EpicPatternEncodingTerminalPart.class, EpicPatternEncodingTerminalPart::new);
    public static final ItemDefinition<PartItem<LegendaryPatternEncodingTerminalPart>> LEGENDARY_PATTERN_ENCODING_TERMINAL =
            createTerminalPart("Legendary Pattern Encoding Terminal", "legendary_pattern_encoding_terminal",
                    LegendaryPatternEncodingTerminalPart.class, LegendaryPatternEncodingTerminalPart::new);

    private static <T extends IPart> ItemDefinition<PartItem<T>> createTerminalPart(String englishName, String id,
            Class<T> partClass, Function<IPartItem<T>, T> partFactory) {
        PartModels.registerModels(PartModelsHelper.createModels(partClass));
        var item = new PartItem<>(new Item.Properties(), partClass, partFactory);
        REGISTER.register(id, () -> item);
        var definition = new ItemDefinition<>(englishName, ExtendedMolecularAssembler.makeId(id), item);
        TERMINAL_PARTS.add(definition);
        return definition;
    }

    private EMAParts() {
    }
}
