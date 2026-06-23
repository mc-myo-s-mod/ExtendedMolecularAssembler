package me.myogoo.extendedmolecularassembler.pattern;

import appeng.core.definitions.AEItems;
import appeng.util.AECodecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Objects;

public record EncodedExtendedCraftingPattern(
        List<ItemStack> inputs,
        ItemStack result,
        ResourceLocation recipeId,
        ResourceLocation tableType,
        int tableTier,
        int tableSideLength,
        boolean canSubstitute,
        boolean canSubstituteFluids) {
    public EncodedExtendedCraftingPattern {
        tableType = Objects.requireNonNullElse(tableType, ExtendedPatternTableTypes.UNKNOWN);
        tableTier = Math.max(0, tableTier);
        tableSideLength = Math.max(0, tableSideLength);
    }

    public static final Codec<EncodedExtendedCraftingPattern> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            AECodecs.FAULT_TOLERANT_OPTIONAL_ITEMSTACK_CODEC.listOf().fieldOf("inputs")
                    .forGetter(EncodedExtendedCraftingPattern::inputs),
            AECodecs.FAULT_TOLERANT_ITEMSTACK_CODEC.fieldOf("result")
                    .forGetter(EncodedExtendedCraftingPattern::result),
            ResourceLocation.CODEC.fieldOf("recipeId").forGetter(EncodedExtendedCraftingPattern::recipeId),
            ResourceLocation.CODEC.optionalFieldOf("tableType", ExtendedPatternTableTypes.UNKNOWN)
                    .forGetter(EncodedExtendedCraftingPattern::tableType),
            Codec.INT.optionalFieldOf("tableTier", 0).forGetter(EncodedExtendedCraftingPattern::tableTier),
            Codec.INT.optionalFieldOf("tableSideLength", 0)
                    .forGetter(EncodedExtendedCraftingPattern::tableSideLength),
            Codec.BOOL.fieldOf("canSubstitute").forGetter(EncodedExtendedCraftingPattern::canSubstitute),
            Codec.BOOL.optionalFieldOf("canSubstituteFluids", true)
                    .forGetter(EncodedExtendedCraftingPattern::canSubstituteFluids))
            .apply(builder, EncodedExtendedCraftingPattern::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, EncodedExtendedCraftingPattern> STREAM_CODEC =
            StreamCodec.ofMember(EncodedExtendedCraftingPattern::writeToStream,
                    EncodedExtendedCraftingPattern::readFromStream);

    public boolean containsMissingContent() {
        return AEItems.MISSING_CONTENT.is(result) || inputs.stream().anyMatch(AEItems.MISSING_CONTENT::is);
    }

    public boolean hasTableMetadata() {
        return !tableType.equals(ExtendedPatternTableTypes.UNKNOWN) && tableTier > 0 && tableSideLength > 0;
    }

    private static EncodedExtendedCraftingPattern readFromStream(RegistryFriendlyByteBuf buffer) {
        var inputs = ItemStack.OPTIONAL_LIST_STREAM_CODEC.decode(buffer);
        var result = ItemStack.STREAM_CODEC.decode(buffer);
        var recipeId = ResourceLocation.STREAM_CODEC.decode(buffer);
        var tableType = ResourceLocation.STREAM_CODEC.decode(buffer);
        var tableTier = buffer.readInt();
        var tableSideLength = buffer.readInt();
        var canSubstitute = buffer.readBoolean();
        var canSubstituteFluids = buffer.readBoolean();
        return new EncodedExtendedCraftingPattern(inputs, result, recipeId, tableType, tableTier, tableSideLength,
                canSubstitute, canSubstituteFluids);
    }

    private void writeToStream(RegistryFriendlyByteBuf buffer) {
        ItemStack.OPTIONAL_LIST_STREAM_CODEC.encode(buffer, inputs);
        ItemStack.STREAM_CODEC.encode(buffer, result);
        ResourceLocation.STREAM_CODEC.encode(buffer, recipeId);
        ResourceLocation.STREAM_CODEC.encode(buffer, tableType);
        buffer.writeInt(tableTier);
        buffer.writeInt(tableSideLength);
        buffer.writeBoolean(canSubstitute);
        buffer.writeBoolean(canSubstituteFluids);
    }
}
