package com.nestorrente.jitl.cache;

import java.util.function.Supplier;

public class CacheableResult<T> implements Supplier<T> {

	private final Supplier<T> resultSupplier;
	private boolean computed;
	private T result;

	public CacheableResult(Supplier<T> resultSupplier) {
		this.resultSupplier = resultSupplier;
		this.computed = false;
		this.result = null;
	}

	@Override
	public T get() {

		if(!this.computed) {
			this.result = this.resultSupplier.get();
			this.computed = true;
		}

		return this.result;

	}

}
