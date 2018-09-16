package com.nestorrente.jitl.param;

import com.nestorrente.jitl.cache.CacheableResult;

import java.util.Map;

public class CacheableParamProviderDecorator implements ParamProvider {

	private final CacheableResult<Map<String, Object>> params;

	public CacheableParamProviderDecorator(ParamProvider realProvider) {
		this.params = new CacheableResult<>(realProvider::params);
	}

	@Override
	public Map<String, Object> params() {
		return this.params.get();
	}

}
