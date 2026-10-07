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
                || progress.getSrcX() != 223 || progress.getSrcY() != 1
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
            for (var tier : new String[] { "extended", "epic", "legendary" }) {
                int extra = tier.equals("epic") ? 36 : tier.equals("legendary") ? 72 : 0;
                var exAssembler = checkStyle("ex_" + tier + "_molecular_assembler",
                        "EXTENDED_MOLECULAR_ASSEMBLER_GRID_1");
                var exBackground = exAssembler.getBackground();
                var exProgress = exAssembler.getImage("progressBar");
                int outputX = tier.equals("epic") ? 221 : tier.equals("legendary") ? 258 : 186;
                int outputY = tier.equals("epic") ? 119 : tier.equals("legendary") ? 126 : 98;
                int spriteX = tier.equals("epic") ? 258 : tier.equals("legendary") ? 294 : 223;
                if (exBackground.getSrcWidth() != 221 + extra || exBackground.getSrcHeight() != 290 + extra
                        || exProgress.getSrcX() != spriteX || exProgress.getSrcY() != 1
                        || exProgress.getSrcWidth() != 4 || exProgress.getSrcHeight() != 16) {
                    throw new IllegalStateException("Ex " + tier + " assembler has incorrect atlas bounds");
                }
                var bounds = new Rect2i(0, 0, 221 + extra, 290 + extra);
                if (extra > 0 && exAssembler.getSlots().containsKey("ENCODED_PATTERN")) {
                    throw new IllegalStateException("Ex " + tier + " assembler must not inherit a pattern slot");
                }
                for (int lane = 1; lane <= 8; lane++) {
                    var grid = exAssembler.getSlots().get("EXTENDED_MOLECULAR_ASSEMBLER_GRID_" + lane)
                            .resolve(bounds);
                    var output = exAssembler.getSlots().get("EXTENDED_MOLECULAR_ASSEMBLER_OUTPUT_" + lane)
                            .resolve(bounds);
                    if (grid.getX() != 10 || grid.getY() != 30
                            || output.getX() != outputX || output.getY() != outputY) {
                        throw new IllegalStateException("Ex " + tier + " assembler has misplaced lane slots");
                    }
                }
                for (var entry : Map.of("PLAYER_INVENTORY", new Point(10, 207 + extra),
                        "PLAYER_HOTBAR", new Point(10, 265 + extra)).entrySet()) {
                    var actual = exAssembler.getSlots().get(entry.getKey()).resolve(bounds);
                    var expected = entry.getValue();
                    if (actual.getX() != expected.getX() || actual.getY() != expected.getY()) {
                        throw new IllegalStateException("Ex " + tier + " assembler has misplaced inventory slots");
                    }
                }
                for (var entry : Map.of("dialog_title", new Point(8, 6), "job", new Point(8, 18),
                        "player_inventory_title", new Point(10, 195 + extra)).entrySet()) {
                    var actual = exAssembler.getText().get(entry.getKey()).getPosition().resolve(bounds);
                    var expected = entry.getValue();
                    if (actual.getX() != expected.getX() || actual.getY() != expected.getY()) {
                        throw new IllegalStateException("Ex " + tier + " assembler has a misplaced label");
                    }
                }
                var bar = exAssembler.getWidget("progressBar").resolve(bounds);
                if (bar.getX() != outputX + 23 || bar.getY() != outputY) {
                    throw new IllegalStateException("Ex " + tier + " assembler has a misplaced progress bar");
                }
            }
            checkStyle("extended_assembler_matrix_pattern_core", "ENCODED_PATTERN");
            checkStyle("extended_crafting_pattern_view", "MACHINE_CRAFTING_GRID");
            checkStyle("epic_crafting_pattern_view", "MACHINE_CRAFTING_GRID");
            checkStyle("legendary_crafting_pattern_view", "MACHINE_CRAFTING_GRID");
            count += 7;
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
            var widgets = ScreenStyle.GSON.toJsonTree(style).getAsJsonObject().getAsJsonObject("widgets");
            for (var navigation : new String[] { "matrix9", "matrix11", "matrix13" }) {
                if (widgets.has(navigation)) {
                    throw new IllegalStateException(name + " must not have tier navigation: " + navigation);
                }
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
            int height = epic ? 382 : legendary ? 418 : 346;
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
                var buttons = large ? Map.of(
                            "clearPattern", new Point(legendary ? width - 36 : 136, height - 96),
                            "canSubstituteFluids", new Point(legendary ? width - 48 : 148, height - 96),
                            "substitutions", new Point(legendary ? width - 60 : 160, height - 96),
                            "recipeCycle", new Point(legendary ? width - 20 : 112, height - 100),
                            "encodePattern", new Point(epic ? 182 : 215, height - 54))
                        : Map.of(
                                "clearPattern", new Point(160, 251),
                                "canSubstituteFluids", new Point(177, 110),
                                "substitutions", new Point(187, 110),
                                "recipeCycle", new Point(178, 124),
                                "encodePattern", new Point(178, 195));
                for (var entry : buttons.entrySet()) {
                    var actual = style.getWidget(entry.getKey()).resolve(bounds);
                    var expected = entry.getValue().move(0, (rows - 3) * 18);
                    if (actual.getX() != expected.getX() || actual.getY() != expected.getY()) {
                        throw new IllegalStateException(name + " has a misplaced " + entry.getKey());
                    }
                }
                var labels = Map.of(
                        "crafting_grid_title", new Point(8, 73),
                        "player_inventory_title", new Point(8, height - 93));
                for (var entry : labels.entrySet()) {
                    var actual = style.getText().get(entry.getKey()).getPosition().resolve(bounds);
                    var expected = entry.getValue().move(0, (rows - 3) * 18);
                    if (actual.getX() != expected.getX() || actual.getY() != expected.getY()) {
                        throw new IllegalStateException(name + " has a misplaced " + entry.getKey());
                    }
                }
            }
        }
        return style;
    }
}
