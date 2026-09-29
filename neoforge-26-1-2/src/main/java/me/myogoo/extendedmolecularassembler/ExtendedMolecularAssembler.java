package me.myogoo.extendedmolecularassembler;

import appeng.api.upgrades.Upgrades;
import appeng.core.definitions.AEItems;
import com.mojang.logging.LogUtils;
import me.myogoo.extendedmolecularassembler.api.annotation.ExtendedCrafting;
import me.myogoo.extendedmolecularassembler.api.annotation.InvTweaks;
import me.myogoo.extendedmolecularassembler.api.annotation.ReAvaritia;
import me.myogoo.extendedmolecularassembler.config.EMAConfig;
import me.myogoo.extendedmolecularassembler.init.EMABlockEntities;
import me.myogoo.extendedmolecularassembler.init.EMABlocks;
import me.myogoo.extendedmolecularassembler.init.EMACapabilities;
import me.myogoo.extendedmolecularassembler.init.EMAConditions;
import me.myogoo.extendedmolecularassembler.init.EMACreativeModeTabs;
import me.myogoo.extendedmolecularassembler.init.EMADataComponents;
import me.myogoo.extendedmolecularassembler.init.EMAItems;
import me.myogoo.extendedmolecularassembler.init.EMAMenus;
import me.myogoo.extendedmolecularassembler.init.EMANetwork;
import me.myogoo.extendedmolecularassembler.init.EMAOptionalIntegrations;
import me.myogoo.extendedmolecularassembler.init.EMAParts;
import me.myogoo.myotus.api.MyotusAPI;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.InterModComms;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import org.slf4j.Logger;

@Mod(ExtendedMolecularAssembler.MODID)
public class ExtendedMolecularAssembler {
    public static final String MODID = "extendedmolecularassembler";
    public static final Logger LOGGER = LogUtils.getLogger();

    public ExtendedMolecularAssembler(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.COMMON, EMAConfig.COMMON, "extendedmolecularassembler-common.toml");

        EMAOptionalIntegrations.registerDeferred();

        EMADataComponents.REGISTER.register(modEventBus);
        EMABlocks.BLOCKS.register(modEventBus);
        EMAItems.ITEMS.register(modEventBus);
        EMAConditions.REGISTER.register(modEventBus);
        EMAParts.REGISTER.register(modEventBus);
        EMABlockEntities.BLOCK_ENTITIES.register(modEventBus);
        EMAMenus.REGISTER.register(modEventBus);
        EMACreativeModeTabs.CREATIVE_MODE_TABS.register(modEventBus);

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(EMAOptionalIntegrations::onCommonSetup);
        modEventBus.addListener(EMACapabilities::register);
        modEventBus.addListener(EMANetwork::init);
        NeoForge.EVENT_BUS.addListener(ExtendedMolecularAssembler::synchronizeTableRecipes);

        if (MyotusAPI.integrations().isLoaded(InvTweaks.class)) {
            InterModComms.sendTo("invtweaks", "blacklist-screen",
                    () -> "me.myogoo.extendedmolecularassembler.client.screen.*");
            InterModComms.sendTo("invtweaks", "blacklist-screen",
                    () -> "me.myogoo.extendedmolecularassembler.integration.extendedae.client.*");
            InterModComms.sendTo("invtweaks", "blacklist-screen",
                    () -> "me.myogoo.extendedmolecularassembler.menu.*");
            InterModComms.sendTo("invtweaks", "blacklist-screen",
                    () -> "me.myogoo.extendedmolecularassembler.integration.extendedae.menu.*");
        }
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            Upgrades.add(AEItems.SPEED_CARD, EMABlocks.EXTENDED_MOLECULAR_ASSEMBLER.get(), 5);
            if (EMABlocks.EX_EXTENDED_MOLECULAR_ASSEMBLER != null) {
                Upgrades.add(AEItems.SPEED_CARD, EMABlocks.EX_EXTENDED_MOLECULAR_ASSEMBLER.get(), 5);
            }
            EMABlockEntities.registerBlockEntityItems();
            EMAOptionalIntegrations.registerBlockEntityItems();
        });
    }

    private static void synchronizeTableRecipes(OnDatapackSyncEvent event) {
        if (MyotusAPI.integrations().isLoaded(ExtendedCrafting.class)) {
            event.sendRecipes(com.blakebr0.extendedcrafting.init.ModRecipeTypes.TABLE.get());
        }
        if (MyotusAPI.integrations().isLoaded(ReAvaritia.class)) {
            event.sendRecipes(committee.nova.mods.avaritia.init.registry.ModRecipeTypes.CRAFTING_TABLE_RECIPE.get());
        }
    }

    public static Identifier makeId(String path) {
        return Identifier.fromNamespaceAndPath(MODID, path);
    }
}
