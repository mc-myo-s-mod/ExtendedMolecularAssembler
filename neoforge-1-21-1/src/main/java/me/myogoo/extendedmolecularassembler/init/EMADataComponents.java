package me.myogoo.extendedmolecularassembler.init;

import me.myogoo.extendedmolecularassembler.ExtendedMolecularAssembler;
import me.myogoo.extendedmolecularassembler.pattern.EncodedExtendedCraftingPattern;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Consumer;

public final class EMADataComponents {
    public static final DeferredRegister.DataComponents REGISTER =
            DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, ExtendedMolecularAssembler.MODID);

    public static final DataComponentType<EncodedExtendedCraftingPattern> ENCODED_EXTENDED_CRAFTING_PATTERN =
            register("encoded_extended_crafting_pattern", builder -> builder
                    .persistent(EncodedExtendedCraftingPattern.CODEC)
                    .networkSynchronized(EncodedExtendedCraftingPattern.STREAM_CODEC));

    private EMADataComponents() {
    }

    private static <T> DataComponentType<T> register(String name, Consumer<DataComponentType.Builder<T>> customizer) {
        var builder = DataComponentType.<T>builder();
        customizer.accept(builder);
        var componentType = builder.build();
        REGISTER.register(name, () -> componentType);
        return componentType;
    }
}
