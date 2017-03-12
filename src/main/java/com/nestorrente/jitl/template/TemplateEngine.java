package com.nestorrente.jitl.template;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import com.nestorrente.jitl.util.ResourceUtils;

public interface TemplateEngine {

	String renderString(String templateContents, Map<String, Object> parameters);

	default String renderResource(String templateResource, Map<String, Object> parameters) {

		try(InputStream is = ResourceUtils.getResourceAsStream(templateResource)) {

			return this.renderString(IOUtils.toString(is, Charset.defaultCharset()), parameters);

		} catch(IOException ex) {
			// TODO replace with a custom exception
			throw new RuntimeException(ex);
		}

	}

}
