package me.myogoo.extendedmolecularassembler.integration.jei.avaritianeo;

import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeTransferRegistration;

public final class AvaritiaNeoJeiIntegration {
    private AvaritiaNeoJeiIntegration() {
    }

    public static void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        // Avaritia Neo's available 1.20.1 artifact is Java 21 bytecode, so the Java 17 Forge
        // module cannot link its JEI category constants without reflection.
    }

    public static void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
        // Transfer depends on the unported ExtendedPatternEncodingTermMenu/part.
    }
}
