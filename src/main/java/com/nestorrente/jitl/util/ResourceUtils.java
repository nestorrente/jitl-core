package com.nestorrente.jitl.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;

import com.nestorrente.jitl.exception.RuntimeIOException;

public class ResourceUtils {

	public static final String FOLDER_SEPARATOR = "/";

	private static final Pattern PACKAGE_SPLIT_PATTERN = Pattern.compile("\\.");

	// com.nestorrente.example.MyClass -> /com/nestorrente/example/my_class/
	// com.nestorrente.example -> /com/nestorrente/example/
	// MyClass -> /my_class/
	public static String packageOrClassNameToUri(String packageName) {
		return Arrays.stream(PACKAGE_SPLIT_PATTERN.split(packageName)).map(StringUtils::camelToLowerUnderscore).collect(Collectors.joining(FOLDER_SEPARATOR, FOLDER_SEPARATOR, FOLDER_SEPARATOR));
	}

	public static String ensureAbsoluteUri(String uri) {
		return uri.startsWith(FOLDER_SEPARATOR) ? uri : FOLDER_SEPARATOR + uri;
	}

	public static boolean resourceExists(String uri) {
		return ResourceUtils.class.getResource(ensureAbsoluteUri(uri)) != null;
	}

	public static InputStream getResourceAsStream(String uri) {

		InputStream is = ResourceUtils.class.getResourceAsStream(ensureAbsoluteUri(uri));

		if(is == null) {
			throw new RuntimeIOException("Resource " + uri + " not found in classpath");
		}

		return is;

	}

	public static String getResourceContents(String uri, Charset charset) {
		try(InputStream is = getResourceAsStream(uri)) {
			return IOUtils.toString(is, charset);
		} catch(IOException ex) {
			throw new RuntimeIOException("I/O error ocurred while reading the resource", ex);
		}
	}

	/**
	 * Method created in order to avoid double opening and closing when using {@link #resourceExists(String)} followed by {@link #getResourceContents(String, Charset)}.
	 *
	 * @param uri Classpath URI of the resource
	 * @param charset Resource encoding
	 * @return Resource contents if resource exists; {@code Optional.empty()} otherwise.
	 */
	public static Optional<String> getResourceContentsIfExists(String uri, Charset charset) {

		InputStream is = ResourceUtils.class.getResourceAsStream(ensureAbsoluteUri(uri));

		if(is == null) {
			return Optional.empty();
		}

		try {

			return Optional.of(IOUtils.toString(is, charset));

		} catch(IOException ex) {

			throw new RuntimeIOException("I/O error ocurred while reading the resource", ex);

		} finally {

			try {
				is.close();
			} catch(IOException ignored) {}

		}

	}

}
