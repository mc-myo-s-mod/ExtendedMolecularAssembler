package me.myogoo.extendedmolecularassembler.data;

import me.myogoo.extendedmolecularassembler.ExtendedMolecularAssembler;
import me.myogoo.extendedmolecularassembler.init.EMABlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;
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
        EMABlocks.BLOCKS.getEntries().stream()
                .map(RegistryObject::get)
                .forEach(pickaxe::add);
    }
}
