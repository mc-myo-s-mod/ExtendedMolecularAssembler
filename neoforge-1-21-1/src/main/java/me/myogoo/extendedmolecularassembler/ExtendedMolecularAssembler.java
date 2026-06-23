package me.myogoo.extendedmolecularassembler;

import appeng.api.upgrades.Upgrades;
import appeng.core.definitions.AEItems;
import com.mojang.logging.LogUtils;
import me.myogoo.extendedmolecularassembler.api.annotation.InvTweaks;
import me.myogoo.extendedmolecularassembler.config.EMAConfig;
import me.myogoo.extendedmolecularassembler.init.EMABlockEntities;
import me.myogoo.extendedmolecularassembler.init.EMABlocks;
import me.myogoo.extendedmolecularassembler.init.EMACapabilities;
import me.myogoo.extendedmolecularassembler.init.EMACreativeModeTabs;
import me.myogoo.extendedmolecularassembler.init.EMADataComponents;
import me.myogoo.extendedmolecularassembler.init.EMAItems;
import me.myogoo.extendedmolecularassembler.init.EMAMenus;
import me.myogoo.extendedmolecularassembler.init.EMANetwork;
import me.myogoo.extendedmolecularassembler.init.EMAOptionalIntegrations;
import me.myogoo.extendedmolecularassembler.init.EMAParts;
import me.myogoo.extendedmolecularassembler.integration.ae2wtlib.EMAAE2WTLibIntegration;
import me.myogoo.myotus.api.MyotusAPI;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.InterModComms;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
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
        EMAAE2WTLibIntegration.registerTerminal();
        EMAParts.REGISTER.register(modEventBus);
        EMABlockEntities.BLOCK_ENTITIES.register(modEventBus);
        EMAMenus.REGISTER.register(modEventBus);
        EMACreativeModeTabs.CREATIVE_MODE_TABS.register(modEventBus);

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(EMAAE2WTLibIntegration::onCommonSetup);
        modEventBus.addListener(EMACapabilities::register);
        modEventBus.addListener(EMANetwork::init);

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

    public static ResourceLocation makeId(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }
}
