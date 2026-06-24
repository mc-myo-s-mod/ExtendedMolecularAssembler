package me.myogoo.extendedmolecularassembler;

import com.mojang.logging.LogUtils;
import me.myogoo.extendedmolecularassembler.client.EMAClient;
import me.myogoo.extendedmolecularassembler.config.EMAConfig;
import me.myogoo.extendedmolecularassembler.data.EMADataGenerators;
import me.myogoo.extendedmolecularassembler.init.EMABlockEntities;
import me.myogoo.extendedmolecularassembler.init.EMABlocks;
import me.myogoo.extendedmolecularassembler.init.EMAItems;
import me.myogoo.extendedmolecularassembler.init.EMAMenus;
import me.myogoo.extendedmolecularassembler.init.EMAModIntegration;
import me.myogoo.extendedmolecularassembler.init.EMAOptionalIntegrations;
import me.myogoo.extendedmolecularassembler.init.EMAParts;
import me.myogoo.extendedmolecularassembler.integration.ae2wtlib.EMAAE2WTLibIntegration;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(ExtendedMolecularAssembler.MODID)
public final class ExtendedMolecularAssembler {
    public static final String MODID = "extendedmolecularassembler";
    public static final Logger LOGGER = LogUtils.getLogger();

    public ExtendedMolecularAssembler() {
        LOGGER.info("Initializing Extended Molecular Assembler Forge 1.20.1");
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, EMAConfig.SPEC);
        var modBus = FMLJavaModLoadingContext.get().getModEventBus();
        EMAModIntegration.initialize();
        EMAOptionalIntegrations.registerDeferred();
        EMABlocks.BLOCKS.register(modBus);
        EMAItems.ITEMS.register(modBus);
        EMAParts.ITEMS.register(modBus);
        EMABlockEntities.BLOCK_ENTITIES.register(modBus);
        EMAMenus.MENUS.register(modBus);
        EMAAE2WTLibIntegration.registerTerminal();
        modBus.addListener(EMAAE2WTLibIntegration::onCommonSetup);
        modBus.addListener(EMADataGenerators::onGatherData);
        DistExecutor.unsafeRunWhenOn(net.minecraftforge.api.distmarker.Dist.CLIENT,
                () -> () -> new EMAClient(modBus));
    }

    public static ResourceLocation makeId(String path) {
        return new ResourceLocation(MODID, path);
    }
}
