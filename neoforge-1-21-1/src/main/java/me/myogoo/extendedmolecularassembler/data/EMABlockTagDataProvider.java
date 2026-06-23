package me.myogoo.extendedmolecularassembler.data;

import me.myogoo.extendedmolecularassembler.ExtendedMolecularAssembler;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class EMABlockTagDataProvider extends BlockTagsProvider {
    public EMABlockTagDataProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries,
                                   @Nullable ExistingFileHelper existingFileHelper) {
        super(output, registries, ExtendedMolecularAssembler.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        var pickaxe = tag(BlockTags.MINEABLE_WITH_PICKAXE);
        BuiltInRegistries.BLOCK.holders()
                .filter(holder -> ExtendedMolecularAssembler.MODID.equals(holder.key().location().getNamespace()))
                .map(holder -> (Block) holder.value())
                .forEach(pickaxe::add);
    }
}
