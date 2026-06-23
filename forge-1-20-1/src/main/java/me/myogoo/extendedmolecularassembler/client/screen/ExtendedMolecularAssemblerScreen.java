package me.myogoo.extendedmolecularassembler.client.screen;

import appeng.client.gui.Icon;
import appeng.client.gui.implementations.UpgradeableScreen;
import appeng.client.gui.style.PaletteColor;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.ProgressBar;
import me.myogoo.extendedmolecularassembler.client.widget.EMAIconButton;
import me.myogoo.extendedmolecularassembler.menu.ExtendedMolecularAssemblerMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class ExtendedMolecularAssemblerScreen extends UpgradeableScreen<ExtendedMolecularAssemblerMenu> {
    private final ProgressBar progressBar;
    private final EMAIconButton nextJobButton;
    private final EMAIconButton previousJobButton;

    public ExtendedMolecularAssemblerScreen(ExtendedMolecularAssemblerMenu menu, Inventory playerInventory,
            Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);

        this.progressBar = new ProgressBar(this.menu, style.getImage("progressBar"), ProgressBar.Direction.VERTICAL);
        widgets.add("progressBar", this.progressBar);

        this.nextJobButton = new EMAIconButton(Icon.ARROW_RIGHT,
                Component.translatable("gui.extendedmolecularassembler.extendedMolecularAssembler.nextJob"),
                btn -> this.menu.selectPage(this.menu.getPage() + 1));
        this.previousJobButton = new EMAIconButton(Icon.ARROW_LEFT,
                Component.translatable("gui.extendedmolecularassembler.extendedMolecularAssembler.previousJob"),
                btn -> this.menu.selectPage(this.menu.getPage() - 1));
        addToLeftToolbar(this.nextJobButton);
        addToLeftToolbar(this.previousJobButton);
    }

    @Override
    protected void updateBeforeRender() {
        super.updateBeforeRender();
        this.menu.showPage();
        var hasPages = this.menu.getPageCount() > 1;
        this.previousJobButton.setVisibility(hasPages && this.menu.getPage() > 0);
        this.nextJobButton.setVisibility(hasPages && this.menu.getPage() < this.menu.getPageCount() - 1);
        this.progressBar.setFullMsg(Component.literal(this.menu.getCurrentProgress() + "%"));
    }

    @Override
    public void drawFG(GuiGraphics guiGraphics, int offsetX, int offsetY, int mouseX, int mouseY) {
        if (this.menu.getPageCount() > 1) {
            guiGraphics.drawString(
                    this.font,
                    Component.translatable("gui.extendedmolecularassembler.extendedMolecularAssembler.job",
                            this.menu.getPage() + 1, this.menu.getPageCount()),
                    8,
                    18,
                    style.getColor(PaletteColor.DEFAULT_TEXT_COLOR).toARGB(),
                    false);
        }
    }
}
