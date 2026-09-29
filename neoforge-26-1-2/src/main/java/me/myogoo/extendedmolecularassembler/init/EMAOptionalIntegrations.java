package me.myogoo.extendedmolecularassembler.init;

import me.myogoo.extendedmolecularassembler.api.annotation.AdvancedAE;
import me.myogoo.extendedmolecularassembler.api.annotation.ExtendedAE;
import me.myogoo.extendedmolecularassembler.api.annotation.ExtendedAEPlus;
import me.myogoo.extendedmolecularassembler.config.EMAConfig;
import me.myogoo.extendedmolecularassembler.integration.advancedae.EMAAdvancedAEIntegration;
import me.myogoo.extendedmolecularassembler.integration.ae2wtlib.EMAAE2WTLibIntegration;
import me.myogoo.extendedmolecularassembler.integration.extendedae.EMAExtendedAEIntegration;
import me.myogoo.extendedmolecularassembler.menu.pattern.ExtendedPatternEncodingTermMenu;
import me.myogoo.myotus.api.MyotusAPI;
import me.myogoo.myotus.api.annotation.mods.AE2WTLib;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public final class EMAOptionalIntegrations {
    private static boolean extendedAERegistered = false;
    private static boolean advancedAERegistered = false;
    private static boolean ae2WTLibRegistered = false;

    private EMAOptionalIntegrations() {
    }

    public static void registerDeferred() {
        if (MyotusAPI.integrations().isLoaded(ExtendedAE.class)) {
            EMABlocks.registerExtendedAEDeferred();
            EMAItems.registerExtendedAEDeferred();
            EMABlockEntities.registerExtendedAEDeferred();
            EMAExtendedAEIntegration.registerDeferred();
            extendedAERegistered = true;
        }
        if (MyotusAPI.integrations().isLoaded(AdvancedAE.class)) {
            EMAAdvancedAEIntegration.registerDeferred();
            advancedAERegistered = true;
        }
        if (MyotusAPI.integrations().isLoaded(AE2WTLib.class)) {
            EMAAE2WTLibIntegration.registerTerminal();
            ae2WTLibRegistered = true;
        }
    }

    public static void addCreativeTabItems(CreativeModeTab.Output output) {
        if (ae2WTLibRegistered) {
            EMAAE2WTLibIntegration.addCreativeTabItems(output);
        }
        if (extendedAERegistered) {
            EMAExtendedAEIntegration.addCreativeTabItems(output, isExtendedAEPlusStyleContentEnabled());
        }
        if (advancedAERegistered) {
            EMAAdvancedAEIntegration.addCreativeTabItems(output);
        }
    }

    public static void onCommonSetup(FMLCommonSetupEvent event) {
        if (ae2WTLibRegistered) {
            EMAAE2WTLibIntegration.onCommonSetup(event);
        }
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        if (extendedAERegistered) {
            EMAExtendedAEIntegration.registerCapabilities(event);
        }
        if (advancedAERegistered) {
            EMAAdvancedAEIntegration.registerCapabilities(event);
        }
    }

    public static void registerBlockEntityItems() {
        if (extendedAERegistered) {
            EMAExtendedAEIntegration.registerBlockEntityItems();
        }
        if (advancedAERegistered) {
            EMAAdvancedAEIntegration.registerBlockEntityItems();
        }
    }

    public static void registerNetwork(PayloadRegistrar registrar) {
        if (extendedAERegistered) {
            EMAExtendedAEIntegration.registerNetwork(registrar);
        }
    }

    public static void addAE2WTLibSingularitySlot(ExtendedPatternEncodingTermMenu menu, Object host) {
        if (ae2WTLibRegistered) {
            EMAAE2WTLibIntegration.addSingularitySlot(menu, host);
        }
    }

    public static boolean isWirelessExtendedPatternEncodingTerminalHost(Object host) {
        return ae2WTLibRegistered && EMAAE2WTLibIntegration.isWirelessExtendedPatternEncodingTerminalHost(host);
    }

    public static boolean isExtendedAEPlusStyleContentEnabled() {
        return extendedAERegistered
                && (MyotusAPI.integrations().isLoaded(ExtendedAEPlus.class)
                        || EMAConfig.standaloneExtendedAEPlusContent());
    }

    public static ItemStack tryInsertIntoExtendedAEAssemblerMatrix(Level level, BlockPos pos, ItemStack stack) {
        if (extendedAERegistered && level != null) {
            return EMAExtendedAEIntegration.tryInsertIntoAssemblerMatrix(level, pos, stack);
        }
        return stack;
    }

    public static boolean hasEligibleExtendedAEPatternUploader(Object menu) {
        return extendedAERegistered
                && isExtendedAEPlusStyleContentEnabled()
                && EMAExtendedAEIntegration.hasEligibleMatrixUploader(menu);
    }

    public static boolean canUploadToExtendedAEAssemblerMatrix(
            ServerPlayer player,
            Object menu,
            ItemStack stack) {
        return extendedAERegistered
                && isExtendedAEPlusStyleContentEnabled()
                && EMAExtendedAEIntegration.canUploadToAssemblerMatrix(player, menu, stack);
    }

    public static boolean extendedAEAssemblerMatrixContainsPattern(
            ServerPlayer player,
            Object menu,
            ItemStack stack) {
        return extendedAERegistered
                && isExtendedAEPlusStyleContentEnabled()
                && EMAExtendedAEIntegration.assemblerMatrixContainsPattern(player, menu, stack);
    }

    public static ItemStack uploadToExtendedAEAssemblerMatrix(
            ServerPlayer player,
            Object menu,
            ItemStack stack) {
        if (isExtendedAEPlusStyleContentEnabled()) {
            return EMAExtendedAEIntegration.uploadToAssemblerMatrix(player, menu, stack);
        }
        return stack;
    }

}
