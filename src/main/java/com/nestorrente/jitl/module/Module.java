package com.nestorrente.jitl.module;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import com.nestorrente.jitl.Jitl;

public abstract class Module {

	private final Collection<String> fileExtensions;
	private final Collection<String> unmodifiableFileExtensionsView;

	public Module(Collection<String> fileExtensions) {
		this.fileExtensions = new ArrayList<>(fileExtensions);
		this.unmodifiableFileExtensionsView = Collections.unmodifiableCollection(this.fileExtensions);
	}

	public final Collection<String> getFileExtensions() {
		return this.unmodifiableFileExtensionsView;
	}

	public Object postProcess(Jitl jitl, Method method, String renderedTemplate, Map<String, Object> parameters) throws Exception {

		if(!String.class.equals(method.getReturnType())) {
			// TODO replace with a custom exception
			throw new IllegalArgumentException("Cannot transform template result to " + method.getGenericReturnType().getTypeName());
		}

		return renderedTemplate;

	}

}
