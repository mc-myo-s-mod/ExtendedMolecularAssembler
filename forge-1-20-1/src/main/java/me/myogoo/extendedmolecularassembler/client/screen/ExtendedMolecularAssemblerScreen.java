package me.myogoo.extendedmolecularassembler.client.screen;

import appeng.client.gui.Icon;
import appeng.client.gui.implementations.UpgradeableScreen;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.style.StyleManager;
import appeng.client.gui.widgets.ProgressBar;
import appeng.client.guidebook.PageAnchor;
import me.myogoo.extendedmolecularassembler.ExtendedMolecularAssembler;
import me.myogoo.extendedmolecularassembler.client.widget.EMAIconButton;
import me.myogoo.extendedmolecularassembler.menu.slot.ExtendedMolecularAssemblerPatternSlot;
import me.myogoo.extendedmolecularassembler.menu.ExtendedMolecularAssemblerMenu;
import me.myogoo.extendedmolecularassembler.lang.EMATranslationKey;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class ExtendedMolecularAssemblerScreen extends UpgradeableScreen<ExtendedMolecularAssemblerMenu> {
    private final ProgressBar progressBar;
    private final EMAIconButton nextJobButton;
    private final EMAIconButton previousJobButton;

    public ExtendedMolecularAssemblerScreen(ExtendedMolecularAssemblerMenu menu, Inventory playerInventory,
            Component title, ScreenStyle style) {
        super(menu, playerInventory, title, menu.getGridSide() > 9 || menu.getPageCount() > 1
                ? StyleManager.loadStyleDoc("/screens/extended_molecular_assembler/"
                        + (menu.getPageCount() > 1 ? "ex_" : "")
                        + (menu.getGridSide() == 11 ? "epic" : menu.getGridSide() == 13 ? "legendary" : "extended")
                        + "_molecular_assembler.json")
                : style);
        setTextContent(TEXT_ID_DIALOG_TITLE, getGuiDisplayName(menu.getHost().getName()));

        this.progressBar = new ProgressBar(this.menu, this.style.getImage("progressBar"), ProgressBar.Direction.VERTICAL);
        widgets.add("progressBar", this.progressBar);

        this.nextJobButton = new EMAIconButton(Icon.ARROW_RIGHT,
                Component.translatable(EMATranslationKey.GUI.EXTENDED_MOLECULAR_ASSEMBLER_NEXT_JOB.key()),
                btn -> this.menu.selectPage(this.menu.getPage() + 1));
        this.previousJobButton = new EMAIconButton(Icon.ARROW_LEFT,
                Component.translatable(EMATranslationKey.GUI.EXTENDED_MOLECULAR_ASSEMBLER_PREVIOUS_JOB.key()),
                btn -> this.menu.selectPage(this.menu.getPage() - 1));
        addToLeftToolbar(this.nextJobButton);
        addToLeftToolbar(this.previousJobButton);
    }

    @Override
    protected PageAnchor getHelpTopic() {
        return new PageAnchor(ExtendedMolecularAssembler.makeId("extended-molecular-assemblers.md"), null);
    }

    @Override
    protected void init() {
        super.init();
        if (this.menu.getGridSide() <= 9) {
            return;
        }

        var gridSlots = this.menu.slots.stream()
                .filter(ExtendedMolecularAssemblerPatternSlot.class::isInstance)
                .map(ExtendedMolecularAssemblerPatternSlot.class::cast)
                .toList();
        if (gridSlots.isEmpty()) {
            return;
        }

        var anchorX = gridSlots.get(0).x;
        var anchorY = gridSlots.get(0).y;
        var side = this.menu.getGridSide();
        for (var slot : gridSlots) {
            var index = slot.getSlotIndex();
            slot.x = anchorX + index % side * 18;
            slot.y = anchorY + index / side * 18;
        }
    }

    @Override
    protected void updateBeforeRender() {
        super.updateBeforeRender();
        var hasPages = this.menu.getPageCount() > 1;
        this.previousJobButton.setVisibility(hasPages && this.menu.getPage() > 0);
        this.nextJobButton.setVisibility(hasPages && this.menu.getPage() < this.menu.getPageCount() - 1);
        this.progressBar.setFullMsg(Component.literal(this.menu.getCurrentProgress() + "%"));
        setTextHidden("job", !hasPages);
        if (hasPages) {
            setTextContent("job", Component.translatable(EMATranslationKey.GUI.EXTENDED_MOLECULAR_ASSEMBLER_JOB.key(),
                    this.menu.getPage() + 1, this.menu.getPageCount()));
        }
    }
}
