package me.myogoo.extendedmolecularassembler.network.clientbound;

import appeng.api.stacks.AEKey;
import appeng.client.render.crafting.AssemblerAnimationStatus;
import me.myogoo.extendedmolecularassembler.ExtendedMolecularAssembler;
import me.myogoo.extendedmolecularassembler.block.blockentity.ExtendedMolecularAssemblerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record EMAAssemblerAnimationPacket(BlockPos pos, byte rate, AEKey what) implements CustomPacketPayload {
    public static final Type<EMAAssemblerAnimationPacket> TYPE =
            new Type<>(ExtendedMolecularAssembler.makeId("assembler_animation"));
    public static final StreamCodec<RegistryFriendlyByteBuf, EMAAssemblerAnimationPacket> STREAM_CODEC =
            StreamCodec.ofMember(EMAAssemblerAnimationPacket::write, EMAAssemblerAnimationPacket::decode);

    @Override
    public Type<EMAAssemblerAnimationPacket> type() {
        return TYPE;
    }

    public static EMAAssemblerAnimationPacket decode(RegistryFriendlyByteBuf data) {
        var pos = data.readBlockPos();
        var rate = data.readByte();
        var what = AEKey.readKey(data);
        return new EMAAssemblerAnimationPacket(pos, rate, what);
    }

    public void write(RegistryFriendlyByteBuf data) {
        data.writeBlockPos(pos);
        data.writeByte(rate);
        AEKey.writeKey(data, what);
    }

    public static void handle(EMAAssemblerAnimationPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> packet.handleOnClient(context.player()));
    }

    @OnlyIn(Dist.CLIENT)
    private void handleOnClient(Player player) {
        BlockEntity blockEntity = player.getCommandSenderWorld().getBlockEntity(pos);
        if (blockEntity instanceof ExtendedMolecularAssemblerBlockEntity assembler) {
            assembler.setAnimationStatus(new AssemblerAnimationStatus(rate, what.wrapForDisplayOrFilter()));
        }
    }
}
