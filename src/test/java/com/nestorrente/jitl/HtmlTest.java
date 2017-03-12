package com.nestorrente.jitl;

import static org.junit.Assert.assertEquals;

import org.junit.BeforeClass;
import org.junit.Test;

public class HtmlTest {

	private static Html INSTANCE;

	@BeforeClass
	public static void createInstance() {

		Jitl jitl = Jitl.builder()
			.addFileExtensions("htm", "html")
			.build();

		INSTANCE = jitl.getInstance(Html.class);

	}

	@Test
	public void welcomeReceivingGunsAndRosesAndTheJungleReturnsWelcomeToTheJunglePage() {

		String message = INSTANCE.welcome("Guns & Roses", "the jungle");

		assertEquals("<html>\n<head>\n<title>Guns & Roses</title>\n</head>\n<body>\n<p>Welcome to the jungle!</p>\n</body>\n</html>", message);

	}

	@Test
	public void headerReceivingJitlReturnsJitl() {

		String message = INSTANCE.header("Jitl");

		assertEquals("<h1>Jitl</h1>", message);

	}

	@Test(expected = RuntimeException.class)
	public void notFoundThrowsRuntimeException() {
		INSTANCE.notFound();
	}

}
