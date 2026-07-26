package me.myogoo.extendedmolecularassembler.data;

import me.myogoo.extendedmolecularassembler.init.EMABlocks;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;
import java.util.Set;

public class EMALootTableProvider extends LootTableProvider {
    private EMALootTableProvider(PackOutput output) {
        super(output, Set.of(), List.of(new SubProviderEntry(EMABlockLoot::new, LootContextParamSets.BLOCK)));
    }

    public static EMALootTableProvider create(PackOutput output) {
        return new EMALootTableProvider(output);
    }

    public static class EMABlockLoot extends BlockLootSubProvider {
        private static final Set<Item> EXPLOSION_RESISTANT = Set.of();

        protected EMABlockLoot() {
            super(EXPLOSION_RESISTANT, FeatureFlags.REGISTRY.allFlags());
        }

        @Override
        protected void generate() {
            for (Block block : getKnownBlocks()) {
                dropSelf(block);
            }
        }

        @Override
        protected Iterable<Block> getKnownBlocks() {
            return EMABlocks.BLOCKS.getEntries().stream()
                    .map(RegistryObject::get)
                    .toList();
        }
    }
}
