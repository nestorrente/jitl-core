package com.nestorrente.jitl.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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

	public static URL getResource(String uri) throws IOException {

		URL is = ResourceUtils.class.getResource(ensureAbsoluteUri(uri));

		if(is == null) {
			throw new IOException("Resource " + uri + " not found in classpath");
		}

		return is;

	}

	public static InputStream getResourceAsStream(String uri) throws IOException {

		InputStream is = ResourceUtils.class.getResourceAsStream(ensureAbsoluteUri(uri));

		if(is == null) {
			throw new IOException("Resource " + uri + " not found in classpath");
		}

		return is;

	}

}
