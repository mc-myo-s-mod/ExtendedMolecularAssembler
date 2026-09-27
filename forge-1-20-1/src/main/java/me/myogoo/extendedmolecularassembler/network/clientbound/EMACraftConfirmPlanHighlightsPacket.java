package me.myogoo.extendedmolecularassembler.network.clientbound;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import appeng.api.stacks.AEKey;
import me.myogoo.extendedmolecularassembler.block.ExportMECraftingProviderTier;
import me.myogoo.extendedmolecularassembler.crafting.ExportPlanEntryHighlight;
import net.minecraft.network.FriendlyByteBuf;

public record EMACraftConfirmPlanHighlightsPacket(int containerId, Map<AEKey, ExportPlanEntryHighlight> highlights) {
    public EMACraftConfirmPlanHighlightsPacket {
        highlights = Map.copyOf(highlights);
    }

    public static EMACraftConfirmPlanHighlightsPacket decode(FriendlyByteBuf data) {
        var containerId = data.readVarInt();
        var count = data.readVarInt();
        if (count < 0 || count > data.readableBytes()) {
            throw new IllegalArgumentException("Invalid crafting-plan highlight count: " + count);
        }
        var highlights = new LinkedHashMap<AEKey, ExportPlanEntryHighlight>();
        for (var i = 0; i < count; i++) {
            var key = Objects.requireNonNull(AEKey.readKey(data), "Unknown AE key type");
            var provider = data.readEnum(ExportMECraftingProviderTier.class);
            highlights.put(key, new ExportPlanEntryHighlight(provider, data.readBoolean()));
        }
        return new EMACraftConfirmPlanHighlightsPacket(containerId, highlights);
    }

    public void write(FriendlyByteBuf data) {
        data.writeVarInt(containerId);
        data.writeVarInt(highlights.size());
        for (var entry : highlights.entrySet()) {
            AEKey.writeKey(data, entry.getKey());
            data.writeEnum(entry.getValue().provider());
            data.writeBoolean(entry.getValue().missingProvider());
        }
    }
}
