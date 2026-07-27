package me.myogoo.extendedmolecularassembler.client;

import me.myogoo.extendedmolecularassembler.api.annotation.ExtendedAE;
import me.myogoo.extendedmolecularassembler.integration.extendedae.client.EMAExtendedAEClientIntegration;
import me.myogoo.myotus.api.MyotusAPI;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

public final class EMAOptionalClientIntegrations {
    private EMAOptionalClientIntegrations() {
    }

    public static void initScreens(RegisterMenuScreensEvent event) {
        if (MyotusAPI.integrations().isLoaded(ExtendedAE.class)) {
            EMAExtendedAEClientIntegration.initScreens(event);
        }
    }
}
