package com.nestorrente.jitl;

import com.nestorrente.jitl.annotation.ClasspathTemplate;
import com.nestorrente.jitl.annotation.InlineTemplate;

public interface HelloWorld {

	String sayHello(String name);

	@ClasspathTemplate("say_hello_nickname.customextension")
	String sayHello(String name, String nickname);

	@InlineTemplate("Goodbye, $name!")
	String sayBye(String name);

}
