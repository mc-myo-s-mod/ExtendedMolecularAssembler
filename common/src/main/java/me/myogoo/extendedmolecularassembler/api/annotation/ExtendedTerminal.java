package me.myogoo.extendedmolecularassembler.api.annotation;

import me.myogoo.myotus.api.annotation.MyoMod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@MyoMod("extendedterminal")
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ExtendedTerminal {
}
