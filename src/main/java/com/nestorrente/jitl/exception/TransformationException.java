package com.nestorrente.jitl.exception;

public class TransformationException extends RuntimeException {

	private static final long serialVersionUID = 8180710032686601042L;

	public TransformationException(String message) {
		super(message);
	}

	public TransformationException(String message, Throwable cause) {
		super(message, cause);
	}

}
