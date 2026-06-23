package me.myogoo.extendedmolecularassembler.client;

import me.myogoo.extendedmolecularassembler.init.EMAModPresence;
import me.myogoo.extendedmolecularassembler.integration.extendedae.client.EMAExtendedAEClientIntegration;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

public final class EMAOptionalClientIntegrations {
    private EMAOptionalClientIntegrations() {
    }

    public static void initScreens(RegisterMenuScreensEvent event) {
        if (EMAModPresence.isExtendedAELoaded()) {
            EMAExtendedAEClientIntegration.initScreens(event);
        }
    }
}
