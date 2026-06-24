package me.myogoo.extendedmolecularassembler.init;

import me.myogoo.extendedmolecularassembler.ExtendedMolecularAssembler;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class EMACreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ExtendedMolecularAssembler.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> EXTENDED_MOLECULAR_ASSEMBLER =
            CREATIVE_MODE_TABS.register("extended_molecular_assembler", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.extendedmolecularassembler"))
                    .icon(() -> EMAItems.EXTENDED_MOLECULAR_ASSEMBLER.get().getDefaultInstance())
                    .displayItems((parameters, output) -> {
                        output.accept(EMAItems.EXTENDED_MOLECULAR_ASSEMBLER.get());
                        output.accept(EMAItems.EX_EXTENDED_MOLECULAR_ASSEMBLER.get());
                        output.accept(EMAItems.BASIC_ME_CRAFTING_PROVIDER.get());
                        output.accept(EMAItems.ADVANCED_ME_CRAFTING_PROVIDER.get());
                        output.accept(EMAItems.ELITE_ME_CRAFTING_PROVIDER.get());
                        output.accept(EMAItems.ULTIMATE_ME_CRAFTING_PROVIDER.get());
                        output.accept(EMAParts.EXTENDED_PATTERN_ENCODING_TERMINAL);
                        output.accept(EMAItems.wirelessExtendedPatternEncodingTerminal());
                        EMAOptionalIntegrations.addCreativeTabItems(output);
                    })
                    .build());

    private EMACreativeModeTabs() {
    }
}
