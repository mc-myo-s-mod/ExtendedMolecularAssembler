package me.myogoo.extendedmolecularassembler.integration.extendedae.network;

import io.netty.buffer.Unpooled;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.gametest.framework.GameTestHelper;




public final class EMAMatrixPatternCoreUpdatePacketChecks {

    private static void rejectsInvalidSizeBeforeReadingItems(GameTestHelper helper) {
        for (var sizes : new int[][] {{-1, 0}, {73, 0}, {72, -1}, {72, 73}, {1, 2}}) {
            var buffer = header(sizes[0], sizes[1]);
            try {
                assertRejected(helper, buffer);
                helper.assertTrue(buffer.readableBytes() == 0, "Bounds were checked after reading stack data");
            } finally {
                buffer.release();
            }
        }
    }


    private static void rejectsInvalidSlotBeforeReadingItsStack(GameTestHelper helper) {
        for (var slot : new int[] {-1, 72}) {
            var buffer = header(72, 1);
            try {
                buffer.writeVarInt(slot);
                assertRejected(helper, buffer);
                helper.assertTrue(buffer.readableBytes() == 0, "Bounds were checked after reading stack data");
            } finally {
                buffer.release();
            }
        }
    }


    private static void acceptsEmptyAndMaximumSizedUpdates(GameTestHelper helper) {
        for (var size : new int[] {0, 72}) {
            var buffer = header(size, 0);
            try {
                var packet = EMAMatrixPatternCoreUpdatePacket.decode(buffer);
                helper.assertTrue(packet.slotCount() == size, "Valid update size changed");
                helper.assertTrue(packet.changes().isEmpty(), "Empty update gained items");
            } finally {
                buffer.release();
            }
        }
    }

    public static void assertPacketBounds(GameTestHelper helper) {
        rejectsInvalidSizeBeforeReadingItems(helper);
        rejectsInvalidSlotBeforeReadingItsStack(helper);
        acceptsEmptyAndMaximumSizedUpdates(helper);
    }

    private static void assertRejected(GameTestHelper helper, RegistryFriendlyByteBuf buffer) {
        try {
            EMAMatrixPatternCoreUpdatePacket.decode(buffer);
        } catch (IllegalArgumentException expected) {
            return;
        }
        helper.fail("Malformed Matrix update was accepted");
    }

    private static RegistryFriendlyByteBuf header(int slots, int count) {
        var buffer = new RegistryFriendlyByteBuf(Unpooled.buffer(), RegistryAccess.EMPTY);
        buffer.writeLong(1);
        buffer.writeVarInt(slots);
        buffer.writeBoolean(true);
        buffer.writeVarInt(count);
        return buffer;
    }
}
