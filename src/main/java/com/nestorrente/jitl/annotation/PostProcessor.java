package com.nestorrente.jitl.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.nestorrente.jitl.postprocessor.JitlPostProcessor;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface PostProcessor {

	Class<? extends JitlPostProcessor> value();

}
