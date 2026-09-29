package me.myogoo.extendedmolecularassembler.condition;

import com.mojang.serialization.MapCodec;
import me.myogoo.extendedmolecularassembler.config.EMAConfig;
import net.neoforged.neoforge.common.conditions.ICondition;
import org.jetbrains.annotations.NotNull;

public enum ExtendedAEPlusStandaloneCondition implements ICondition {
    INSTANCE;

    public static final MapCodec<ExtendedAEPlusStandaloneCondition> CODEC = MapCodec.unit(INSTANCE).stable();

    @Override
    public boolean test(IContext context) {
        return EMAConfig.standaloneExtendedAEPlusContent();
    }

    @Override
    public @NotNull MapCodec<? extends ICondition> codec() {
        return CODEC;
    }
}
