package com.nestorrente.jitl.processor;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public abstract class Processor {

	private final Collection<String> fileExtensions;
	private final Collection<String> unmodifiableFileExtensionsView;

	public Processor() {
		this.fileExtensions = new ArrayList<>();
		this.unmodifiableFileExtensionsView = Collections.unmodifiableCollection(this.fileExtensions);
	}

	// @Deprecated
	public Processor(Collection<String> fileExtensions) {
		this();
		this.fileExtensions.addAll(fileExtensions);
	}

	// protected final void addExtension(String extension) {
	// 	this.fileExtensions.add(extension);
	// }
	//
	// protected final void addExtensions(String... extensions) {
	// 	ArrayUtils.addAll(this.fileExtensions, extensions);
	// }
	//
	// protected final void addExtensions(Collection<String> extensions) {
	// 	this.fileExtensions.addAll(extensions);
	// }

	public final Collection<String> getFileExtensions() {
		return this.unmodifiableFileExtensionsView;
	}

	public abstract Object process(Method method, String renderedTemplate, Map<String, Object> parameters) throws Exception;

}
