package com.nestorrente.jitl.util;

import com.nestorrente.jitl.annotation.Cacheable;

public class CacheableUtils {

	public static boolean isCacheable(Class<?> clazz) {
		return AnnotationUtils.get(clazz, Cacheable.class).isPresent();
	}

	public static boolean isCacheable(Class<?> clazz, boolean hierarchicalLookup) {
		return hierarchicalLookup ? findCacheableInHierarchy(clazz) : isCacheable(clazz);
	}

	public static boolean isCacheable(Object obj) {
		return isCacheable(obj.getClass());
	}

	public static boolean isCacheable(Object obj, boolean hierarchicalLookup) {
		return isCacheable(obj.getClass(), hierarchicalLookup);
	}

	private static boolean findCacheableInHierarchy(Class<?> clazz) {

		if(AnnotationUtils.get(clazz, Cacheable.class).isPresent()) {
			return true;
		}

		Class<?> superclass = clazz.getSuperclass();

		if(superclass == Object.class || superclass == null) {
			return false;
		}

		return findCacheableInHierarchy(superclass);

	}

}
