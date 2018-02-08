package com.nestorrente.jitl;

import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class HelloWorldTest {

	private static HelloWorld INSTANCE;

	@BeforeClass
	public static void createInstance() {

		Jitl jitl = Jitl.defaultInstance();

		INSTANCE = jitl.getInstance(HelloWorld.class);

	}

	@Test
	public void sayHelloReceivingWorldReturnsHelloWorld() {

		String message = INSTANCE.sayHello("world");

		assertEquals("Hello, world!", message);

	}

	@Test
	public void sayHelloReceivingWorldAndEarthReturnsHelloWorld() {

		String message = INSTANCE.sayHello("world", "Earth");

		assertEquals("Hello, world (a.k.a. Earth)!", message);

	}

	@Test
	public void sayByeReceivingWorldReturnsGoodbyeWorld() {

		String message = INSTANCE.sayBye("world");

		assertEquals("Goodbye, world!", message);

	}

}
