package me.myogoo.extendedmolecularassembler.client.widget;

import appeng.client.gui.Icon;
import appeng.client.gui.widgets.IconButton;
import net.minecraft.network.chat.Component;

import java.util.List;

public class EMAIconButton extends IconButton {
    private final Icon icon;
    private List<Component> tooltip;

    public EMAIconButton(Icon icon, Component tooltip, OnPress onPress) {
        super(onPress);
        this.icon = icon;
        setTooltipMessage(List.of(tooltip));
    }

    public void setTooltipMessage(List<Component> tooltip) {
        this.tooltip = List.copyOf(tooltip);
        if (!this.tooltip.isEmpty()) {
            setMessage(this.tooltip.get(0));
        }
    }

    @Override
    public List<Component> getTooltipMessage() {
        return this.tooltip;
    }

    @Override
    protected Icon getIcon() {
        return this.icon;
    }
}
