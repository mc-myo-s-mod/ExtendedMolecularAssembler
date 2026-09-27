package me.myogoo.extendedmolecularassembler.integration.jei.avaritianeo;

import me.myogoo.extendedmolecularassembler.integration.jei.handler.ExtendedPatternDirectRecipeTransferHandler;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import net.byAqua3.avaritia.compat.jei.AvaritiaJEIPlugin;

@me.myogoo.extendedmolecularassembler.api.annotation.AvaritiaNeo
public final class AvaritiaNeoJeiIntegration {
    private AvaritiaNeoJeiIntegration() {
    }

    public static void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
        registration.addRecipeTransferHandler(
                new ExtendedPatternDirectRecipeTransferHandler<>(AvaritiaJEIPlugin.EXTREME_CRAFTING),
                AvaritiaJEIPlugin.EXTREME_CRAFTING);
    }
}
