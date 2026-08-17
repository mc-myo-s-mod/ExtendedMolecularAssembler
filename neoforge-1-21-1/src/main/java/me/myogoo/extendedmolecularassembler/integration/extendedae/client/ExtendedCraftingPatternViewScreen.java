package me.myogoo.extendedmolecularassembler.integration.extendedae.client;

import appeng.client.Point;
import appeng.client.gui.Icon;
import appeng.client.gui.style.Blitter;
import appeng.client.gui.style.PaletteColor;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.style.WidgetStyle;
import appeng.core.localization.ButtonToolTips;
import appeng.menu.SlotSemantics;
import appeng.menu.slot.IOptionalSlot;
import com.glodblock.github.extendedae.client.gui.pattern.GuiPattern;
import me.myogoo.extendedmolecularassembler.integration.extendedae.menu.ExtendedCraftingPatternViewMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ExtendedCraftingPatternViewScreen extends GuiPattern<ExtendedCraftingPatternViewMenu> {
    private static final String TABLE_PREVIEW_WIDGET = "craftingTablePreview";
    private static final String SUBSTITUTE_TEXT_WIDGET = "substituteText";
    private static final String FLUID_SUBSTITUTE_TEXT_WIDGET = "fluidSubstituteText";

    private final Blitter background;
    private final WidgetStyle tablePreviewStyle;
    private final WidgetStyle substituteTextStyle;
    private final WidgetStyle fluidSubstituteTextStyle;
    private final Point tablePosition;
    private final Point substitutePosition;
    private final Point fluidSubstitutePosition;
    private final int textColor;

    public ExtendedCraftingPatternViewScreen(
            ExtendedCraftingPatternViewMenu menu,
            Inventory inventory,
            Component title,
            ScreenStyle style) {
        super(menu, inventory, title);
        Objects.requireNonNull(style, "style");
        this.background = Objects.requireNonNull(
                style.getBackground(),
                "Pattern view style is missing a background");
        this.imageWidth = this.background.getSrcWidth();
        this.imageHeight = this.background.getSrcHeight();
        this.tablePreviewStyle = style.getWidget(TABLE_PREVIEW_WIDGET);
        this.substituteTextStyle = style.getWidget(SUBSTITUTE_TEXT_WIDGET);
        this.fluidSubstituteTextStyle = style.getWidget(FLUID_SUBSTITUTE_TEXT_WIDGET);
        var bounds = new Rect2i(0, 0, this.imageWidth, this.imageHeight);
        this.tablePosition = this.tablePreviewStyle.resolve(bounds);
        this.substitutePosition = this.substituteTextStyle.resolve(bounds);
        this.fluidSubstitutePosition = this.fluidSubstituteTextStyle.resolve(bounds);
        this.textColor = style.getColor(PaletteColor.DEFAULT_TEXT_COLOR).toARGB();
        if (this.tablePreviewStyle.getWidth() <= 0 || this.tablePreviewStyle.getHeight() <= 0) {
            throw new IllegalStateException("Pattern view table preview must have a positive size");
        }
    }

    public static ExtendedCraftingPatternViewMenu.PatternViewLayout createMenuLayout(ScreenStyle style) {
        var background = Objects.requireNonNull(
                style.getBackground(),
                "Pattern view style is missing a background");
        var bounds = new Rect2i(0, 0, background.getSrcWidth(), background.getSrcHeight());
        var craftingGrid = resolveSlotPositions(
                style,
                bounds,
                SlotSemantics.MACHINE_CRAFTING_GRID.id(),
                ExtendedCraftingPatternViewMenu.GRID_SLOT_COUNT);
        var output = resolveSlotPositions(
                style,
                bounds,
                SlotSemantics.MACHINE_OUTPUT.id(),
                1).getFirst();
        return new ExtendedCraftingPatternViewMenu.PatternViewLayout(craftingGrid, output);
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        this.background.dest(this.leftPos, this.topPos).blit(guiGraphics);
        drawSlotBackgrounds(guiGraphics, this.leftPos, this.topPos);

        drawTablePreview(
                guiGraphics,
                this.leftPos + this.tablePosition.getX(),
                this.topPos + this.tablePosition.getY(),
                this.tablePreviewStyle.getWidth(),
                this.tablePreviewStyle.getHeight());
    }

    @Override
    protected void renderLabels(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(
                this.font,
                Component.translatable(
                        "gui.pattern_view.craft.substitute",
                        this.menu.canSubstitute() ? ButtonToolTips.On.text() : ButtonToolTips.Off.text()),
                this.substitutePosition.getX(),
                this.substitutePosition.getY(),
                this.textColor,
                false);

        guiGraphics.drawString(
                this.font,
                Component.translatable(
                        "gui.pattern_view.craft.fluid_substitute",
                        this.menu.canSubstituteFluids() ? ButtonToolTips.On.text() : ButtonToolTips.Off.text()),
                this.fluidSubstitutePosition.getX(),
                this.fluidSubstitutePosition.getY(),
                this.textColor,
                false);
    }

    @Override
    protected void renderTooltip(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.renderTooltip(guiGraphics, mouseX, mouseY);
        if (this.menu.tableStack().isEmpty()) {
            return;
        }

        int localX = mouseX - this.leftPos;
        int localY = mouseY - this.topPos;
        if (localX >= this.tablePosition.getX()
                && localX < this.tablePosition.getX() + this.tablePreviewStyle.getWidth()
                && localY >= this.tablePosition.getY()
                && localY < this.tablePosition.getY() + this.tablePreviewStyle.getHeight()) {
            guiGraphics.renderTooltip(this.font, this.menu.tableStack(), mouseX, mouseY);
        }
    }

    private void drawTablePreview(GuiGraphics guiGraphics, int x, int y, int width, int height) {
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(x, y, 100.0F);
        guiGraphics.pose().scale(width / 16.0F, height / 16.0F, 1.0F);
        guiGraphics.renderFakeItem(this.menu.tableStack(), 0, 0);
        guiGraphics.pose().popPose();
    }

    private void drawSlotBackgrounds(GuiGraphics guiGraphics, int left, int top) {
        for (var slot : this.menu.slots) {
            if (!(slot instanceof IOptionalSlot optionalSlot) || !optionalSlot.isRenderDisabled()) {
                continue;
            }

            var position = optionalSlot.getBackgroundPos();
            float alpha = optionalSlot.isSlotEnabled() ? 1.0F : 0.2F;
            Icon.SLOT_BACKGROUND.getBlitter()
                    .dest(left + position.getX(), top + position.getY())
                    .color(1.0F, 1.0F, 1.0F, alpha)
                    .blit(guiGraphics);
        }
    }

    private static List<Point> resolveSlotPositions(
            ScreenStyle style,
            Rect2i bounds,
            String styleId,
            int slotCount) {
        var slotPosition = style.getSlots().get(styleId);
        if (slotPosition == null) {
            throw new IllegalStateException("Pattern view style is missing slot layout: " + styleId);
        }
        if (slotPosition.isHidden()) {
            throw new IllegalStateException("Pattern view slot layout cannot be hidden: " + styleId);
        }

        Point basePosition = slotPosition.resolve(bounds);
        var grid = slotPosition.getGrid();
        var positions = new ArrayList<Point>(slotCount);
        for (int index = 0; index < slotCount; index++) {
            var position = grid == null
                    ? basePosition
                    : grid.getPosition(basePosition.getX(), basePosition.getY(), index);
            positions.add(position);
        }
        return List.copyOf(positions);
    }

}
