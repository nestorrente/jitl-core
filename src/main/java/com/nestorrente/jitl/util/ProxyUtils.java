package com.nestorrente.jitl.util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

public class ProxyUtils {

	public static <T> T createProxy(Class<T> interfaze, InvocationHandler handler) {

		@SuppressWarnings("unchecked")
		T proxy = (T) Proxy.newProxyInstance(ProxyUtils.class.getClassLoader(), new Class<?>[] { interfaze }, handler);

		return proxy;

	}

}
