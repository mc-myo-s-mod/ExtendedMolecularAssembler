package me.myogoo.extendedmolecularassembler.client.widget;

import appeng.client.gui.Icon;
import appeng.client.gui.widgets.IconButton;
import net.minecraft.network.chat.Component;

import java.util.List;

public class EMAIconButton extends IconButton {
    private final Icon icon;
    private final List<Component> tooltip;

    public EMAIconButton(Icon icon, Component tooltip, OnPress onPress) {
        super(onPress);
        this.icon = icon;
        this.tooltip = List.of(tooltip);
        setMessage(tooltip);
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
