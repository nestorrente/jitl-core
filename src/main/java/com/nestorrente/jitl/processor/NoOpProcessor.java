package com.nestorrente.jitl.processor;

import com.nestorrente.jitl.exception.TransformationException;

import java.lang.reflect.Method;
import java.util.Map;

public class NoOpProcessor extends Processor {

	public static final NoOpProcessor INSTANCE = new NoOpProcessor();

	@Override
	public Object process(Method method, String renderedTemplate, Map<String, Object> parameters) throws Exception {

		if(!String.class.equals(method.getReturnType())) {
			throw new TransformationException("Cannot transform template result to " + method.getGenericReturnType().getTypeName());
		}

		return renderedTemplate;

	}

}
