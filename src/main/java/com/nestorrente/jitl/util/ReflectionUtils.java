package com.nestorrente.jitl.util;

import com.google.common.reflect.TypeToken;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.function.Consumer;

public class ReflectionUtils {

	public static boolean isArray(Object obj) {
		return obj != null && obj.getClass().isArray();
	}

	/**
	 * Transforms an array into an {@code Object[]}.
	 * <p>
	 * If {@code array} is already an {@code Object[]}, then {@code array} is returned.
	 * If {@code array} is an array of primitve types, then a new {@code Object[]} is created, filled and returned.
	 *
	 * @param array an {@code Object} or primitive array
	 * @return an {@code Object[]}
	 */
	public static Object[] toObjectArray(Object array) {

		if(!isArray(array)) {
			throw new IllegalArgumentException("Argument is not an array");
		}

		if(array instanceof Object[]) {
			return (Object[]) array;
		}

		int length = Array.getLength(array);

		Object[] result = new Object[length];

		for(int i = 0; i < length; ++i) {
			result[i] = Array.get(array, i);
		}
		// System.arraycopy(array, 0, result, 0, length);

		return result;

	}

	public static <T> void forEach(Object array, Consumer<T> consumer) {

		if(!isArray(array)) {
			throw new IllegalArgumentException("Argument is not an array");
		}

		int length = Array.getLength(array);

		for(int i = 0; i < length; ++i) {

			@SuppressWarnings("unchecked")
			T element = (T) Array.get(array, i);

			consumer.accept(element);

		}

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

	public static <T> TypeToken<?> getSuperclassTypeArgument(TypeToken<? extends T> typeToken, Class<T> superclass, int typeArgumentIndex) {

		TypeToken<?> superclassTypeToken = typeToken.getSupertype(superclass);

		ParameterizedType superclassAsParameterizedType = (ParameterizedType) superclassTypeToken.getType();

		return TypeToken.of(superclassAsParameterizedType.getActualTypeArguments()[typeArgumentIndex]);

	}

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
			} catch(NoSuchFieldException ignored) {
			}
		}

		throw firstException;

	}

	public static Object getFieldValue(Object obj, Field field) {
		try {
			field.setAccessible(true);
			return field.get(obj);
		} catch(IllegalAccessException ex) {
			throw new RuntimeException(ex);
		}
	}

	public static void setFieldValue(Object obj, Field field, Object value) {
		try {
			field.setAccessible(true);
			field.set(obj, value);
		} catch(IllegalAccessException ex) {
			throw new RuntimeException(ex);
		}
	}

	public static boolean returnsVoid(Method method) {

		Class<?> returnType = method.getReturnType();

		return void.class.equals(returnType) || Void.class.equals(returnType);

	}

	public static boolean returnsInt(Method method) {

		Class<?> returnType = method.getReturnType();

		return int.class.equals(returnType) || Integer.class.equals(returnType);

	}

}
