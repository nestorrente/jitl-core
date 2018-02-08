package com.nestorrente.jitl.param.provider;

import com.nestorrente.jitl.annotation.InlineTemplate;
import com.nestorrente.jitl.annotation.param.ParamProviders;

@ParamProviders({ GreetingParamProvider.class, MessageParamProvider.class })
public interface UsingMultipleClassLevelProviders {

	@InlineTemplate("$greeting, $name! $message!")
	String test();

}
