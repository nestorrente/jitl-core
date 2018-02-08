package com.nestorrente.jitl.param.provider;

import com.nestorrente.jitl.annotation.InlineTemplate;
import com.nestorrente.jitl.annotation.param.ParamProviders;

public interface UsingDynamicProvider {

	@InlineTemplate("Today is $year-$month-$day")
	@ParamProviders(CurrentUTCTimeParamProvider.class)
	String getCurrentUTCDate();

}
