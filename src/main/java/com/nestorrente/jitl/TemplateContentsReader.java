package com.nestorrente.jitl;

import com.nestorrente.jitl.annotation.BaseClasspath;
import com.nestorrente.jitl.annotation.ClasspathTemplate;
import com.nestorrente.jitl.annotation.Encoding;
import com.nestorrente.jitl.annotation.InlineTemplate;
import com.nestorrente.jitl.cache.CacheManager;
import com.nestorrente.jitl.cache.CacheStrategy;
import com.nestorrente.jitl.exception.RuntimeIOException;
import com.nestorrente.jitl.exception.WrongAnnotationUseException;
import com.nestorrente.jitl.util.AnnotationUtils;
import com.nestorrente.jitl.util.ResourceUtils;
import com.nestorrente.jitl.util.StringUtils;

import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.Optional;
import java.util.function.Supplier;

// TODO refactor this class for doing unit-tests (i.e., a method that allows get the resource path of a class method)
// TODO think the way to use Mockito in order to test cache-calls
class TemplateContentsReader {

	private final Method method;
	private final Charset encoding;
	private final CacheStrategy cacheStrategy;
	private final CacheManager cacheManager;
	private final Supplier<Iterable<String>> fileExtensionsSupplier;

	TemplateContentsReader(
			Method method,
			Charset encoding,
			CacheStrategy cacheStrategy,
			CacheManager cacheManager,
			Supplier<Iterable<String>> fileExtensionsSupplier) {
		this.method = method;
		this.encoding = encoding;
		this.cacheStrategy = cacheStrategy;
		this.cacheManager = cacheManager;
		this.fileExtensionsSupplier = fileExtensionsSupplier;
	}

	String getTemplateContents() {

		Optional<String> inlineAnnotationValue = AnnotationUtils.get(this.method, InlineTemplate.class)
				.map(InlineTemplate::value);

		Optional<String> classpathAnnotationValue = AnnotationUtils.get(this.method, ClasspathTemplate.class)
				.map(ClasspathTemplate::value);

		if(inlineAnnotationValue.isPresent()) {

			if(classpathAnnotationValue.isPresent()) {
				throw new WrongAnnotationUseException(String.format("Cannot use @%s and @%s annotations at the same time", InlineTemplate.class.getSimpleName(), ClasspathTemplate.class.getSimpleName()));
			}

			return inlineAnnotationValue.get();

		}

		return this.getClasspathTemplateContents(classpathAnnotationValue.orElse(null));

	}

	private Charset getTemplateEncoding() {
		return AnnotationUtils.getFromMethodOrClass(this.method, Encoding.class)
				.map(Encoding::value)
				.map(Charset::forName)
				.orElse(this.encoding);
	}

	private String getClasspathTemplateContents(String classpathAnnotationValue) {

		// FIXME si la implementación de de CacheManager va a ser personalizable,
		// igual no conviene llamar a getContents sin comprobar si la estrategia de caché es CONTENTS
		return this.cacheManager.getContents(this.method).orElseGet(() -> {

			// FIXME si la implementación de de CacheManager va a ser personalizable,
			// igual no conviene llamar a getUri sin comprobar si la estrategia de caché es URI
			String templateUri = this.cacheManager.getUri(this.method)
					.orElseGet(() -> this.computeAndCacheTemplateUri(classpathAnnotationValue));

			Charset templateCharset = this.getTemplateEncoding();

			return this.readAndCacheTemplateContents(templateUri, templateCharset);

		});

	}

	private void cacheTemplateUri(String templateUri) {
		if(this.cacheStrategy == CacheStrategy.URI) {
			this.cacheManager.cacheUri(this.method, templateUri);
		}
	}

	private String computeAndCacheTemplateUri(String classpathAnnotationValue) {

		String templateUri = this.computeTemplateUri(classpathAnnotationValue);

		this.cacheTemplateUri(templateUri);

		return templateUri;

	}

	private String computeTemplateUri(String classpathAnnotationValue) {

		Class<?> declaringClass = this.method.getDeclaringClass();

		// TODO allow custom className/methodName to classpath URI transformation?

		String templateUri = Optional.ofNullable(classpathAnnotationValue)
				.filter(s -> !s.isEmpty())
				.orElseGet(() -> StringUtils.camelToLowerUnderscore(this.method.getName()));

		if(templateUri.charAt(0) == '/') {
			return templateUri;
		}

		String baseClasspathUri = AnnotationUtils.get(declaringClass, BaseClasspath.class)
				.map(BaseClasspath::value)
				.map(ResourceUtils::ensureAbsoluteUri)
				.orElseGet(() -> ResourceUtils.packageOrClassNameToUri(declaringClass.getCanonicalName()));

		return baseClasspathUri + templateUri;

	}

	private void cacheTemplateContents(String templateContents) {
		if(this.cacheStrategy == CacheStrategy.CONTENTS) {
			this.cacheManager.cacheContents(this.method, templateContents);
		}
	}

	private String readAndCacheTemplateContents(String templateUri, Charset templateCharset) {

		String templateContents = this.readTemplateContents(templateUri, templateCharset);

		this.cacheTemplateContents(templateContents);

		return templateContents;

	}

	private String readTemplateContents(String templateUri, Charset templateCharset) {

		Optional<String> contents = ResourceUtils.getResourceContentsIfExists(templateUri, templateCharset);

		if(contents.isPresent()) {
			return contents.get();
		}

		for(String extension : this.fileExtensionsSupplier.get()) {

			String templateUriWithExtension = templateUri + "." + extension;

			contents = ResourceUtils.getResourceContentsIfExists(templateUriWithExtension, templateCharset);

			if(contents.isPresent()) {
				return contents.get();
			}

		}

		throw new RuntimeIOException("Resource not found: " + templateUri);

	}

}
