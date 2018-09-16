package com.nestorrente.jitl.cache;

import java.util.function.Supplier;

public abstract class AbstractCacheManager<K, V> implements CacheManager<K, V> {

	@Override
	public V getOrCompute(K key, Supplier<? extends V> valueSupplier) {
		return this.getIfExists(key).orElseGet(() -> {
			V value = valueSupplier.get();
			this.storeComputedValue(key, value);
			return value;
		});
	}

	protected abstract void storeComputedValue(K key, V value);

}
