package com.nestorrente.jitl.annotation.param;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(AdditionalParams.class)
public @interface AdditionalParam {

	String name();

	String value();

}
