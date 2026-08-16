package me.myogoo.extendedmolecularassembler.init;

import me.myogoo.extendedmolecularassembler.ExtendedMolecularAssembler;
import me.myogoo.extendedmolecularassembler.api.annotation.ExPatternProvider;
import me.myogoo.extendedmolecularassembler.lang.EMATranslationKey;
import me.myogoo.myotus.api.MyotusAPI;
import me.myogoo.myotus.api.annotation.mods.AE2WTLib;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public final class EMACreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ExtendedMolecularAssembler.MODID);

    public static final RegistryObject<CreativeModeTab> EXTENDED_MOLECULAR_ASSEMBLER =
            CREATIVE_MODE_TABS.register("extended_molecular_assembler", () -> CreativeModeTab.builder()
                    .title(Component.translatable(EMATranslationKey.ITEM_GROUP.key()))
                    .icon(() -> EMAItems.EXTENDED_MOLECULAR_ASSEMBLER.get().getDefaultInstance())
                    .displayItems((parameters, output) -> {
                        output.accept(EMAItems.EXTENDED_MOLECULAR_ASSEMBLER.get());
                        if (MyotusAPI.integrations().isLoaded(ExPatternProvider.class)) {
                            output.accept(EMAItems.EX_EXTENDED_MOLECULAR_ASSEMBLER.get());
                        }
                        output.accept(EMAItems.BASIC_ME_CRAFTING_PROVIDER.get());
                        output.accept(EMAItems.ADVANCED_ME_CRAFTING_PROVIDER.get());
                        output.accept(EMAItems.ELITE_ME_CRAFTING_PROVIDER.get());
                        output.accept(EMAItems.ULTIMATE_ME_CRAFTING_PROVIDER.get());
                        output.accept(EMAItems.RE_AVARITIA_SCULK_ME_CRAFTING_PROVIDER.get());
                        output.accept(EMAItems.RE_AVARITIA_NETHER_ME_CRAFTING_PROVIDER.get());
                        output.accept(EMAItems.RE_AVARITIA_END_ME_CRAFTING_PROVIDER.get());
                        output.accept(EMAItems.XTREME_ME_CRAFTING_PROVIDER.get());
                        output.accept(EMAParts.EXTENDED_PATTERN_ENCODING_TERMINAL.get());
                        EMAOptionalIntegrations.addCreativeTabItems(output);
                        if (MyotusAPI.integrations().isLoaded(AE2WTLib.class)) {
                            output.accept(EMAItems.WIRELESS_EXTENDED_PATTERN_ENCODING_TERMINAL.get());
                        }
                    })
                    .build());

    private EMACreativeModeTabs() {
    }
}
