package com.nestorrente.jitl.cache;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class MapCacheManager<K, V> extends AbstractCacheManager<K, V> {

	private final Map<K, V> map;

	public MapCacheManager() {
		// FIXME utilizar Locks para proteger esto de forma correcta:
		// - Pueden leer varios a la vez.
		// - Si alguien quiere escribir, nadie puede escribir ni leer.
		// - Estos locks deben ser por key, para no bloquear a otros.
		this.map = new ConcurrentHashMap<>();
	}

	@Override
	public Optional<V> getIfExists(K key) {
		return Optional.ofNullable(this.map.get(key));
	}

	@Override
	protected void storeComputedValue(K key, V value) {
		this.map.put(key, value);
	}

}
