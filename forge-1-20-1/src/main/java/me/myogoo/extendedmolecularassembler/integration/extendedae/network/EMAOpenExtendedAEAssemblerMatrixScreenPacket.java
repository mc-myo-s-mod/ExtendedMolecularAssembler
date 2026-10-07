package me.myogoo.extendedmolecularassembler.integration.extendedae.network;

import appeng.menu.MenuOpener;
import appeng.menu.AEBaseMenu;
import appeng.menu.locator.MenuLocators;
import com.glodblock.github.extendedae.common.tileentities.matrix.TileAssemblerMatrixBase;
import com.glodblock.github.extendedae.container.ContainerAssemblerMatrix;
import me.myogoo.extendedmolecularassembler.integration.extendedae.ExtendedAssemblerMatrixPatternCoreBlockEntity;
import me.myogoo.extendedmolecularassembler.integration.extendedae.EMAExtendedAEIntegration;
import me.myogoo.extendedmolecularassembler.integration.extendedae.menu.ExtendedAssemblerMatrixPatternCoreMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;
import java.util.function.Supplier;

public record EMAOpenExtendedAEAssemblerMatrixScreenPacket(BlockPos pos, Target target) {
    private static final double MAX_INTERACTION_DISTANCE_SQR = 64.0;

    public static EMAOpenExtendedAEAssemblerMatrixScreenPacket decode(FriendlyByteBuf data) {
        return new EMAOpenExtendedAEAssemblerMatrixScreenPacket(data.readBlockPos(), data.readEnum(Target.class));
    }

    public static void encode(EMAOpenExtendedAEAssemblerMatrixScreenPacket packet, FriendlyByteBuf data) {
        data.writeBlockPos(packet.pos);
        data.writeEnum(packet.target);
    }

    public static void handle(EMAOpenExtendedAEAssemblerMatrixScreenPacket packet,
            Supplier<NetworkEvent.Context> contextSupplier) {
        var context = contextSupplier.get();
        context.enqueueWork(() -> {
            var player = context.getSender();
            if (player != null) {
                packet.handleOnServer(player);
            }
        });
        context.setPacketHandled(true);
    }

    private void handleOnServer(ServerPlayer player) {
        if (this.target == Target.MATRIX) {
            this.openMatrixFromPatternCore(player);
        } else if (player.containerMenu instanceof ContainerAssemblerMatrix) {
            this.openPatternCoreFromMatrix(player);
        }
    }

    private void openPatternCoreFromMatrix(ServerPlayer player) {
        if (!(player.containerMenu instanceof ContainerAssemblerMatrix menu) || !isValidMenu(menu, player)) {
            return;
        }

        var matrixBlock = menu.getHost();
        if (!this.pos.equals(matrixBlock.getBlockPos()) || !isValidInteractionHost(player, matrixBlock)) {
            return;
        }

        var patternCore = findPatternCore(matrixBlock);
        if (patternCore == null || !isLiveMatrixBlock(player, patternCore)
                || !isSameActiveCluster(matrixBlock, patternCore)) {
            return;
        }

        MenuOpener.open(EMAExtendedAEIntegration.EXTENDED_ASSEMBLER_MATRIX_PATTERN_CORE_MENU.get(), player,
                MenuLocators.forBlockEntity(patternCore));
    }

    private void openMatrixFromPatternCore(ServerPlayer player) {
        if (!(player.containerMenu instanceof ExtendedAssemblerMatrixPatternCoreMenu menu)
                || !isValidMenu(menu, player)) {
            return;
        }

        var patternCore = menu.getHost();
        if (!isLiveMatrixBlock(player, patternCore)) {
            return;
        }

        var matrixBlock = getLoadedMatrixBlock(player, this.pos);
        if (matrixBlock == null || !isValidInteractionHost(player, matrixBlock)
                || !isSameActiveCluster(patternCore, matrixBlock)) {
            return;
        }

        MenuOpener.open(ContainerAssemblerMatrix.TYPE, player, MenuLocators.forBlockEntity(matrixBlock));
    }

    private static boolean isValidMenu(AEBaseMenu menu, ServerPlayer player) {
        return menu.isValidMenu() && menu.stillValid(player);
    }

    private static TileAssemblerMatrixBase getLoadedMatrixBlock(ServerPlayer player, BlockPos pos) {
        var level = player.serverLevel();
        if (!level.isLoaded(pos)) {
            return null;
        }

        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof TileAssemblerMatrixBase matrixBlock) {
            return matrixBlock;
        }
        return null;
    }

    private static boolean isValidInteractionHost(ServerPlayer player, TileAssemblerMatrixBase matrixBlock) {
        return isLiveMatrixBlock(player, matrixBlock)
                && isWithinInteractionDistance(player, matrixBlock.getBlockPos());
    }

    private static boolean isLiveMatrixBlock(ServerPlayer player, TileAssemblerMatrixBase matrixBlock) {
        return matrixBlock.getLevel() == player.serverLevel()
                && !matrixBlock.isRemoved()
                && matrixBlock.getLevel().getBlockEntity(matrixBlock.getBlockPos()) == matrixBlock
                && matrixBlock.isActive()
                && matrixBlock.isFormed();
    }

    private static boolean isWithinInteractionDistance(ServerPlayer player, BlockPos pos) {
        return player.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5)
                <= MAX_INTERACTION_DISTANCE_SQR;
    }

    private static boolean isSameActiveCluster(TileAssemblerMatrixBase source, TileAssemblerMatrixBase target) {
        var cluster = source.getCluster();
        return cluster != null && !cluster.isDestroyed() && target.getCluster() == cluster;
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
        MATRIX,
        PATTERN_CORE
    }
}
