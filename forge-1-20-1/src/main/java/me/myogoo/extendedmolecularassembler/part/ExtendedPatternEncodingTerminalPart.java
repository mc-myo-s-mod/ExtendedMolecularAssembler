package me.myogoo.extendedmolecularassembler.part;

import appeng.api.parts.IPartItem;
import appeng.api.parts.IPartModel;
import appeng.parts.PartModel;
import appeng.parts.reporting.AbstractTerminalPart;
import me.myogoo.extendedmolecularassembler.ExtendedMolecularAssembler;
import me.myogoo.extendedmolecularassembler.menu.pattern.ExtendedPatternEncodingLogic;
import me.myogoo.extendedmolecularassembler.menu.pattern.ExtendedPatternEncodingTermMenu;
import me.myogoo.extendedmolecularassembler.menu.pattern.ExtendedPatternRecipeType;
import me.myogoo.extendedmolecularassembler.menu.pattern.IExtendedPatternEncodingTerminalHost;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class ExtendedPatternEncodingTerminalPart extends AbstractTerminalPart
        implements IExtendedPatternEncodingTerminalHost {
    private static final String REMEMBER_RECIPE_TYPE = "rememberExtendedPatternRecipeType";
    private static final String SELECTED_RECIPE_PROVIDER = "selectedExtendedPatternRecipeProvider";
    private static final String SELECTED_RECIPE_TABLE_TIER = "selectedExtendedPatternRecipeTableTier";
    private static final String SELECTED_RECIPE_TABLE_SIDE = "selectedExtendedPatternRecipeTableSide";

    public static final ResourceLocation MODEL_OFF =
            ExtendedMolecularAssembler.makeId("part/extended_pattern_encoding_terminal_off");
    public static final ResourceLocation MODEL_ON =
            ExtendedMolecularAssembler.makeId("part/extended_pattern_encoding_terminal_on");

    public static final IPartModel MODELS_OFF = new PartModel(MODEL_BASE, MODEL_OFF, MODEL_STATUS_OFF);
    public static final IPartModel MODELS_ON = new PartModel(MODEL_BASE, MODEL_ON, MODEL_STATUS_ON);
    public static final IPartModel MODELS_HAS_CHANNEL = new PartModel(MODEL_BASE, MODEL_ON, MODEL_STATUS_HAS_CHANNEL);

    private final ExtendedPatternEncodingLogic logic = new ExtendedPatternEncodingLogic(this);
    private boolean rememberRecipeType = true;
    private ExtendedPatternRecipeType rememberedRecipeType;

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
    public void readFromNBT(CompoundTag data) {
        super.readFromNBT(data);
        logic.readFromNBT(data);
        this.rememberRecipeType = !data.contains(REMEMBER_RECIPE_TYPE, Tag.TAG_BYTE)
                || data.getBoolean(REMEMBER_RECIPE_TYPE);
        this.rememberedRecipeType = ExtendedPatternRecipeType.readFromNBT(data,
                SELECTED_RECIPE_PROVIDER,
                SELECTED_RECIPE_TABLE_TIER,
                SELECTED_RECIPE_TABLE_SIDE);
    }

    @Override
    public void writeToNBT(CompoundTag data) {
        super.writeToNBT(data);
        logic.writeToNBT(data);
        data.putBoolean(REMEMBER_RECIPE_TYPE, rememberRecipeType);
        if (rememberedRecipeType != null) {
            rememberedRecipeType.writeToNBT(data,
                    SELECTED_RECIPE_PROVIDER,
                    SELECTED_RECIPE_TABLE_TIER,
                    SELECTED_RECIPE_TABLE_SIDE);
        } else {
            data.remove(SELECTED_RECIPE_PROVIDER);
            data.remove(SELECTED_RECIPE_TABLE_TIER);
            data.remove(SELECTED_RECIPE_TABLE_SIDE);
        }
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
    public boolean rememberRecipeType() {
        return rememberRecipeType;
    }

    @Override
    public void setRememberRecipeType(boolean remember) {
        this.rememberRecipeType = remember;
        if (!remember) {
            this.rememberedRecipeType = null;
        }
        markForSave();
    }

    @Override
    public ExtendedPatternRecipeType getRememberedRecipeType() {
        return rememberedRecipeType;
    }

    @Override
    public void setRememberedRecipeType(ExtendedPatternRecipeType recipeType) {
        this.rememberedRecipeType = recipeType;
        markForSave();
    }

    @Override
    public void markForSave() {
        getHost().markForSave();
    }
}
