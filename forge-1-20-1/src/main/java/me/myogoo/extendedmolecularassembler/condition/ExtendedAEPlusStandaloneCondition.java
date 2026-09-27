package me.myogoo.extendedmolecularassembler.condition;

import com.google.gson.JsonObject;
import me.myogoo.extendedmolecularassembler.ExtendedMolecularAssembler;
import me.myogoo.extendedmolecularassembler.config.EMAConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;

public enum ExtendedAEPlusStandaloneCondition implements ICondition {
    INSTANCE;

    private static final ResourceLocation ID = ExtendedMolecularAssembler.makeId("extendedae_plus_standalone");

    @Override
    public boolean test(IContext context) {
        return EMAConfig.standaloneExtendedAEPlusContent();
    }

    @Override
    public ResourceLocation getID() {
        return ID;
    }

    public static final IConditionSerializer<ExtendedAEPlusStandaloneCondition> SERIALIZER =
            new IConditionSerializer<>() {
                public void write(JsonObject json, ExtendedAEPlusStandaloneCondition value) {
                }

                public ExtendedAEPlusStandaloneCondition read(JsonObject json) {
                    return INSTANCE;
                }

                public ResourceLocation getID() {
                    return ID;
                }
            };
}
