package me.myogoo.extendedmolecularassembler.init;

import me.myogoo.extendedmolecularassembler.ExtendedMolecularAssembler;
import me.myogoo.extendedmolecularassembler.network.clientbound.EMAAssemblerAnimationPacket;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

public final class EMANetwork {
    private EMANetwork() {
    }

    public static void init(RegisterPayloadHandlersEvent event) {
        var registrar = event.registrar(ExtendedMolecularAssembler.MODID);
        registrar.playToClient(
                EMAAssemblerAnimationPacket.TYPE,
                EMAAssemblerAnimationPacket.STREAM_CODEC,
                EMAAssemblerAnimationPacket::handle);
        EMAOptionalIntegrations.registerNetwork(registrar);
    }
}
