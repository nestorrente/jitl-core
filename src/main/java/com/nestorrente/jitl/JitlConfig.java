package com.nestorrente.jitl;

import com.nestorrente.jitl.param.ParamProviderRegister;
import com.nestorrente.jitl.processor.Processor;
import com.nestorrente.jitl.template.TemplateEngine;

import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Map;

public class JitlConfig {

	private final TemplateEngine templateEngine;
	private final Collection<String> fileExtensions;

	private final Map<Class<? extends Processor>, Processor> processors;

	private final Charset encoding;

	private final boolean cacheTemplates;

	private final ParamProviderRegister paramProviderRegister;
	private final boolean paramProviderAutoRegisterEnabled;

	public JitlConfig(
			TemplateEngine templateEngine,
			Collection<String> fileExtensions,
			Map<Class<? extends Processor>, Processor> processors,
			Charset encoding,
			boolean cacheTemplates,
			ParamProviderRegister paramProviderRegister,
			boolean paramProviderAutoRegisterEnabled
	) {
		this.templateEngine = templateEngine;
		this.fileExtensions = fileExtensions;
		this.processors = processors;
		this.encoding = encoding;
		this.cacheTemplates = cacheTemplates;
		this.paramProviderRegister = paramProviderRegister;
		this.paramProviderAutoRegisterEnabled = paramProviderAutoRegisterEnabled;
	}

	public TemplateEngine getTemplateEngine() {
		return this.templateEngine;
	}

	public Collection<String> getFileExtensions() {
		return this.fileExtensions;
	}

	public Map<Class<? extends Processor>, Processor> getProcessors() {
		return this.processors;
	}

	public Charset getEncoding() {
		return this.encoding;
	}

	public boolean isCacheTemplates() {
		return this.cacheTemplates;
	}

	public ParamProviderRegister getParamProviderRegister() {
		return this.paramProviderRegister;
	}

	public boolean isParamProviderAutoRegisterEnabled() {
		return this.paramProviderAutoRegisterEnabled;
	}

}
