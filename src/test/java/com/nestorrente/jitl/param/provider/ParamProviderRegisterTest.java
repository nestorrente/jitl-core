package com.nestorrente.jitl.param.provider;

import com.nestorrente.jitl.param.CacheableParamProviderDecorator;
import com.nestorrente.jitl.param.ParamProvider;
import com.nestorrente.jitl.param.ParamProviderRegister;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ParamProviderRegisterTest {

	@Test(expected = IllegalStateException.class)
	public void getUnregisteredProviderWithoutUsingAutoRegisterFails() {

		ParamProviderRegister register = new ParamProviderRegister();

		register.getParamProvider(CurrentUTCTimeParamProvider.class, false);

	}

	@Test
	public void getUnregisteredProviderUsingAutoRegisterAlwaysReturnsTheFirstReturnedInstance() {

		ParamProviderRegister register = new ParamProviderRegister();

		ParamProvider provider1 = register.getParamProvider(CurrentUTCTimeParamProvider.class, true);
		ParamProvider provider2 = register.getParamProvider(CurrentUTCTimeParamProvider.class, true);

		assertEquals(provider1, provider2);

	}

	@Test
	public void getRegisteredProviderWithoutUsingAutoRegisterReturnsTheRegisteredInstance() {

		ParamProviderRegister register = new ParamProviderRegister();

		CurrentUTCTimeParamProvider provider1 = new CurrentUTCTimeParamProvider();
		register.registerParamProvider(provider1, false);

		ParamProvider provider2 = register.getParamProvider(CurrentUTCTimeParamProvider.class, false);

		assertEquals(provider1, provider2);

	}

	@Test
	public void getRegisteredProviderUsingAutoRegisterReturnsTheRegisteredInstance() {

		ParamProviderRegister register = new ParamProviderRegister();

		CurrentUTCTimeParamProvider provider1 = new CurrentUTCTimeParamProvider();
		register.registerParamProvider(provider1, false);

		ParamProvider provider2 = register.getParamProvider(CurrentUTCTimeParamProvider.class, true);

		assertEquals(provider1, provider2);

	}

	@Test
	public void getCacheableRegisteredProviderWithoutUsingAutoRegisterReturnsCacheableDecoratorInstance() {

		ParamProviderRegister register = new ParamProviderRegister();

		CurrentUTCTimeParamProvider provider1 = new CurrentUTCTimeParamProvider();
		register.registerParamProvider(provider1, true);

		ParamProvider provider2 = register.getParamProvider(CurrentUTCTimeParamProvider.class, false);

		assertTrue(provider2 instanceof CacheableParamProviderDecorator);

	}

	@Test
	public void getCacheableRegisteredProviderUsingAutoRegisterReturnsCacheableDecoratorInstance() {

		ParamProviderRegister register = new ParamProviderRegister();

		CurrentUTCTimeParamProvider provider = new CurrentUTCTimeParamProvider();
		register.registerParamProvider(provider, true);

		ParamProvider cacheableProvider = register.getParamProvider(CurrentUTCTimeParamProvider.class, true);

		assertTrue(cacheableProvider instanceof CacheableParamProviderDecorator);

	}

}
