package me.myogoo.extendedmolecularassembler.init;

import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.LoadingModList;
import net.neoforged.fml.loading.moddiscovery.ModInfo;

public final class EMAModPresence {
    private EMAModPresence() {
    }

    public static boolean isExtendedCraftingLoaded() {
        return isLoaded("extendedcrafting");
    }

    public static boolean isReAvaritiaLoaded() {
        return isLoaded("avaritia")
                && hasDisplayName("avaritia", "Re-Avaritia");
    }

    public static boolean isAvaritiaNeoLoaded() {
        return isLoaded("avaritia")
                && hasDisplayName("avaritia", "Avaritia");
    }

    public static boolean hasExtendedPatternRecipeProvider() {
        return isExtendedCraftingLoaded() || isReAvaritiaLoaded() || isAvaritiaNeoLoaded();
    }

    public static boolean isExtendedAELoaded() {
        return isLoaded("extendedae");
    }

    public static boolean isExtendedAEPlusLoaded() {
        return isLoaded("extendedae_plus");
    }

    public static boolean isAdvancedAELoaded() {
        return isLoaded("advanced_ae");
    }

    private static boolean isLoaded(String modId) {
        return ModList.get() != null
                ? ModList.get().isLoaded(modId)
                : LoadingModList.get().getMods().stream().map(ModInfo::getModId).anyMatch(modId::equals);
    }

    private static boolean hasDisplayName(String modId, String displayName) {
        if (ModList.get() != null) {
            return ModList.get().getModContainerById(modId)
                    .map(container -> displayName.equals(container.getModInfo().getDisplayName()))
                    .orElse(false);
        }

        return LoadingModList.get().getMods().stream()
                .anyMatch(info -> modId.equals(info.getModId()) && displayName.equals(info.getDisplayName()));
    }
}
