package me.myogoo.extendedmolecularassembler.client;

import appeng.api.util.AEColor;
import appeng.client.render.StaticItemColor;
import appeng.init.client.InitScreens;
import me.myogoo.extendedmolecularassembler.block.blockentity.ExportMECraftingProviderBlockEntity;
import me.myogoo.extendedmolecularassembler.client.render.ExtendedMolecularAssemblerRenderer;
import me.myogoo.extendedmolecularassembler.client.screen.ExtendedMolecularAssemblerScreen;
import me.myogoo.extendedmolecularassembler.client.screen.ExtendedPatternEncodingTermScreen;
import me.myogoo.extendedmolecularassembler.init.EMABlockEntities;
import me.myogoo.extendedmolecularassembler.init.EMABlocks;
import me.myogoo.extendedmolecularassembler.init.EMAConfigTab;
import me.myogoo.extendedmolecularassembler.init.EMAItems;
import me.myogoo.extendedmolecularassembler.init.EMAParts;
import me.myogoo.extendedmolecularassembler.menu.ExtendedMolecularAssemblerMenu;
import me.myogoo.extendedmolecularassembler.menu.pattern.ExtendedPatternEncodingTermMenu;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;

public class EMAClient {
    private static final String EXTENDED_PATTERN_ENCODING_TERMINAL_STYLE =
            "/screens/extended_molecular_assembler/extended_pattern_encoding_terminal.json";

    public EMAClient(IEventBus eventBus) {
        eventBus.addListener(EMAClient::clientSetup);
        eventBus.addListener(EMAClient::initRenderers);
        eventBus.addListener(EMAClient::initProviderBlockColors);
        eventBus.addListener(EMAClient::initItemColors);
    }

    private static void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            initScreens();
            EMAConfigTab.initialize();
        });
    }

    private static void initScreens() {
        InitScreens.register(ExtendedMolecularAssemblerMenu.TYPE, ExtendedMolecularAssemblerScreen::new,
                "/screens/extended_molecular_assembler/extended_molecular_assembler.json");
        InitScreens.<ExtendedPatternEncodingTermMenu, ExtendedPatternEncodingTermScreen>register(
                ExtendedPatternEncodingTermMenu.TYPE, (menu, playerInventory, title, style) -> {
                    var optionalScreen = EMAOptionalClientIntegrations.tryCreateAE2WTLibPatternEncodingScreen(
                            menu,
                            playerInventory,
                            title);
                    if (optionalScreen != null) {
                        return optionalScreen;
                    }
                    return new ExtendedPatternEncodingTermScreen(
                            menu,
                            playerInventory,
                            title,
                            style);
                }, EXTENDED_PATTERN_ENCODING_TERMINAL_STYLE);
        EMAOptionalClientIntegrations.initScreens();
    }

    private static void initRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(EMABlockEntities.EPIC_MOLECULAR_ASSEMBLER.get(),
                ExtendedMolecularAssemblerRenderer::new);
        event.registerBlockEntityRenderer(EMABlockEntities.LEGENDARY_MOLECULAR_ASSEMBLER.get(),
                ExtendedMolecularAssemblerRenderer::new);
        event.registerBlockEntityRenderer(EMABlockEntities.EXTENDED_MOLECULAR_ASSEMBLER.get(),
                ExtendedMolecularAssemblerRenderer::new);
        if (EMABlockEntities.EX_EXTENDED_MOLECULAR_ASSEMBLER != null) {
            event.registerBlockEntityRenderer(EMABlockEntities.EX_EXTENDED_MOLECULAR_ASSEMBLER.get(),
                    ExtendedMolecularAssemblerRenderer::new);
            event.registerBlockEntityRenderer(EMABlockEntities.EX_EPIC_MOLECULAR_ASSEMBLER.get(),
                    ExtendedMolecularAssemblerRenderer::new);
            event.registerBlockEntityRenderer(EMABlockEntities.EX_LEGENDARY_MOLECULAR_ASSEMBLER.get(),
                    ExtendedMolecularAssemblerRenderer::new);
        }
    }

    private static void initProviderBlockColors(RegisterColorHandlersEvent.Block event) {
        event.register((state, level, pos, tintIndex) -> {
                    if (tintIndex < AEColor.TINTINDEX_DARK || tintIndex > AEColor.TINTINDEX_BRIGHT) {
                        return 0xFFFFFFFF;
                    }

                    var color = AEColor.TRANSPARENT;
                    if (level != null && pos != null
                            && level.getBlockEntity(pos) instanceof ExportMECraftingProviderBlockEntity provider) {
                        color = provider.getCableColor();
                    }
                    return color.getVariantByTintIndex(tintIndex) | 0xFF000000;
                },
                EMABlocks.BASIC_ME_CRAFTING_PROVIDER.get(),
                EMABlocks.ADVANCED_ME_CRAFTING_PROVIDER.get(),
                EMABlocks.ELITE_ME_CRAFTING_PROVIDER.get(),
                EMABlocks.ULTIMATE_ME_CRAFTING_PROVIDER.get(),
                EMABlocks.RE_AVARITIA_SCULK_ME_CRAFTING_PROVIDER.get(),
                EMABlocks.RE_AVARITIA_NETHER_ME_CRAFTING_PROVIDER.get(),
                EMABlocks.RE_AVARITIA_END_ME_CRAFTING_PROVIDER.get(),
                EMABlocks.XTREME_ME_CRAFTING_PROVIDER.get(),
                EMABlocks.EPIC_ME_CRAFTING_PROVIDER.get(),
                EMABlocks.LEGENDARY_ME_CRAFTING_PROVIDER.get());
    }

    private static void initItemColors(RegisterColorHandlersEvent.Item event) {
        event.register((stack, tintIndex) ->
                        new StaticItemColor(AEColor.TRANSPARENT).getColor(stack, tintIndex) | 0xFF000000,
                EMAParts.TERMINAL_PARTS.stream().map(part -> (ItemLike) part).toArray(ItemLike[]::new));

        event.register((stack, tintIndex) -> AEColor.GRAY.getVariantByTintIndex(tintIndex) | 0xFF000000,
                EMAItems.BASIC_ME_CRAFTING_PROVIDER.get(),
                EMAItems.ADVANCED_ME_CRAFTING_PROVIDER.get(),
                EMAItems.ELITE_ME_CRAFTING_PROVIDER.get(),
                EMAItems.ULTIMATE_ME_CRAFTING_PROVIDER.get(),
                EMAItems.RE_AVARITIA_SCULK_ME_CRAFTING_PROVIDER.get(),
                EMAItems.RE_AVARITIA_NETHER_ME_CRAFTING_PROVIDER.get(),
                EMAItems.RE_AVARITIA_END_ME_CRAFTING_PROVIDER.get(),
                EMAItems.XTREME_ME_CRAFTING_PROVIDER.get(),
                EMAItems.EPIC_ME_CRAFTING_PROVIDER.get(),
                EMAItems.LEGENDARY_ME_CRAFTING_PROVIDER.get());
    }
}
