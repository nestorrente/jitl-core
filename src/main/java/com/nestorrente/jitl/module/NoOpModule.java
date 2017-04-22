package com.nestorrente.jitl.module;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;

import com.nestorrente.jitl.Jitl;

public class NoOpModule extends Module {

	public static final NoOpModule INSTANCE = new NoOpModule();

	private NoOpModule() {
		super(Collections.emptyList());
	}

	@Override
	public Object postProcess(Jitl jitl, Method method, String renderedTemplate, Map<String, Object> parameters) throws Exception {

		if(!String.class.equals(method.getReturnType())) {
			// TODO replace with a custom exception
			throw new IllegalArgumentException("Cannot transform template result to " + method.getGenericReturnType().getTypeName());
		}

		return renderedTemplate;

	}

}
