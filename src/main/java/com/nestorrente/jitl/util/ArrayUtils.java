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

	public static void main(String[] args) {

		int[] pepe = { 3, 4 };
		Integer[] pepito = { 3, 4 };

		Object oPepe = pepe;
		System.out.println(oPepe instanceof Object[]);
		System.out.println(pepito instanceof Object[]);

	}

}
