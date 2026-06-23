package me.myogoo.extendedmolecularassembler.integration.extendedae.network;

import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuLocators;
import com.glodblock.github.extendedae.common.tileentities.matrix.TileAssemblerMatrixBase;
import com.glodblock.github.extendedae.container.ContainerAssemblerMatrix;
import me.myogoo.extendedmolecularassembler.ExtendedMolecularAssembler;
import me.myogoo.extendedmolecularassembler.integration.extendedae.ExtendedAssemblerMatrixPatternCoreBlockEntity;
import me.myogoo.extendedmolecularassembler.integration.extendedae.menu.ExtendedAssemblerMatrixPatternCoreMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record EMAOpenExtendedAEAssemblerMatrixScreenPacket(BlockPos pos, Target target)
        implements CustomPacketPayload {
    public static final Type<EMAOpenExtendedAEAssemblerMatrixScreenPacket> TYPE =
            new Type<>(ExtendedMolecularAssembler.makeId("open_extendedae_assembler_matrix_screen"));
    public static final StreamCodec<RegistryFriendlyByteBuf, EMAOpenExtendedAEAssemblerMatrixScreenPacket>
            STREAM_CODEC = StreamCodec.ofMember(
                    EMAOpenExtendedAEAssemblerMatrixScreenPacket::write,
                    EMAOpenExtendedAEAssemblerMatrixScreenPacket::decode);

    @Override
    public Type<EMAOpenExtendedAEAssemblerMatrixScreenPacket> type() {
        return TYPE;
    }

    public static EMAOpenExtendedAEAssemblerMatrixScreenPacket decode(RegistryFriendlyByteBuf data) {
        return new EMAOpenExtendedAEAssemblerMatrixScreenPacket(data.readBlockPos(), data.readEnum(Target.class));
    }

    public void write(RegistryFriendlyByteBuf data) {
        data.writeBlockPos(this.pos);
        data.writeEnum(this.target);
    }

    public static void handle(EMAOpenExtendedAEAssemblerMatrixScreenPacket packet, IPayloadContext context) {
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
        if (!(blockEntity instanceof TileAssemblerMatrixBase matrixBlock)
                || !matrixBlock.isActive()
                || !matrixBlock.isFormed()) {
            return;
        }

        if (this.target == Target.MATRIX) {
            MenuOpener.open(ContainerAssemblerMatrix.TYPE, player, MenuLocators.forBlockEntity(matrixBlock));
            return;
        }

        var patternCore = findPatternCore(matrixBlock);
        if (patternCore != null) {
            MenuOpener.open(ExtendedAssemblerMatrixPatternCoreMenu.TYPE, player,
                    MenuLocators.forBlockEntity(patternCore));
        }
    }

    private static ExtendedAssemblerMatrixPatternCoreBlockEntity findPatternCore(TileAssemblerMatrixBase matrixBlock) {
        if (matrixBlock instanceof ExtendedAssemblerMatrixPatternCoreBlockEntity patternCore) {
            return patternCore;
        }

        var cluster = matrixBlock.getCluster();
        if (cluster == null || cluster.isDestroyed()) {
            return null;
        }

        var iterator = cluster.getBlockEntities();
        while (iterator.hasNext()) {
            if (iterator.next() instanceof ExtendedAssemblerMatrixPatternCoreBlockEntity patternCore) {
                return patternCore;
            }
        }
        return null;
    }

    public enum Target {
        PATTERN_CORE,
        MATRIX
    }
}
