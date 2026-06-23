package me.myogoo.extendedmolecularassembler.part;

import appeng.api.parts.IPartItem;
import appeng.api.parts.IPartModel;
import appeng.items.parts.PartModels;
import appeng.parts.PartModel;
import appeng.parts.reporting.AbstractTerminalPart;
import me.myogoo.extendedmolecularassembler.ExtendedMolecularAssembler;
import me.myogoo.extendedmolecularassembler.menu.pattern.ExtendedPatternEncodingLogic;
import me.myogoo.extendedmolecularassembler.menu.pattern.ExtendedPatternEncodingTermMenu;
import me.myogoo.extendedmolecularassembler.menu.pattern.IExtendedPatternEncodingTerminalHost;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class ExtendedPatternEncodingTerminalPart extends AbstractTerminalPart
        implements IExtendedPatternEncodingTerminalHost {
    @PartModels
    public static final ResourceLocation MODEL_OFF =
            ExtendedMolecularAssembler.makeId("part/extended_pattern_encoding_terminal_off");
    @PartModels
    public static final ResourceLocation MODEL_ON =
            ExtendedMolecularAssembler.makeId("part/extended_pattern_encoding_terminal_on");

    public static final IPartModel MODELS_OFF = new PartModel(MODEL_BASE, MODEL_OFF, MODEL_STATUS_OFF);
    public static final IPartModel MODELS_ON = new PartModel(MODEL_BASE, MODEL_ON, MODEL_STATUS_ON);
    public static final IPartModel MODELS_HAS_CHANNEL = new PartModel(MODEL_BASE, MODEL_ON, MODEL_STATUS_HAS_CHANNEL);

    private final ExtendedPatternEncodingLogic logic = new ExtendedPatternEncodingLogic(this);

    public ExtendedPatternEncodingTerminalPart(IPartItem<?> partItem) {
        super(partItem);
    }

    @Override
    public void addAdditionalDrops(List<ItemStack> drops, boolean wrenched) {
        super.addAdditionalDrops(drops, wrenched);
        for (var stack : logic.getBlankPatternInv()) {
            if (!stack.isEmpty()) {
                drops.add(stack);
            }
        }
        for (var stack : logic.getEncodedPatternInv()) {
            if (!stack.isEmpty()) {
                drops.add(stack);
            }
        }
    }

    @Override
    public void clearContent() {
        super.clearContent();
        logic.clearAll();
    }

    @Override
    public void readFromNBT(CompoundTag data, HolderLookup.Provider registries) {
        super.readFromNBT(data, registries);
        logic.readFromNBT(data, registries);
    }

    @Override
    public void writeToNBT(CompoundTag data, HolderLookup.Provider registries) {
        super.writeToNBT(data, registries);
        logic.writeToNBT(data, registries);
    }

    @Override
    public MenuType<?> getMenuType(Player player) {
        return ExtendedPatternEncodingTermMenu.TYPE;
    }

    @Override
    public IPartModel getStaticModels() {
        return selectModel(MODELS_OFF, MODELS_ON, MODELS_HAS_CHANNEL);
    }

    @Override
    public ExtendedPatternEncodingLogic getExtendedPatternEncodingLogic() {
        return logic;
    }

    @Override
    public void markForSave() {
        getHost().markForSave();
    }
}
