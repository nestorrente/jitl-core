package com.nestorrente.jitl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.builder.Builder;

import com.nestorrente.jitl.module.Module;
import com.nestorrente.jitl.template.DefaultTemplateEngine;
import com.nestorrente.jitl.template.TemplateEngine;
import com.nestorrente.jitl.util.ArrayUtils;

public class JitlBuilder implements Builder<Jitl> {

	private TemplateEngine templateEngine;
	private final List<String> fileExtensions;
	private final Map<Class<? extends Module>, Module> modules;

	JitlBuilder() {
		this.templateEngine = DefaultTemplateEngine.getInstance();
		this.fileExtensions = new ArrayList<>();
		this.modules = new HashMap<>();
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

	public JitlBuilder addModule(Module module) {
		this.modules.put(module.getClass(), module);
		return this;
	}

	@Override
	public Jitl build() {

		List<String> instanceFileExtensions = new ArrayList<>(this.fileExtensions);

		Collections.reverse(instanceFileExtensions);

		return new Jitl(this.templateEngine, instanceFileExtensions, this.modules);

	}

}
