package com.nestorrente.jitl.util;

import java.util.Collection;

public class ArrayUtils {

	public static <T> boolean addAll(Collection<T> collection, T[] array) {

		boolean result = false;

		for(T element : array) {
			result |= collection.add(element);
		}

		return result;

	}

}
