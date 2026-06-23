package me.myogoo.extendedmolecularassembler.api.annotation.condition;

import me.myogoo.myotus.api.integration.MyoCustomCondition;
import me.myogoo.myotus.dto.MyoModInfo;

public final class ReAvaritiaCondition implements MyoCustomCondition {
    @Override
    public boolean test(MyoModInfo modInfo) {
        return "Re-Avaritia".equals(modInfo.displayName());
    }
}
