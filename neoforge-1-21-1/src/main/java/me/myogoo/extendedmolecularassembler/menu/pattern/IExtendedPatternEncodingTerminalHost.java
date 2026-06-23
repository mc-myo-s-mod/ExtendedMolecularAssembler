package me.myogoo.extendedmolecularassembler.menu.pattern;

import appeng.api.storage.ITerminalHost;
import net.minecraft.world.level.Level;

public interface IExtendedPatternEncodingTerminalHost extends ITerminalHost {
    ExtendedPatternEncodingLogic getExtendedPatternEncodingLogic();

    Level getLevel();

    void markForSave();
}
