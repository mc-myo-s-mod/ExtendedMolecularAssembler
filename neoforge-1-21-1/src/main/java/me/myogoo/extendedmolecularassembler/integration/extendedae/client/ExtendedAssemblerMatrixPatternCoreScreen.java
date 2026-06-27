package me.myogoo.extendedmolecularassembler.integration.extendedae.client;

import appeng.api.crafting.PatternDetailsHelper;
import appeng.api.stacks.GenericStack;
import appeng.client.Point;
import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.Icon;
import appeng.client.gui.style.PaletteColor;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.AETextField;
import appeng.client.gui.widgets.Scrollbar;
import appeng.core.localization.GuiText;
import appeng.core.network.serverbound.InventoryActionPacket;
import appeng.crafting.pattern.EncodedPatternItem;
import appeng.helpers.InventoryAction;
import com.glodblock.github.extendedae.client.button.ActionEPPButton;
import com.glodblock.github.extendedae.client.button.CycleEPPButton;
import com.glodblock.github.extendedae.util.FCUtil;
import me.myogoo.extendedmolecularassembler.client.widget.EMAIconButton;
import me.myogoo.extendedmolecularassembler.integration.extendedae.menu.ExtendedAssemblerMatrixPatternCoreMenu;
import me.myogoo.extendedmolecularassembler.integration.extendedae.menu.ExtendedAssemblerMatrixPatternCoreMenu.PatternEntry;
import me.myogoo.extendedmolecularassembler.integration.extendedae.network.EMAOpenExtendedAEAssemblerMatrixScreenPacket;
import me.myogoo.extendedmolecularassembler.integration.extendedae.network.EMARequestMatrixCraftingStatusPacket;
import me.myogoo.extendedmolecularassembler.pattern.ExtendedTableCraftingPattern;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;

public class ExtendedAssemblerMatrixPatternCoreScreen
        extends AEBaseScreen<ExtendedAssemblerMatrixPatternCoreMenu> {
    private static final int PATTERN_COLS = 9;
    private static final int VISIBLE_PATTERN_ROWS = 4;
    private static final int VISIBLE_PATTERN_SLOTS = PATTERN_COLS * VISIBLE_PATTERN_ROWS;
    private static final int SLOT_SIZE = 18;
    private static final int PATTERN_LEFT = 8;
    private static final int PATTERN_TOP = 31;

    private final ActionEPPButton cancelJobsButton;
    private final ActionEPPButton backToMatrixButton;
    private final EMAIconButton craftingStatusButton;
    private final CycleEPPButton patternAccessButton;
    private final Scrollbar patternScrollbar;
    private final AETextField searchField;
    private final List<PatternEntry> filteredPatternEntries = new ArrayList<>();
    private int lastRunningThreads = Integer.MIN_VALUE;
    private Component runningThreadsText = Component.empty();

    public ExtendedAssemblerMatrixPatternCoreScreen(ExtendedAssemblerMatrixPatternCoreMenu menu,
            Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);

        this.patternScrollbar = this.widgets.addScrollBar("patternScrollbar", Scrollbar.BIG);
        this.patternScrollbar.setHeight(VISIBLE_PATTERN_ROWS * SLOT_SIZE - 2);
        this.patternScrollbar.setCaptureMouseWheel(true);

        this.searchField = this.widgets.addTextField("search");
        this.searchField.setResponder(ignored -> this.refreshFilteredPatternEntries());
        this.searchField.setPlaceholder(GuiText.SearchPlaceholder.text());
        this.searchField.setTooltipMessage(List.of(
                Component.translatable("gui.extendedmolecularassembler.matrix.searchPatterns")));

        this.backToMatrixButton = new ActionEPPButton(
                btn -> PacketDistributor.sendToServer(new EMAOpenExtendedAEAssemblerMatrixScreenPacket(
                        AssemblerMatrixNavigationContext.consumeMatrixPosOr(this.menu.getHost().getBlockPos()),
                        EMAOpenExtendedAEAssemblerMatrixScreenPacket.Target.MATRIX)),
                Icon.BACK);
        this.backToMatrixButton.setMessage(Component.translatable("gui.extendedmolecularassembler.matrix.backToMatrix"));

        this.craftingStatusButton = new EMAIconButton(Icon.CRAFT_HAMMER,
                Component.translatable("gui.extendedmolecularassembler.matrix.craftingStatus"),
                btn -> PacketDistributor.sendToServer(new EMARequestMatrixCraftingStatusPacket(
                        this.menu.getHost().getBlockPos())));

        this.patternAccessButton = new CycleEPPButton();
        this.patternAccessButton.addActionPair(Icon.PATTERN_ACCESS_SHOW,
                Component.translatable("gui.extendedmolecularassembler.matrix.showInPatternAccess"),
                btn -> this.menu.setPatternAccessVisibleFromClient(true));
        this.patternAccessButton.addActionPair(Icon.PATTERN_ACCESS_HIDE,
                Component.translatable("gui.extendedmolecularassembler.matrix.hideFromPatternAccess"),
                btn -> this.menu.setPatternAccessVisibleFromClient(false));

        this.cancelJobsButton = new ActionEPPButton(btn -> this.menu.cancelJobsFromClient(), Icon.CLEAR);
        this.cancelJobsButton.setMessage(Component.translatable("gui.extendedmolecularassembler.matrix.cancelJobs"));

        addToLeftToolbar(this.cancelJobsButton);
        addToLeftToolbar(this.patternAccessButton);
        addToLeftToolbar(this.backToMatrixButton);

        this.refreshFilteredPatternEntries();
    }

    @Override
    public void init() {
        super.init();
        this.setInitialFocus(this.searchField);
        this.craftingStatusButton.setX(this.leftPos + this.imageWidth - 22);
        this.craftingStatusButton.setY(this.topPos + 4);
        this.addRenderableWidget(this.craftingStatusButton);
        this.refreshFilteredPatternEntries();
    }

    @Override
    public void removed() {
        AssemblerMatrixNavigationContext.clear();
        super.removed();
    }

    @Override
    public boolean mouseClicked(double xCoord, double yCoord, int btn) {
        if (btn == 1 && this.searchField.isMouseOver(xCoord, yCoord)) {
            this.searchField.setValue("");
            this.refreshFilteredPatternEntries();
            return true;
        }

        var entry = getHoveredPatternEntry(xCoord, yCoord);
        if (entry != null) {
            var action = switch (btn) {
                case 1 -> hasShiftDown() ? InventoryAction.PICKUP_SINGLE : InventoryAction.SPLIT_OR_PLACE_SINGLE;
                case 2 -> this.minecraft != null && this.minecraft.player != null
                        && this.minecraft.player.getAbilities().instabuild
                                ? InventoryAction.CREATIVE_DUPLICATE
                                : InventoryAction.PICKUP_OR_SET_DOWN;
                default -> hasShiftDown() ? InventoryAction.SHIFT_CLICK : InventoryAction.PICKUP_OR_SET_DOWN;
            };
            PacketDistributor.sendToServer(new InventoryActionPacket(action, entry.slot(), entry.coreId()));
            return true;
        }

        return super.mouseClicked(xCoord, yCoord, btn);
    }

    @Override
    protected void updateBeforeRender() {
        super.updateBeforeRender();
        this.cancelJobsButton.setVisibility(true);
        this.patternAccessButton.setVisibility(true);
        this.patternAccessButton.setState(this.menu.patternAccessVisible ? 1 : 0);
        if (this.lastRunningThreads != this.menu.runningThreads) {
            this.lastRunningThreads = this.menu.runningThreads;
            this.runningThreadsText = Component.translatable(
                    "gui.extendedmolecularassembler.matrix.activeJobs", this.menu.runningThreads);
        }

        updatePatternScrollbar();
    }

    @Override
    public void drawFG(GuiGraphics guiGraphics, int offsetX, int offsetY, int mouseX, int mouseY) {
        var color = style.getColor(PaletteColor.DEFAULT_TEXT_COLOR).toARGB();
        var jobsWidth = this.font.width(this.runningThreadsText);
        guiGraphics.drawString(this.font, this.runningThreadsText, 187 - jobsWidth, 20, color, false);

        drawPatternEntries(guiGraphics, mouseX, mouseY);

        var hovered = getHoveredPatternEntry(mouseX, mouseY);
        if (hovered != null && !hovered.stack().isEmpty()) {
            guiGraphics.renderTooltip(this.font, displayStack(hovered.stack()), mouseX - this.leftPos, mouseY - this.topPos);
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double deltaX, double deltaY) {
        if (isMouseOverPatternGrid(mouseX, mouseY) && this.patternScrollbar.isVisible()) {
            return this.patternScrollbar.onMouseWheel(
                    new Point((int) mouseX - this.leftPos, (int) mouseY - this.topPos), deltaY);
        }
        return super.mouseScrolled(mouseX, mouseY, deltaX, deltaY);
    }

    private void drawPatternEntries(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        var firstVisibleSlot = this.patternScrollbar.getCurrentScroll() * PATTERN_COLS;
        var filterActive = !this.searchField.getValue().isBlank();
        var hoveredVisibleIndex = getHoveredVisiblePatternIndex(mouseX, mouseY);
        for (int visibleIndex = 0; visibleIndex < VISIBLE_PATTERN_SLOTS; visibleIndex++) {
            var x = PATTERN_LEFT + visibleIndex % PATTERN_COLS * SLOT_SIZE;
            var y = PATTERN_TOP + visibleIndex / PATTERN_COLS * SLOT_SIZE;

            Icon.SLOT_BACKGROUND.getBlitter()
                    .dest(x - 1, y - 1)
                    .blit(guiGraphics);

            var entryIndex = firstVisibleSlot + visibleIndex;
            if (entryIndex >= 0 && entryIndex < this.filteredPatternEntries.size()) {
                var entry = this.filteredPatternEntries.get(entryIndex);
                if (filterActive) {
                    fillRect(guiGraphics, new Rect2i(x, y, 16, 16), 0x8A00FF00);
                }
                var display = displayStack(entry.stack());
                if (!display.isEmpty()) {
                    guiGraphics.renderItem(display, x, y);
                    guiGraphics.renderItemDecorations(this.font, display, x, y);
                }
            }

            if (visibleIndex == hoveredVisibleIndex) {
                renderVirtualSlotHighlight(guiGraphics, x, y);
            }
        }
    }

    private static void renderVirtualSlotHighlight(GuiGraphics guiGraphics, int x, int y) {
        var w = 16;
        var h = 16;
        guiGraphics.hLine(x, x + w, y - 1, 0xFFdaffff);
        guiGraphics.hLine(x - 1, x + w, y + h, 0xFFdaffff);
        guiGraphics.vLine(x - 1, y - 2, y + h, 0xFFdaffff);
        guiGraphics.vLine(x + w, y - 2, y + h, 0xFFdaffff);
        guiGraphics.fillGradient(RenderType.guiOverlay(), x, y, x + w, y + h, 0x669cd3ff, 0x669cd3ff, 0);
    }

    private void updatePatternScrollbar() {
        this.refreshFilteredPatternEntries();
        var totalSlots = this.filteredPatternEntries.size();
        var totalRows = Math.max(1, (totalSlots + PATTERN_COLS - 1) / PATTERN_COLS);
        var maxScroll = Math.max(0, totalRows - VISIBLE_PATTERN_ROWS);
        this.patternScrollbar.setRange(0, maxScroll, 1);
        this.patternScrollbar.setVisible(maxScroll > 0);
    }

    private void refreshFilteredPatternEntries() {
        this.filteredPatternEntries.clear();
        var filter = this.searchField == null ? "" : this.searchField.getValue();
        if (filter == null || filter.isBlank()) {
            this.filteredPatternEntries.addAll(this.menu.getPatternEntries());
            return;
        }

        var tokens = FCUtil.tokenize(filter);
        for (var entry : this.menu.getPatternEntries()) {
            if (itemStackMatchesSearchTerm(entry.stack(), tokens)) {
                this.filteredPatternEntries.add(entry);
            }
        }
    }

    private boolean itemStackMatchesSearchTerm(ItemStack stack, List<String> searchTokens) {
        if (!(stack.getItem() instanceof EncodedPatternItem<?>)) {
            return false;
        }

        try {
            var pattern = PatternDetailsHelper.decodePattern(stack, this.menu.getPlayer().level());
            if (pattern == null) {
                return false;
            }

            for (var output : pattern.getOutputs()) {
                if (output != null && FCUtil.compareTokens(searchTokens,
                        FCUtil.tokenize(output.what().getDisplayName().getString()))) {
                    return true;
                }
            }

            for (var input : pattern.getInputs()) {
                if (input != null && input.getPossibleInputs().length > 0 && FCUtil.compareTokens(searchTokens,
                        FCUtil.tokenize(input.getPossibleInputs()[0].what().getDisplayName().getString()))) {
                    return true;
                }
            }
        } catch (RuntimeException ignored) {
            return false;
        }

        return false;
    }

    private ItemStack displayStack(ItemStack encodedPattern) {
        if (encodedPattern.isEmpty()) {
            return ItemStack.EMPTY;
        }

        try {
            if (PatternDetailsHelper.decodePattern(encodedPattern, this.menu.getPlayer().level())
                    instanceof ExtendedTableCraftingPattern pattern) {
                var output = pattern.getPrimaryOutput();
                if (output != null) {
                    return displayWithAmount(GenericStack.wrapInItemStack(output));
                }
            }
        } catch (RuntimeException ignored) {
            // Broken or foreign pattern data falls back to the encoded pattern stack.
        }

        return displayWithAmount(encodedPattern);
    }

    private static ItemStack displayWithAmount(ItemStack stack) {
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        }
        return stack.copy();
    }

    private int getHoveredVisiblePatternIndex(double mouseX, double mouseY) {
        if (!isMouseOverPatternGrid(mouseX, mouseY)) {
            return -1;
        }
        var relativeX = (int) (mouseX - this.leftPos) - PATTERN_LEFT;
        var relativeY = (int) (mouseY - this.topPos) - PATTERN_TOP;
        var col = relativeX / SLOT_SIZE;
        var row = relativeY / SLOT_SIZE;
        if (col < 0 || col >= PATTERN_COLS || row < 0 || row >= VISIBLE_PATTERN_ROWS) {
            return -1;
        }
        return row * PATTERN_COLS + col;
    }

    private PatternEntry getHoveredPatternEntry(double mouseX, double mouseY) {
        var visibleIndex = getHoveredVisiblePatternIndex(mouseX, mouseY);
        if (visibleIndex < 0) {
            return null;
        }
        var index = this.patternScrollbar.getCurrentScroll() * PATTERN_COLS + visibleIndex;
        if (index < 0 || index >= this.filteredPatternEntries.size()) {
            return null;
        }
        return this.filteredPatternEntries.get(index);
    }

    private boolean isMouseOverPatternGrid(double mouseX, double mouseY) {
        var relativeX = mouseX - this.leftPos;
        var relativeY = mouseY - this.topPos;
        return relativeX >= PATTERN_LEFT
                && relativeX < PATTERN_LEFT + PATTERN_COLS * SLOT_SIZE
                && relativeY >= PATTERN_TOP
                && relativeY < PATTERN_TOP + VISIBLE_PATTERN_ROWS * SLOT_SIZE;
    }
}
