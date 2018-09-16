package com.nestorrente.jitl.cache;

import java.util.Optional;
import java.util.function.Supplier;

public interface CacheManager<K, V> {

	V getOrCompute(K key, Supplier<? extends V> valueSupplier);

	Optional<V> getIfExists(K key);

}
