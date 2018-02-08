package com.nestorrente.jitl.cache;

import org.junit.Test;
import org.mockito.Mockito;

import java.util.function.Supplier;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class CacheableResultTest {

	@Test
	public void noCallsResultsInNoCalls() throws InterruptedException {

		Supplier<?> mockSupplier = Mockito.mock(Supplier.class);

		CacheableResult<?> result = new CacheableResult<>(mockSupplier);

		verify(mockSupplier, times(0)).get();

	}

	@Test
	public void fourCallsResultsInOneRealCall() throws InterruptedException {

		Supplier<?> mockSupplier = Mockito.mock(Supplier.class);

		CacheableResult<?> result = new CacheableResult<>(mockSupplier);

		result.get();
		result.get();
		result.get();
		result.get();

		verify(mockSupplier, times(1)).get();

	}

}
