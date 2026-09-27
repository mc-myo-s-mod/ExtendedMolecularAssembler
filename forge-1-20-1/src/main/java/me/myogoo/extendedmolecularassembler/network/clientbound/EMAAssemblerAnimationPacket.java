package me.myogoo.extendedmolecularassembler.network.clientbound;

import appeng.api.stacks.AEKey;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;

public record EMAAssemblerAnimationPacket(BlockPos pos, byte rate, AEKey what) {
    public static EMAAssemblerAnimationPacket decode(FriendlyByteBuf data) {
        return new EMAAssemblerAnimationPacket(data.readBlockPos(), data.readByte(), AEKey.readKey(data));
    }

    public void write(FriendlyByteBuf data) {
        data.writeBlockPos(pos);
        data.writeByte(rate);
        AEKey.writeKey(data, what);
    }
}
