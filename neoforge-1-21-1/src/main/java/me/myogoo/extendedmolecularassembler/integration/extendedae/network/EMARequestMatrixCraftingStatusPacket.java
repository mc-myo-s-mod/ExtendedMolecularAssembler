package me.myogoo.extendedmolecularassembler.integration.extendedae.network;

import com.glodblock.github.extendedae.common.tileentities.matrix.TileAssemblerMatrixBase;
import me.myogoo.extendedmolecularassembler.ExtendedMolecularAssembler;
import me.myogoo.extendedmolecularassembler.integration.extendedae.ExtendedAEAssemblerMatrixBridge;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record EMARequestMatrixCraftingStatusPacket(BlockPos pos) implements CustomPacketPayload {
    public static final Type<EMARequestMatrixCraftingStatusPacket> TYPE =
            new Type<>(ExtendedMolecularAssembler.makeId("request_matrix_crafting_status"));
    public static final StreamCodec<RegistryFriendlyByteBuf, EMARequestMatrixCraftingStatusPacket> STREAM_CODEC =
            StreamCodec.ofMember(EMARequestMatrixCraftingStatusPacket::write,
                    EMARequestMatrixCraftingStatusPacket::decode);

    @Override
    public Type<EMARequestMatrixCraftingStatusPacket> type() {
        return TYPE;
    }

    public static EMARequestMatrixCraftingStatusPacket decode(RegistryFriendlyByteBuf data) {
        return new EMARequestMatrixCraftingStatusPacket(data.readBlockPos());
    }

    public void write(RegistryFriendlyByteBuf data) {
        data.writeBlockPos(this.pos);
    }

    public static void handle(EMARequestMatrixCraftingStatusPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player) {
                packet.handleOnServer(player);
            }
        });
    }

    private void handleOnServer(ServerPlayer player) {
        var level = player.serverLevel();
        if (!level.isLoaded(this.pos)) {
            return;
        }

        BlockEntity blockEntity = level.getBlockEntity(this.pos);
        if (!(blockEntity instanceof TileAssemblerMatrixBase matrixBlock)) {
            return;
        }

        var summary = ExtendedAEAssemblerMatrixBridge.describeMatrixStatus(matrixBlock);
        PacketDistributor.sendToPlayer(player, new EMAShowMatrixCraftingStatusPacket(summary.matrix(), summary.ema()));
    }
}
