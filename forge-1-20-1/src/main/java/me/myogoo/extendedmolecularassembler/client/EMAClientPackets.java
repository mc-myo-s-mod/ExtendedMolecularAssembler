package me.myogoo.extendedmolecularassembler.client;

import appeng.client.render.crafting.AssemblerAnimationStatus;
import me.myogoo.extendedmolecularassembler.block.blockentity.ExtendedMolecularAssemblerBlockEntity;
import me.myogoo.extendedmolecularassembler.menu.crafting.CraftConfirmExportPlanGate;
import me.myogoo.extendedmolecularassembler.network.clientbound.EMAAssemblerAnimationPacket;
import me.myogoo.extendedmolecularassembler.network.clientbound.EMACraftConfirmPlanBlockPacket;
import me.myogoo.extendedmolecularassembler.network.clientbound.EMACraftConfirmPlanHighlightsPacket;
import net.minecraft.client.Minecraft;

public final class EMAClientPackets {
    private EMAClientPackets() {
    }

    public static void handle(EMAAssemblerAnimationPacket packet) {
        var level = Minecraft.getInstance().level;
        if (level != null && packet.what() != null
                && level.getBlockEntity(packet.pos()) instanceof ExtendedMolecularAssemblerBlockEntity assembler) {
            assembler.setAnimationStatus(new AssemblerAnimationStatus(packet.rate(), packet.what().wrapForDisplayOrFilter()));
        }
    }

    public static void handle(EMACraftConfirmPlanBlockPacket packet) {
        var player = Minecraft.getInstance().player;
        if (player != null && player.containerMenu.containerId == packet.containerId()
                && player.containerMenu instanceof CraftConfirmExportPlanGate gate) {
            gate.ema$setExportPlanBlockReason(packet.reason());
        }
    }

    public static void handle(EMACraftConfirmPlanHighlightsPacket packet) {
        var player = Minecraft.getInstance().player;
        if (player != null && player.containerMenu.containerId == packet.containerId()
                && player.containerMenu instanceof CraftConfirmExportPlanGate gate) {
            gate.ema$setExportPlanEntryHighlights(packet.highlights());
        }
    }
}
