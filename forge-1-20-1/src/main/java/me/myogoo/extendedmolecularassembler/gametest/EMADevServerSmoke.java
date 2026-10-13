package me.myogoo.extendedmolecularassembler.gametest;

import me.myogoo.extendedmolecularassembler.ExtendedMolecularAssembler;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ExtendedMolecularAssembler.MODID)
public final class EMADevServerSmoke {
    private EMADevServerSmoke() {
    }

    @SubscribeEvent
    public static void onServerStarted(ServerStartedEvent event) {
        if (!Boolean.getBoolean("ema.serverSmoke")) {
            return;
        }
        var server = event.getServer();
        if (server.getLevel(Level.OVERWORLD) == null) {
            throw new AssertionError("Ordinary server started without its overworld");
        }
        ExtendedMolecularAssembler.LOGGER.info("EMA ordinary dedicated-server startup PASS");
        server.halt(false);
    }
}
