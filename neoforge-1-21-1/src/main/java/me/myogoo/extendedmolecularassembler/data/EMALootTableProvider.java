package me.myogoo.extendedmolecularassembler.data;

import me.myogoo.extendedmolecularassembler.ExtendedMolecularAssembler;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class EMALootTableProvider extends LootTableProvider {
    private EMALootTableProvider(PackOutput output, Set<ResourceKey<LootTable>> requiredTables,
                                 List<SubProviderEntry> subProviders,
                                 CompletableFuture<HolderLookup.Provider> registries) {
        super(output, requiredTables, subProviders, registries);
    }

    public static EMALootTableProvider create(PackOutput output,
                                              CompletableFuture<HolderLookup.Provider> registries) {
        return new EMALootTableProvider(
                output,
                Set.of(),
                List.of(new SubProviderEntry(EMABlockLoot::new, LootContextParamSets.BLOCK)),
                registries);
    }

    public static class EMABlockLoot extends BlockLootSubProvider {
        private static final Set<Item> EXPLOSION_RESISTANT = Set.of();

        protected EMABlockLoot(HolderLookup.Provider registries) {
            super(EXPLOSION_RESISTANT, FeatureFlags.REGISTRY.allFlags(), registries);
        }

        @Override
        protected void generate() {
            for (Block block : getKnownBlocks()) {
                dropSelf(block);
            }
        }

        @Override
        protected Iterable<Block> getKnownBlocks() {
            return BuiltInRegistries.BLOCK.holders()
                    .filter(holder -> ExtendedMolecularAssembler.MODID.equals(holder.key().location().getNamespace()))
                    .map(Holder::value)
                    .toList();
        }
    }
}
