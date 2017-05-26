package com.nestorrente.jitl.module;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;

import com.nestorrente.jitl.Jitl;
import com.nestorrente.jitl.exception.TransformationException;

public class NoOpModule extends Module {

	public static final NoOpModule INSTANCE = new NoOpModule();

	private NoOpModule() {
		super(Collections.emptyList());
	}

	@Override
	public Object postProcess(Jitl jitl, Method method, String renderedTemplate, Map<String, Object> parameters) throws Exception {

		if(!String.class.equals(method.getReturnType())) {
			throw new TransformationException("Cannot transform template result to " + method.getGenericReturnType().getTypeName());
		}

		return renderedTemplate;

	}

}
