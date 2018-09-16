package com.nestorrente.jitl.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Optional;

// TODO rename to ParameterAttributesReader or something like that?
@Deprecated
public class ParameterUtils {

	/*
	 * FIXME y si cambiamos toda esta parafernalia por algo mucho más sencillo?
	 *
	 *  Para que no se tenga que leer toda esta información cuando no es necesaria,
	 * quizá se podría hacer simplemente un:
	 *
	 * - T getAttribute(method, paramIndex, MyParamAttribute.class);
	 *     -> Para obtener uno que funciona a nivel de parámetro.
	 * - T getAttribute(method, paramIndex, MyParamAttribute.class, MyParamsAttributes.class);
	 *     -> Para obtener uno que puede funcionar a nivel de parámetro o de método.
	 *
	 * Incluso habría que evaluar la posibilidad de que exista uno para obtener solo a nivel de método,
	 * aunque el nombre en ese caso tendría que ser diferente.
	 *
	 * Estaría bien que, además, existan sus equivalentes getOptionalAttribute.
	 *
	 * NOTA: el problema es que si se van a pedir muchos atributos, esto accedería varias veces al
	 * método y el parámetro. No obstante, es cierto que va directo a por la anotación en vez de
	 * recorrer todas. Habría que evaluarlo, quizá hacer pruebas de rendimiento, y ver qué es mejor.
	 *
	 */

	public static <T> Optional<T> getAttribute(Method method, int parameterIndex, Class<? extends Annotation> parameterAnnotationClass) {
		return getAttribute(method, parameterIndex, parameterAnnotationClass, null);
	}

	public static <T> Optional<T> getAttribute(Method method, int parameterIndex, Class<? extends Annotation> parameterAnnotationClass, Class<? extends Annotation> methodAnnotationClass) {

		Parameter parameter = method.getParameters()[parameterIndex];

		Optional<T> attributeValue = AnnotationUtils.get(parameter, parameterAnnotationClass)
				.map(AnnotationUtils::getValue);

		if(attributeValue.isPresent()) {
			return attributeValue;
		}

		// TODO darle una vuelta a todo esto

		return AnnotationUtils.get(method, methodAnnotationClass)
				.map(AnnotationUtils::getValue)
				.map(v -> {

					@SuppressWarnings("unchecked")
					T value = (T) Array.get(v, parameterIndex);

					return value;

				});

	}

}
