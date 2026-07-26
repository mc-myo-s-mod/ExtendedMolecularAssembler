package me.myogoo.extendedmolecularassembler.api.annotation;

import me.myogoo.myotus.api.annotation.MyoMod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks integrations for the Forge 1.20.1 ExtendedAE release line, whose mod id is
 * {@code expatternprovider}.
 */
@MyoMod(value = "expatternprovider", versionRange = "[1.20-1.4.12-forge,]")
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ExPatternProvider {
}
