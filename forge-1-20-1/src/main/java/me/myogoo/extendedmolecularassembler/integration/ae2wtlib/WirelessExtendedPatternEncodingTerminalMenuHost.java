package me.myogoo.extendedmolecularassembler.integration.ae2wtlib;

import appeng.api.implementations.blockentities.IViewCellStorage;
import appeng.menu.ISubMenu;
import de.mari_023.ae2wtlib.terminal.WTMenuHost;
import me.myogoo.extendedmolecularassembler.menu.pattern.ExtendedPatternEncodingLogic;
import me.myogoo.extendedmolecularassembler.menu.pattern.ExtendedPatternRecipeType;
import me.myogoo.extendedmolecularassembler.menu.pattern.IExtendedPatternEncodingTerminalHost;
import me.myogoo.myotus.api.annotation.mods.AE2WTLib;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.function.BiConsumer;

@AE2WTLib
public class WirelessExtendedPatternEncodingTerminalMenuHost extends WTMenuHost
        implements IExtendedPatternEncodingTerminalHost, IViewCellStorage {
    private static final String LOGIC_TAG = "extendedmolecularassembler:extendedPatternEncoding";
    private static final String EPIC_LOGIC_TAG = "extendedmolecularassembler:epicPatternEncoding";
    private static final String LEGENDARY_LOGIC_TAG = "extendedmolecularassembler:legendaryPatternEncoding";
    private static final String REMEMBER_RECIPE_TYPE = "rememberExtendedPatternRecipeType";
    private static final String SELECTED_RECIPE_PROVIDER = "selectedExtendedPatternRecipeProvider";
    private static final String SELECTED_RECIPE_TABLE_TIER = "selectedExtendedPatternRecipeTableTier";
    private static final String SELECTED_RECIPE_TABLE_SIDE = "selectedExtendedPatternRecipeTableSide";

    private final int gridSide;
    private final String logicTag;
    private final ExtendedPatternEncodingLogic logic;

    public WirelessExtendedPatternEncodingTerminalMenuHost(Player player, Integer slot, ItemStack itemStack,
            BiConsumer<Player, ISubMenu> returnToMainMenu, int gridSide) {
        super(player, slot, itemStack, returnToMainMenu);
        this.gridSide = gridSide;
        this.logicTag = switch (gridSide) {
            case 11 -> EPIC_LOGIC_TAG;
            case 13 -> LEGENDARY_LOGIC_TAG;
            default -> LOGIC_TAG;
        };
        this.logic = new ExtendedPatternEncodingLogic(this);
        readFromNbt();
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
    public int getGridSide() {
        return gridSide;
    }

    @Override
    public void markForSave() {
        if (!getLevel().isClientSide()) {
            writeExtendedPatternData();
            super.saveChanges();
        }
    }

    @Override
    public boolean rememberRecipeType() {
        var tag = getExtendedPatternTag();
        return !tag.contains(REMEMBER_RECIPE_TYPE, Tag.TAG_BYTE) || tag.getBoolean(REMEMBER_RECIPE_TYPE);
    }

    @Override
    public void setRememberRecipeType(boolean remember) {
        var tag = getExtendedPatternTag();
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
        var tag = getExtendedPatternTag();
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
        var root = getRootTag();
        var logicTag = tagForRead();
        logic.writeToNBT(logicTag);
        root.put(this.logicTag, logicTag);
        getItemStack().setTag(root);
    }

    private CompoundTag getRootTag() {
        return getItemStack().getOrCreateTag().copy();
    }

    private CompoundTag tagForRead() {
        var root = getRootTag();
        if (root.contains(logicTag, Tag.TAG_COMPOUND)) {
            return root.getCompound(logicTag).copy();
        }
        return new CompoundTag();
    }

    private CompoundTag getExtendedPatternTag() {
        return tagForRead();
    }

    private void saveExtendedPatternTag(CompoundTag tag) {
        var root = getRootTag();
        root.put(logicTag, tag);
        getItemStack().setTag(root);
    }

    private static void clearRememberedRecipeType(CompoundTag tag) {
        tag.remove(SELECTED_RECIPE_PROVIDER);
        tag.remove(SELECTED_RECIPE_TABLE_TIER);
        tag.remove(SELECTED_RECIPE_TABLE_SIDE);
    }
}
