package me.myogoo.extendedmolecularassembler;

import appeng.api.upgrades.Upgrades;
import appeng.core.definitions.AEItems;
import com.mojang.logging.LogUtils;
import me.myogoo.extendedmolecularassembler.api.annotation.InvTweaks;
import me.myogoo.extendedmolecularassembler.config.EMAConfig;
import me.myogoo.extendedmolecularassembler.client.EMAClient;
import me.myogoo.extendedmolecularassembler.data.EMADataGenerators;
import me.myogoo.extendedmolecularassembler.init.EMABlockEntities;
import me.myogoo.extendedmolecularassembler.init.EMABlocks;
import me.myogoo.extendedmolecularassembler.init.EMAConditions;
import me.myogoo.extendedmolecularassembler.init.EMACreativeModeTabs;
import me.myogoo.extendedmolecularassembler.init.EMAItems;
import me.myogoo.extendedmolecularassembler.init.EMAMenus;
import me.myogoo.extendedmolecularassembler.init.EMANetwork;
import me.myogoo.extendedmolecularassembler.init.EMAOptionalIntegrations;
import me.myogoo.extendedmolecularassembler.init.EMAParts;
import me.myogoo.myotus.api.MyotusAPI;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(ExtendedMolecularAssembler.MODID)
public class ExtendedMolecularAssembler {
    public static final String MODID = "extendedmolecularassembler";
    public static final Logger LOGGER = LogUtils.getLogger();

    public ExtendedMolecularAssembler() {
        var modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, EMAConfig.COMMON);
        EMAConditions.register();
        EMANetwork.register();
        EMABlocks.BLOCKS.register(modEventBus);
        EMAItems.ITEMS.register(modEventBus);
        EMAParts.REGISTER.register(modEventBus);
        EMABlockEntities.BLOCK_ENTITIES.register(modEventBus);
        EMAMenus.REGISTER.register(modEventBus);
        EMACreativeModeTabs.CREATIVE_MODE_TABS.register(modEventBus);

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::construct);
        modEventBus.addListener(EMAOptionalIntegrations::onCommonSetup);
        modEventBus.addListener(EMADataGenerators::onGatherData);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> new EMAClient(modEventBus));
    }

    private void construct(FMLConstructModEvent event) {
        // Myotus discovers annotated integrations during mod construction.
        event.enqueueWork(EMAOptionalIntegrations::registerDeferred);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
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
            Upgrades.add(AEItems.SPEED_CARD, EMABlocks.EXTENDED_MOLECULAR_ASSEMBLER.get(), 5);
            Upgrades.add(AEItems.SPEED_CARD, EMABlocks.EPIC_MOLECULAR_ASSEMBLER.get(), 5);
            Upgrades.add(AEItems.SPEED_CARD, EMABlocks.LEGENDARY_MOLECULAR_ASSEMBLER.get(), 5);
            if (EMABlocks.EX_EXTENDED_MOLECULAR_ASSEMBLER != null) {
                Upgrades.add(AEItems.SPEED_CARD, EMABlocks.EX_EXTENDED_MOLECULAR_ASSEMBLER.get(), 5);
                Upgrades.add(AEItems.SPEED_CARD, EMABlocks.EX_EPIC_MOLECULAR_ASSEMBLER.get(), 5);
                Upgrades.add(AEItems.SPEED_CARD, EMABlocks.EX_LEGENDARY_MOLECULAR_ASSEMBLER.get(), 5);
            }
            EMABlockEntities.registerBlockEntityItems();
            EMAOptionalIntegrations.registerBlockEntityItems();
            EMAOptionalIntegrations.registerNetwork();
        });
    }

    public static ResourceLocation makeId(String path) {
        return new ResourceLocation(MODID, path);
    }
}
