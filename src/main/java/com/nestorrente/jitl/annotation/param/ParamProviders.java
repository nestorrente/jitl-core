package com.nestorrente.jitl.annotation.param;

import com.nestorrente.jitl.param.ParamProvider;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ParamProviders {

	Class<? extends ParamProvider>[] value();

}
