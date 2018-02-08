package com.nestorrente.jitl.cache;

import java.util.function.Supplier;

public class CacheableResult<T> implements Supplier<T> {

	private final Supplier<T> resultSupplier;
	private boolean executed;
	private T result;

	public CacheableResult(Supplier<T> resultSupplier) {
		this.resultSupplier = resultSupplier;
		this.executed = false;
		this.result = null;
	}

	@Override
	public T get() {

		if(!this.executed) {
			this.result = this.resultSupplier.get();
			this.executed = true;
		}

		return this.result;

	}

}
