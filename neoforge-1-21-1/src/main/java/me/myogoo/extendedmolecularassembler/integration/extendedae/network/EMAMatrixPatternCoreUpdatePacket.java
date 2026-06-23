package me.myogoo.extendedmolecularassembler.integration.extendedae.network;

import me.myogoo.extendedmolecularassembler.ExtendedMolecularAssembler;
import me.myogoo.extendedmolecularassembler.integration.extendedae.menu.ExtendedAssemblerMatrixPatternCoreMenu;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.HashMap;
import java.util.Map;

public record EMAMatrixPatternCoreUpdatePacket(long coreId, int slotCount, boolean full,
        Map<Integer, ItemStack> changes) implements CustomPacketPayload {
    public static final Type<EMAMatrixPatternCoreUpdatePacket> TYPE =
            new Type<>(ExtendedMolecularAssembler.makeId("matrix_pattern_core_update"));
    public static final StreamCodec<RegistryFriendlyByteBuf, EMAMatrixPatternCoreUpdatePacket> STREAM_CODEC =
            StreamCodec.ofMember(EMAMatrixPatternCoreUpdatePacket::write, EMAMatrixPatternCoreUpdatePacket::decode);

    @Override
    public Type<EMAMatrixPatternCoreUpdatePacket> type() {
        return TYPE;
    }

    public static EMAMatrixPatternCoreUpdatePacket decode(RegistryFriendlyByteBuf data) {
        var coreId = data.readLong();
        var slotCount = data.readVarInt();
        var full = data.readBoolean();
        var count = data.readVarInt();
        var changes = new HashMap<Integer, ItemStack>();
        for (int i = 0; i < count; i++) {
            changes.put(data.readVarInt(), ItemStack.OPTIONAL_STREAM_CODEC.decode(data));
        }
        return new EMAMatrixPatternCoreUpdatePacket(coreId, slotCount, full, changes);
    }

    public void write(RegistryFriendlyByteBuf data) {
        data.writeLong(this.coreId);
        data.writeVarInt(this.slotCount);
        data.writeBoolean(this.full);
        data.writeVarInt(this.changes.size());
        for (var entry : this.changes.entrySet()) {
            data.writeVarInt(entry.getKey());
            ItemStack.OPTIONAL_STREAM_CODEC.encode(data, entry.getValue());
        }
    }

    public static void handle(EMAMatrixPatternCoreUpdatePacket packet, IPayloadContext context) {
        context.enqueueWork(() -> handleOnClient(packet, context));
    }

    @OnlyIn(Dist.CLIENT)
    private static void handleOnClient(EMAMatrixPatternCoreUpdatePacket packet, IPayloadContext context) {
        if (context.player().containerMenu instanceof ExtendedAssemblerMatrixPatternCoreMenu menu) {
            menu.applyPatternCoreUpdate(packet.coreId, packet.slotCount, packet.full, packet.changes);
        }
    }
}
