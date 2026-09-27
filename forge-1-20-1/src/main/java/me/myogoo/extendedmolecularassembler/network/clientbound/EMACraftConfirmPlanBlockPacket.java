package me.myogoo.extendedmolecularassembler.network.clientbound;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

public record EMACraftConfirmPlanBlockPacket(int containerId, @Nullable Component reason) {
    public static EMACraftConfirmPlanBlockPacket decode(FriendlyByteBuf data) {
        return new EMACraftConfirmPlanBlockPacket(data.readVarInt(), data.readNullable(FriendlyByteBuf::readComponent));
    }

    public void write(FriendlyByteBuf data) {
        data.writeVarInt(containerId);
        data.writeNullable(reason, FriendlyByteBuf::writeComponent);
    }
}
