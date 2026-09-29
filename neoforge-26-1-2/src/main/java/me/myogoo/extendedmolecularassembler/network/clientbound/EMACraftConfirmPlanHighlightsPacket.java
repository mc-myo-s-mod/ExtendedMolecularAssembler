package me.myogoo.extendedmolecularassembler.network.clientbound;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import appeng.api.stacks.AEKey;
import appeng.menu.me.crafting.CraftConfirmMenu;
import me.myogoo.extendedmolecularassembler.ExtendedMolecularAssembler;
import me.myogoo.extendedmolecularassembler.block.ExportMECraftingProviderTier;
import me.myogoo.extendedmolecularassembler.crafting.ExportPlanEntryHighlight;
import me.myogoo.extendedmolecularassembler.menu.crafting.CraftConfirmExportPlanGate;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record EMACraftConfirmPlanHighlightsPacket(int containerId, Map<AEKey, ExportPlanEntryHighlight> highlights)
        implements CustomPacketPayload {
    public static final Type<EMACraftConfirmPlanHighlightsPacket> TYPE =
            new Type<>(ExtendedMolecularAssembler.makeId("craft_confirm_plan_highlights"));
    public static final StreamCodec<RegistryFriendlyByteBuf, EMACraftConfirmPlanHighlightsPacket> STREAM_CODEC =
            StreamCodec.ofMember(EMACraftConfirmPlanHighlightsPacket::write,
                    EMACraftConfirmPlanHighlightsPacket::decode);

    public EMACraftConfirmPlanHighlightsPacket {
        highlights = Map.copyOf(highlights);
    }

    @Override
    public Type<EMACraftConfirmPlanHighlightsPacket> type() {
        return TYPE;
    }

    public static EMACraftConfirmPlanHighlightsPacket decode(RegistryFriendlyByteBuf data) {
        var containerId = data.readVarInt();
        var count = data.readVarInt();
        var highlights = new LinkedHashMap<AEKey, ExportPlanEntryHighlight>(count);
        for (int i = 0; i < count; i++) {
            var key = Objects.requireNonNull(AEKey.readKey(data), "Unknown AE key type in crafting plan highlight");
            var provider = data.readEnum(ExportMECraftingProviderTier.class);
            highlights.put(key, new ExportPlanEntryHighlight(provider, data.readBoolean()));
        }
        return new EMACraftConfirmPlanHighlightsPacket(containerId, highlights);
    }

    public void write(RegistryFriendlyByteBuf data) {
        data.writeVarInt(containerId);
        data.writeVarInt(highlights.size());
        for (var entry : highlights.entrySet()) {
            AEKey.writeKey(data, entry.getKey());
            data.writeEnum(entry.getValue().provider());
            data.writeBoolean(entry.getValue().missingProvider());
        }
    }

    public static void handle(EMACraftConfirmPlanHighlightsPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> packet.handleOnClient(context.player()));
    }
    private void handleOnClient(Player player) {
        if (player.containerMenu.containerId == containerId
                && player.containerMenu instanceof CraftConfirmMenu
                && player.containerMenu instanceof CraftConfirmExportPlanGate gate) {
            gate.ema$setExportPlanEntryHighlights(highlights);
        }
    }
}
