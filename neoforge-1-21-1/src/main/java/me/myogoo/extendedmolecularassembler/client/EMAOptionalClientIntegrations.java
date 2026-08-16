package me.myogoo.extendedmolecularassembler.client;

import me.myogoo.extendedmolecularassembler.api.annotation.ExtendedAE;
import me.myogoo.extendedmolecularassembler.client.screen.ExtendedPatternEncodingTermScreen;
import me.myogoo.extendedmolecularassembler.integration.ae2wtlib.client.EMAAE2WTLibClientIntegration;
import me.myogoo.extendedmolecularassembler.integration.extendedae.client.EMAExtendedAEClientIntegration;
import me.myogoo.extendedmolecularassembler.menu.pattern.ExtendedPatternEncodingTermMenu;
import me.myogoo.myotus.api.MyotusAPI;
import me.myogoo.myotus.api.annotation.mods.AE2WTLib;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import org.jetbrains.annotations.Nullable;

public final class EMAOptionalClientIntegrations {
    private EMAOptionalClientIntegrations() {
    }

    public static void initScreens(RegisterMenuScreensEvent event) {
        if (MyotusAPI.integrations().isLoaded(ExtendedAE.class)) {
            EMAExtendedAEClientIntegration.initScreens(event);
        }
    }

    @Nullable
    public static ExtendedPatternEncodingTermScreen tryCreateAE2WTLibPatternEncodingScreen(
            ExtendedPatternEncodingTermMenu menu,
            Inventory playerInventory,
            Component title) {
        if (MyotusAPI.integrations().isLoaded(AE2WTLib.class)) {
            return EMAAE2WTLibClientIntegration.tryCreatePatternEncodingScreen(menu, playerInventory, title);
        }
        return null;
    }
}
