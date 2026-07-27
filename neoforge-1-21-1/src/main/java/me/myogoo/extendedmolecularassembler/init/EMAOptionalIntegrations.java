package me.myogoo.extendedmolecularassembler.init;

import me.myogoo.extendedmolecularassembler.api.annotation.AdvancedAE;
import me.myogoo.extendedmolecularassembler.api.annotation.ExtendedAE;
import me.myogoo.extendedmolecularassembler.api.annotation.ExtendedAEPlus;
import me.myogoo.extendedmolecularassembler.integration.AssemblerMatrixJobContext;
import me.myogoo.extendedmolecularassembler.integration.advancedae.EMAAdvancedAEIntegration;
import me.myogoo.extendedmolecularassembler.integration.extendedae.EMAExtendedAEIntegration;
import me.myogoo.myotus.api.MyotusAPI;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.jetbrains.annotations.Nullable;

public final class EMAOptionalIntegrations {
    private static boolean extendedAERegistered = false;
    private static boolean advancedAERegistered = false;

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
    }

    public static void addCreativeTabItems(CreativeModeTab.Output output) {
        if (extendedAERegistered) {
            EMAExtendedAEIntegration.addCreativeTabItems(output);
        }
        if (advancedAERegistered) {
            EMAAdvancedAEIntegration.addCreativeTabItems(output);
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

    public static ItemStack tryInsertIntoExtendedAEAssemblerMatrix(Level level, BlockPos pos, ItemStack stack) {
        if (extendedAERegistered && level != null) {
            return EMAExtendedAEIntegration.tryInsertIntoAssemblerMatrix(level, pos, stack);
        }
        return stack;
    }

    public static boolean hasEligibleExtendedAEPatternUploader(Object menu) {
        return extendedAERegistered
                && MyotusAPI.integrations().isLoaded(ExtendedAEPlus.class)
                && EMAExtendedAEIntegration.hasEligibleMatrixUploader(menu);
    }

    public static boolean canUploadToExtendedAEAssemblerMatrix(
            ServerPlayer player,
            Object menu,
            ItemStack stack) {
        return extendedAERegistered
                && MyotusAPI.integrations().isLoaded(ExtendedAEPlus.class)
                && EMAExtendedAEIntegration.canUploadToAssemblerMatrix(player, menu, stack);
    }

    public static boolean extendedAEAssemblerMatrixContainsPattern(
            ServerPlayer player,
            Object menu,
            ItemStack stack) {
        return extendedAERegistered
                && MyotusAPI.integrations().isLoaded(ExtendedAEPlus.class)
                && EMAExtendedAEIntegration.assemblerMatrixContainsPattern(player, menu, stack);
    }

    public static ItemStack uploadToExtendedAEAssemblerMatrix(
            ServerPlayer player,
            Object menu,
            ItemStack stack) {
        if (extendedAERegistered && MyotusAPI.integrations().isLoaded(ExtendedAEPlus.class)) {
            return EMAExtendedAEIntegration.uploadToAssemblerMatrix(player, menu, stack);
        }
        return stack;
    }

    @Nullable
    public static AssemblerMatrixJobContext claimExtendedAEAssemblerMatrixJobContext() {
        if (extendedAERegistered) {
            return EMAExtendedAEIntegration.claimAssemblerMatrixJobContext();
        }
        return null;
    }
}
