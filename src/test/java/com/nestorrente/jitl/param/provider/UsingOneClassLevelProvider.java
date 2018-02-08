package com.nestorrente.jitl.param.provider;

import com.nestorrente.jitl.annotation.InlineTemplate;
import com.nestorrente.jitl.annotation.param.ParamProviders;

@ParamProviders(GreetingParamProvider.class)
public interface UsingOneClassLevelProvider {

	@InlineTemplate("$greeting, $name!")
	String useClassLevelProvider();

}
