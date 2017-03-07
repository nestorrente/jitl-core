package com.nestorrente.jitl;

import com.nestorrente.jitl.annotation.InlineTemplate;
import com.nestorrente.jitl.annotation.Param;

public interface Messages {

	@InlineTemplate("Hello, $name!")
	String sayHello(@Param("name") String name);

}
