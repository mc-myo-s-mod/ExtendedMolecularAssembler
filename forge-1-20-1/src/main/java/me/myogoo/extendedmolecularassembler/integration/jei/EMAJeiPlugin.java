package me.myogoo.extendedmolecularassembler.integration.jei;

import me.myogoo.extendedmolecularassembler.ExtendedMolecularAssembler;
import me.myogoo.extendedmolecularassembler.api.annotation.AvaritiaNeo;
import me.myogoo.extendedmolecularassembler.api.annotation.ExPatternProvider;
import me.myogoo.extendedmolecularassembler.api.annotation.ExtendedCrafting;
import me.myogoo.extendedmolecularassembler.api.annotation.ReAvaritia;
import me.myogoo.extendedmolecularassembler.init.EMAItems;
import me.myogoo.extendedmolecularassembler.integration.jei.avaritianeo.AvaritiaNeoJeiIntegration;
import me.myogoo.extendedmolecularassembler.integration.jei.extendedcrafting.ExtendedCraftingJeiIntegration;
import me.myogoo.extendedmolecularassembler.integration.jei.reavaritia.ReAvaritiaJeiIntegration;
import me.myogoo.myotus.api.MyotusAPI;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
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
        registration.addRecipeCatalyst(new ItemStack(EMAItems.EXTENDED_MOLECULAR_ASSEMBLER.get()), RecipeTypes.CRAFTING);
        if (MyotusAPI.integrations().isLoaded(ExPatternProvider.class)) {
            registration.addRecipeCatalyst(
                    new ItemStack(EMAItems.EX_EXTENDED_MOLECULAR_ASSEMBLER.get()),
                    RecipeTypes.CRAFTING);
        }

        if (MyotusAPI.integrations().isLoaded(ExtendedCrafting.class)) {
            ExtendedCraftingJeiIntegration.registerRecipeCatalysts(registration);
        }
        if (MyotusAPI.integrations().isLoaded(ReAvaritia.class)) {
            ReAvaritiaJeiIntegration.registerRecipeCatalysts(registration);
        }
        if (MyotusAPI.integrations().isLoaded(AvaritiaNeo.class)) {
            AvaritiaNeoJeiIntegration.registerRecipeCatalysts(registration);
        }
    }

    @Override
    public void registerRecipeTransferHandlers(@NotNull IRecipeTransferRegistration registration) {
        if (MyotusAPI.integrations().isLoaded(ExtendedCrafting.class)) {
            ExtendedCraftingJeiIntegration.registerRecipeTransferHandlers(registration);
        }
        if (MyotusAPI.integrations().isLoaded(ReAvaritia.class)) {
            ReAvaritiaJeiIntegration.registerRecipeTransferHandlers(registration);
        }
        if (MyotusAPI.integrations().isLoaded(AvaritiaNeo.class)) {
            AvaritiaNeoJeiIntegration.registerRecipeTransferHandlers(registration);
        }
    }
}
