package me.myogoo.extendedmolecularassembler.api.annotation;

import me.myogoo.myotus.api.annotation.MyoMod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks integrations for the NeoForge 1.21.1+ ExtendedAE mod, whose mod id is {@code extendedae}.
 *
 * <p>Forge 1.20.1 uses the distinct {@link ExPatternProvider} marker because that release line uses the
 * {@code expatternprovider} mod id.</p>
 */
@MyoMod(value = "extendedae", versionRange = "[1.21-2.2.29-neoforge,]")
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ExtendedAE {
}
