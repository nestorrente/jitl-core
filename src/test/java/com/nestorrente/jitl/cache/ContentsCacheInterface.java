package com.nestorrente.jitl.cache;

import com.nestorrente.jitl.annotation.BaseClasspath;
import com.nestorrente.jitl.annotation.Cache;
import com.nestorrente.jitl.annotation.ClasspathTemplate;
import com.nestorrente.jitl.annotation.Encoding;

@BaseClasspath("com/nestorrente/jitl/rubik/single/")
@Encoding("UTF-8")
@Cache(CacheStrategy.URI)
public interface ContentsCacheInterface {

	@ClasspathTemplate("template")
	String defaultCache();

	@Cache(CacheStrategy.NONE)
	@ClasspathTemplate("template")
	String noCache();

	@Cache(CacheStrategy.URI)
	@ClasspathTemplate("template")
	String cacheUri();

	@Cache(CacheStrategy.CONTENTS)
	@ClasspathTemplate("template")
	String cacheContents();

}
