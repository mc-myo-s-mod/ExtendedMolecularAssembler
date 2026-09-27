package me.myogoo.extendedmolecularassembler.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

public final class EMAOptionalBlockLootDataProvider implements DataProvider {
    private final PackOutput.PathProvider pathProvider;
    private final PackOutput.PathProvider itemTagPathProvider;

    public EMAOptionalBlockLootDataProvider(PackOutput output) {
        this.pathProvider = output.createPathProvider(PackOutput.Target.DATA_PACK, "loot_tables/blocks");
        this.itemTagPathProvider = output.createPathProvider(PackOutput.Target.DATA_PACK, "tags/items");
    }

    @Override
    public @NotNull CompletableFuture<?> run(@NotNull CachedOutput output) {
        var writes = new ArrayList<CompletableFuture<?>>();
        for (var block : EMAOptionalContentData.BLOCKS) {
            writes.add(DataProvider.saveStable(output, createSelfDrop(block), pathProvider.json(block)));
            var itemTag = new JsonObject();
            var optionalItem = new JsonObject();
            optionalItem.addProperty("id", block.toString());
            optionalItem.addProperty("required", false);
            var values = new JsonArray();
            values.add(optionalItem);
            itemTag.add("values", values);
            writes.add(DataProvider.saveStable(output, itemTag, itemTagPathProvider.json(dropTag(block))));
        }
        return CompletableFuture.allOf(writes.toArray(CompletableFuture[]::new));
    }

    @Override
    public @NotNull String getName() {
        return "Extended Molecular Assembler optional block loot tables";
    }

    private static JsonObject createSelfDrop(ResourceLocation block) {
        JsonObject root = new JsonObject();
        root.addProperty("type", "minecraft:block");

        JsonObject pool = new JsonObject();
        pool.addProperty("bonus_rolls", 0.0F);

        JsonObject survivesExplosion = new JsonObject();
        survivesExplosion.addProperty("condition", "minecraft:survives_explosion");
        JsonArray lootConditions = new JsonArray();
        lootConditions.add(survivesExplosion);
        pool.add("conditions", lootConditions);

        JsonObject entry = new JsonObject();
        entry.addProperty("type", "minecraft:tag");
        entry.addProperty("name", dropTag(block).toString());
        entry.addProperty("expand", true);
        JsonArray entries = new JsonArray();
        entries.add(entry);
        pool.add("entries", entries);
        pool.addProperty("rolls", 1.0F);

        JsonArray pools = new JsonArray();
        pools.add(pool);
        root.add("pools", pools);
        root.addProperty(
                "random_sequence",
                new ResourceLocation(block.getNamespace(), "blocks/" + block.getPath())
                        .toString());
        return root;
    }

    private static ResourceLocation dropTag(ResourceLocation block) {
        return new ResourceLocation(block.getNamespace(), "optional_block_drops/" + block.getPath());
    }
}
