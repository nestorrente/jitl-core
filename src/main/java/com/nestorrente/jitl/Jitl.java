package com.nestorrente.jitl;

import com.nestorrente.jitl.cache.CacheManager;
import com.nestorrente.jitl.cache.CacheStrategy;
import com.nestorrente.jitl.param.ParamProviderRegister;
import com.nestorrente.jitl.processor.Processor;
import com.nestorrente.jitl.template.TemplateEngine;
import com.nestorrente.jitl.util.ProxyUtils;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

// TODO Important: add case-converters (or path-converters) for allowing custom "class+method to filepath" transformation
// TODO Interesting: give a way to create Jitl instances with default engines and post-processors? (i.e., a Jitl instance with Jtwig template engine and SQL post-processor)
// TODO create more post-processors and template engines
public class Jitl {

	private final TemplateEngine templateEngine;
	private final Collection<String> unmodifiableFileExtensionsView;

	private final Map<Class<? extends Processor>, Processor> processors;

	private final Charset encoding;

	private final CacheStrategy cacheStrategy;
	private final Supplier<? extends CacheManager> cacheManagerSupplier;

	private final ParamProviderRegister paramProviderRegister;
	private final boolean autoRegisterParamProviders;

	Jitl(
			TemplateEngine templateEngine,
			Collection<String> fileExtensions,
			Map<Class<? extends Processor>, Processor> processors,
			Charset encoding,
			CacheStrategy cacheStrategy,
			Supplier<? extends CacheManager> cacheManagerSupplier,
			ParamProviderRegister paramProviderRegister,
			boolean autoRegisterParamProviders
	) {

		this.templateEngine = templateEngine;

		// TODO rethink this variable
		Collection<String> fileExtensionsCopy = new ArrayList<>(fileExtensions);
		fileExtensionsCopy.add("txt");
		fileExtensionsCopy.add("tpl");

		this.unmodifiableFileExtensionsView = Collections.unmodifiableCollection(fileExtensionsCopy);

		this.processors = new HashMap<>(processors);

		this.encoding = encoding;

		this.cacheStrategy = cacheStrategy;
		this.cacheManagerSupplier = cacheManagerSupplier;

		this.paramProviderRegister = paramProviderRegister;
		this.autoRegisterParamProviders = autoRegisterParamProviders;

	}

	/**
	 * @param interfaze Interface to be implemented by resultant object.
	 * @return An object that implements {@code interfaze} method's.
	 */
	public <T> T getInstance(Class<T> interfaze) {

		if(!interfaze.isInterface()) {
			throw new IllegalArgumentException(String.format("Class %s is not an interface", interfaze.getName()));
		}

		// FIXME ensure singleton instances of each interface

		return ProxyUtils.createProxy(interfaze, new JitlProxyInvocationHandler(
				this.templateEngine,
				this.processors,
				this.unmodifiableFileExtensionsView,
				this.encoding,
				this.cacheStrategy,
				this.cacheManagerSupplier.get(),
				this.paramProviderRegister,
				this.autoRegisterParamProviders
		));

	}

	/* Build methods */

	public static JitlBuilder builder() {
		return new JitlBuilder();
	}

	public static Jitl defaultInstance() {
		return builder().build();
	}

}
