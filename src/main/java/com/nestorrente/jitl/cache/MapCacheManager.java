package com.nestorrente.jitl.cache;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class MapCacheManager implements CacheManager {

	private final Map<Method, String> uriCache = new ConcurrentHashMap<>();
	private final Map<Method, String> contentsCache = new ConcurrentHashMap<>();

	@Override public void cacheUri(Method method, String uri) {
		this.uriCache.put(method, uri);
	}

	@Override public Optional<String> getUri(Method method) {
		return Optional.ofNullable(this.uriCache.get(method));
	}

	@Override public void cacheContents(Method method, String contents) {
		this.uriCache.put(method, contents);
	}

	@Override public Optional<String> getContents(Method method) {
		return Optional.ofNullable(this.contentsCache.get(method));
	}

}
