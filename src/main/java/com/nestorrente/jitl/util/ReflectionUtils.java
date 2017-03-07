package com.nestorrente.jitl.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;

import com.google.common.reflect.TypeToken;

public class ReflectionUtils {

	public static boolean isArray(Object obj) {
		return obj.getClass().isArray();
	}

	public static <T> boolean addAllFromArray(Collection<T> collection, Object array) {

		if(!isArray(array)) {
			throw new IllegalArgumentException("Argument is not an array");
		}

		int length = Array.getLength(array);

		boolean result = false;

		for(int i = 0; i < length; ++i) {

			@SuppressWarnings("unchecked")
			T element = (T) Array.get(array, i);

			result |= collection.add(element);

		}

		return result;

	}

	public static <A extends Annotation> Optional<A> getAnnotation(AnnotatedElement annotatedElement, Class<A> annotationClass) {
		return annotatedElement.isAnnotationPresent(annotationClass) ? Optional.of(annotatedElement.getAnnotationsByType(annotationClass)[0]) : Optional.empty();
	}

	public static <A extends Annotation, T> Optional<T> getAnnotationValue(AnnotatedElement annotatedElement, Class<A> annotationClass, Function<A, T> valueGetter) {

		Optional<A> annotation = getAnnotation(annotatedElement, annotationClass);

		return annotation.isPresent() ? Optional.of(valueGetter.apply(annotation.get())) : Optional.empty();

	}

	public static <T> TypeToken<?> getSuperclassTypeArgument(TypeToken<? extends T> typeToken, Class<T> superclass, int typeArgumentIndex) {

		TypeToken<?> superclassTypeToken = typeToken.getSupertype(superclass);

		ParameterizedType superclassAsParameterizedType = (ParameterizedType) superclassTypeToken.getType();

		return TypeToken.of(superclassAsParameterizedType.getActualTypeArguments()[typeArgumentIndex]);

	}

	// public static <T> TypeToken<?>[] getSuperclassTypeArguments(TypeToken<? extends T> typeToken, Class<T> superclass, int typeArgumentIndex) {
	//
	// TypeToken<?> superclassTypeToken = typeToken.getSupertype(superclass);
	//
	// ParameterizedType superclassAsParameterizedType = (ParameterizedType) superclassTypeToken.getType();
	//
	// return Arrays.stream(superclassAsParameterizedType.getActualTypeArguments()).map(t -> TypeToken.of(t)).toArray(l -> new TypeToken<?>[l]);
	//
	// }

	public static Field getField(Class<?> clazz, String name) throws NoSuchFieldException {

		NoSuchFieldException firstException;

		try {
			return clazz.getDeclaredField(name);
		} catch(NoSuchFieldException ex) {
			firstException = ex;
		}

		for(Class<?> current = clazz.getSuperclass(); current != null; current = current.getSuperclass()) {
			try {
				return current.getDeclaredField(name);
			} catch(NoSuchFieldException ignore) {}
		}

		throw firstException;

	}

	public static void setField(Object obj, Field field, Object value) {
		try {
			field.setAccessible(true);
			field.set(obj, value);
		} catch(IllegalArgumentException | IllegalAccessException ex) {
			throw new RuntimeException(ex);
		}
	}

}
