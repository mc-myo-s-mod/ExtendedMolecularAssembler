package me.myogoo.extendedmolecularassembler.client;

import appeng.init.client.InitScreens;
import me.myogoo.extendedmolecularassembler.client.render.ExtendedMolecularAssemblerRenderer;
import me.myogoo.extendedmolecularassembler.client.screen.ExtendedMolecularAssemblerScreen;
import me.myogoo.extendedmolecularassembler.init.EMABlockEntities;
import me.myogoo.extendedmolecularassembler.menu.ExtendedMolecularAssemblerMenu;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public final class EMAClient {
    public EMAClient(IEventBus eventBus) {
        eventBus.addListener(this::initClient);
        eventBus.addListener(this::initRenderers);
    }

    private void initClient(FMLClientSetupEvent event) {
        event.enqueueWork(() -> InitScreens.register(ExtendedMolecularAssemblerMenu.TYPE,
                ExtendedMolecularAssemblerScreen::new,
                "/screens/extended_molecular_assembler/extended_molecular_assembler.json"));
    }

    private void initRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(EMABlockEntities.EXTENDED_MOLECULAR_ASSEMBLER.get(),
                ExtendedMolecularAssemblerRenderer::new);
        event.registerBlockEntityRenderer(EMABlockEntities.EX_EXTENDED_MOLECULAR_ASSEMBLER.get(),
                ExtendedMolecularAssemblerRenderer::new);
    }
}
