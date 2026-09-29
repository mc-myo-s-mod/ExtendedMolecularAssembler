package me.myogoo.extendedmolecularassembler.integration.ae2wtlib;

import appeng.api.implementations.blockentities.IViewCellStorage;
import appeng.menu.ISubMenu;
import appeng.menu.locator.ItemMenuHostLocator;
import de.mari_023.ae2wtlib.api.terminal.ItemWT;
import de.mari_023.ae2wtlib.api.terminal.WTMenuHost;
import me.myogoo.extendedmolecularassembler.menu.pattern.ExtendedPatternEncodingLogic;
import me.myogoo.extendedmolecularassembler.menu.pattern.ExtendedPatternRecipeType;
import me.myogoo.extendedmolecularassembler.menu.pattern.IExtendedPatternEncodingTerminalHost;
import me.myogoo.myotus.api.annotation.mods.AE2WTLib;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;

import java.util.function.BiConsumer;

@AE2WTLib
public class WirelessExtendedPatternEncodingTerminalMenuHost extends WTMenuHost
        implements IExtendedPatternEncodingTerminalHost, IViewCellStorage {
    private static final String LOGIC_TAG = "extendedmolecularassembler:extendedPatternEncoding";
    private static final String REMEMBER_RECIPE_TYPE = "rememberExtendedPatternRecipeType";
    private static final String SELECTED_RECIPE_PROVIDER = "selectedExtendedPatternRecipeProvider";
    private static final String SELECTED_RECIPE_TABLE_TIER = "selectedExtendedPatternRecipeTableTier";
    private static final String SELECTED_RECIPE_TABLE_SIDE = "selectedExtendedPatternRecipeTableSide";

    private final ExtendedPatternEncodingLogic logic = new ExtendedPatternEncodingLogic(this);

    public WirelessExtendedPatternEncodingTerminalMenuHost(ItemWT item, Player player, ItemMenuHostLocator locator,
            BiConsumer<Player, ISubMenu> returnToMainMenu) {
        super(item, player, locator, returnToMainMenu);
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
        if (!getLevel().isClientSide()) {
            writeExtendedPatternData();
        }
    }

    @Override
    public boolean rememberRecipeType() {
        var tag = getExtendedPatternTag();
        return tag.getBooleanOr(REMEMBER_RECIPE_TYPE, true);
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
            logic.readFromNBT(tag, getPlayer().registryAccess());
        }
    }

    private void writeExtendedPatternData() {
        var root = getRootTag();
        var logicTag = tagForRead();
        logic.writeToNBT(logicTag, getPlayer().registryAccess());
        root.put(LOGIC_TAG, logicTag);
        getItemStack().set(DataComponents.CUSTOM_DATA, CustomData.of(root));
    }

    private CompoundTag getRootTag() {
        return getItemStack().getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
    }

    private CompoundTag tagForRead() {
        var root = getRootTag();
        return root.getCompound(LOGIC_TAG).map(CompoundTag::copy).orElseGet(CompoundTag::new);
    }

    private CompoundTag getExtendedPatternTag() {
        return tagForRead();
    }

    private void saveExtendedPatternTag(CompoundTag tag) {
        var root = getRootTag();
        root.put(LOGIC_TAG, tag);
        getItemStack().set(DataComponents.CUSTOM_DATA, CustomData.of(root));
    }

    private static void clearRememberedRecipeType(CompoundTag tag) {
        tag.remove(SELECTED_RECIPE_PROVIDER);
        tag.remove(SELECTED_RECIPE_TABLE_TIER);
        tag.remove(SELECTED_RECIPE_TABLE_SIDE);
    }
}
