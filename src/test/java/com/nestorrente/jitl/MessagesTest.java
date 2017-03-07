package com.nestorrente.jitl;

import static org.junit.Assert.assertEquals;

import org.junit.BeforeClass;
import org.junit.Test;

public class MessagesTest {

	private static Messages MESSAGES;

	@BeforeClass
	public static void createMessages() {

		Jitl jitl = new JitlBuilder()
			.build();

		MESSAGES = jitl.getInstance(Messages.class);

	}

	@Test
	public void sayHelloReceivingWorldReturnsHelloWorld() {

		String message = MESSAGES.sayHello("world");

		assertEquals("Hello, world!", message);

	}

}
