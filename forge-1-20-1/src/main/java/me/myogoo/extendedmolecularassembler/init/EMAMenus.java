package me.myogoo.extendedmolecularassembler.init;

import me.myogoo.extendedmolecularassembler.ExtendedMolecularAssembler;
import me.myogoo.extendedmolecularassembler.menu.ExtendedMolecularAssemblerMenu;
import me.myogoo.extendedmolecularassembler.menu.pattern.ExtendedPatternEncodingTermMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class EMAMenus {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, ExtendedMolecularAssembler.MODID);

    public static final RegistryObject<MenuType<ExtendedMolecularAssemblerMenu>> EXTENDED_MOLECULAR_ASSEMBLER =
            MENUS.register("extended_molecular_assembler", () -> ExtendedMolecularAssemblerMenu.TYPE);
    public static final RegistryObject<MenuType<ExtendedPatternEncodingTermMenu>> EXTENDED_PATTERN_ENCODING_TERMINAL =
            MENUS.register("extended_pattern_encoding_terminal", () -> ExtendedPatternEncodingTermMenu.TYPE);

    private EMAMenus() {
    }
}
