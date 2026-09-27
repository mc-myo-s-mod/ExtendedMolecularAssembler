package me.myogoo.extendedmolecularassembler.integration.extendedae.network;

import me.myogoo.extendedmolecularassembler.integration.extendedae.menu.ExtendedAssemblerMatrixPatternCoreMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;
import java.util.function.Supplier;

import java.util.HashMap;
import java.util.Map;

public record EMAMatrixPatternCoreUpdatePacket(long coreId, int slotCount, boolean full,
        Map<Integer, ItemStack> changes) {
    public static EMAMatrixPatternCoreUpdatePacket decode(FriendlyByteBuf data) {
        var coreId = data.readLong();
        var slotCount = data.readVarInt();
        var full = data.readBoolean();
        var count = data.readVarInt();
        if (slotCount < 0 || slotCount > 72 || count < 0 || count > slotCount) {
            throw new IllegalArgumentException("Invalid matrix pattern update size");
        }
        var changes = new HashMap<Integer, ItemStack>();
        for (int i = 0; i < count; i++) {
            var slot = data.readVarInt();
            if (slot < 0 || slot >= slotCount) {
                throw new IllegalArgumentException("Invalid matrix pattern slot");
            }
            changes.put(slot, data.readItem());
        }
        return new EMAMatrixPatternCoreUpdatePacket(coreId, slotCount, full, changes);
    }

    public static void encode(EMAMatrixPatternCoreUpdatePacket packet, FriendlyByteBuf data) {
        data.writeLong(packet.coreId);
        data.writeVarInt(packet.slotCount);
        data.writeBoolean(packet.full);
        data.writeVarInt(packet.changes.size());
        for (var entry : packet.changes.entrySet()) {
            data.writeVarInt(entry.getKey());
            data.writeItem(entry.getValue());
        }
    }

    public static void handle(EMAMatrixPatternCoreUpdatePacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        var context = contextSupplier.get();
        context.enqueueWork(() -> handleOnClient(packet));
        context.setPacketHandled(true);
    }

    @OnlyIn(Dist.CLIENT)
    private static void handleOnClient(EMAMatrixPatternCoreUpdatePacket packet) {
        var player = Minecraft.getInstance().player;
        if (player != null && player.containerMenu instanceof ExtendedAssemblerMatrixPatternCoreMenu menu) {
            menu.applyPatternCoreUpdate(packet.coreId, packet.slotCount, packet.full, packet.changes);
        }
    }
}
