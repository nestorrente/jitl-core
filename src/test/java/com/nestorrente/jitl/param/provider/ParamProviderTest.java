package com.nestorrente.jitl.param.provider;

import com.nestorrente.jitl.Jitl;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Calendar;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;

public class ParamProviderTest {

	private static Jitl jitl;

	@BeforeClass
	public static void createInstance() {

		jitl = Jitl.builder()
				.registerParamProvider(new GreetingParamProvider("JUnit"))
				.autoRegisterParamProviders()
				.build();

	}

	/**
	 * Testing a dynamic param provider (it returns different parameter values in each call).
	 */
	@Test
	public void callingGetCurrentUTCDateReturnsCurrentDateMessage() {

		UsingDynamicProvider instance = jitl.getInstance(UsingDynamicProvider.class);

		String message = instance.getCurrentUTCDate();

		Calendar utcCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

		String expected = String.format(
				"Today is %s-%s-%s",
				utcCalendar.get(Calendar.YEAR),
				utcCalendar.get(Calendar.MONTH),
				utcCalendar.get(Calendar.DAY_OF_MONTH));

		assertEquals(expected, message);

	}

}
