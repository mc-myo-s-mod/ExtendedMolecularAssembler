package me.myogoo.extendedmolecularassembler.integration.ae2wtlib;

import appeng.menu.ISubMenu;
import de.mari_023.ae2wtlib.terminal.WTMenuHost;
import me.myogoo.extendedmolecularassembler.menu.pattern.ExtendedPatternEncodingLogic;
import me.myogoo.extendedmolecularassembler.menu.pattern.ExtendedPatternRecipeType;
import me.myogoo.extendedmolecularassembler.menu.pattern.IExtendedPatternEncodingTerminalHost;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.function.BiConsumer;

public class WirelessExtendedPatternEncodingTerminalMenuHost extends WTMenuHost
        implements IExtendedPatternEncodingTerminalHost {
    private static final String LOGIC_TAG = "extendedmolecularassembler:extendedPatternEncoding";
    private static final String REMEMBER_RECIPE_TYPE = "rememberExtendedPatternRecipeType";
    private static final String SELECTED_RECIPE_PROVIDER = "selectedExtendedPatternRecipeProvider";
    private static final String SELECTED_RECIPE_TABLE_TIER = "selectedExtendedPatternRecipeTableTier";
    private static final String SELECTED_RECIPE_TABLE_SIDE = "selectedExtendedPatternRecipeTableSide";

    private final ExtendedPatternEncodingLogic logic = new ExtendedPatternEncodingLogic(this);

    public WirelessExtendedPatternEncodingTerminalMenuHost(Player player, Integer slot, ItemStack itemStack,
            BiConsumer<Player, ISubMenu> returnToMainMenu) {
        super(player, slot, itemStack, returnToMainMenu);
        readExtendedPatternData();
    }

    @Override
    public ExtendedPatternEncodingLogic getExtendedPatternEncodingLogic() {
        return logic;
    }

    @Override
    public Level getLevel() {
        return getPlayer().level();
    }

    @Override
    public void markForSave() {
        writeExtendedPatternData();
        super.saveChanges();
    }

    @Override
    public boolean rememberRecipeType() {
        var tag = tagForRead();
        return !tag.contains(REMEMBER_RECIPE_TYPE, Tag.TAG_BYTE) || tag.getBoolean(REMEMBER_RECIPE_TYPE);
    }

    @Override
    public void setRememberRecipeType(boolean remember) {
        var tag = tagForRead();
        tag.putBoolean(REMEMBER_RECIPE_TYPE, remember);
        if (!remember) {
            clearRememberedRecipeType(tag);
        }
        saveExtendedPatternTag(tag);
    }

    @Override
    public ExtendedPatternRecipeType getRememberedRecipeType() {
        return ExtendedPatternRecipeType.readFromNBT(tagForRead(),
                SELECTED_RECIPE_PROVIDER,
                SELECTED_RECIPE_TABLE_TIER,
                SELECTED_RECIPE_TABLE_SIDE);
    }

    @Override
    public void setRememberedRecipeType(ExtendedPatternRecipeType recipeType) {
        var tag = tagForRead();
        if (recipeType == null) {
            clearRememberedRecipeType(tag);
        } else {
            recipeType.writeToNBT(tag,
                    SELECTED_RECIPE_PROVIDER,
                    SELECTED_RECIPE_TABLE_TIER,
                    SELECTED_RECIPE_TABLE_SIDE);
        }
        saveExtendedPatternTag(tag);
    }

    private void readExtendedPatternData() {
        var tag = tagForRead();
        if (!tag.isEmpty()) {
            logic.readFromNBT(tag);
        }
    }

    private void writeExtendedPatternData() {
        var tag = tagForRead();
        logic.writeToNBT(tag);
        saveExtendedPatternTag(tag);
    }

    private CompoundTag tagForRead() {
        var root = getItemStack().getOrCreateTag();
        if (root.contains(LOGIC_TAG, Tag.TAG_COMPOUND)) {
            return root.getCompound(LOGIC_TAG).copy();
        }
        return new CompoundTag();
    }

    private void saveExtendedPatternTag(CompoundTag tag) {
        getItemStack().getOrCreateTag().put(LOGIC_TAG, tag);
    }

    private static void clearRememberedRecipeType(CompoundTag tag) {
        tag.remove(SELECTED_RECIPE_PROVIDER);
        tag.remove(SELECTED_RECIPE_TABLE_TIER);
        tag.remove(SELECTED_RECIPE_TABLE_SIDE);
    }
}
