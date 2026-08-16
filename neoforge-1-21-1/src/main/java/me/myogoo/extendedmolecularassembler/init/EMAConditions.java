package me.myogoo.extendedmolecularassembler.init;

import com.mojang.serialization.MapCodec;
import me.myogoo.extendedmolecularassembler.ExtendedMolecularAssembler;
import me.myogoo.extendedmolecularassembler.condition.ExtendedAEPlusStandaloneCondition;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public final class EMAConditions {
    public static final DeferredRegister<MapCodec<? extends ICondition>> REGISTER =
            DeferredRegister.create(NeoForgeRegistries.Keys.CONDITION_CODECS, ExtendedMolecularAssembler.MODID);

    public static final Supplier<MapCodec<ExtendedAEPlusStandaloneCondition>> EXTENDEDAE_PLUS_STANDALONE =
            REGISTER.register("extendedae_plus_standalone", () -> ExtendedAEPlusStandaloneCondition.CODEC);

    private EMAConditions() {
    }
}
