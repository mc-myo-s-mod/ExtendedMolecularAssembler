package me.myogoo.extendedmolecularassembler.integration.extendedae.network;

import me.myogoo.extendedmolecularassembler.ExtendedMolecularAssembler;
import me.myogoo.extendedmolecularassembler.integration.extendedae.MatrixCraftingStatus;
import me.myogoo.extendedmolecularassembler.integration.extendedae.client.MatrixCraftingStatusScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record EMAShowMatrixCraftingStatusPacket(MatrixCraftingStatus matrix, MatrixCraftingStatus ema)
        implements CustomPacketPayload {
    public static final Type<EMAShowMatrixCraftingStatusPacket> TYPE =
            new Type<>(ExtendedMolecularAssembler.makeId("show_matrix_crafting_status"));
    public static final StreamCodec<RegistryFriendlyByteBuf, EMAShowMatrixCraftingStatusPacket> STREAM_CODEC =
            StreamCodec.ofMember(EMAShowMatrixCraftingStatusPacket::write,
                    EMAShowMatrixCraftingStatusPacket::decode);

    @Override
    public Type<EMAShowMatrixCraftingStatusPacket> type() {
        return TYPE;
    }

    public static EMAShowMatrixCraftingStatusPacket decode(RegistryFriendlyByteBuf data) {
        return new EMAShowMatrixCraftingStatusPacket(readStatus(data), readStatus(data));
    }

    public void write(RegistryFriendlyByteBuf data) {
        writeStatus(data, this.matrix);
        writeStatus(data, this.ema);
    }

    public static void handle(EMAShowMatrixCraftingStatusPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> handleOnClient(packet));
    }

    @OnlyIn(Dist.CLIENT)
    private static void handleOnClient(EMAShowMatrixCraftingStatusPacket packet) {
        Minecraft.getInstance().setScreen(new MatrixCraftingStatusScreen(packet.matrix, packet.ema));
    }

    private static MatrixCraftingStatus readStatus(RegistryFriendlyByteBuf data) {
        return new MatrixCraftingStatus(
                data.readVarInt(),
                data.readVarInt(),
                data.readVarInt(),
                data.readVarInt(),
                data.readVarInt());
    }

    private static void writeStatus(RegistryFriendlyByteBuf data, MatrixCraftingStatus status) {
        data.writeVarInt(status.availableParallelCrafters());
        data.writeVarInt(status.totalParallelCrafters());
        data.writeVarInt(status.totalPatternSlots());
        data.writeVarInt(status.speed());
        data.writeVarInt(status.maxSpeed());
    }
}
