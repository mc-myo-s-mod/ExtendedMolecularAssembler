package me.myogoo.extendedmolecularassembler.network.clientbound;

import java.util.Optional;

import appeng.menu.me.crafting.CraftConfirmMenu;
import me.myogoo.extendedmolecularassembler.ExtendedMolecularAssembler;
import me.myogoo.extendedmolecularassembler.menu.crafting.CraftConfirmExportPlanGate;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.Nullable;

public record EMACraftConfirmPlanBlockPacket(int containerId, @Nullable Component reason)
        implements CustomPacketPayload {
    public static final Type<EMACraftConfirmPlanBlockPacket> TYPE =
            new Type<>(ExtendedMolecularAssembler.makeId("craft_confirm_plan_block"));
    public static final StreamCodec<RegistryFriendlyByteBuf, EMACraftConfirmPlanBlockPacket> STREAM_CODEC =
            StreamCodec.ofMember(EMACraftConfirmPlanBlockPacket::write, EMACraftConfirmPlanBlockPacket::decode);

    @Override
    public Type<EMACraftConfirmPlanBlockPacket> type() {
        return TYPE;
    }

    public static EMACraftConfirmPlanBlockPacket decode(RegistryFriendlyByteBuf data) {
        var containerId = data.readVarInt();
        var reason = ComponentSerialization.TRUSTED_OPTIONAL_STREAM_CODEC.decode(data).orElse(null);
        return new EMACraftConfirmPlanBlockPacket(containerId, reason);
    }

    public void write(RegistryFriendlyByteBuf data) {
        data.writeVarInt(containerId);
        ComponentSerialization.TRUSTED_OPTIONAL_STREAM_CODEC.encode(data, Optional.ofNullable(reason));
    }

    public static void handle(EMACraftConfirmPlanBlockPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> packet.handleOnClient(context.player()));
    }
    private void handleOnClient(Player player) {
        if (player.containerMenu.containerId == containerId
                && player.containerMenu instanceof CraftConfirmMenu
                && player.containerMenu instanceof CraftConfirmExportPlanGate gate) {
            gate.ema$setExportPlanBlockReason(reason);
        }
    }
}
