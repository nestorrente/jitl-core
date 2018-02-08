package com.nestorrente.jitl.param.provider;

import com.nestorrente.jitl.annotation.InlineTemplate;
import com.nestorrente.jitl.annotation.param.ParamProviders;

public interface UsingMethodLevelProviders {

	@InlineTemplate("$greeting, $name!")
	@ParamProviders(GreetingParamProvider.class)
	String oneProvider();

	@InlineTemplate("$greeting, $name! $message!")
	@ParamProviders({ GreetingParamProvider.class, MessageParamProvider.class })
	String multipleProviders();

}
