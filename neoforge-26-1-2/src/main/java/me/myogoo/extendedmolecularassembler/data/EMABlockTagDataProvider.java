package me.myogoo.extendedmolecularassembler.data;

import me.myogoo.extendedmolecularassembler.ExtendedMolecularAssembler;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class EMABlockTagDataProvider extends BlockTagsProvider {
    public EMABlockTagDataProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, ExtendedMolecularAssembler.MODID);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        var pickaxe = tag(BlockTags.MINEABLE_WITH_PICKAXE);
        BuiltInRegistries.BLOCK.listElements()
                .filter(holder -> ExtendedMolecularAssembler.MODID.equals(holder.key().identifier().getNamespace()))
                .filter(holder -> !EMAOptionalContentData.isOptionalBlock(holder.key().identifier()))
                .map(holder -> (Block) holder.value())
                .forEach(pickaxe::add);
        EMAOptionalContentData.BLOCKS.stream()
                .map(EMAOptionalContentData.OptionalBlock::id)
                .forEach(getOrCreateRawBuilder(BlockTags.MINEABLE_WITH_PICKAXE)::addOptionalElement);
    }
}
