package com.nestorrente.jitl.template;

import com.nestorrente.jitl.util.PatternUtils;

import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

public class DefaultTemplateEngine implements TemplateEngine {

	private static final TemplateEngine INSTANCE = new DefaultTemplateEngine();

	public static TemplateEngine getInstance() {
		return INSTANCE;
	}

	@Override
	public String render(String templateContents, Map<String, Object> parameters) {

		Pattern regex = Pattern.compile("\\$([A-Za-z_][A-Za-z_0-9]+)");

		return PatternUtils.replace(templateContents, regex, (match, backrefs) -> Objects.toString(parameters.get(backrefs[1])));

	}

}
