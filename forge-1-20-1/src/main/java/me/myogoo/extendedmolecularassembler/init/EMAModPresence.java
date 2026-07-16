package me.myogoo.extendedmolecularassembler.init;

import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.LoadingModList;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;

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

    public static boolean isExtendedAELoaded() {
        return isLoaded("expatternprovider");
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
