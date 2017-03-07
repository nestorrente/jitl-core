package com.nestorrente.jitl.template;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;

import com.nestorrente.jitl.util.PatternUtils;
import com.nestorrente.jitl.util.ResourceUtils;

public class DefaultTemplateProcessor implements TemplateProcessor {

	private static final TemplateProcessor INSTANCE = new DefaultTemplateProcessor();

	public static TemplateProcessor getInstance() {
		return INSTANCE;
	}

	@Override
	public String renderString(String templateContents, Map<String, Object> parameters) {
		return PatternUtils.replace(templateContents, Pattern.compile("\\$([A-Za-z_][A-Za-z_0-9]+)"), (match, backrefs) -> {
			return Objects.toString(parameters.get(backrefs[1]));
		});
	}

	@Override
	public String renderResource(String templateResource, Map<String, Object> parameters) {

		try(InputStream is = ResourceUtils.getResourceAsStream(templateResource)) {

			return this.renderString(IOUtils.toString(is, Charset.defaultCharset()), parameters);

		} catch(IOException ex) {
			// TODO replace with a better exception
			throw new RuntimeException(ex);
		}

	}

}
