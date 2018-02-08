package com.nestorrente.jitl.annotation;

import com.nestorrente.jitl.cache.CacheStrategy;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;

@Target({ TYPE, METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Cache {

	CacheStrategy value();

}
