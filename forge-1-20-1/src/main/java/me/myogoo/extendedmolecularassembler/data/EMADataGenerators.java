package me.myogoo.extendedmolecularassembler.data;

import me.myogoo.extendedmolecularassembler.ExtendedMolecularAssembler;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

public final class EMADataGenerators {
    private EMADataGenerators() {
    }

    @SubscribeEvent
    public static void onGatherData(GatherDataEvent event) {
        var generator = event.getGenerator();
        var output = generator.getPackOutput();
        var registries = event.getLookupProvider();
        var existingFileHelper = event.getExistingFileHelper();
        generator.addProvider(event.includeServer(), new EMARecipeDataProvider(output));
        generator.addProvider(event.includeServer(),
                new EMABlockTagDataProvider(output, registries, existingFileHelper));
        generator.addProvider(event.includeServer(), EMALootTableProvider.create(output));
    }
}
