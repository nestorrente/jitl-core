package com.nestorrente.jitl.exception;

import java.io.IOException;

public class RuntimeIOException extends RuntimeException {

	private static final long serialVersionUID = -7894313314719866367L;

	public RuntimeIOException(String message) {
		super(message);
	}

	public RuntimeIOException(IOException cause) {
		super(cause);
	}

	public RuntimeIOException(String message, IOException cause) {
		super(message, cause);
	}

}
