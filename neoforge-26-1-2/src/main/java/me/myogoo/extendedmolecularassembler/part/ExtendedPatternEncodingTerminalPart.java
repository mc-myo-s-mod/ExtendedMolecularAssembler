package me.myogoo.extendedmolecularassembler.part;

import appeng.api.parts.IPartItem;
import appeng.parts.reporting.AbstractTerminalPart;
import me.myogoo.extendedmolecularassembler.menu.pattern.ExtendedPatternEncodingLogic;
import me.myogoo.extendedmolecularassembler.menu.pattern.ExtendedPatternRecipeType;
import me.myogoo.extendedmolecularassembler.menu.pattern.ExtendedPatternEncodingTermMenu;
import me.myogoo.extendedmolecularassembler.menu.pattern.IExtendedPatternEncodingTerminalHost;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.List;

public class ExtendedPatternEncodingTerminalPart extends AbstractTerminalPart
        implements IExtendedPatternEncodingTerminalHost {
    private static final String REMEMBER_RECIPE_TYPE = "rememberExtendedPatternRecipeType";
    private static final String SELECTED_RECIPE_PROVIDER = "selectedExtendedPatternRecipeProvider";
    private static final String SELECTED_RECIPE_TABLE_TIER = "selectedExtendedPatternRecipeTableTier";
    private static final String SELECTED_RECIPE_TABLE_SIDE = "selectedExtendedPatternRecipeTableSide";

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
    public void readFromNBT(ValueInput input) {
        super.readFromNBT(input);
        logic.readFromNBT(input);
        this.rememberRecipeType = input.getBooleanOr(REMEMBER_RECIPE_TYPE, true);
        this.rememberedRecipeType = ExtendedPatternRecipeType.readFromNBT(input,
                SELECTED_RECIPE_PROVIDER,
                SELECTED_RECIPE_TABLE_TIER,
                SELECTED_RECIPE_TABLE_SIDE);
    }

    @Override
    public void writeToNBT(ValueOutput output) {
        super.writeToNBT(output);
        logic.writeToNBT(output);
        output.putBoolean(REMEMBER_RECIPE_TYPE, rememberRecipeType);
        if (rememberedRecipeType != null) {
            rememberedRecipeType.writeToNBT(output,
                    SELECTED_RECIPE_PROVIDER,
                    SELECTED_RECIPE_TABLE_TIER,
                    SELECTED_RECIPE_TABLE_SIDE);
        }
    }

    @Override
    public MenuType<?> getMenuType(Player player) {
        return ExtendedPatternEncodingTermMenu.TYPE;
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
