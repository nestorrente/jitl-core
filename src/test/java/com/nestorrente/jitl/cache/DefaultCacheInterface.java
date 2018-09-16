package com.nestorrente.jitl.cache;

import com.nestorrente.jitl.annotation.BaseClasspath;
import com.nestorrente.jitl.annotation.ClasspathTemplate;
import com.nestorrente.jitl.annotation.Encoding;
import com.nestorrente.jitl.annotation.cache.CacheTemplate;

@BaseClasspath("com/nestorrente/jitl/rubik/average/")
@Encoding("UTF-8")
public interface DefaultCacheInterface {

	@ClasspathTemplate("template")
	String defaultCache();

	@ClasspathTemplate("template")
	@CacheTemplate(false)
	String noCache();

	@ClasspathTemplate("template")
	@CacheTemplate
	String cacheContents();

}
