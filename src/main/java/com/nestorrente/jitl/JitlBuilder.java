package com.nestorrente.jitl;

import com.nestorrente.jitl.cache.CacheManager;
import com.nestorrente.jitl.cache.CacheStrategy;
import com.nestorrente.jitl.cache.MapCacheManager;
import com.nestorrente.jitl.param.ParamProvider;
import com.nestorrente.jitl.param.ParamProviderRegister;
import com.nestorrente.jitl.processor.Processor;
import com.nestorrente.jitl.template.DefaultTemplateEngine;
import com.nestorrente.jitl.template.TemplateEngine;
import com.nestorrente.jitl.util.ArrayUtils;
import org.apache.commons.lang3.builder.Builder;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

public class JitlBuilder implements Builder<Jitl> {

	private TemplateEngine templateEngine;
	private final List<String> fileExtensions;
	private final Map<Class<? extends Processor>, Processor> processors;
	private Charset encoding;
	private CacheStrategy cacheStrategy;
	private Supplier<? extends CacheManager> cacheManagerSupplier;
	private final ParamProviderRegister paramProviderRegister;
	private boolean autoRegisterParamProviders;

	JitlBuilder() {
		this.templateEngine = DefaultTemplateEngine.getInstance();
		this.fileExtensions = new ArrayList<>();
		this.processors = new HashMap<>();
		this.encoding = Charset.defaultCharset();
		this.cacheStrategy = CacheStrategy.URI;
		this.cacheManagerSupplier = MapCacheManager::new;
		this.paramProviderRegister = new ParamProviderRegister();
		this.autoRegisterParamProviders = false;
	}

	public JitlBuilder setTemplateEngine(TemplateEngine templateEngine) {
		this.templateEngine = templateEngine;
		return this;
	}

	public JitlBuilder addFileExtension(String extension) {
		this.fileExtensions.add(extension);
		return this;
	}

	public JitlBuilder addFileExtensions(Collection<String> extensions) {
		this.fileExtensions.addAll(extensions);
		return this;
	}

	public JitlBuilder addFileExtensions(String... extensions) {
		ArrayUtils.addAll(this.fileExtensions, extensions);
		return this;
	}

	public JitlBuilder registerProcessor(Processor processor) {

		Class<? extends Processor> clazz = processor.getClass();

		if(this.processors.containsKey(clazz)) {
			throw new IllegalStateException(String.format("Repeated processor: %s", clazz.getName()));
		}

		this.processors.put(clazz, processor);

		return this;

	}

	public JitlBuilder setEncoding(Charset encoding) {
		this.encoding = Objects.requireNonNull(encoding);
		return this;
	}

	public JitlBuilder setCacheStrategy(CacheStrategy cacheStrategy) {
		this.cacheStrategy = Objects.requireNonNull(cacheStrategy);
		return this;
	}

	public JitlBuilder setCacheManagerSupplier(Supplier<? extends CacheManager> cacheManagerSupplier) {
		Objects.requireNonNull(cacheManagerSupplier);
		this.cacheManagerSupplier = cacheManagerSupplier;
		return this;
	}

	public JitlBuilder registerParamProvider(ParamProvider paramProvider) {
		this.paramProviderRegister.registerParamProvider(paramProvider);
		return this;
	}

	public JitlBuilder registerParamProvider(ParamProvider paramProvider, boolean cacheable) {
		this.paramProviderRegister.registerParamProvider(paramProvider, cacheable);
		return this;
	}

	public JitlBuilder autoRegisterParamProviders() {
		this.autoRegisterParamProviders = true;
		return this;
	}

	@Override
	public Jitl build() {

		List<String> instanceFileExtensions = new ArrayList<>(this.fileExtensions);

		Collections.reverse(instanceFileExtensions);

		return new Jitl(
				this.templateEngine,
				instanceFileExtensions,
				this.processors, this.encoding,
				this.cacheStrategy,
				this.cacheManagerSupplier,
				this.paramProviderRegister,
				this.autoRegisterParamProviders
		);

	}

}
