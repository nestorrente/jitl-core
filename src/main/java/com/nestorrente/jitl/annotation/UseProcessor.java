package com.nestorrente.jitl.annotation;

import com.nestorrente.jitl.processor.Processor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface UseProcessor {

	Class<? extends Processor> value();

}
