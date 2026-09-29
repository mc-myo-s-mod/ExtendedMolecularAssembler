package me.myogoo.extendedmolecularassembler.gametest;

import java.util.Map;

import appeng.client.Point;
import appeng.client.gui.layout.SlotGridLayout;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.style.StyleManager;
import me.myogoo.extendedmolecularassembler.ExtendedMolecularAssembler;
import me.myogoo.extendedmolecularassembler.integration.extendedae.client.ExtendedCraftingPatternViewScreen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ExtendedMolecularAssembler.MODID, value = Dist.CLIENT,
        bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class EMAClientStyleChecks {
    private static boolean checked;

    private EMAClientStyleChecks() {
    }

    @SubscribeEvent
    public static void onScreenOpening(ScreenEvent.Opening event) {
        if (!Boolean.getBoolean("ema.checkClientStyles") || checked || !(event.getNewScreen() instanceof TitleScreen)) {
            return;
        }
        checked = true;
        var assembler = checkStyle("extended_molecular_assembler", "EXTENDED_MOLECULAR_ASSEMBLER_GRID_1");
        var background = assembler.getBackground();
        var progress = assembler.getImage("progressBar");
        if (background == null || background.getSrcX() != 0 || background.getSrcY() != 0
                || background.getSrcWidth() != 221 || background.getSrcHeight() != 290
                || assembler.getGeneratedBackground() != null || !assembler.getText().containsKey("job")
                || progress.getSrcX() != 222 || progress.getSrcY() != 1
                || progress.getSrcWidth() != 4 || progress.getSrcHeight() != 16) {
            throw new IllegalStateException("Assembler must use its custom 1.20.1 background and progress sprite");
        }
        checkStyle("extended_pattern_encoding_terminal", "EXTENDED_PATTERN_CRAFTING_GRID");
        int count = 2;
        for (var tier : new String[] { "epic", "legendary" }) {
            int extra = tier.equals("epic") ? 36 : 72;
            var largeAssembler = checkStyle(tier + "_molecular_assembler", "EXTENDED_MOLECULAR_ASSEMBLER_GRID_1");
            if (largeAssembler.getBackground().getSrcWidth() != 221 + extra
                    || largeAssembler.getBackground().getSrcHeight() != 290 + extra) {
                throw new IllegalStateException(tier + " assembler has an incorrect background size");
            }
            checkStyle(tier + "_pattern_encoding_terminal", "EXTENDED_PATTERN_CRAFTING_GRID");
            count += 2;
        }
        if (ModList.get().isLoaded("expatternprovider")) {
            checkStyle("extended_assembler_matrix_pattern_core", "ENCODED_PATTERN");
            checkStyle("epic_assembler_matrix_pattern_core", "ENCODED_PATTERN");
            checkStyle("legendary_assembler_matrix_pattern_core", "ENCODED_PATTERN");
            checkStyle("extended_crafting_pattern_view", "MACHINE_CRAFTING_GRID");
            checkStyle("epic_crafting_pattern_view", "MACHINE_CRAFTING_GRID");
            checkStyle("legendary_crafting_pattern_view", "MACHINE_CRAFTING_GRID");
            count += 6;
        }
        if (ModList.get().isLoaded("ae2wtlib")) {
            checkStyle("wireless_extended_pattern_encoding_terminal", "EXTENDED_PATTERN_CRAFTING_GRID");
            checkStyle("epic_wireless_extended_pattern_encoding_terminal", "EXTENDED_PATTERN_CRAFTING_GRID");
            checkStyle("legendary_wireless_extended_pattern_encoding_terminal", "EXTENDED_PATTERN_CRAFTING_GRID");
            count += 3;
        }
        ExtendedMolecularAssembler.LOGGER.info("EMA client style checks passed: {} native styles", count);
    }

    private static ScreenStyle checkStyle(String name, String gridSemantic) {
        var style = StyleManager.loadStyleDoc("/screens/extended_molecular_assembler/" + name + ".json");
        var grid = style.getSlots().get(gridSemantic);
        if (grid == null || grid.getGrid() != SlotGridLayout.BREAK_AFTER_9COLS) {
            throw new IllegalStateException(name + " must use BREAK_AFTER_9COLS for " + gridSemantic);
        }
        if (gridSemantic.equals("ENCODED_PATTERN")) {
            var background = style.getBackground();
            if (background == null || background.getSrcWidth() != 195 || background.getSrcHeight() != 199
                    || style.getGeneratedBackground() != null) {
                throw new IllegalStateException(name + " must use ExtendedAE's original core background");
            }
            var bounds = new Rect2i(0, 0, 195, 199);
            if (style.getSlots().get("PLAYER_INVENTORY").resolve(bounds).getY() != 117
                    || style.getSlots().get("PLAYER_HOTBAR").resolve(bounds).getY() != 175) {
                throw new IllegalStateException(name + " has misplaced inventory slots");
            }
        }
        if (gridSemantic.equals("MACHINE_CRAFTING_GRID")) {
            int side = name.startsWith("epic_") ? 11 : name.startsWith("legendary_") ? 13 : 9;
            int extra = (side - 9) * 18;
            var background = style.getGeneratedBackground();
            if (background == null || style.getBackground() != null
                    || background.getWidth() != 252 + extra || background.getHeight() != 216 + extra) {
                throw new IllegalStateException(name + " must use a correctly sized generated background");
            }
            var layout = ExtendedCraftingPatternViewScreen.createMenuLayout(style, side);
            var lastSlot = layout.craftingGrid().get(side * side - 1);
            if (lastSlot.getX() != 8 + (side - 1) * 18 || lastSlot.getY() != 36 + (side - 1) * 18
                    || layout.output().getX() != 219 + extra || layout.output().getY() != 110 + extra / 2) {
                throw new IllegalStateException(name + " changed pattern preview slot positions");
            }
        }
        if (gridSemantic.equals("EXTENDED_PATTERN_CRAFTING_GRID")) {
            if (name.contains("wireless_") && (style.getImage("singularityBackground") == null
                    || !style.getSlots().containsKey("AE2WTLIB_SINGULARITY"))) {
                throw new IllegalStateException(name + " must include the AE2WTLib wireless controls");
            }
            boolean epic = name.startsWith("epic_");
            boolean legendary = name.startsWith("legendary_");
            boolean large = epic || legendary;
            int width = epic ? 210 : legendary ? 246 : 204;
            int height = epic ? 382 : legendary ? 418 : 371;
            var terminal = style.getTerminalStyle();
            if (terminal.getScreenWidth() != width || terminal.getScreenHeight(3) != height
                    || terminal.getScreenHeight(7) != height + 72) {
                throw new IllegalStateException(name + " must use the custom 1.20.1 terminal background");
            }
            var positions = Map.of(
                    "EXTENDED_PATTERN_CRAFTING_GRID", new Point(8, 84),
                    "EXTENDED_PATTERN_CRAFTING_RESULT", epic ? new Point(182, 308)
                            : legendary ? new Point(215, 342) : new Point(178, 174),
                    "BLANK_PATTERN", epic ? new Point(182, 285)
                            : legendary ? new Point(185, 342) : new Point(178, 86),
                    "ENCODED_PATTERN", epic ? new Point(182, 354)
                            : legendary ? new Point(215, 390) : new Point(178, 221),
                    "PLAYER_INVENTORY", new Point(8, height - 82),
                    "PLAYER_HOTBAR", new Point(8, height - 24));
            for (int rows : new int[] { 3, 7 }) {
                var bounds = new Rect2i(0, 0, terminal.getScreenWidth(), terminal.getScreenHeight(rows));
                for (var entry : positions.entrySet()) {
                    var actual = style.getSlots().get(entry.getKey()).resolve(bounds);
                    var expected = entry.getValue().move(0, (rows - 3) * 18);
                    if (actual.getX() != expected.getX() || actual.getY() != expected.getY()) {
                        throw new IllegalStateException(name + " has a misplaced " + entry.getKey());
                    }
                }
                if (large) {
                    var buttons = Map.of(
                            "clearPattern", new Point(legendary ? width - 36 : 136, height - 96),
                            "canSubstituteFluids", new Point(legendary ? width - 48 : 148, height - 96),
                            "substitutions", new Point(legendary ? width - 60 : 160, height - 96),
                            "recipeCycle", new Point(legendary ? width - 20 : 112, height - 100),
                            "encodePattern", new Point(epic ? 182 : 215, height - 54));
                    for (var entry : buttons.entrySet()) {
                        var actual = style.getWidget(entry.getKey()).resolve(bounds);
                        var expected = entry.getValue().move(0, (rows - 3) * 18);
                        if (actual.getX() != expected.getX() || actual.getY() != expected.getY()) {
                            throw new IllegalStateException(name + " has a misplaced " + entry.getKey());
                        }
                    }
                }
            }
        }
        return style;
    }
}
