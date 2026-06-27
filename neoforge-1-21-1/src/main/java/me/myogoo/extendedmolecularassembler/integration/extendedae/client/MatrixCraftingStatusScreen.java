package me.myogoo.extendedmolecularassembler.integration.extendedae.client;

import appeng.client.gui.style.BackgroundGenerator;
import appeng.client.gui.widgets.AE2Button;
import me.myogoo.extendedmolecularassembler.integration.extendedae.MatrixCraftingStatus;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class MatrixCraftingStatusScreen extends Screen {
    private static final int PANEL_WIDTH = 230;
    private static final int PANEL_HEIGHT = 128;
    private static final int TAB_WIDTH = 92;
    private static final int TAB_HEIGHT = 20;
    private static final int ROW_LEFT = 22;
    private static final int ROW_TOP = 58;
    private static final int ROW_GAP = 18;

    private final MatrixCraftingStatus matrixStatus;
    private final MatrixCraftingStatus emaStatus;
    private Tab selectedTab = Tab.MATRIX;
    private AE2Button matrixTabButton;
    private AE2Button emaTabButton;

    public MatrixCraftingStatusScreen(MatrixCraftingStatus matrixStatus, MatrixCraftingStatus emaStatus) {
        super(Component.translatable("gui.extendedmolecularassembler.matrix.craftingStatus"));
        this.matrixStatus = matrixStatus;
        this.emaStatus = emaStatus;
    }

    @Override
    protected void init() {
        var x = panelX();
        var y = panelY();
        this.matrixTabButton = addRenderableWidget(new AE2Button(
                x + 12, y + 28, TAB_WIDTH, TAB_HEIGHT,
                tabTitle(Tab.MATRIX),
                btn -> selectTab(Tab.MATRIX)));
        this.emaTabButton = addRenderableWidget(new AE2Button(
                x + 12 + TAB_WIDTH + 6, y + 28, TAB_WIDTH, TAB_HEIGHT,
                tabTitle(Tab.EMA),
                btn -> selectTab(Tab.EMA)));
        addRenderableWidget(new AE2Button(
                x + PANEL_WIDTH - 64, y + PANEL_HEIGHT - 28, 52, 20,
                Component.translatable("gui.done"),
                btn -> onClose()));
        updateTabButtons();
    }

    private void selectTab(Tab tab) {
        this.selectedTab = tab;
        updateTabButtons();
    }

    private void updateTabButtons() {
        if (this.matrixTabButton != null) {
            this.matrixTabButton.setMessage(tabTitle(Tab.MATRIX));
        }
        if (this.emaTabButton != null) {
            this.emaTabButton.setMessage(tabTitle(Tab.EMA));
        }
    }

    private Component tabTitle(Tab tab) {
        var key = tab == Tab.MATRIX
                ? "gui.extendedmolecularassembler.matrix.status.tab.matrix"
                : "gui.extendedmolecularassembler.matrix.status.tab.ema";
        var label = Component.translatable(key);
        return this.selectedTab == tab ? Component.literal("▶ ").append(label) : label;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        var x = panelX();
        var y = panelY();
        BackgroundGenerator.draw(PANEL_WIDTH, PANEL_HEIGHT, guiGraphics, x, y);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        drawStatus(guiGraphics, x, y);
    }

    private void drawStatus(GuiGraphics guiGraphics, int x, int y) {
        var color = 0xFF404040;
        guiGraphics.drawString(this.font, this.title, x + 12, y + 12, color, false);

        var status = this.selectedTab == Tab.MATRIX ? this.matrixStatus : this.emaStatus;
        drawRow(guiGraphics, x, y, 0,
                Component.translatable("gui.extendedmolecularassembler.matrix.status.parallelCrafters"),
                Component.literal(status.availableParallelCrafters() + " / " + status.totalParallelCrafters()));
        drawRow(guiGraphics, x, y, 1,
                Component.translatable("gui.extendedmolecularassembler.matrix.status.patternSlots"),
                Component.literal(Integer.toString(status.totalPatternSlots())));
        drawRow(guiGraphics, x, y, 2,
                Component.translatable("gui.extendedmolecularassembler.matrix.status.speed"),
                Component.literal(status.speed() + " / " + status.maxSpeed()));
    }

    private void drawRow(GuiGraphics guiGraphics, int x, int y, int row, Component label, Component value) {
        var rowY = y + ROW_TOP + row * ROW_GAP;
        var labelX = x + ROW_LEFT;
        var valueX = x + PANEL_WIDTH - ROW_LEFT - this.font.width(value);
        guiGraphics.drawString(this.font, label, labelX, rowY, 0xFF606060, false);
        guiGraphics.drawString(this.font, value, valueX, rowY, 0xFF202020, false);
    }

    private int panelX() {
        return (this.width - PANEL_WIDTH) / 2;
    }

    private int panelY() {
        return (this.height - PANEL_HEIGHT) / 2;
    }

    private enum Tab {
        MATRIX,
        EMA
    }
}
