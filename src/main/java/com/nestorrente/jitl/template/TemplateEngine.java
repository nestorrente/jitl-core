package com.nestorrente.jitl.template;

import java.util.Map;

public interface TemplateEngine {

	String renderString(String templateContents, Map<String, Object> parameters);

	String renderResource(String templateResource, Map<String, Object> parameters);

}
