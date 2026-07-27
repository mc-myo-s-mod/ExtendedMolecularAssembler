package me.myogoo.extendedmolecularassembler.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

import static me.myogoo.myotus.data.recipe.ExternalRecipeBuilder.myoCondition;

public final class EMAOptionalBlockLootDataProvider implements DataProvider {
    private final PackOutput.PathProvider pathProvider;

    public EMAOptionalBlockLootDataProvider(PackOutput output) {
        this.pathProvider = output.createPathProvider(PackOutput.Target.DATA_PACK, "loot_table/blocks");
    }

    @Override
    public @NotNull CompletableFuture<?> run(@NotNull CachedOutput output) {
        return CompletableFuture.allOf(EMAOptionalContentData.BLOCKS.stream()
                .map(block -> DataProvider.saveStable(
                        output,
                        createSelfDrop(block),
                        this.pathProvider.json(block.id())))
                .toArray(CompletableFuture[]::new));
    }

    @Override
    public @NotNull String getName() {
        return "Extended Molecular Assembler optional block loot tables";
    }

    private static JsonObject createSelfDrop(EMAOptionalContentData.OptionalBlock block) {
        JsonObject root = new JsonObject();
        root.addProperty("type", "minecraft:block");

        JsonObject pool = new JsonObject();
        JsonArray conditions = new JsonArray();
        for (String activeIntegration : block.activeIntegrations()) {
            conditions.add(myoCondition(activeIntegration));
        }
        pool.add("neoforge:conditions", conditions);
        pool.addProperty("bonus_rolls", 0.0F);

        JsonObject survivesExplosion = new JsonObject();
        survivesExplosion.addProperty("condition", "minecraft:survives_explosion");
        JsonArray lootConditions = new JsonArray();
        lootConditions.add(survivesExplosion);
        pool.add("conditions", lootConditions);

        JsonObject entry = new JsonObject();
        entry.addProperty("type", "minecraft:item");
        entry.addProperty("name", block.id().toString());
        JsonArray entries = new JsonArray();
        entries.add(entry);
        pool.add("entries", entries);
        pool.addProperty("rolls", 1.0F);

        JsonArray pools = new JsonArray();
        pools.add(pool);
        root.add("pools", pools);
        root.addProperty(
                "random_sequence",
                ResourceLocation.fromNamespaceAndPath(block.id().getNamespace(), "blocks/" + block.id().getPath())
                        .toString());
        return root;
    }
}
