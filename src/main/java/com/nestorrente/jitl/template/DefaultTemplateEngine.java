package com.nestorrente.jitl.template;

import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

import com.nestorrente.jitl.util.PatternUtils;

public class DefaultTemplateEngine implements TemplateEngine {

	private static final TemplateEngine INSTANCE = new DefaultTemplateEngine();

	public static TemplateEngine getInstance() {
		return INSTANCE;
	}

	@Override
	public String renderString(String templateContents, Map<String, Object> parameters) {
		return PatternUtils.replace(templateContents, Pattern.compile("\\$([A-Za-z_][A-Za-z_0-9]+)"), (match, backrefs) -> {
			return Objects.toString(parameters.get(backrefs[1]));
		});
	}

}
