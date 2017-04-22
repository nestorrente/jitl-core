package com.nestorrente.jitl;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.nestorrente.jitl.module.Module;
import com.nestorrente.jitl.template.TemplateEngine;

// TODO Important: add case-converters (or path-converters) for allowing custom "class+method to filepath" transformation
// TODO Interesting: give a way to create Jitl instances with default engines and post-processors? (i.e., a Jitl instance with Jtwig template engine and SQL post-processor)
// TODO create more post-processors and template engines
public class Jitl {

	private final TemplateEngine templateEngine;

	private final Collection<String> unmodifiableFileExtensionsView;

	private final Map<Class<? extends Module>, Module> modules;

	Jitl(TemplateEngine templateEngine, Collection<String> fileExtensions, Map<Class<? extends Module>, Module> modules) {

		this.templateEngine = templateEngine;

		// TODO rethink this variable
		Collection<String> fileExtensionsCopy = new ArrayList<>(fileExtensions);
		fileExtensionsCopy.add("txt");
		fileExtensionsCopy.add("tpl");

		this.unmodifiableFileExtensionsView = Collections.unmodifiableCollection(fileExtensionsCopy);

		this.modules = new HashMap<>(modules);

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

	TemplateEngine getTemplateEngine() {
		return this.templateEngine;
	}

	Collection<String> getFileExtensions() {
		return this.unmodifiableFileExtensionsView;
	}

	<M extends Module> M getModule(Class<M> moduleClass) {

		@SuppressWarnings("unchecked")
		M module = (M) this.modules.get(moduleClass);

		return module;

	}

	/* Build methods */

	public static JitlBuilder builder() {
		return new JitlBuilder();
	}

	public static Jitl defaultInstance() {
		return builder().build();
	}

}
