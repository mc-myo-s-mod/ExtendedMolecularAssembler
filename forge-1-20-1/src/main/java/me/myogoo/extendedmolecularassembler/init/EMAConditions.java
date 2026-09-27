package me.myogoo.extendedmolecularassembler.init;

import me.myogoo.extendedmolecularassembler.condition.ExtendedAEPlusStandaloneCondition;
import net.minecraftforge.common.crafting.CraftingHelper;

public final class EMAConditions {
    public static void register() {
        CraftingHelper.register(ExtendedAEPlusStandaloneCondition.SERIALIZER);
    }

    private EMAConditions() {
    }
}
