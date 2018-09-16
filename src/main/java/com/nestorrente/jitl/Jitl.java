package com.nestorrente.jitl;

import com.nestorrente.jitl.annotation.Singleton;
import com.nestorrente.jitl.cache.CacheManager;
import com.nestorrente.jitl.cache.MapCacheManager;
import com.nestorrente.jitl.util.ProxyUtils;

import java.lang.reflect.InvocationHandler;

// TODO Important: add case-converters (or path-converters) for allowing custom "class+method to filepath" transformation
// TODO Interesting: give a way to create Jitl instances with default engines and post-processors? (i.e., a Jitl instance with Jtwig template engine and SQL post-processor)
// TODO create more post-processors and template engines
public class Jitl {

	private final JitlConfig config;
	private final CacheManager<Class<?>, Object> singletonInstances;

	Jitl(JitlConfig config) {
		this.config = config;
		this.singletonInstances = new MapCacheManager<>();
	}

	/**
	 * @param <T>       type of the interface.
	 * @param interfaze Interface to be implemented by resultant object.
	 * @return An object that implements {@code interfaze} method's.
	 */
	public <T> T getInstance(Class<T> interfaze) {

		if(!interfaze.isInterface()) {
			throw new IllegalArgumentException(String.format("Class %s is not an interface", interfaze.getName()));
		}

		boolean isSingleton = interfaze.isAnnotationPresent(Singleton.class);

		if(isSingleton) {
			return this.getSingletonInstance(interfaze);
		} else {
			return this.createInstance(interfaze);
		}

	}

	private <T> T createInstance(Class<T> interfaze) {

		InvocationHandler invocationHandler = new JitlProxyInvocationHandler(this.config);

		return ProxyUtils.createProxy(interfaze, invocationHandler);

	}

	private <T> T getSingletonInstance(Class<T> interfaze) {

		@SuppressWarnings("unchecked")
		T instance = (T) this.singletonInstances.getOrCompute(interfaze, () -> this.createInstance(interfaze));

		return instance;

	}

	/* Build methods */

	public static JitlBuilder builder() {
		return new JitlBuilder();
	}

	public static Jitl defaultInstance() {
		return builder().build();
	}

}
