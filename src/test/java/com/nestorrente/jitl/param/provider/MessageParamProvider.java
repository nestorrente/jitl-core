package com.nestorrente.jitl.param.provider;

import com.nestorrente.jitl.annotation.cache.Cacheable;
import com.nestorrente.jitl.param.ParamProvider;

import java.util.HashMap;
import java.util.Map;

@Cacheable
public class MessageParamProvider implements ParamProvider {

	@Override
	public Map<String, Object> params() {

		Map<String, Object> params = new HashMap<>();

		params.put("message", "Nice to meet you");

		return params;

	}

}
