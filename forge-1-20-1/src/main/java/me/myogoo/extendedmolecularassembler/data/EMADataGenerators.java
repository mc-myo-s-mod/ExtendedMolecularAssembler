package me.myogoo.extendedmolecularassembler.data;

import net.minecraftforge.data.event.GatherDataEvent;

public final class EMADataGenerators {
    private EMADataGenerators() {
    }

    public static void onGatherData(GatherDataEvent event) {
        var generator = event.getGenerator();
        var registries = event.getLookupProvider();
        var existingFileHelper = event.getExistingFileHelper();
        var output = generator.getPackOutput();
        generator.addProvider(event.includeServer(), new EMARecipeDataProvider(output));
        generator.addProvider(event.includeServer(), new EMABlockTagDataProvider(output, registries, existingFileHelper));
        generator.addProvider(event.includeServer(), EMALootTableProvider.create(output));
        generator.addProvider(event.includeServer(), new EMAOptionalBlockLootDataProvider(output));
    }
}
