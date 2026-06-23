package me.myogoo.extendedmolecularassembler.part;

import appeng.api.parts.IPartItem;
import appeng.api.parts.IPartModel;
import appeng.parts.PartModel;
import appeng.parts.encoding.PatternEncodingTerminalPart;
import me.myogoo.extendedmolecularassembler.ExtendedMolecularAssembler;
import net.minecraft.resources.ResourceLocation;

/**
 * Forge 1.20.1 / AE2 15 minimal port of the EMA pattern encoding terminal part.
 *
 * <p>AE2 15's pattern terminal menu, slot, and encoding APIs differ from the 1.21
 * implementation. This class intentionally reuses AE2's stock
 * {@link PatternEncodingTerminalPart} behavior while registering EMA's own item/model
 * so the part exists without breaking compilation. Full extended-table encoding needs a
 * separate AE2-15 menu rewrite.</p>
 */
public class ExtendedPatternEncodingTerminalPart extends PatternEncodingTerminalPart {
    public static final ResourceLocation MODEL_OFF =
            ExtendedMolecularAssembler.makeId("part/extended_pattern_encoding_terminal_off");
    public static final ResourceLocation MODEL_ON =
            ExtendedMolecularAssembler.makeId("part/extended_pattern_encoding_terminal_on");

    public static final IPartModel MODELS_OFF = new PartModel(MODEL_BASE, MODEL_OFF, MODEL_STATUS_OFF);
    public static final IPartModel MODELS_ON = new PartModel(MODEL_BASE, MODEL_ON, MODEL_STATUS_ON);
    public static final IPartModel MODELS_HAS_CHANNEL = new PartModel(MODEL_BASE, MODEL_ON, MODEL_STATUS_HAS_CHANNEL);

    public ExtendedPatternEncodingTerminalPart(IPartItem<?> partItem) {
        super(partItem);
    }

    @Override
    public IPartModel getStaticModels() {
        return selectModel(MODELS_OFF, MODELS_ON, MODELS_HAS_CHANNEL);
    }
}
