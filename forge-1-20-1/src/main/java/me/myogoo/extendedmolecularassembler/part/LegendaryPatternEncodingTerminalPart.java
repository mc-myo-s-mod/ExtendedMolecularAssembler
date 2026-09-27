package me.myogoo.extendedmolecularassembler.part;

import appeng.api.parts.IPartItem;
import appeng.api.parts.IPartModel;
import appeng.items.parts.PartModels;
import appeng.parts.PartModel;
import me.myogoo.extendedmolecularassembler.ExtendedMolecularAssembler;
import net.minecraft.resources.ResourceLocation;

public final class LegendaryPatternEncodingTerminalPart extends ExtendedPatternEncodingTerminalPart {
    @PartModels
    public static final ResourceLocation MODEL_OFF =
            ExtendedMolecularAssembler.makeId("part/legendary_pattern_encoding_terminal_off");
    @PartModels
    public static final ResourceLocation MODEL_ON =
            ExtendedMolecularAssembler.makeId("part/legendary_pattern_encoding_terminal_on");

    private static final IPartModel OFF = new PartModel(MODEL_BASE, MODEL_OFF, MODEL_STATUS_OFF);
    private static final IPartModel ON = new PartModel(MODEL_BASE, MODEL_ON, MODEL_STATUS_ON);
    private static final IPartModel CONNECTED = new PartModel(MODEL_BASE, MODEL_ON, MODEL_STATUS_HAS_CHANNEL);

    public LegendaryPatternEncodingTerminalPart(IPartItem<?> partItem) {
        super(partItem, 13);
    }

    @Override
    public IPartModel getStaticModels() {
        return selectModel(OFF, ON, CONNECTED);
    }
}
