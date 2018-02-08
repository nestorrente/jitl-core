package com.nestorrente.jitl.template;

import java.util.Map;

public interface TemplateEngine {

	String render(String templateContents, Map<String, Object> parameters);

}
