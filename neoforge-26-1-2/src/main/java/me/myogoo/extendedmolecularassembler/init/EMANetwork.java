package me.myogoo.extendedmolecularassembler.init;

import me.myogoo.extendedmolecularassembler.ExtendedMolecularAssembler;
import me.myogoo.extendedmolecularassembler.network.clientbound.EMAAssemblerAnimationPacket;
import me.myogoo.extendedmolecularassembler.network.clientbound.EMACraftConfirmPlanBlockPacket;
import me.myogoo.extendedmolecularassembler.network.clientbound.EMACraftConfirmPlanHighlightsPacket;
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
        registrar.playToClient(
                EMACraftConfirmPlanBlockPacket.TYPE,
                EMACraftConfirmPlanBlockPacket.STREAM_CODEC,
                EMACraftConfirmPlanBlockPacket::handle);
        registrar.playToClient(
                EMACraftConfirmPlanHighlightsPacket.TYPE,
                EMACraftConfirmPlanHighlightsPacket.STREAM_CODEC,
                EMACraftConfirmPlanHighlightsPacket::handle);
        EMAOptionalIntegrations.registerNetwork(registrar);
    }
}
