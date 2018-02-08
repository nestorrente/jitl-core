package com.nestorrente.jitl.exception;

public class UncheckedReflectiveOperationException extends RuntimeException {

	private static final long serialVersionUID = -3022278389348878455L;

	public UncheckedReflectiveOperationException(ReflectiveOperationException cause) {
		super(cause);
	}

}
