package com.nestorrente.jitl.util;

import com.nestorrente.jitl.exception.UncheckedReflectiveOperationException;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

public class AnnotationUtils {

	public static <A extends Annotation> Optional<A> get(AnnotatedElement annotatedElement, Class<A> annotationType) {
		return Optional.ofNullable(annotatedElement.getAnnotation(annotationType));
	}

	public static <A extends Annotation> Optional<A> getFromMethodOrClass(Method method, Class<A> annotationType) {
		return OptionalUtils.or(get(method, annotationType), () -> get(method.getDeclaringClass(), annotationType));
	}

	public static <A extends Annotation> Stream<A> getRepeatable(AnnotatedElement annotatedElement, Class<A> annotationType) {
		return Arrays.stream(annotatedElement.getAnnotationsByType(annotationType));
	}

	public static <A extends Annotation> Stream<A> getRepeatableFromMethodAndClass(Method method, Class<A> annotationType) {

		Stream<A> annotationsInMethod = getRepeatable(method, annotationType);

		Stream<A> annotationsInClass = getRepeatable(method.getDeclaringClass(), annotationType);

		return Stream.concat(annotationsInMethod, annotationsInClass);

	}

	/**
	 * Gets the value of any annotation type.
	 * <p>
	 * This method is useful when you want to get the value of an annotation instance without knowing its type.
	 *
	 * @param annotation the instance of the annotation
	 * @param <T>        the type of the annotation value
	 * @return the annotation value
	 */
	public static <T> T getValue(Annotation annotation) {

		try {

			@SuppressWarnings("unchecked")
			T value = (T) annotation.annotationType().getMethod("value").invoke(annotation);

			return value;

		} catch(ReflectiveOperationException ex) {
			throw new UncheckedReflectiveOperationException(ex);
		}

	}

}
