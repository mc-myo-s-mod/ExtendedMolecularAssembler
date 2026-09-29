package me.myogoo.extendedmolecularassembler.integration.jei;

import me.myogoo.extendedmolecularassembler.ExtendedMolecularAssembler;
import me.myogoo.extendedmolecularassembler.api.annotation.ExtendedCrafting;
import me.myogoo.extendedmolecularassembler.api.annotation.ReAvaritia;
import me.myogoo.extendedmolecularassembler.integration.jei.extendedcrafting.ExtendedCraftingJeiIntegration;
import me.myogoo.extendedmolecularassembler.integration.jei.reavaritia.ReAvaritiaJeiIntegration;
import me.myogoo.myotus.api.MyotusAPI;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

@JeiPlugin
public class EMAJeiPlugin implements IModPlugin {
    private static final Identifier UID = Identifier.fromNamespaceAndPath(
            ExtendedMolecularAssembler.MODID, "jei_plugin");

    @Override
    public @NotNull Identifier getPluginUid() {
        return UID;
    }

    @Override
    public void registerRecipeTransferHandlers(@NotNull IRecipeTransferRegistration registration) {
        if (MyotusAPI.integrations().isLoaded(ExtendedCrafting.class)) {
            ExtendedCraftingJeiIntegration.registerRecipeTransferHandlers(registration);
        }
        if (MyotusAPI.integrations().isLoaded(ReAvaritia.class)) {
            ReAvaritiaJeiIntegration.registerRecipeTransferHandlers(registration);
        }
    }
}
