package com.nestorrente.jitl.param;

import com.nestorrente.jitl.annotation.cache.Cacheable;
import com.nestorrente.jitl.exception.UncheckedReflectiveOperationException;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ParamProviderRegister {

	private final Map<Class<? extends ParamProvider>, ParamProvider> paramProviders;

	public ParamProviderRegister() {
		this.paramProviders = new HashMap<>();
	}

	public void registerParamProvider(ParamProvider paramProvider) {

		boolean cacheable = paramProvider.getClass().isAnnotationPresent(Cacheable.class);

		this.registerParamProvider(paramProvider, cacheable);

	}

	public void registerParamProvider(ParamProvider paramProvider, boolean cacheable) {

		Class<? extends ParamProvider> providerClass = paramProvider.getClass();

		if(this.paramProviders.containsKey(providerClass)) {
			throw new IllegalStateException(String.format("Repeated %s: %s", ParamProvider.class.getSimpleName(), providerClass.getName()));
		}

		if(cacheable) {

			CacheableParamProviderDecorator cacheableParamProvider = new CacheableParamProviderDecorator(paramProvider);

			this.paramProviders.put(providerClass, cacheableParamProvider);

		} else {

			this.paramProviders.put(providerClass, paramProvider);

		}

	}

	/**
	 * Returns the {@link ParamProvider} registered instance, or auto-registers a new one if {@code autoRegister} is {@code true}.
	 *
	 * @param providerClass parameter provider's class reference.
	 * @param autoRegister  when {@code true}, the provider is instantiated and registered if it was not registered before.
	 * @return parameter provider's instance
	 */
	public ParamProvider getParamProvider(Class<? extends ParamProvider> providerClass, boolean autoRegister) {

		return Optional.ofNullable(this.paramProviders.get(providerClass))
				.orElseGet(() -> {

					if(!autoRegister) {
						throw new IllegalStateException(String.format("Unregistered provider: %s", providerClass.getName()));
					}

					try {

						ParamProvider provider = providerClass.newInstance();

						this.registerParamProvider(provider);

						return provider;

					} catch(IllegalAccessException | InstantiationException ex) {
						throw new UncheckedReflectiveOperationException(ex);
					}

				});

	}

}
