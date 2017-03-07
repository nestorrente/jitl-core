package com.nestorrente.jitl;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.nestorrente.jitl.module.JitlModule;
import com.nestorrente.jitl.template.TemplateEngine;

// TODO IMPORTANTE: añadir case converters (o path converters) para transformar las clases/métodos en filepaths
// TODO Interesante: añadir formas de crear instancias por defecto de Jitl sin rallarse? Implicaría eso necesariamente tener algún template engine y algún módulo en el core?
// TODO crear más módulos y adaptar más procesadores de plantillas.
public class Jitl {

	private final TemplateEngine templateEngine;

	private final Collection<String> fileExtensions;
	private final Collection<String> unmodifiableFileExtensionsView;

	private final Map<Class<? extends JitlModule>, JitlModule> modules;
	private final Map<Class<? extends JitlModule>, JitlModule> unmodifiableModulesView;

	Jitl(TemplateEngine templateEngine, Collection<String> fileExtensions, Map<Class<? extends JitlModule>, JitlModule> modules) {

		this.templateEngine = templateEngine;

		this.fileExtensions = new ArrayList<>(fileExtensions);
		this.fileExtensions.add("txt");
		this.fileExtensions.add("tpl");

		this.unmodifiableFileExtensionsView = Collections.unmodifiableCollection(this.fileExtensions);

		this.modules = new HashMap<>(modules);

		this.unmodifiableModulesView = Collections.unmodifiableMap(this.modules);

	}

	/**
	 * @param interfaze Interface to be implemented by resultant object.
	 * @return An object that implements {@code interfaze} method's.
	 */
	public <T> T getInstance(Class<T> interfaze) {

		if(!interfaze.isInterface()) {
			throw new IllegalArgumentException(String.format("Class %s is not an interface", interfaze.getName()));
		}

		@SuppressWarnings("unchecked")
		T object = (T) Proxy.newProxyInstance(Jitl.class.getClassLoader(), new Class<?>[] { interfaze }, new JitlMethodInvocationHandler(interfaze, this));

		return object;

	}

	public TemplateEngine getTemplateProcessor() {
		return this.templateEngine;
	}

	public Collection<String> getFileExtensions() {
		return this.unmodifiableFileExtensionsView;
	}

	public Map<Class<? extends JitlModule>, JitlModule> getModules() {
		return this.unmodifiableModulesView;
	}

}
