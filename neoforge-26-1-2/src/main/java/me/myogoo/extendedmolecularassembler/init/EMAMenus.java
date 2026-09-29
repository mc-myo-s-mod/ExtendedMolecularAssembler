package me.myogoo.extendedmolecularassembler.init;

import me.myogoo.extendedmolecularassembler.ExtendedMolecularAssembler;
import me.myogoo.extendedmolecularassembler.menu.ExtendedMolecularAssemblerMenu;
import me.myogoo.extendedmolecularassembler.menu.pattern.ExtendedPatternEncodingTermMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public final class EMAMenus {
    public static final DeferredRegister<MenuType<?>> REGISTER =
            DeferredRegister.create(Registries.MENU, ExtendedMolecularAssembler.MODID);

    public static final Supplier<MenuType<ExtendedPatternEncodingTermMenu>> EXTENDED_PATTERN_ENCODING_TERMINAL =
            REGISTER.register("extended_pattern_encoding_terminal", () -> ExtendedPatternEncodingTermMenu.TYPE);
    public static final Supplier<MenuType<ExtendedMolecularAssemblerMenu>> EXTENDED_MOLECULAR_ASSEMBLER =
            REGISTER.register("extended_molecular_assembler", () -> ExtendedMolecularAssemblerMenu.TYPE);

    private EMAMenus() {
    }
}
