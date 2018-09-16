package com.nestorrente.jitl.param.provider;

import com.nestorrente.jitl.annotation.cache.Cacheable;
import com.nestorrente.jitl.param.ParamProvider;

import java.util.HashMap;
import java.util.Map;

@Cacheable
public class GreetingParamProvider implements ParamProvider {

	private final String name;

	public GreetingParamProvider() {
		this("World");
	}

	public GreetingParamProvider(String name) {
		this.name = name;
	}

	@Override
	public Map<String, Object> params() {

		Map<String, Object> params = new HashMap<>();

		params.put("greeting", "Hello");
		params.put("name", this.name);

		return params;

	}

}
