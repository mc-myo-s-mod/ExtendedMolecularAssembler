package me.myogoo.extendedmolecularassembler.integration.extendedae.network;

import io.netty.buffer.Unpooled;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public final class EMAMatrixPatternCoreUpdatePacketChecks {
    private EMAMatrixPatternCoreUpdatePacketChecks() {
    }

    public static void assertPacketBounds(GameTestHelper helper) {
        for (var sizes : new int[][] {{-1, 0}, {73, 0}, {72, -1}, {72, 73}, {1, 2}}) {
            var buffer = header(sizes[0], sizes[1]);
            try {
                assertRejected(helper, buffer);
                helper.assertTrue(buffer.readableBytes() == 0, "Size check read item data");
            } finally {
                buffer.release();
            }
        }
        for (var slot : new int[] {-1, 72}) {
            var buffer = header(72, 1);
            try {
                buffer.writeVarInt(slot);
                assertRejected(helper, buffer);
                helper.assertTrue(buffer.readableBytes() == 0, "Slot check read item data");
            } finally {
                buffer.release();
            }
        }
        var buffer = header(72, 1);
        try {
            var stack = new ItemStack(Items.DIAMOND);
            buffer.writeVarInt(71);
            buffer.writeItem(stack);
            var packet = EMAMatrixPatternCoreUpdatePacket.decode(buffer);
            helper.assertTrue(packet.slotCount() == 72 && packet.changes().size() == 1
                    && ItemStack.matches(packet.changes().get(71), stack), "Valid maximum-slot update changed");
        } finally {
            buffer.release();
        }
    }

    private static void assertRejected(GameTestHelper helper, FriendlyByteBuf buffer) {
        try {
            EMAMatrixPatternCoreUpdatePacket.decode(buffer);
        } catch (IllegalArgumentException expected) {
            return;
        }
        helper.fail("Malformed Matrix update was accepted");
    }

    private static FriendlyByteBuf header(int slots, int count) {
        var buffer = new FriendlyByteBuf(Unpooled.buffer());
        buffer.writeLong(1);
        buffer.writeVarInt(slots);
        buffer.writeBoolean(true);
        buffer.writeVarInt(count);
        return buffer;
    }
}
