package com.nestorrente.jitl.cache;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class TestCacheManager implements CacheManager {

	// we can use HashMap - we don't need a thread-safe cache in tests
	private final Map<Method, String> uriCache = new HashMap<>();
	private final Map<Method, String> contentsCache = new HashMap<>();

	private int uriCacheCalls = 0;
	private int uriGetCalls = 0;

	private int contentsCacheCalls = 0;
	private int contentsGetCalls = 0;

	@Override public void cacheUri(Method method, String uri) {
		this.uriCacheCalls++;
		this.uriCache.put(method, uri);
	}

	@Override public Optional<String> getUri(Method method) {
		this.uriGetCalls++;
		return Optional.ofNullable(this.uriCache.get(method));
	}

	@Override public void cacheContents(Method method, String contents) {
		this.uriCacheCalls++;
		this.contentsCache.put(method, contents);
	}

	@Override public Optional<String> getContents(Method method) {
		this.uriGetCalls++;
		return Optional.ofNullable(this.contentsCache.get(method));
	}
}
