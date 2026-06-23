package me.myogoo.extendedmolecularassembler.data;

import me.myogoo.extendedmolecularassembler.ExtendedMolecularAssembler;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber(modid = ExtendedMolecularAssembler.MODID, bus = EventBusSubscriber.Bus.MOD)
public final class EMADataGenerators {
    private EMADataGenerators() {
    }

    @SubscribeEvent
    public static void onGatherData(GatherDataEvent event) {
        var generator = event.getGenerator();
        var registries = event.getLookupProvider();
        var existingFileHelper = event.getExistingFileHelper();
        var pack = generator.getVanillaPack(true);

        pack.addProvider(EMARecipeDataProvider::new);
        pack.addProvider(output -> new EMABlockTagDataProvider(output, registries, existingFileHelper));
        pack.addProvider(output -> EMALootTableProvider.create(output, registries));
    }
}
