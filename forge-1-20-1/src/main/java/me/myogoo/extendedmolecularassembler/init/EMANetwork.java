package me.myogoo.extendedmolecularassembler.init;

import me.myogoo.extendedmolecularassembler.ExtendedMolecularAssembler;
import me.myogoo.extendedmolecularassembler.client.EMAClientPackets;
import me.myogoo.extendedmolecularassembler.network.clientbound.EMAAssemblerAnimationPacket;
import me.myogoo.extendedmolecularassembler.network.clientbound.EMACraftConfirmPlanBlockPacket;
import me.myogoo.extendedmolecularassembler.network.clientbound.EMACraftConfirmPlanHighlightsPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public final class EMANetwork {
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            ExtendedMolecularAssembler.makeId("main"), () -> "1", "1"::equals, "1"::equals);

    private EMANetwork() {
    }

    public static void register() {
        CHANNEL.messageBuilder(EMAAssemblerAnimationPacket.class, 0, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(EMAAssemblerAnimationPacket::write)
                .decoder(EMAAssemblerAnimationPacket::decode)
                .consumerMainThread((packet, context) -> DistExecutor.unsafeRunWhenOn(
                        Dist.CLIENT, () -> () -> EMAClientPackets.handle(packet)))
                .add();
        CHANNEL.messageBuilder(EMACraftConfirmPlanBlockPacket.class, 1, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(EMACraftConfirmPlanBlockPacket::write)
                .decoder(EMACraftConfirmPlanBlockPacket::decode)
                .consumerMainThread((packet, context) -> DistExecutor.unsafeRunWhenOn(
                        Dist.CLIENT, () -> () -> EMAClientPackets.handle(packet)))
                .add();
        CHANNEL.messageBuilder(EMACraftConfirmPlanHighlightsPacket.class, 2, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(EMACraftConfirmPlanHighlightsPacket::write)
                .decoder(EMACraftConfirmPlanHighlightsPacket::decode)
                .consumerMainThread((packet, context) -> DistExecutor.unsafeRunWhenOn(
                        Dist.CLIENT, () -> () -> EMAClientPackets.handle(packet)))
                .add();
    }

    public static void sendToTracking(Level level, BlockPos pos, Object packet) {
        CHANNEL.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(pos)), packet);
    }

    public static void sendToClient(ServerPlayer player, Object packet) {
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), packet);
    }

    public static void sendToServer(Object packet) {
        CHANNEL.sendToServer(packet);
    }
}
