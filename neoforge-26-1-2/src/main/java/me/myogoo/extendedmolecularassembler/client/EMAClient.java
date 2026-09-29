package me.myogoo.extendedmolecularassembler.client;

import appeng.api.util.AEColor;
import appeng.client.InitScreens;
import appeng.client.gui.style.StyleManager;
import me.myogoo.extendedmolecularassembler.ExtendedMolecularAssembler;
import me.myogoo.extendedmolecularassembler.block.blockentity.ExportMECraftingProviderBlockEntity;
import me.myogoo.extendedmolecularassembler.client.render.ExtendedMolecularAssemblerRenderer;
import me.myogoo.extendedmolecularassembler.client.screen.ExtendedMolecularAssemblerScreen;
import me.myogoo.extendedmolecularassembler.client.screen.ExtendedPatternEncodingTermScreen;
import me.myogoo.extendedmolecularassembler.init.EMABlockEntities;
import me.myogoo.extendedmolecularassembler.init.EMABlocks;
import me.myogoo.extendedmolecularassembler.init.EMAConfigTab;
import me.myogoo.extendedmolecularassembler.menu.ExtendedMolecularAssemblerMenu;
import me.myogoo.extendedmolecularassembler.menu.pattern.ExtendedPatternEncodingTermMenu;
import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.DefaultDataComponentsBoundEvent;

import java.util.stream.IntStream;

@Mod(value = ExtendedMolecularAssembler.MODID, dist = Dist.CLIENT)
public class EMAClient {
    private static final String EXTENDED_PATTERN_ENCODING_TERMINAL_STYLE =
            "/screens/extended_molecular_assembler/extended_pattern_encoding_terminal.json";

    public EMAClient(IEventBus eventBus) {
        NeoForge.EVENT_BUS.addListener(EMAClient::onComponentsBound);
        eventBus.addListener(EMAClient::initScreens);
        eventBus.addListener(EMAClient::initRenderers);
        eventBus.addListener(ExtendedMolecularAssemblerRenderer::registerStandaloneModels);
        eventBus.addListener(RegisterColorHandlersEvent.BlockTintSources.class, EMAClient::initProviderBlockColors);
    }

    private static void onComponentsBound(DefaultDataComponentsBoundEvent event) {
        if (event.getUpdateCause() == DefaultDataComponentsBoundEvent.UpdateCause.CLIENT_PACKET_RECEIVED) {
            EMAConfigTab.initialize();
        }
    }

    private static void initScreens(RegisterMenuScreensEvent event) {
        InitScreens.register(event, ExtendedMolecularAssemblerMenu.TYPE, ExtendedMolecularAssemblerScreen::new,
                "/screens/extended_molecular_assembler/extended_molecular_assembler.json");
        event.<ExtendedPatternEncodingTermMenu, ExtendedPatternEncodingTermScreen>register(
                ExtendedPatternEncodingTermMenu.TYPE, (menu, playerInventory, title) -> {
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
                            StyleManager.loadStyleDoc(EXTENDED_PATTERN_ENCODING_TERMINAL_STYLE));
                });
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

    private static void initProviderBlockColors(RegisterColorHandlersEvent.BlockTintSources event) {
        event.register(IntStream.range(0, 5).<BlockTintSource>mapToObj(ProviderBlockTintSource::new).toList(),
                EMABlocks.BASIC_ME_CRAFTING_PROVIDER.get(),
                EMABlocks.ADVANCED_ME_CRAFTING_PROVIDER.get(),
                EMABlocks.ELITE_ME_CRAFTING_PROVIDER.get(),
                EMABlocks.ULTIMATE_ME_CRAFTING_PROVIDER.get(),
                EMABlocks.RE_AVARITIA_SCULK_ME_CRAFTING_PROVIDER.get(),
                EMABlocks.RE_AVARITIA_NETHER_ME_CRAFTING_PROVIDER.get(),
                EMABlocks.RE_AVARITIA_END_ME_CRAFTING_PROVIDER.get(),
                EMABlocks.XTREME_ME_CRAFTING_PROVIDER.get());
    }

    private record ProviderBlockTintSource(int tintIndex) implements BlockTintSource {
        @Override
        public int color(BlockState state) {
            return color(AEColor.TRANSPARENT);
        }

        @Override
        public int colorInWorld(BlockState state, BlockAndTintGetter level, BlockPos pos) {
            var color = AEColor.TRANSPARENT;
            if (level.getBlockEntity(pos) instanceof ExportMECraftingProviderBlockEntity provider) {
                color = provider.getCableColor();
            }
            return color(color);
        }

        private int color(AEColor color) {
            if (tintIndex < AEColor.TINTINDEX_DARK || tintIndex > AEColor.TINTINDEX_BRIGHT) {
                return 0xFFFFFFFF;
            }
            return color.getVariantByTintIndex(tintIndex) | 0xFF000000;
        }
    }
}
