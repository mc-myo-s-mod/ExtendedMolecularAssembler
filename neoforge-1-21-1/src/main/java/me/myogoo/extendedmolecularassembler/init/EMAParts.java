package me.myogoo.extendedmolecularassembler.init;

import appeng.api.parts.IPart;
import appeng.api.parts.IPartItem;
import appeng.api.parts.PartModels;
import appeng.core.definitions.ItemDefinition;
import appeng.items.parts.PartItem;
import appeng.items.parts.PartModelsHelper;
import me.myogoo.extendedmolecularassembler.ExtendedMolecularAssembler;
import me.myogoo.extendedmolecularassembler.part.ExtendedPatternEncodingTerminalPart;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public final class EMAParts {
    public static final DeferredRegister.Items REGISTER = DeferredRegister.createItems(ExtendedMolecularAssembler.MODID);

    public static final List<ItemDefinition<? extends PartItem<?>>> TERMINAL_PARTS = new ArrayList<>();

    public static final ItemDefinition<PartItem<ExtendedPatternEncodingTerminalPart>>
            EXTENDED_PATTERN_ENCODING_TERMINAL = createTerminalPart(
                    "extended pattern encoding terminal",
                    "extended_pattern_encoding_terminal",
                    ExtendedPatternEncodingTerminalPart.class,
                    ExtendedPatternEncodingTerminalPart::new);

    private static <T extends IPart> ItemDefinition<PartItem<T>> createTerminalPart(String englishName, String id,
            Class<T> partClass, Function<IPartItem<T>, T> partFactory) {
        PartModels.registerModels(PartModelsHelper.createModels(partClass));
        var definition = new ItemDefinition<>(englishName,
                REGISTER.registerItem(id, properties -> new PartItem<>(properties, partClass, partFactory)));
        TERMINAL_PARTS.add(definition);
        return definition;
    }

    private EMAParts() {
    }
}
