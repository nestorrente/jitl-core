package com.nestorrente.jitl.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

public class AnnotationUtils {

	public static <A extends Annotation> Optional<A> get(AnnotatedElement annotatedElement, Class<A> annotationClass) {
		return annotatedElement.isAnnotationPresent(annotationClass) ? Optional.of(annotatedElement.getAnnotation(annotationClass)) : Optional.empty();
	}

	public static <A extends Annotation> Optional<A> getFromMethodOrClass(Method method, Class<A> annotationClass) {
		return OptionalUtils.or(get(method, annotationClass), () -> get(method.getDeclaringClass(), annotationClass));
	}

	public static <A extends Annotation> Stream<A> getRepeatable(AnnotatedElement annotatedElement, Class<A> annotationClass) {
		return Arrays.stream(annotatedElement.getAnnotationsByType(annotationClass));
	}

	public static <A extends Annotation> Stream<A> getRepeatableFromMethodAndClass(Method method, Class<A> annotationClass) {

		Stream<A> annotationsInMethod = getRepeatable(method, annotationClass);

		Stream<A> annotationsInClass = getRepeatable(method.getDeclaringClass(), annotationClass);

		return Stream.concat(annotationsInMethod, annotationsInClass);

	}

}
