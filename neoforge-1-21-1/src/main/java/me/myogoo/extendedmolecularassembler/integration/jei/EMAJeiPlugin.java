package me.myogoo.extendedmolecularassembler.integration.jei;

import me.myogoo.extendedmolecularassembler.ExtendedMolecularAssembler;
import me.myogoo.extendedmolecularassembler.init.EMAModPresence;
import me.myogoo.extendedmolecularassembler.integration.jei.avaritianeo.AvaritiaNeoJeiIntegration;
import me.myogoo.extendedmolecularassembler.integration.jei.extendedcrafting.ExtendedCraftingJeiIntegration;
import me.myogoo.extendedmolecularassembler.integration.jei.reavaritia.ReAvaritiaJeiIntegration;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

@JeiPlugin
public class EMAJeiPlugin implements IModPlugin {
    private static final ResourceLocation UID = ExtendedMolecularAssembler.makeId("jei_plugin");

    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return UID;
    }

    @Override
    public void registerRecipeCatalysts(@NotNull IRecipeCatalystRegistration registration) {
        if (EMAModPresence.isExtendedCraftingLoaded()) {
            ExtendedCraftingJeiIntegration.registerRecipeCatalysts(registration);
        }
        if (EMAModPresence.isReAvaritiaLoaded()) {
            ReAvaritiaJeiIntegration.registerRecipeCatalysts(registration);
        }
        if (EMAModPresence.isAvaritiaNeoLoaded()) {
            AvaritiaNeoJeiIntegration.registerRecipeCatalysts(registration);
        }
    }

    @Override
    public void registerRecipeTransferHandlers(@NotNull IRecipeTransferRegistration registration) {
        if (EMAModPresence.isExtendedCraftingLoaded()) {
            ExtendedCraftingJeiIntegration.registerRecipeTransferHandlers(registration);
        }
        if (EMAModPresence.isReAvaritiaLoaded()) {
            ReAvaritiaJeiIntegration.registerRecipeTransferHandlers(registration);
        }
        if (EMAModPresence.isAvaritiaNeoLoaded()) {
            AvaritiaNeoJeiIntegration.registerRecipeTransferHandlers(registration);
        }
    }
}
