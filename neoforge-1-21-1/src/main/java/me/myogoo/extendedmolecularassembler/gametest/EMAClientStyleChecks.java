package me.myogoo.extendedmolecularassembler.gametest;

import appeng.client.gui.layout.SlotGridLayout;
import appeng.client.gui.style.StyleManager;
import me.myogoo.extendedmolecularassembler.ExtendedMolecularAssembler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.TitleScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import org.spongepowered.asm.mixin.MixinEnvironment;

@EventBusSubscriber(modid = ExtendedMolecularAssembler.MODID, value = Dist.CLIENT)
public final class EMAClientStyleChecks {
    private static boolean checked;

    private EMAClientStyleChecks() {
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        var minecraft = Minecraft.getInstance();
        if (!Boolean.getBoolean("ema.clientSmoke") || checked || minecraft.getOverlay() != null
                || !(minecraft.screen instanceof TitleScreen)) {
            return;
        }
        checked = true;
        checkStyle("extended_molecular_assembler", "EXTENDED_MOLECULAR_ASSEMBLER_GRID_1");
        checkStyle("extended_pattern_encoding_terminal", "EXTENDED_PATTERN_CRAFTING_GRID");
        if (ModList.get().isLoaded("ae2wtlib")) {
            checkStyle("wireless_extended_pattern_encoding_terminal", "EXTENDED_PATTERN_CRAFTING_GRID");
        }
        if (ModList.get().isLoaded("extendedae")) {
            checkStyle("extended_assembler_matrix_pattern_core", "ENCODED_PATTERN");
        }
        MixinEnvironment.getCurrentEnvironment().audit();
        ExtendedMolecularAssembler.LOGGER.info("EMA client style and mixin audit PASS");
        minecraft.stop();
    }

    private static void checkStyle(String name, String gridSemantic) {
        var style = StyleManager.loadStyleDoc("/screens/extended_molecular_assembler/" + name + ".json");
        var grid = style.getSlots().get(gridSemantic);
        if (grid == null || grid.getGrid() != SlotGridLayout.BREAK_AFTER_9COLS) {
            throw new AssertionError(name + " has an incorrect " + gridSemantic + " layout");
        }
    }
}
