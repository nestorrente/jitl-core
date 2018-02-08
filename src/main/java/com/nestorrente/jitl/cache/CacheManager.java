package com.nestorrente.jitl.cache;

import java.lang.reflect.Method;
import java.util.Optional;

public interface CacheManager {

	void cacheUri(Method method, String uri);

	Optional<String> getUri(Method method);

	void cacheContents(Method method, String contents);

	Optional<String> getContents(Method method);

}
