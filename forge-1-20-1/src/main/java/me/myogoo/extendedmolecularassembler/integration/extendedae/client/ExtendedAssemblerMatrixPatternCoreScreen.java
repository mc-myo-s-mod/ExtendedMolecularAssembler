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
import appeng.client.guidebook.PageAnchor;
import me.myogoo.extendedmolecularassembler.ExtendedMolecularAssembler;
import appeng.core.localization.GuiText;
import appeng.core.sync.packets.InventoryActionPacket;
import appeng.core.sync.network.NetworkHandler;
import appeng.crafting.pattern.EncodedPatternItem;
import appeng.helpers.InventoryAction;
import com.glodblock.github.extendedae.client.button.ActionEPPButton;
import com.glodblock.github.extendedae.client.button.CycleEPPButton;
import com.glodblock.github.extendedae.util.FCUtil;
import me.myogoo.extendedmolecularassembler.integration.extendedae.menu.ExtendedAssemblerMatrixPatternCoreMenu;
import me.myogoo.extendedmolecularassembler.integration.extendedae.menu.ExtendedAssemblerMatrixPatternCoreMenu.PatternEntry;
import me.myogoo.extendedmolecularassembler.integration.extendedae.network.EMAOpenExtendedAEAssemblerMatrixScreenPacket;
import me.myogoo.extendedmolecularassembler.pattern.ExtendedTableCraftingPattern;
import me.myogoo.extendedmolecularassembler.lang.EMATranslationKey;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import me.myogoo.extendedmolecularassembler.init.EMANetwork;

import java.util.ArrayList;
import java.util.List;

public class ExtendedAssemblerMatrixPatternCoreScreen
        extends AEBaseScreen<ExtendedAssemblerMatrixPatternCoreMenu> {
    private static final int PATTERN_COLS = 9;

    @Override
    protected PageAnchor getHelpTopic() {
        return new PageAnchor(ExtendedMolecularAssembler.makeId("matrix-cores.md"), "pattern-core");
    }
    private static final int VISIBLE_PATTERN_ROWS = 4;
    private static final int VISIBLE_PATTERN_SLOTS = PATTERN_COLS * VISIBLE_PATTERN_ROWS;
    private static final int SLOT_SIZE = 18;
    private static final int PATTERN_LEFT = 8;
    private static final int PATTERN_TOP = 31;

    private final ActionEPPButton cancelJobsButton;
    private final ActionEPPButton backToMatrixButton;
    private final ActionEPPButton matrix9Button;
    private final ActionEPPButton matrix11Button;
    private final ActionEPPButton matrix13Button;
    private final CycleEPPButton patternAccessButton;
    private final Scrollbar patternScrollbar;
    private final AETextField searchField;
    private final List<CachedPatternEntry> cachedPatternEntries = new ArrayList<>();
    private final List<CachedPatternEntry> filteredPatternEntries = new ArrayList<>();
    private long cachedPatternRevision = Long.MIN_VALUE;
    private String cachedSearchText;
    private int lastRunningThreads = Integer.MIN_VALUE;
    private Component runningThreadsText = Component.empty();

    public ExtendedAssemblerMatrixPatternCoreScreen(ExtendedAssemblerMatrixPatternCoreMenu menu,
            Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);

        this.patternScrollbar = this.widgets.addScrollBar("patternScrollbar", Scrollbar.DEFAULT);
        this.patternScrollbar.setHeight(VISIBLE_PATTERN_ROWS * SLOT_SIZE - 2);
        this.patternScrollbar.setCaptureMouseWheel(true);

        this.searchField = this.widgets.addTextField("search");
        this.searchField.setResponder(ignored -> this.refreshPatternCaches());
        this.searchField.setPlaceholder(GuiText.SearchPlaceholder.text());
        this.searchField.setTooltipMessage(List.of(
                Component.translatable(EMATranslationKey.GUI.MATRIX_SEARCH_PATTERNS.key())));

        this.backToMatrixButton = new ActionEPPButton(
                btn -> {
                    var matrixPos = AssemblerMatrixNavigationContext.matrixPosOr(this.menu.getHost().getBlockPos());
                    AssemblerMatrixNavigationContext.clear();
                    EMANetwork.sendToServer(new EMAOpenExtendedAEAssemblerMatrixScreenPacket(
                            matrixPos,
                            EMAOpenExtendedAEAssemblerMatrixScreenPacket.Target.MATRIX));
                },
                Icon.ARROW_LEFT);
        this.backToMatrixButton.setMessage(Component.translatable(EMATranslationKey.GUI.MATRIX_BACK_TO_MATRIX.key()));

        this.matrix9Button = createPatternViewButton(9);
        this.matrix11Button = createPatternViewButton(11);
        this.matrix13Button = createPatternViewButton(13);
        this.widgets.add("matrix9", this.matrix9Button);
        this.widgets.add("matrix11", this.matrix11Button);
        this.widgets.add("matrix13", this.matrix13Button);

        this.patternAccessButton = new CycleEPPButton();
        this.patternAccessButton.addActionPair(Icon.PATTERN_ACCESS_SHOW,
                Component.translatable(EMATranslationKey.GUI.MATRIX_SHOW_IN_PATTERN_ACCESS.key()),
                btn -> this.menu.setPatternAccessVisibleFromClient(true));
        this.patternAccessButton.addActionPair(Icon.PATTERN_ACCESS_HIDE,
                Component.translatable(EMATranslationKey.GUI.MATRIX_HIDE_FROM_PATTERN_ACCESS.key()),
                btn -> this.menu.setPatternAccessVisibleFromClient(false));

        this.cancelJobsButton = new ActionEPPButton(btn -> this.menu.cancelJobsFromClient(), Icon.CLEAR);
        this.cancelJobsButton.setMessage(Component.translatable(EMATranslationKey.GUI.MATRIX_CANCEL_JOBS.key()));

        addToLeftToolbar(this.cancelJobsButton);
        addToLeftToolbar(this.patternAccessButton);
        addToLeftToolbar(this.backToMatrixButton);

        this.refreshPatternCaches();
    }

    @Override
    public void init() {
        super.init();
        this.setInitialFocus(this.searchField);
        this.refreshPatternCaches();
        this.updatePatternScrollbar();
    }

    @Override
    public void removed() {
        var switchingPatternCore = this.minecraft != null
                && this.minecraft.player != null
                && this.minecraft.player.containerMenu instanceof ExtendedAssemblerMatrixPatternCoreMenu currentMenu
                && currentMenu != this.menu;
        if (!switchingPatternCore) {
            AssemblerMatrixNavigationContext.clear();
        }
        super.removed();
    }

    private ActionEPPButton createPatternViewButton(int sideLength) {
        var button = new ActionEPPButton(btn -> {
            EMANetwork.sendToServer(new EMAOpenExtendedAEAssemblerMatrixScreenPacket(
                    this.menu.getHost().getBlockPos(),
                    EMAOpenExtendedAEAssemblerMatrixScreenPacket.Target.forPatternSide(sideLength)));
        }, Icon.ARROW_RIGHT);
        button.setMessage(Component.translatable(EMATranslationKey.GUI.MATRIX_PATTERN_CORE_SHORT.key())
                .append(Component.literal(" (" + sideLength + "x" + sideLength + ")")));
        button.active = this.menu.getHost().getPatternSideLength() != sideLength;
        return button;
    }

    @Override
    public boolean mouseClicked(double xCoord, double yCoord, int btn) {
        if (btn == 1 && this.searchField.isMouseOver(xCoord, yCoord)) {
            this.searchField.setValue("");
            this.refreshPatternCaches();
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
            NetworkHandler.instance().sendToServer(
                    new InventoryActionPacket(action, entry.entry().slot(), entry.entry().coreId()));
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
                    EMATranslationKey.GUI.MATRIX_THREADS.key(), this.menu.runningThreads);
        }

        this.refreshPatternCaches();
    }

    @Override
    public void drawFG(GuiGraphics guiGraphics, int offsetX, int offsetY, int mouseX, int mouseY) {
        var color = style.getColor(PaletteColor.DEFAULT_TEXT_COLOR).toARGB();
        guiGraphics.drawString(this.font, this.runningThreadsText, 80, 19, color, false);

        drawPatternEntries(guiGraphics, mouseX, mouseY);

        var hovered = getHoveredPatternEntry(mouseX, mouseY);
        if (hovered != null && !hovered.displayStack().isEmpty()) {
            guiGraphics.renderTooltip(this.font, hovered.displayStack(), mouseX - this.leftPos,
                    mouseY - this.topPos);
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double deltaY) {
        if (isMouseOverPatternGrid(mouseX, mouseY) && this.patternScrollbar.isVisible()) {
            return this.patternScrollbar.onMouseWheel(
                    new Point((int) mouseX - this.leftPos, (int) mouseY - this.topPos), deltaY);
        }
        return super.mouseScrolled(mouseX, mouseY, deltaY);
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
                var cachedEntry = this.filteredPatternEntries.get(entryIndex);
                if (filterActive) {
                    fillRect(guiGraphics, new Rect2i(x, y, 16, 16), 0x8A00FF00);
                }
                var display = cachedEntry.displayStack();
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
        guiGraphics.fillGradient(x, y, x + w, y + h, 0x669cd3ff, 0x669cd3ff);
    }

    private void updatePatternScrollbar() {
        var totalSlots = this.filteredPatternEntries.size();
        var totalRows = Math.max(1, (totalSlots + PATTERN_COLS - 1) / PATTERN_COLS);
        var maxScroll = Math.max(0, totalRows - VISIBLE_PATTERN_ROWS);
        this.patternScrollbar.setRange(0, maxScroll, 1);
        this.patternScrollbar.setVisible(maxScroll > 0);
    }

    private void refreshPatternCaches() {
        var revision = this.menu.getPatternRevision();
        if (this.cachedPatternRevision != revision) {
            this.cachedPatternEntries.clear();
            for (var entry : this.menu.getPatternEntries()) {
                this.cachedPatternEntries.add(cachePatternEntry(entry));
            }
            this.cachedPatternRevision = revision;
            this.cachedSearchText = null;
        }

        this.refreshFilteredPatternEntries();
    }

    private void refreshFilteredPatternEntries() {
        var filter = this.searchField == null ? "" : this.searchField.getValue();
        if (filter == null) {
            filter = "";
        }
        if (filter.equals(this.cachedSearchText)) {
            return;
        }

        this.cachedSearchText = filter;
        this.filteredPatternEntries.clear();
        if (filter.isBlank()) {
            this.filteredPatternEntries.addAll(this.cachedPatternEntries);
        } else {
            var tokens = FCUtil.tokenize(filter);
            for (var entry : this.cachedPatternEntries) {
                if (matchesSearchTerm(entry, tokens)) {
                    this.filteredPatternEntries.add(entry);
                }
            }
        }
        this.updatePatternScrollbar();
    }

    private static boolean matchesSearchTerm(CachedPatternEntry entry, List<String> searchTokens) {
        for (var displayTokens : entry.searchTokens()) {
            if (FCUtil.compareTokens(searchTokens, displayTokens)) {
                return true;
            }
        }
        return false;
    }

    private CachedPatternEntry cachePatternEntry(PatternEntry entry) {
        var encodedPattern = entry.stack();
        var display = displayWithAmount(encodedPattern);
        var searchTokens = new ArrayList<List<String>>();

        if (encodedPattern.getItem() instanceof EncodedPatternItem) {
            try {
                var pattern = PatternDetailsHelper.decodePattern(encodedPattern, this.menu.getPlayer().level());
                if (pattern != null) {
                    for (var output : pattern.getOutputs()) {
                        if (output != null) {
                            searchTokens.add(FCUtil.tokenize(output.what().getDisplayName().getString()));
                        }
                    }

                    for (var input : pattern.getInputs()) {
                        if (input != null && input.getPossibleInputs().length > 0
                                && input.getPossibleInputs()[0] != null) {
                            searchTokens.add(FCUtil.tokenize(
                                    input.getPossibleInputs()[0].what().getDisplayName().getString()));
                        }
                    }

                    if (pattern instanceof ExtendedTableCraftingPattern extendedPattern) {
                        var output = extendedPattern.getPrimaryOutput();
                        if (output != null) {
                            display = displayWithAmount(GenericStack.wrapInItemStack(output));
                        }
                    }
                }
            } catch (RuntimeException ignored) {
                // Broken or foreign pattern data falls back to the encoded pattern stack and is not searchable.
                display = displayWithAmount(encodedPattern);
                searchTokens.clear();
            }
        }

        return new CachedPatternEntry(entry, display, List.copyOf(searchTokens));
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

    private CachedPatternEntry getHoveredPatternEntry(double mouseX, double mouseY) {
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

    private record CachedPatternEntry(PatternEntry entry, ItemStack displayStack,
            List<List<String>> searchTokens) {
    }
}
