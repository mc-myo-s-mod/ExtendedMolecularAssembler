package me.myogoo.extendedmolecularassembler.init;

import me.myogoo.extendedmolecularassembler.ExtendedMolecularAssembler;
import me.myogoo.extendedmolecularassembler.lang.EMATranslationKey;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;

public final class EMACreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ExtendedMolecularAssembler.MODID);

    public static final RegistryObject<CreativeModeTab> EXTENDED_MOLECULAR_ASSEMBLER =
            CREATIVE_MODE_TABS.register("extended_molecular_assembler", () -> CreativeModeTab.builder()
                    .title(Component.translatable(EMATranslationKey.ITEM_GROUP.key()))
                    .icon(() -> EMAItems.EXTENDED_MOLECULAR_ASSEMBLER.get().getDefaultInstance())
                    .displayItems((parameters, output) -> {
                        // Assemblers: normal tiers, then Ex tiers.
                        output.accept(EMAItems.EXTENDED_MOLECULAR_ASSEMBLER.get());
                        output.accept(EMAItems.EPIC_MOLECULAR_ASSEMBLER.get());
                        output.accept(EMAItems.LEGENDARY_MOLECULAR_ASSEMBLER.get());
                        if (EMAItems.EX_EXTENDED_MOLECULAR_ASSEMBLER != null) {
                            output.accept(EMAItems.EX_EXTENDED_MOLECULAR_ASSEMBLER.get());
                            output.accept(EMAItems.EX_EPIC_MOLECULAR_ASSEMBLER.get());
                            output.accept(EMAItems.EX_LEGENDARY_MOLECULAR_ASSEMBLER.get());
                        }

                        // Kits: tier upgrades, then normal-to-Ex conversions.
                        output.accept(EMAItems.EPIC_ASSEMBLER_UPGRADE_KIT.get());
                        output.accept(EMAItems.LEGENDARY_ASSEMBLER_UPGRADE_KIT.get());
                        if (EMAItems.EX_EXTENDED_MOLECULAR_ASSEMBLER != null) {
                            output.accept(EMAItems.EX_EPIC_ASSEMBLER_UPGRADE_KIT.get());
                            output.accept(EMAItems.EX_LEGENDARY_ASSEMBLER_UPGRADE_KIT.get());
                            output.accept(EMAItems.EX_EXTENDED_ASSEMBLER_UPGRADE_KIT.get());
                            output.accept(EMAItems.EPIC_ASSEMBLER_EX_UPGRADE_KIT.get());
                            output.accept(EMAItems.LEGENDARY_ASSEMBLER_EX_UPGRADE_KIT.get());
                        }

                        // Providers: Extended Crafting tiers, then Avaritia tiers.
                        output.accept(EMAItems.BASIC_ME_CRAFTING_PROVIDER.get());
                        output.accept(EMAItems.ADVANCED_ME_CRAFTING_PROVIDER.get());
                        output.accept(EMAItems.ELITE_ME_CRAFTING_PROVIDER.get());
                        output.accept(EMAItems.ULTIMATE_ME_CRAFTING_PROVIDER.get());
                        output.accept(EMAItems.EPIC_ME_CRAFTING_PROVIDER.get());
                        output.accept(EMAItems.LEGENDARY_ME_CRAFTING_PROVIDER.get());
                        output.accept(EMAItems.RE_AVARITIA_SCULK_ME_CRAFTING_PROVIDER.get());
                        output.accept(EMAItems.RE_AVARITIA_NETHER_ME_CRAFTING_PROVIDER.get());
                        output.accept(EMAItems.RE_AVARITIA_END_ME_CRAFTING_PROVIDER.get());
                        output.accept(EMAItems.XTREME_ME_CRAFTING_PROVIDER.get());

                        // Wired terminals, followed by wireless terminals, matrix cores and Quantum.
                        output.accept(EMAParts.EXTENDED_PATTERN_ENCODING_TERMINAL);
                        output.accept(EMAParts.EPIC_PATTERN_ENCODING_TERMINAL);
                        output.accept(EMAParts.LEGENDARY_PATTERN_ENCODING_TERMINAL);
                        EMAOptionalIntegrations.addCreativeTabItems(output);
                    })
                    .build());

    private EMACreativeModeTabs() {
    }
}
