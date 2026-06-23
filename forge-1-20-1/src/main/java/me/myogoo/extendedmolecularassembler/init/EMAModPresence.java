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
        return isLoaded("reavaritia") || isLoaded("avaritia");
    }

    public static boolean isAvaritiaNeoLoaded() {
        return isLoaded("avaritianeo") || isLoaded("avaritia");
    }

    public static boolean isExtendedAELoaded() {
        return isLoaded("expatternprovider");
    }

    private static boolean isLoaded(String modId) {
        return ModList.get() != null
                ? ModList.get().isLoaded(modId)
                : LoadingModList.get().getMods().stream().map(ModInfo::getModId).anyMatch(modId::equals);
    }
}
