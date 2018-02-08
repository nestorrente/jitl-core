package com.nestorrente.jitl.template;

import java.util.Map;

public class NoOpTemplateEngine implements TemplateEngine {

	private static final TemplateEngine INSTANCE = new NoOpTemplateEngine();

	public static TemplateEngine getInstance() {
		return INSTANCE;
	}

	@Override
	public String render(String templateContents, Map<String, Object> parameters) {
		return templateContents;
	}

}
