package me.myogoo.extendedmolecularassembler.init;

import appeng.api.parts.IPart;
import appeng.api.parts.IPartItem;
import appeng.api.parts.PartModels;
import appeng.items.parts.PartItem;
import appeng.items.parts.PartModelsHelper;
import me.myogoo.extendedmolecularassembler.ExtendedMolecularAssembler;
import me.myogoo.extendedmolecularassembler.part.ExtendedPatternEncodingTerminalPart;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Function;

public final class EMAParts {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, ExtendedMolecularAssembler.MODID);

    public static final RegistryObject<PartItem<ExtendedPatternEncodingTerminalPart>>
            EXTENDED_PATTERN_ENCODING_TERMINAL = createTerminalPart(
                    "extended_pattern_encoding_terminal",
                    ExtendedPatternEncodingTerminalPart.class,
                    ExtendedPatternEncodingTerminalPart::new);

    private static <T extends IPart> RegistryObject<PartItem<T>> createTerminalPart(String id,
            Class<T> partClass, Function<IPartItem<T>, T> partFactory) {
        PartModels.registerModels(PartModelsHelper.createModels(partClass));
        return ITEMS.register(id, () -> new PartItem<>(new Item.Properties(), partClass, partFactory));
    }

    private EMAParts() {
    }
}
