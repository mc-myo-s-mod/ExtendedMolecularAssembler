package me.myogoo.extendedmolecularassembler.client;

import appeng.api.util.AEColor;
import appeng.client.render.StaticItemColor;
import appeng.init.client.InitScreens;
import me.myogoo.extendedmolecularassembler.ExtendedMolecularAssembler;
import me.myogoo.extendedmolecularassembler.block.blockentity.TieredMECraftingProviderBlockEntity;
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
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@Mod(value = ExtendedMolecularAssembler.MODID, dist = Dist.CLIENT)
public class EMAClient {
    public EMAClient(IEventBus eventBus) {
        eventBus.addListener(EMAClient::clientSetup);
        eventBus.addListener(EMAClient::initScreens);
        eventBus.addListener(EMAClient::initRenderers);
        eventBus.addListener(RegisterColorHandlersEvent.Block.class, EMAClient::initProviderBlockColors);
        eventBus.addListener(RegisterColorHandlersEvent.Item.class, EMAClient::initItemColors);
    }

    private static void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(EMAConfigTab::initialize);
    }

    private static void initScreens(RegisterMenuScreensEvent event) {
        InitScreens.register(event, ExtendedMolecularAssemblerMenu.TYPE, ExtendedMolecularAssemblerScreen::new,
                "/screens/extended_molecular_assembler/extended_molecular_assembler.json");
        InitScreens.register(event, ExtendedPatternEncodingTermMenu.TYPE, ExtendedPatternEncodingTermScreen::new,
                "/screens/extended_molecular_assembler/extended_pattern_encoding_terminal.json");
        EMAOptionalClientIntegrations.initScreens(event);
    }

    private static void initRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(EMABlockEntities.EXTENDED_MOLECULAR_ASSEMBLER.get(),
                ExtendedMolecularAssemblerRenderer::new);
        if (EMABlockEntities.EX_EXTENDED_MOLECULAR_ASSEMBLER != null) {
            event.registerBlockEntityRenderer(EMABlockEntities.EX_EXTENDED_MOLECULAR_ASSEMBLER.get(),
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
                            && level.getBlockEntity(pos) instanceof TieredMECraftingProviderBlockEntity provider) {
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
                EMABlocks.XTREME_ME_CRAFTING_PROVIDER.get());
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
                EMAItems.XTREME_ME_CRAFTING_PROVIDER.get());
    }
}
