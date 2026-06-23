package me.myogoo.extendedmolecularassembler.integration.jei.reavaritia;

import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeTransferRegistration;

public final class ReAvaritiaJeiIntegration {
    private ReAvaritiaJeiIntegration() {
    }

    public static void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        // The available Re-Avaritia artifact is Java 21 bytecode, so the Java 17 Forge module
        // cannot link its JEI category constants without reflection.
    }

    public static void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
        // Transfer depends on the unported ExtendedPatternEncodingTermMenu/part.
    }

}
