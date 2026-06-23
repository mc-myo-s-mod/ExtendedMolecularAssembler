package me.myogoo.extendedmolecularassembler.api.annotation;

import me.myogoo.extendedmolecularassembler.api.annotation.condition.ReAvaritiaCondition;
import me.myogoo.myotus.api.annotation.MyoMod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@MyoMod(value = "avaritia", alias = "Re-Avaritia", versionRange = "[1.3.9.6,]", customCondition = ReAvaritiaCondition.class)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ReAvaritia {
}
