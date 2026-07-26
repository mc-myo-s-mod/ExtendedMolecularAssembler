package me.myogoo.extendedmolecularassembler.menu.pattern;

import appeng.api.storage.ITerminalHost;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public interface IExtendedPatternEncodingTerminalHost extends ITerminalHost {
    ExtendedPatternEncodingLogic getExtendedPatternEncodingLogic();

    Level getLevel();

    boolean rememberRecipeType();

    void setRememberRecipeType(boolean remember);

    @Nullable
    ExtendedPatternRecipeType getRememberedRecipeType();

    void setRememberedRecipeType(@Nullable ExtendedPatternRecipeType recipeType);

    void markForSave();
}
